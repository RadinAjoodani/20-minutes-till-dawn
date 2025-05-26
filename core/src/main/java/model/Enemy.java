package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

<<<<<<< HEAD
public class Enemy {
    private EnemyData enemyData;
    private float x, y;
    private float currentHp;
    private ObjectMap<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> deathAnimation;

    private float stateTime;
    private String currentAnimationName;
    private float drawWidth, drawHeight;
    public static final float ENEMY_SCALE_FACTOR = 1.5f;

    private float attackTimer;
    private float shootTimer;

    private boolean isDashing = false;
    private float dashTimer = 0f;
    private static final float DASH_DURATION = 3.0f;
    private static final float DASH_SPEED_MULTIPLIER = 8f;
    private Vector2 dashTargetPosition = null;
    private Vector2 dashDirection = null;
    private static final float BOSS_DRAW_WIDTH = 160f;
    private static final float BOSS_DRAW_HEIGHT = 160f;

    private boolean isDying = false;
    private float deathAnimationStateTime = 0f;
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86


    public Enemy(EnemyData enemyData, ObjectMap<String, Animation<TextureRegion>> animations, float startX, float startY) {
        this.enemyData = enemyData;
        this.animations = animations;
        this.x = startX;
        this.y = startY;
<<<<<<< HEAD

        if (GameAssetManager.getGameAssetManager() != null) {
            this.deathAnimation = GameAssetManager.getGameAssetManager().getEnemyDeathAnimation("damage");
            if (this.deathAnimation == null) {
                Gdx.app.log("Enemy", "Generic Enemy 'death' (explosion) animation not found for " + (enemyData != null ? enemyData.getName() : "Unknown") + ".");
            }
        } else {
            Gdx.app.error("Enemy", "GameAssetManager is null, cannot load death animation.");
        }

        if (this.enemyData != null) {
            this.currentHp = enemyData.getHp();
            this.currentAnimationName = "show";

            if (enemyData.getName().equals("Boss")) {
                this.drawWidth = BOSS_DRAW_WIDTH;
                this.drawHeight = BOSS_DRAW_HEIGHT;
            } else {
                TextureRegion firstFrame = getAnimationFrameForSetup(currentAnimationName);
                if (firstFrame != null) {
                    this.drawWidth = firstFrame.getRegionWidth() * ENEMY_SCALE_FACTOR;
                    this.drawHeight = firstFrame.getRegionHeight() * ENEMY_SCALE_FACTOR;
                } else {
                    firstFrame = getAnimationFrameForSetup("idle");
                    if (firstFrame != null) {
                        this.currentAnimationName = "idle";
                        this.drawWidth = firstFrame.getRegionWidth() * ENEMY_SCALE_FACTOR;
                        this.drawHeight = firstFrame.getRegionHeight() * ENEMY_SCALE_FACTOR;
                    } else {
                        Gdx.app.error("Enemy", "No 'show' or 'idle' animation for " + enemyData.getName() + ". Using default size.");
                        this.drawWidth = 50 * ENEMY_SCALE_FACTOR;
                        this.drawHeight = 50 * ENEMY_SCALE_FACTOR;
                    }
                }
            }
        } else {
            Gdx.app.error("Enemy", "CRITICAL: EnemyData is null in Enemy constructor.");
            this.currentHp = 0; this.drawWidth = 50; this.drawHeight = 50; this.currentAnimationName = "none";
        }
        this.stateTime = 0f;
        this.attackTimer = 0f;
        this.shootTimer = 0f;
    }

    private TextureRegion getAnimationFrameForSetup(String animName) {
        if (animations == null || animName == null) return null;
        Animation<TextureRegion> anim = animations.get(animName);
        if (anim != null && anim.getKeyFrames() != null && anim.getKeyFrames().length > 0) {
            return anim.getKeyFrame(0);
        }
        return null;
    }


    public void update(float delta, float targetX, float targetY) {
        if (enemyData == null) return;
        if (isDying) {
            deathAnimationStateTime += delta;
            return;
        }

        stateTime += delta;
        attackTimer += delta;
        shootTimer += delta;

        if (isDashing) {
            dashTimer += delta;
            if (dashTimer < DASH_DURATION && dashDirection != null) {
                float actualDashSpeed = enemyData.getSpeed() * DASH_SPEED_MULTIPLIER;
                x += dashDirection.x * actualDashSpeed * delta;
                y += dashDirection.y * actualDashSpeed * delta;
            } else {
                isDashing = false;
                dashTimer = 0f;
                dashTargetPosition = null;
                dashDirection = null;
                setAnimation("show");
                Gdx.app.log(getName(), "Dash ended.");
            }
        } else {
            if (enemyData.getSpeed() > 0 && !(enemyData.getName().equals("Tree"))) {
                Vector2 enemyPos = new Vector2(x, y);
                Vector2 targetPos = new Vector2(targetX, targetY);
                Vector2 direction = targetPos.sub(enemyPos);
                if (direction.len2() > 0.001f) {
                    direction.nor();
                    x += direction.x * enemyData.getSpeed() * delta;
                    y += direction.y * enemyData.getSpeed() * delta;
                }
            }
        }

        if (animations != null && currentAnimationName != null) {
            if (currentAnimationName.equals("spawn")) {
                Animation<TextureRegion> spawnAnim = animations.get("spawn");
                if (spawnAnim != null && stateTime >= spawnAnim.getAnimationDuration() && !isDashing) {
                    setAnimation("show");
                }
            }
        }
    }

    public void startDash(float targetPlayerX, float targetPlayerY) {
        if (enemyData == null || !enemyData.getName().equals("Boss") || isDashing || isDying) return;
        isDashing = true;
        dashTimer = 0f;
        this.dashTargetPosition = new Vector2(targetPlayerX, targetPlayerY);
        this.dashDirection = new Vector2(targetPlayerX - x, targetPlayerY - y).nor();
        Gdx.app.log(getName(), "Started dash towards (" + targetPlayerX + ", " + targetPlayerY + ") with direction (" + dashDirection.x + ", " + dashDirection.y + ")");
    }

    public boolean isDashing() { return isDashing; }
    public boolean isDying() { return isDying; }

    public void draw(SpriteBatch batch) {
        if (enemyData == null) return;

        TextureRegion currentFrame = null;
        if (isDying && deathAnimation != null) {
            currentFrame = deathAnimation.getKeyFrame(deathAnimationStateTime, false);
        } else if (currentAnimationName != null && !currentAnimationName.equals("none")) {
            currentFrame = getAnimationFrame(currentAnimationName, isDashing ? dashTimer : stateTime);
        }

        if (currentFrame != null) {
            batch.draw(currentFrame, x, y, drawWidth, drawHeight);
        }
    }

    public boolean takeDamage(int amount) {
        if (enemyData == null || isDying || currentHp <= 0) return true;
        currentHp -= amount;
        Gdx.app.log("Enemy", enemyData.getName() + " took " + amount + " damage. HP: " + currentHp);
        if (currentHp <= 0) {
            currentHp = 0;
            if (deathAnimation != null) {
                isDying = true;
                deathAnimationStateTime = 0f;
                Gdx.app.log("Enemy", enemyData.getName() + " is now dying.");
            } else {
                Gdx.app.log("Enemy", enemyData.getName() + " died (no death animation).");
            }
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        if (isDying && deathAnimation != null) {
            return !deathAnimation.isAnimationFinished(deathAnimationStateTime);
        }
        return currentHp > 0;
    }

    public boolean isDeathAnimationFinished() {
        if (isDying) {
            if (deathAnimation != null) {
                return deathAnimation.isAnimationFinished(deathAnimationStateTime);
            }
            return true;
        }

        return !isDying && currentHp <= 0;
    }


    public String getName() { return enemyData != null ? enemyData.getName() : "UnknownEnemy"; }
    public EnemyData getEnemyData() { return enemyData; }
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getBounds() {
        if (isDying) return new Rectangle(-1000, -1000, 0, 0);
        return new Rectangle(x, y, drawWidth, drawHeight);
    }

    private TextureRegion getAnimationFrame(String animName, float time) {
        if (animations == null || animName == null) return null;
        Animation<TextureRegion> anim = animations.get(animName);
        if (anim != null) {
            boolean looping = animName.equals("show") || animName.equals("idle");
            if (animName.equals("spawn") || animName.equals("attack") || animName.equals("damaged") || animName.equals("death") || animName.equals("dash")) {
                looping = false;
            }
            return anim.getKeyFrame(time, looping);
=======
        this.currentHp = enemyData.getHp();
        this.stateTime = 0f;
        this.currentAnimationName = "show"; // Default animation
        this.attackTimer = 0f; // Initialize attack timer
        this.shootTimer = 0f; // Initialize shoot timer

        // Calculate initial draw dimensions based on the first frame of the default animation
        TextureRegion firstFrame = getAnimationFrame(currentAnimationName, 0f);
        if (firstFrame != null) {
            this.drawWidth = firstFrame.getRegionWidth() * ENEMY_SCALE_FACTOR;
            this.drawHeight = firstFrame.getRegionHeight() * ENEMY_SCALE_FACTOR;
        } else {
            Gdx.app.error("Enemy", "No 'show' animation found for " + enemyData.getName() + ". Using default size.");
            this.drawWidth = 50 * ENEMY_SCALE_FACTOR; // Fallback size
            this.drawHeight = 50 * ENEMY_SCALE_FACTOR;
        }
    }

    /**
     * Updates the enemy's state, including movement and animation.
     * @param delta Time since last frame.
     * @param targetX X-coordinate of the target (e.g., player).
     * @param targetY Y-coordinate of the target (e.g., player).
     */
    public void update(float delta, float targetX, float targetY) {
        stateTime += delta;
        attackTimer += delta; // Update attack timer
        shootTimer += delta; // Update shoot timer

        // Simple movement towards the target if speed > 0
        if (enemyData.getSpeed() > 0) { // Only move if speed is greater than 0
            Vector2 enemyPos = new Vector2(x, y);
            Vector2 targetPos = new Vector2(targetX, targetY);
            Vector2 direction = targetPos.sub(enemyPos).nor(); // Normalize to get unit direction vector

            x += direction.x * enemyData.getSpeed() * delta;
            y += direction.y * enemyData.getSpeed() * delta;
        }

        // Handle animation transitions if needed (e.g., after spawn animation finishes)
        if (currentAnimationName.equals("spawn")) {
            Animation<TextureRegion> spawnAnim = animations.get("spawn");
            if (spawnAnim != null && stateTime >= spawnAnim.getAnimationDuration()) {
                currentAnimationName = "show"; // Switch to show/idle after spawn
                stateTime = 0f; // Reset state time for new animation
            }
        }
        // If you add a "damaged" animation for trees, you'd manage its transition here:
        // if (currentAnimationName.equals("damaged") && animations.get("damaged").isAnimationFinished(stateTime)) {
        //     currentAnimationName = "show"; // Return to show after damage animation
        // }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = getAnimationFrame(currentAnimationName, stateTime);
        if (currentFrame != null) {
            batch.draw(currentFrame, x, y, drawWidth, drawHeight);
        } else {
            Gdx.app.error("Enemy", "Current animation frame is null for " + enemyData.getName() + " (" + currentAnimationName + ")");
        }
    }

    /**
     * Applies damage to the enemy.
     * @param amount The amount of damage to take.
     * @return True if the enemy's HP drops to 0 or below, false otherwise.
     */
    public boolean takeDamage(int amount) {
        currentHp -= amount;
        Gdx.app.log("Enemy", enemyData.getName() + " took " + amount + " damage. HP: " + currentHp);
        if (currentHp <= 0) {
            return true; // Enemy is defeated
        } else {
            // Optional: If you have a 'damaged' animation, activate it here
            // if (animations.containsKey("damaged") && !currentAnimationName.equals("damaged")) {
            //     setAnimation("damaged");
            // }
            return false;
        }
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public String getName() {
        return enemyData.getName();
    }

    public EnemyData getEnemyData() { // NEW: Getter for EnemyData
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

    /**
     * Helper to get the current animation frame.
     * @param animName The name of the animation (e.g., "show", "spawn").
     * @param time The current state time for the animation.
     * @return The TextureRegion for the current frame, or null if animation not found.
     */
    private TextureRegion getAnimationFrame(String animName, float time) {
        Animation<TextureRegion> anim = animations.get(animName);
        if (anim != null) {
            // Loop "show" animation, play "spawn" and "damaged" once
            return anim.getKeyFrame(time, (animName.equals("show") || animName.equals("idle")));
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }
        return null;
    }

<<<<<<< HEAD
    public void setAnimation(String animationName) {
        if (isDying) return;
        if (animations == null) return;
        if (!animations.containsKey(animationName)) {
            if (animations.containsKey("show")) this.currentAnimationName = "show";
            else if (animations.containsKey("idle")) this.currentAnimationName = "idle";
            else this.currentAnimationName = "none";
            this.stateTime = 0f;
            return;
        }
        if (this.currentAnimationName == null || !this.currentAnimationName.equals(animationName) ||
            (animationName.equals("show") && isDashing)) {
            if (animationName.equals("show") && isDashing) return;
            this.currentAnimationName = animationName;
            if (!isDashing || !animationName.equals("dash")) {
                this.stateTime = 0f;
            }
        }
    }

    public boolean canAttack(float delta) {
        if (enemyData == null || enemyData.getDamage() <= 0 || isDashing || isDying) return false;
        if (attackTimer >= enemyData.getDamage_rate()) {
            attackTimer = 0f; return true;
=======
    // You can add methods to change currentAnimationName, e.g., for attack animations
    public void setAnimation(String animationName) {
        if (animations.containsKey(animationName) && !this.currentAnimationName.equals(animationName)) {
            this.currentAnimationName = animationName;
            this.stateTime = 0f; // Reset animation time when changing animation
        }
    }

    // NEW: Check if enemy can attack (melee) based on damage_rate
    public boolean canAttack(float delta) {
        if (enemyData.getDamage() > 0 && attackTimer >= enemyData.getDamage_rate()) { // Only attack if damage > 0
            attackTimer = 0f;
            return true;
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }
        return false;
    }

<<<<<<< HEAD
    public boolean canShoot(float delta) {
        if (enemyData == null || !enemyData.getName().equals("EyeBat") || enemyData.getDamage_rate() <= 0 || isDashing || isDying) return false;
        if (shootTimer >= enemyData.getDamage_rate()) {
            shootTimer = 0f; return true;
=======
    // NEW: Check if enemy can shoot (ranged) based on damage_rate
    public boolean canShoot(float delta) {
        // Only specific enemies shoot (e.g., EyeBat)
        if (enemyData.getName().equals("EyeBat") && enemyData.getDamage_rate() > 0 && shootTimer >= enemyData.getDamage_rate()) {
            shootTimer = 0f;
            return true;
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }
        return false;
    }
}
