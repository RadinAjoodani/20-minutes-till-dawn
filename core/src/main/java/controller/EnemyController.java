package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import model.Enemy;
import model.EnemyData;
import model.GameAssetManager;
import model.Player;
import model.Bullet;
import model.Seed;

public class EnemyController {
    private Array<Enemy> activeEnemies;
    private GameAssetManager assetManager;
    private Player player;

    private float generalSpawnTimer;
    private float generalSpawnInterval = 3.0f;

    private float tentacleMonsterSpawnTimer;
    private float tentacleMonsterSpawnInterval = 3.0f;
    private final String TENTACLE_MONSTER_NAME = "TentacleMonster";

    private float eyeBatSpawnTimer;
    private float eyeBatSpawnInterval = 10.0f;
    private final String EYE_BAT_NAME = "EyeBat";
    private float gameTotalDurationSeconds;
    private float eyeBatSpawnStartTime;

    private float treeSpawnTimer;
    private float treeSpawnInterval = 20.0f;
    private int maxTreesOnMap = 3;
    private final String TREE_ENEMY_NAME = "Tree";
    private int initialTreeCount = 2;

    // --- Boss Specific Fields ---
    private final String BOSS_ENEMY_NAME = "Boss";
    private boolean bossHasSpawned = false;
    private float bossSpawnTimeThreshold; // Half of game duration
    private float bossDashAbilityTimer = 0f;
    private static final float BOSS_DASH_COOLDOWN = 5.0f; // Boss dashes every 5 seconds

    private Array<Bullet> enemyBullets;
    private TextureRegion enemyBulletTextureRegion;
    private static final float ENEMY_BULLET_DRAW_WIDTH = 15f;
    private static final float ENEMY_BULLET_DRAW_HEIGHT = 15f;

    private Array<Seed> droppedSeeds;
    private TextureRegion seedTextureRegion;

    private int enemiesKilled = 0;
    private float stateTimeForPlayerBounds = 0f; // Separate state time for player bounds in this controller context

    public EnemyController(GameAssetManager assetManager, Player player, float gameTotalDurationMinutes) {
        this.assetManager = assetManager;
        this.player = player;
        this.activeEnemies = new Array<>();
        this.generalSpawnTimer = 0f;
        this.tentacleMonsterSpawnTimer = 0f;
        this.eyeBatSpawnTimer = 0f;
        this.treeSpawnTimer = 0f;
        this.enemyBullets = new Array<>();
        this.droppedSeeds = new Array<>();

        this.gameTotalDurationSeconds = gameTotalDurationMinutes * 60f;
        if (this.gameTotalDurationSeconds <= 0) {
            this.gameTotalDurationSeconds = 1.0f;
            Gdx.app.error("EnemyController", "Game total duration is zero or negative, defaulting to 1s.");
        }
        this.eyeBatSpawnStartTime = this.gameTotalDurationSeconds / 4f;
        this.bossSpawnTimeThreshold = this.gameTotalDurationSeconds / 2f; // Boss spawns at half game time

        Texture bulletTexture = assetManager.getTexture(assetManager.BULLET_TEXTURE_PATH);
        if (bulletTexture != null) enemyBulletTextureRegion = new TextureRegion(bulletTexture);
        else {
            Gdx.app.error("EnemyController", "Failed to load enemy bullet texture.");
            enemyBulletTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
        }

        Texture seedTexture = assetManager.getTexture(assetManager.ENEMY_SEED_TEXTURE_PATH);
        if (seedTexture != null) seedTextureRegion = new TextureRegion(seedTexture);
        else {
            Gdx.app.error("EnemyController", "Failed to load seed texture.");
            seedTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
        }

        Gdx.app.log("EnemyController", "Spawning initial Trees...");
        for (int i = 0; i < initialTreeCount; i++) {
            if (countSpecificEnemy(TREE_ENEMY_NAME) < maxTreesOnMap) spawnSpecificEnemyAtRandomPosition(TREE_ENEMY_NAME);
            else break;
        }
    }

    public void update(float delta, float gameElapsedTimeSeconds) {
        stateTimeForPlayerBounds += delta;

        // --- Boss Spawning Logic ---
        if (!bossHasSpawned && gameElapsedTimeSeconds >= bossSpawnTimeThreshold) {
            Gdx.app.log("EnemyController", "Half game time reached. Attempting to spawn Boss near player.");
            spawnSpecificEnemyAtRandomPosition(BOSS_ENEMY_NAME); // Changed to spawn near player
            bossHasSpawned = true;
        }

        // --- Boss Dashing Logic ---
        if (bossHasSpawned) {
            bossDashAbilityTimer += delta;
            if (bossDashAbilityTimer >= BOSS_DASH_COOLDOWN) {
                for (Enemy enemy : activeEnemies) {
                    if (enemy.getName().equals(BOSS_ENEMY_NAME) && enemy.isAlive() && !enemy.isDashing() && !enemy.isDying()) {
                        Gdx.app.log("EnemyController", "Boss attempting dash.");
                        enemy.startDash(player.getX(), player.getY()); // Dash towards current player position
                        break;
                    }
                }
                bossDashAbilityTimer = 0f;
            }
        }

        // Tentacle Monster Spawning Logic
        tentacleMonsterSpawnTimer += delta;
        if (tentacleMonsterSpawnTimer >= tentacleMonsterSpawnInterval) {
            int numToSpawn = (int) (gameElapsedTimeSeconds / 30.0f);
            numToSpawn = Math.max(1, numToSpawn);
            if (numToSpawn > 0) {
                for (int i = 0; i < numToSpawn; i++) spawnSpecificEnemy(TENTACLE_MONSTER_NAME); // Spawns at edge
            }
            tentacleMonsterSpawnTimer = 0f;
        }

        // Eye Bat Spawning Logic
        eyeBatSpawnTimer += delta;
        if (gameElapsedTimeSeconds >= eyeBatSpawnStartTime && eyeBatSpawnTimer >= eyeBatSpawnInterval) {
            float formulaResult = (4 * gameElapsedTimeSeconds - gameTotalDurationSeconds + 30) / 30.0f;
            int numToSpawn = Math.max(0, MathUtils.floor(formulaResult));
            numToSpawn = Math.max(1, numToSpawn);
            if (numToSpawn > 0) {
                for (int i = 0; i < numToSpawn; i++) spawnSpecificEnemy(EYE_BAT_NAME); // Spawns at edge
            }
            eyeBatSpawnTimer = 0f;
        }

        // Tree Dynamic Spawning Logic
        treeSpawnTimer += delta;
        int currentTrees = countSpecificEnemy(TREE_ENEMY_NAME);
        if (currentTrees < maxTreesOnMap) {
            float dynamicTreeSpawnInterval = Math.max(5.0f, treeSpawnInterval * (1.0f - (gameElapsedTimeSeconds / gameTotalDurationSeconds * 0.5f)));
            if (treeSpawnTimer >= dynamicTreeSpawnInterval) {
                spawnSpecificEnemyAtRandomPosition(TREE_ENEMY_NAME);
                treeSpawnTimer = 0f;
            }
        }

        // Update existing enemies
        for (int i = activeEnemies.size - 1; i >= 0; i--) {
            Enemy enemy = activeEnemies.get(i);
            float playerWorldX = player.getX();
            float playerWorldY = player.getY();
            float playerTargetX = playerWorldX;
            float playerTargetY = playerWorldY;

            // Player target calculation (already good from previous logic)
            // Animation<TextureRegion> playerAnim = player.getIdleAnimation();
            // if (playerAnim != null) {
            //     TextureRegion playerFrame = playerAnim.getKeyFrame(0);
            //     if (playerFrame != null) {
            //         playerTargetX = playerWorldX;
            //         playerTargetY = playerWorldY;
            //     }
            // }

            enemy.update(delta, playerTargetX, playerTargetY); // Update first, including dying animation timer

            // Check if enemy is completely dead (animation finished)
            if (enemy.isDying() && enemy.isDeathAnimationFinished()) {
                Gdx.app.log("EnemyController", enemy.getName() + " death animation finished. Removing.");
                if (seedTextureRegion != null) {
                    droppedSeeds.add(new Seed(enemy.getX() + enemy.getBounds().width / 2, // Use current bounds for seed drop
                        enemy.getY() + enemy.getBounds().height / 2,
                        seedTextureRegion, enemy.getName().equals(TREE_ENEMY_NAME) || enemy.getName().equals(BOSS_ENEMY_NAME) ? 10 : 3));
                }
                activeEnemies.removeIndex(i);
                enemiesKilled++;
                continue; // Skip further processing for this removed enemy
            }

            // If not dying or death animation not finished, do other checks
            if (player.isAlive() && !enemy.isDying() && // Don't attack if enemy is dying
                enemy.getEnemyData() != null && enemy.getEnemyData().getDamage() > 0 &&
                enemy.getBounds().overlaps(player.getBounds(stateTimeForPlayerBounds))) {
                if (enemy.canAttack(delta)) {
                    player.takeDamage(enemy.getEnemyData().getDamage()); // Player takeDamage triggers its own animation
                }
            }

            if (enemy.getName().equals(EYE_BAT_NAME) && enemy.isAlive() && !enemy.isDying()) { // Check not dying
                if (enemy.canShoot(delta)) shootEnemyBullet(enemy, playerTargetX, playerTargetY);
            }
        }

        // Update enemy bullets
        for (int i = enemyBullets.size - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            bullet.update(delta);
            if (player.isAlive() && !player.isTakingDamage() && // Player shouldn't take damage if already in hit animation (optional invincibility frames)
                bullet.getBounds().overlaps(player.getBounds(stateTimeForPlayerBounds))) {
                player.takeDamage(bullet.getDamage());
                enemyBullets.removeIndex(i);
            } else if (bullet.getX() < -bullet.getWidth() - 200 || bullet.getX() > Gdx.graphics.getWidth() + 200 ||
                bullet.getY() < -bullet.getHeight() - 200 || bullet.getY() > Gdx.graphics.getHeight() + 200) {
                enemyBullets.removeIndex(i);
            }
        }
        // Check for player collecting seeds
        if (player.isAlive()) {
            Rectangle playerBounds = player.getBounds(stateTimeForPlayerBounds);
            for (int i = droppedSeeds.size - 1; i >= 0; i--) {
                Seed seed = droppedSeeds.get(i);
                if (playerBounds.overlaps(seed.getBounds())) {
                    player.addXp(seed.getXpValue());
                    Gdx.app.log("EnemyController", "Player collected a seed, gained " + seed.getXpValue() + " XP!");
                    droppedSeeds.removeIndex(i);
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Enemy enemy : activeEnemies) enemy.draw(batch);
        for (Bullet bullet : enemyBullets) bullet.draw(batch);
        for (Seed seed : droppedSeeds) seed.draw(batch);
    }

    private void spawnSpecificEnemy(String enemyName) {
        EnemyData enemyData = assetManager.getEnemyDataByName(enemyName);
        if (enemyData == null) {
            Gdx.app.error("EnemyController", "EdgeSpawn: Enemy data not found for: " + enemyName + ". Cannot spawn.");
            return;
        }
        // For Boss, spawnSpecificEnemyAtRandomPosition is now called directly by cheat/timer
        if (enemyName.equals(BOSS_ENEMY_NAME)) {
            Gdx.app.debug("EnemyController", "spawnSpecificEnemy called for Boss, using near player logic via spawnEnemy(data, false).");
            spawnEnemy(enemyData, false); // Spawn Boss near player
        } else {
            spawnEnemy(enemyData, true);
        }
    }

    private void spawnSpecificEnemyAtRandomPosition(String enemyName) {
        EnemyData enemyData = assetManager.getEnemyDataByName(enemyName);
        if (enemyData == null) {
            Gdx.app.error("EnemyController", "RandomMapSpawn: Enemy data not found for: " + enemyName + ". Cannot spawn.");
            return;
        }
        spawnEnemy(enemyData, false);
    }

    private void spawnEnemy(EnemyData enemyDataToSpawn, boolean spawnAtEdge) {
        Gdx.app.debug("EnemyController", "spawnEnemy called for: " + enemyDataToSpawn.getName() + ", spawnAtEdge: " + spawnAtEdge);

        ObjectMap<String, Animation<TextureRegion>> enemyAnimations = loadAllAnimationsForEnemy(enemyDataToSpawn);
        if (enemyAnimations.isEmpty() && enemyDataToSpawn.getAnimations() != null && !enemyDataToSpawn.getAnimations().isEmpty()) {
            Gdx.app.error("EnemyController", "CRITICAL: Failed to load ANY animations for " + enemyDataToSpawn.getName() + ". Check paths/loading.");
            return;
        }

        float spawnX, spawnY;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float placementWidth;
        float placementHeight;

        if (enemyDataToSpawn.getName().equals(BOSS_ENEMY_NAME)) {
            placementWidth = 160f;
            placementHeight = 160f;
        } else {
            TextureRegion firstFrame = null;
            if (enemyAnimations.containsKey("show")) firstFrame = enemyAnimations.get("show").getKeyFrame(0);
            else if (enemyAnimations.containsKey("idle")) firstFrame = enemyAnimations.get("idle").getKeyFrame(0);
            else if (!enemyAnimations.isEmpty()) firstFrame = enemyAnimations.values().next().getKeyFrame(0);
            placementWidth = (firstFrame != null) ? firstFrame.getRegionWidth() * Enemy.ENEMY_SCALE_FACTOR : 50 * Enemy.ENEMY_SCALE_FACTOR;
            placementHeight = (firstFrame != null) ? firstFrame.getRegionHeight() * Enemy.ENEMY_SCALE_FACTOR : 50 * Enemy.ENEMY_SCALE_FACTOR;
        }

        float padding = 20f;
        float minSpawnDistFromPlayer = 150f;

        if (spawnAtEdge) {
            float cameraX = player.getX();
            float cameraY = player.getY();
            int edge = MathUtils.random(0, 3);
            switch (edge) {
                case 0: spawnX = MathUtils.random(cameraX - screenWidth/2f, cameraX + screenWidth/2f); spawnY = cameraY + screenHeight/2f + placementHeight; break;
                case 1: spawnX = MathUtils.random(cameraX - screenWidth/2f, cameraX + screenWidth/2f); spawnY = cameraY - screenHeight/2f - placementHeight; break;
                case 2: spawnX = cameraX - screenWidth/2f - placementWidth; spawnY = MathUtils.random(cameraY - screenHeight/2f, cameraY + screenHeight/2f); break;
                case 3: spawnX = cameraX + screenWidth/2f + placementWidth; spawnY = MathUtils.random(cameraY - screenHeight/2f, cameraY + screenHeight/2f); break;
                default: spawnX = cameraX + screenWidth/2f + placementWidth; spawnY = cameraY; break;
            }
        } else {
            float angle = MathUtils.random(0, 360f);
            float distance = minSpawnDistFromPlayer + MathUtils.random(screenWidth * (enemyDataToSpawn.getName().equals(BOSS_ENEMY_NAME) ? 0.25f : 0.1f), screenWidth * (enemyDataToSpawn.getName().equals(BOSS_ENEMY_NAME) ? 0.4f : 0.3f));

            spawnX = player.getX() + MathUtils.cosDeg(angle) * distance;
            spawnY = player.getY() + MathUtils.sinDeg(angle) * distance;

            float cameraX = player.getX();
            float cameraY = player.getY();
            // Clamp to be somewhat within the current camera's wider view to prevent extreme off-screen spawns for "near player"
            float maxSpawnRangeX = screenWidth; // Can spawn up to one screen width away
            float maxSpawnRangeY = screenHeight; // Can spawn up to one screen height away

            spawnX = MathUtils.clamp(spawnX, cameraX - maxSpawnRangeX, cameraX + maxSpawnRangeX);
            spawnY = MathUtils.clamp(spawnY, cameraY - maxSpawnRangeY, cameraY + maxSpawnRangeY);
        }

        Enemy newEnemy = new Enemy(enemyDataToSpawn, enemyAnimations, spawnX, spawnY);
        activeEnemies.add(newEnemy);
        Gdx.app.log("EnemyController", "Spawned " + newEnemy.getName() + " at (" + String.format("%.0f",spawnX) + ", " + String.format("%.0f",spawnY) + "). Type: " + (spawnAtEdge ? "Edge" : "NearPlayer") + ". Active " + newEnemy.getName() + "s: " + countSpecificEnemy(newEnemy.getName()));
    }

    private int countSpecificEnemy(String enemyName) {
        int count = 0;
        for (Enemy enemy : activeEnemies) if (enemy.getName().equals(enemyName)) count++;
        return count;
    }

    private ObjectMap<String, Animation<TextureRegion>> loadAllAnimationsForEnemy(EnemyData enemyData) {
        ObjectMap<String, Animation<TextureRegion>> loadedAnimations = new ObjectMap<>();
        ObjectMap<String, Array<String>> animationPaths = enemyData.getAnimations();
        if (animationPaths == null || animationPaths.isEmpty()) {
            Gdx.app.log("EnemyController", "No animation paths defined for: " + enemyData.getName());
            return loadedAnimations;
        }
        for (ObjectMap.Entry<String, Array<String>> entry : animationPaths.entries()) {
            String animationName = entry.key;
            Array<String> paths = entry.value;
            if (paths != null && paths.size > 0) {
                Array<TextureRegion> frames = new Array<>();
                for (String path : paths) {
                    Texture texture = assetManager.getTexture(path);
                    if (texture != null) frames.add(new TextureRegion(texture));
                    else Gdx.app.error("EnemyController", "TEXTURE NOT FOUND for enemy ("+enemyData.getName()+") anim '"+animationName+"' frame: [" + path + "]");
                }
                if (frames.size > 0) {
                    Animation.PlayMode playMode = Animation.PlayMode.LOOP;
                    float frameDuration = 0.1f;
                    if (animationName.equalsIgnoreCase("spawn") || animationName.equalsIgnoreCase("damaged") || animationName.equalsIgnoreCase("death") || animationName.equalsIgnoreCase("dash")) {
                        playMode = Animation.PlayMode.NORMAL;
                        frameDuration = animationName.equalsIgnoreCase("spawn") ? 0.15f : (animationName.equalsIgnoreCase("dash") ? 0.05f : 0.1f) ;
                    }
                    loadedAnimations.put(animationName, new Animation<>(frameDuration, frames, playMode));
                } else Gdx.app.error("EnemyController", "No frames loaded for anim '"+animationName+"' for enemy: " + enemyData.getName());
            }
        }
        if (loadedAnimations.isEmpty() && !animationPaths.isEmpty()) Gdx.app.error("EnemyController", "CRITICAL: No animations loaded for " + enemyData.getName() + " despite paths in JSON.");
        return loadedAnimations;
    }

    private void shootEnemyBullet(Enemy shooter, float targetX, float targetY) {
        Vector2 enemyCenter = new Vector2(shooter.getX() + shooter.getBounds().width / 2, shooter.getY() + shooter.getBounds().height / 2);
        Vector2 directionToPlayer = new Vector2(targetX - enemyCenter.x, targetY - enemyCenter.y).nor();
        float offsetDistance = shooter.getBounds().width / 2 + 10;
        float bulletStartX = enemyCenter.x + directionToPlayer.x * offsetDistance - ENEMY_BULLET_DRAW_WIDTH / 2;
        float bulletStartY = enemyCenter.y + directionToPlayer.y * offsetDistance - ENEMY_BULLET_DRAW_HEIGHT / 2;
        int bulletSpeed = 300;
        int bulletDamage = shooter.getEnemyData().getDamage();
        if (enemyBulletTextureRegion == null) {Gdx.app.error("EnemyController", "Cannot shoot, enemyBulletTextureRegion is null"); return;}
        enemyBullets.add(new Bullet(bulletStartX, bulletStartY, bulletSpeed, directionToPlayer.x, directionToPlayer.y, enemyBulletTextureRegion, ENEMY_BULLET_DRAW_WIDTH, ENEMY_BULLET_DRAW_HEIGHT, bulletDamage));
    }

    public void checkBulletCollisions(Array<Bullet> bullets) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet playerBullet = bullets.get(i);
            Rectangle bulletBounds = playerBullet.getBounds();
            for (int j = activeEnemies.size - 1; j >= 0; j--) {
                Enemy enemy = activeEnemies.get(j);
                if (enemy.isAlive() && !enemy.isDying() && bulletBounds.overlaps(enemy.getBounds())) {
                    enemy.takeDamage(playerBullet.getDamage());
                    bullets.removeIndex(i);
                    break;
                }
            }
        }
    }

    public void spawnBossNow() {
        if (!bossHasSpawned) {
            Gdx.app.log("CHEAT", "Spawning Boss via cheat code.");
            spawnSpecificEnemyAtRandomPosition(BOSS_ENEMY_NAME);
            bossHasSpawned = true;
        } else {
            Gdx.app.log("CHEAT", "Boss already spawned. Spawning another one via cheat.");
            spawnSpecificEnemyAtRandomPosition(BOSS_ENEMY_NAME);
        }
    }

    public void killRandomEnemies(int count) {
        int killedCount = 0;
        for (int i = activeEnemies.size - 1; i >= 0 && killedCount < count; i--) {
            Enemy enemy = activeEnemies.get(i);
            if (enemy.isAlive() && !enemy.isDying() && !enemy.getName().equals(BOSS_ENEMY_NAME)) {
                Gdx.app.log("CHEAT", "Killing enemy: " + enemy.getName());
                enemy.takeDamage(enemy.getEnemyData() != null ? enemy.getEnemyData().getHp() * 2 : 10000);
                killedCount++;
            }
        }
        Gdx.app.log("CHEAT", "Killed " + killedCount + " non-Boss enemies.");
    }

    public Array<Enemy> getActiveEnemies() { return activeEnemies; }
    public Array<Bullet> getEnemyBullets() { return enemyBullets; }
    public Array<Seed> getDroppedSeeds() { return droppedSeeds; }
    public int getEnemiesKilled() { return enemiesKilled; }
}
