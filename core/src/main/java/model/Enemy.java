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
        }
        return null;
    }

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
        }
        return false;
    }

    // NEW: Check if enemy can shoot (ranged) based on damage_rate
    public boolean canShoot(float delta) {
        // Only specific enemies shoot (e.g., EyeBat)
        if (enemyData.getName().equals("EyeBat") && enemyData.getDamage_rate() > 0 && shootTimer >= enemyData.getDamage_rate()) {
            shootTimer = 0f;
            return true;
        }
        return false;
    }
}
