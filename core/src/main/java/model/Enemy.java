package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

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
    private Vector2 dashDirection = null; // Added this field back
    private static final float BOSS_DRAW_WIDTH = 160f;
    private static final float BOSS_DRAW_HEIGHT = 160f;

    private boolean isDying = false;
    private float deathAnimationStateTime = 0f;


    public Enemy(EnemyData enemyData, ObjectMap<String, Animation<TextureRegion>> animations, float startX, float startY) {
        this.enemyData = enemyData;
        this.animations = animations;
        this.x = startX;
        this.y = startY;

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
        this.dashDirection = new Vector2(targetPlayerX - x, targetPlayerY - y).nor(); // Ensure dashDirection is initialized
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
        // If not dying but HP is <= 0 (e.g. no death animation), it's considered "finished" in terms of being dead.
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
        }
        return null;
    }

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
        }
        return false;
    }

    public boolean canShoot(float delta) {
        if (enemyData == null || !enemyData.getName().equals("EyeBat") || enemyData.getDamage_rate() <= 0 || isDashing || isDying) return false;
        if (shootTimer >= enemyData.getDamage_rate()) {
            shootTimer = 0f; return true;
        }
        return false;
    }
}
