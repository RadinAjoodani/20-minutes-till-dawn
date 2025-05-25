package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Represents an active enemy in the game, managing its state, position, and animations.
 */
public class Enemy {
    private EnemyData enemyData;
    private float x, y; // Current position
    private float currentHp;
    private ObjectMap<String, Animation<TextureRegion>> animations; // Loaded animations (e.g., "show", "spawn")
    private float stateTime; // Time accumulator for current animation
    private String currentAnimationName; // "show", "spawn", "attack", etc.
    private float drawWidth, drawHeight; // Scaled drawing dimensions
    public static final float ENEMY_SCALE_FACTOR = 1.5f; // Example scale factor

    private float attackTimer; // Cooldown for melee attack
    private float shootTimer; // Cooldown for ranged attack (e.g., EyeBat)


    public Enemy(EnemyData enemyData, ObjectMap<String, Animation<TextureRegion>> animations, float startX, float startY) {
        this.enemyData = enemyData;
        this.animations = animations;
        this.x = startX;
        this.y = startY;
        if (this.enemyData != null) { // Null check for enemyData
            this.currentHp = enemyData.getHp();
            this.currentAnimationName = "show"; // Default animation
            // Calculate initial draw dimensions based on the first frame of the default animation
            TextureRegion firstFrame = getAnimationFrame(currentAnimationName, 0f);
            if (firstFrame != null) {
                this.drawWidth = firstFrame.getRegionWidth() * ENEMY_SCALE_FACTOR;
                this.drawHeight = firstFrame.getRegionHeight() * ENEMY_SCALE_FACTOR;
            } else {
                // Try "idle" if "show" isn't found
                firstFrame = getAnimationFrame("idle", 0f);
                if (firstFrame != null) {
                    this.currentAnimationName = "idle";
                    this.drawWidth = firstFrame.getRegionWidth() * ENEMY_SCALE_FACTOR;
                    this.drawHeight = firstFrame.getRegionHeight() * ENEMY_SCALE_FACTOR;
                } else {
                    Gdx.app.error("Enemy", "No 'show' or 'idle' animation found for " + enemyData.getName() + ". Using default size.");
                    this.drawWidth = 50 * ENEMY_SCALE_FACTOR; // Fallback size
                    this.drawHeight = 50 * ENEMY_SCALE_FACTOR;
                }
            }
        } else {
            Gdx.app.error("Enemy", "CRITICAL: EnemyData is null in Enemy constructor. Enemy will not function.");
            // Initialize with placeholder/default values to prevent further NPEs, though this enemy is broken
            this.currentHp = 0;
            this.drawWidth = 50 * ENEMY_SCALE_FACTOR;
            this.drawHeight = 50 * ENEMY_SCALE_FACTOR;
            this.currentAnimationName = "none";
        }
        this.stateTime = 0f;
        this.attackTimer = 0f;
        this.shootTimer = 0f;
    }

    /**
     * Updates the enemy's state, including movement and animation.
     * @param delta Time since last frame.
     * @param targetX X-coordinate of the target (e.g., player).
     * @param targetY Y-coordinate of the target (e.g., player).
     */
    public void update(float delta, float targetX, float targetY) {
        if (enemyData == null) { // If enemyData is null, can't do much
            return;
        }

        stateTime += delta;
        attackTimer += delta;
        shootTimer += delta;

        // Movement logic
        if (enemyData.getSpeed() > 0 && !(enemyData.getName().equals("Tree"))) {
            float oldX = x;
            float oldY = y;

            Vector2 enemyPos = new Vector2(x, y);
            Vector2 targetPos = new Vector2(targetX, targetY);
            Vector2 direction = targetPos.sub(enemyPos); // Get vector from enemy to target

            // Log for a specific enemy type to avoid spamming console too much
            if (enemyData.getName().equals("TentacleMonster") || enemyData.getName().equals("EyeBat")) {
                Gdx.app.log("EnemyMoveDebug_" + enemyData.getName(), "Delta: " + delta +
                    ", Speed: " + enemyData.getSpeed() +
                    ", CurrentPos: (" + String.format("%.2f", x) + "," + String.format("%.2f", y) + ")" +
                    ", TargetPos: (" + String.format("%.2f", targetX) + "," + String.format("%.2f", targetY) + ")" +
                    ", DirectionBeforeNor: (" + String.format("%.2f", direction.x) + "," + String.format("%.2f", direction.y) + ")");
            }

            if (direction.len2() > 0) { // Only normalize and move if not already at target (or very close)
                direction.nor(); // Normalize to get unit direction vector

                float moveX = direction.x * enemyData.getSpeed() * delta;
                float moveY = direction.y * enemyData.getSpeed() * delta;

                x += moveX;
                y += moveY;

                if (enemyData.getName().equals("TentacleMonster") || enemyData.getName().equals("EyeBat")) {
                    Gdx.app.log("EnemyMoveDebug_" + enemyData.getName(), "NormalizedDir: (" + String.format("%.2f", direction.x) + "," + String.format("%.2f", direction.y) + ")" +
                        ", MoveOffset: (" + String.format("%.4f", moveX) + "," + String.format("%.4f", moveY) + ")" +
                        ", NewPos: (" + String.format("%.2f", x) + "," + String.format("%.2f", y) + ")");
                    if(x == oldX && y == oldY && (Math.abs(moveX) > 0.0001f || Math.abs(moveY) > 0.0001f)) {
                        Gdx.app.error("EnemyMoveDebug_" + enemyData.getName(), "Movement applied but position did not change!");
                    }
                }
            } else {
                if (enemyData.getName().equals("TentacleMonster") || enemyData.getName().equals("EyeBat")) {
                    Gdx.app.log("EnemyMoveDebug_" + enemyData.getName(), "Enemy is at target or direction vector is zero. No movement.");
                }
            }
        }

        // Handle animation transitions if needed
        if (animations != null && currentAnimationName != null) {
            if (currentAnimationName.equals("spawn")) {
                Animation<TextureRegion> spawnAnim = animations.get("spawn");
                if (spawnAnim != null && stateTime >= spawnAnim.getAnimationDuration()) {
                    setAnimation("show"); // Switch to show/idle after spawn
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (enemyData == null || currentAnimationName == null || currentAnimationName.equals("none")) return; // Don't draw if no data or anim

        TextureRegion currentFrame = getAnimationFrame(currentAnimationName, stateTime);
        if (currentFrame != null) {
            batch.draw(currentFrame, x, y, drawWidth, drawHeight);
        } else {
            // This error is now less likely due to constructor checks but good to keep
            // Gdx.app.error("Enemy", "Current animation frame is null for " + enemyData.getName() + " (" + currentAnimationName + ")");
        }
    }

    public boolean takeDamage(int amount) {
        if (enemyData == null) return false;
        currentHp -= amount;
        Gdx.app.log("Enemy", enemyData.getName() + " took " + amount + " damage. HP: " + currentHp);
        if (currentHp <= 0) {
            currentHp = 0; // Ensure HP doesn't go negative for checks
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public String getName() {
        return enemyData != null ? enemyData.getName() : "UnknownEnemy";
    }

    public EnemyData getEnemyData() {
        return enemyData;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, drawWidth, drawHeight);
    }

    private TextureRegion getAnimationFrame(String animName, float time) {
        if (animations == null || animName == null) return null;
        Animation<TextureRegion> anim = animations.get(animName);
        if (anim != null) {
            boolean looping = animName.equals("show") || animName.equals("idle"); // Default looping for these
            // Add other non-looping animations if needed
            if (animName.equals("spawn") || animName.equals("attack") || animName.equals("damaged") || animName.equals("death")) {
                looping = false;
            }
            return anim.getKeyFrame(time, looping);
        }
        // Gdx.app.warn("Enemy", "Animation not found: " + animName + " for " + (enemyData != null ? enemyData.getName() : "UnknownEnemy"));
        return null;
    }

    public void setAnimation(String animationName) {
        if (animations == null || !animations.containsKey(animationName)) {
            // Gdx.app.warn("Enemy", "Cannot set animation: " + animationName + ". Not found for " + (enemyData != null ? enemyData.getName() : "UnknownEnemy"));
            // Fallback to "show" or "idle" if the requested one isn't found and a default exists
            if (animations != null && animations.containsKey("show")) {
                this.currentAnimationName = "show";
            } else if (animations != null && animations.containsKey("idle")) {
                this.currentAnimationName = "idle";
            } else {
                this.currentAnimationName = "none"; // Indicates no valid animation
            }
            this.stateTime = 0f;
            return;
        }

        if (this.currentAnimationName == null || !this.currentAnimationName.equals(animationName)) {
            this.currentAnimationName = animationName;
            this.stateTime = 0f;
        }
    }

    public boolean canAttack(float delta) {
        if (enemyData == null || enemyData.getDamage() <= 0) return false; // Only attack if damage > 0

        // Use getDamage_rate() which should be loaded if JSON key is damage_rate
        if (attackTimer >= enemyData.getDamage_rate()) {
            attackTimer = 0f;
            return true;
        }
        return false;
    }

    public boolean canShoot(float delta) {
        if (enemyData == null || !enemyData.getName().equals("EyeBat") || enemyData.getDamage_rate() <= 0) {
            return false;
        }
        if (shootTimer >= enemyData.getDamage_rate()) {
            shootTimer = 0f;
            return true;
        }
        return false;
    }
}
