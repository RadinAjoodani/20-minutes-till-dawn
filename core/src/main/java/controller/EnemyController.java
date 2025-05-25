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

/**
 * Manages the spawning, updating, and interaction of enemies in the game.
 */
public class EnemyController {
    private Array<Enemy> activeEnemies;
    private GameAssetManager assetManager;
    private Player player; // Reference to the player for targeting

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
    private float treeSpawnInterval = 20.0f; // Base interval, dynamically adjusted
    private int maxTreesOnMap = 3; // Max active trees
    private final String TREE_ENEMY_NAME = "Tree";
    private int initialTreeCount = 2; // Number of trees to spawn at the very start

    private Array<Bullet> enemyBullets;
    private TextureRegion enemyBulletTextureRegion;
    private static final float ENEMY_BULLET_DRAW_WIDTH = 15f;
    private static final float ENEMY_BULLET_DRAW_HEIGHT = 15f;

    private Array<Seed> droppedSeeds;
    private TextureRegion seedTextureRegion;

    private int enemiesKilled = 0;
    private float stateTime = 0f;

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
        if (this.gameTotalDurationSeconds <= 0) { // Prevent division by zero if duration is 0 or less
            this.gameTotalDurationSeconds = 1.0f; // Default to 1 second to avoid math errors, though game logic might be odd
            Gdx.app.error("EnemyController", "Game total duration is zero or negative, defaulting to 1s. This might cause issues.");
        }
        this.eyeBatSpawnStartTime = this.gameTotalDurationSeconds / 4f;

        Texture bulletTexture = assetManager.getTexture(assetManager.BULLET_TEXTURE_PATH);
        if (bulletTexture != null) {
            enemyBulletTextureRegion = new TextureRegion(bulletTexture);
        } else {
            Gdx.app.error("EnemyController", "Failed to load enemy bullet texture.");
            enemyBulletTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
        }

        Texture seedTexture = assetManager.getTexture(assetManager.ENEMY_SEED_TEXTURE_PATH);
        if (seedTexture != null) {
            seedTextureRegion = new TextureRegion(seedTexture);
        } else {
            Gdx.app.error("EnemyController", "Failed to load seed texture.");
            seedTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
        }

        // Spawn initial set of Trees
        Gdx.app.log("EnemyController", "Spawning initial Trees...");
        for (int i = 0; i < initialTreeCount; i++) {
            if (countSpecificEnemy(TREE_ENEMY_NAME) < maxTreesOnMap) {
                spawnSpecificEnemyAtRandomPosition(TREE_ENEMY_NAME);
            } else {
                Gdx.app.log("EnemyController", "Max trees reached during initial spawn.");
                break;
            }
        }
    }

    public void update(float delta, float gameElapsedTimeSeconds) {
        stateTime += delta;

        // --- Tentacle Monster Spawning Logic ---
        tentacleMonsterSpawnTimer += delta;
        if (tentacleMonsterSpawnTimer >= tentacleMonsterSpawnInterval) {
            int numToSpawn = (int) (gameElapsedTimeSeconds / 30.0f);
            numToSpawn = Math.max(1, numToSpawn);
            if (numToSpawn > 0) {
                Gdx.app.debug("EnemyController", "Attempting to spawn " + numToSpawn + " TentacleMonsters. Elapsed time: " + gameElapsedTimeSeconds);
                for (int i = 0; i < numToSpawn; i++) {
                    spawnSpecificEnemy(TENTACLE_MONSTER_NAME); // Spawns at edge
                }
            }
            tentacleMonsterSpawnTimer = 0f;
        }

        // --- Eye Bat Spawning Logic ---
        eyeBatSpawnTimer += delta;
        if (gameElapsedTimeSeconds >= eyeBatSpawnStartTime && eyeBatSpawnTimer >= eyeBatSpawnInterval) {
            float formulaResult = (4 * gameElapsedTimeSeconds - gameTotalDurationSeconds + 30) / 30.0f;
            int numToSpawn = Math.max(0, MathUtils.floor(formulaResult));
            numToSpawn = Math.max(1, numToSpawn);
            if (numToSpawn > 0) {
                Gdx.app.debug("EnemyController", "Attempting to spawn " + numToSpawn + " EyeBats. Elapsed time: " + gameElapsedTimeSeconds);
                for (int i = 0; i < numToSpawn; i++) {
                    spawnSpecificEnemy(EYE_BAT_NAME); // Spawns at edge
                }
            }
            eyeBatSpawnTimer = 0f;
        }

        // --- Tree Dynamic Spawning Logic (during game) ---
        treeSpawnTimer += delta;
        int currentTrees = countSpecificEnemy(TREE_ENEMY_NAME);

        if (currentTrees < maxTreesOnMap) {
            float dynamicTreeSpawnInterval = Math.max(5.0f, treeSpawnInterval * (1.0f - (gameElapsedTimeSeconds / gameTotalDurationSeconds * 0.5f)));
            if (treeSpawnTimer >= dynamicTreeSpawnInterval) {
                Gdx.app.debug("EnemyController", "Attempting to dynamically spawn a Tree. Elapsed time: " + gameElapsedTimeSeconds + ", Dynamic Interval: " + dynamicTreeSpawnInterval + ", TreeTimer: " + treeSpawnTimer + ", Current Trees: " + currentTrees);
                spawnSpecificEnemyAtRandomPosition(TREE_ENEMY_NAME); // Spawn new trees randomly on map
                treeSpawnTimer = 0f;
            }
        }

        // Update existing enemies
        for (int i = activeEnemies.size - 1; i >= 0; i--) {
            Enemy enemy = activeEnemies.get(i);
            float playerCenterX = player.getX() + player.getIdleAnimation().getKeyFrame(0).getRegionWidth() * Player.PLAYER_SCALE_FACTOR / 2;
            float playerCenterY = player.getY() + player.getIdleAnimation().getKeyFrame(0).getRegionHeight() * Player.PLAYER_SCALE_FACTOR / 2;

            if (!enemy.getName().equals(TREE_ENEMY_NAME)) { // Trees don't move towards player
                enemy.update(delta, playerCenterX, playerCenterY);
            } else {
                enemy.update(delta, enemy.getX(), enemy.getY()); // Update animation state but not position
            }

            // Player collision and attack logic (if applicable)
            if (player.isAlive() && enemy.getEnemyData().getDamage() > 0 && // only if enemy can deal damage
                enemy.getBounds().overlaps(player.getBounds(stateTime))) {
                if (enemy.canAttack(delta)) { // Respects enemy's damage_rate
                    player.takeDamage(enemy.getEnemyData().getDamage());
                }
            }

            // Specific enemy behaviors (like EyeBat shooting)
            if (enemy.getName().equals(EYE_BAT_NAME) && enemy.isAlive()) {
                if (enemy.canShoot(delta)) {
                    shootEnemyBullet(enemy, playerCenterX, playerCenterY);
                }
            }

            if (!enemy.isAlive()) {
                Gdx.app.log("EnemyController", enemy.getName() + " defeated!");
                droppedSeeds.add(new Seed(enemy.getX() + enemy.getBounds().width / 2,
                    enemy.getY() + enemy.getBounds().height / 2,
                    seedTextureRegion, enemy.getName().equals(TREE_ENEMY_NAME) ? 5 : 3));
                activeEnemies.removeIndex(i);
                enemiesKilled++;
            }
        }

        // Update enemy bullets
        for (int i = enemyBullets.size - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            bullet.update(delta);
            if (player.isAlive() && bullet.getBounds().overlaps(player.getBounds(stateTime))) {
                player.takeDamage(bullet.getDamage());
                enemyBullets.removeIndex(i);
            } else if (bullet.getX() < -bullet.getWidth() || bullet.getX() > Gdx.graphics.getWidth() ||
                bullet.getY() < -bullet.getHeight() || bullet.getY() > Gdx.graphics.getHeight()) {
                enemyBullets.removeIndex(i);
            }
        }

        // Check for player collecting seeds
        if (player.isAlive()) {
            Rectangle playerBounds = player.getBounds(stateTime);
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
        for (Enemy enemy : activeEnemies) {
            enemy.draw(batch);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(batch);
        }
        for (Seed seed : droppedSeeds) {
            seed.draw(batch);
        }
    }

    // Spawns enemies at the screen edge (used by TentacleMonster, EyeBat)
    private void spawnSpecificEnemy(String enemyName) {
        EnemyData enemyData = assetManager.getEnemyDataByName(enemyName);
        if (enemyData == null) {
            Gdx.app.error("EnemyController", "EdgeSpawn: Enemy data not found for: " + enemyName + ". Cannot spawn.");
            return;
        }
        Gdx.app.debug("EnemyController", "EdgeSpawn: Found enemy data for: " + enemyName + ". Attempting to call spawnEnemy (edge).");
        spawnEnemy(enemyData, true); // true indicates edge spawn
    }

    // New method to spawn Trees at a random valid map position
    private void spawnSpecificEnemyAtRandomPosition(String enemyName) {
        EnemyData enemyData = assetManager.getEnemyDataByName(enemyName);
        if (enemyData == null) {
            Gdx.app.error("EnemyController", "RandomMapSpawn: Enemy data not found for: " + enemyName + ". Cannot spawn.");
            return;
        }
        Gdx.app.debug("EnemyController", "RandomMapSpawn: Found enemy data for: " + enemyName + ". Attempting to call spawnEnemy (random map pos).");
        spawnEnemy(enemyData, false); // false indicates random map position spawn
    }


    // Modified spawnEnemy to handle edge spawning vs. random map position
    private void spawnEnemy(EnemyData enemyDataToSpawn, boolean spawnAtEdge) {
        Gdx.app.debug("EnemyController", "spawnEnemy called for: " + enemyDataToSpawn.getName() + ", spawnAtEdge: " + spawnAtEdge);

        ObjectMap<String, Animation<TextureRegion>> enemyAnimations = loadAllAnimationsForEnemy(enemyDataToSpawn);
        // Basic check for animation loading success
        if (enemyAnimations.isEmpty() && enemyDataToSpawn.getAnimations() != null && !enemyDataToSpawn.getAnimations().isEmpty()) {
            Gdx.app.error("EnemyController", "CRITICAL: Failed to load ANY animations for " + enemyDataToSpawn.getName() + ". Check texture paths and GameAssetManager preloading.");
            return;
        }
        if (enemyAnimations.isEmpty() && (enemyDataToSpawn.getAnimations() == null || enemyDataToSpawn.getAnimations().isEmpty())) {
//            Gdx.app.warn("EnemyController", "No animations defined or loaded for " + enemyDataToSpawn.getName() + ". Enemy might be invisible or use default sizes.");
        }

        float spawnX, spawnY;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        TextureRegion firstFrame = null;
        if (enemyAnimations.containsKey("show")) firstFrame = enemyAnimations.get("show").getKeyFrame(0);
        else if (enemyAnimations.containsKey("idle")) firstFrame = enemyAnimations.get("idle").getKeyFrame(0);
        else if (!enemyAnimations.isEmpty()) firstFrame = enemyAnimations.values().next().getKeyFrame(0);

        float enemyWidth = (firstFrame != null) ? firstFrame.getRegionWidth() * Enemy.ENEMY_SCALE_FACTOR : 50 * Enemy.ENEMY_SCALE_FACTOR;
        float enemyHeight = (firstFrame != null) ? firstFrame.getRegionHeight() * Enemy.ENEMY_SCALE_FACTOR : 50 * Enemy.ENEMY_SCALE_FACTOR;
        float padding = 20f; // Padding from screen edges or for map placement

        if (spawnAtEdge) {
            int edge = MathUtils.random(0, 3);
            switch (edge) {
                case 0: spawnX = MathUtils.random(padding, screenWidth - enemyWidth - padding); spawnY = screenHeight + enemyHeight / 2; break; // Top
                case 1: spawnX = MathUtils.random(padding, screenWidth - enemyWidth - padding); spawnY = -enemyHeight - enemyHeight / 2; break; // Bottom
                case 2: spawnX = -enemyWidth - enemyWidth / 2; spawnY = MathUtils.random(padding, screenHeight - enemyHeight - padding); break; // Left
                case 3: spawnX = screenWidth + enemyWidth / 2; spawnY = MathUtils.random(padding, screenHeight - enemyHeight - padding); break; // Right
                default: spawnX = MathUtils.random(padding, screenWidth - enemyWidth - padding); spawnY = screenHeight + enemyHeight / 2; break;
            }
        } else { // Spawn randomly within map boundaries (e.g., for Trees)
            spawnX = MathUtils.random(padding, screenWidth - enemyWidth - padding);
            spawnY = MathUtils.random(padding, screenHeight - enemyHeight - padding);
        }

        Enemy newEnemy = new Enemy(enemyDataToSpawn, enemyAnimations, spawnX, spawnY);
        activeEnemies.add(newEnemy);
        Gdx.app.log("EnemyController", "Successfully Spawned " + newEnemy.getName() + " at (" + (int)spawnX + ", " + (int)spawnY + "). Type: " + (spawnAtEdge ? "Edge" : "RandomMap") + ". Active " + newEnemy.getName() + "s: " + countSpecificEnemy(newEnemy.getName()));
    }

    private int countSpecificEnemy(String enemyName) {
        int count = 0;
        for (Enemy enemy : activeEnemies) {
            if (enemy.getName().equals(enemyName)) {
                count++;
            }
        }
        return count;
    }

    private ObjectMap<String, Animation<TextureRegion>> loadAllAnimationsForEnemy(EnemyData enemyData) {
        ObjectMap<String, Animation<TextureRegion>> loadedAnimations = new ObjectMap<>();
        ObjectMap<String, Array<String>> animationPaths = enemyData.getAnimations();

        if (animationPaths == null || animationPaths.isEmpty()) {
//            Gdx.app.warn("EnemyController", "No animation paths defined in JSON for enemy: " + enemyData.getName());
            return loadedAnimations;
        }
        Gdx.app.debug("EnemyController", "Loading animations for: " + enemyData.getName());// + " with paths: " + animationPaths); // Path logging can be verbose

        for (ObjectMap.Entry<String, Array<String>> entry : animationPaths.entries()) {
            String animationName = entry.key;
            Array<String> paths = entry.value;
            if (paths != null && paths.size > 0) {
                Array<TextureRegion> frames = new Array<>();
                for (String path : paths) {
                    Texture texture = assetManager.getTexture(path);
                    if (texture != null) {
                        frames.add(new TextureRegion(texture));
                    } else {
                        Gdx.app.error("EnemyController", "TEXTURE NOT FOUND for enemy ("+enemyData.getName()+") animation '"+animationName+"' frame: [" + path + "]. Ensure GameAssetManager preloads this texture and path is correct in JSON.");
                    }
                }
                if (frames.size > 0) {
                    Animation.PlayMode playMode = Animation.PlayMode.LOOP; // Default
                    float frameDuration = 0.1f; // Default frame duration, e.g. 10 FPS

                    // Customize play mode and duration for specific animations
                    if (animationName.equalsIgnoreCase("spawn") ||
                        animationName.equalsIgnoreCase("damaged") ||
                        animationName.equalsIgnoreCase("death")) {
                        playMode = Animation.PlayMode.NORMAL; // Play once
                        frameDuration = animationName.equalsIgnoreCase("spawn") ? 0.15f : 0.1f;
                    } else if (animationName.equalsIgnoreCase("show") && enemyData.getName().equals(TREE_ENEMY_NAME)) {
                        frameDuration = 0.2f; // Slower animation for Tree's "show" if it's an idle animation
                    }
                    // Add other animation-specific settings if needed

                    loadedAnimations.put(animationName, new Animation<>(frameDuration, frames, playMode));
                    Gdx.app.debug("EnemyController", "Successfully loaded animation '" + animationName + "' for " + enemyData.getName() + " with " + frames.size + " frames.");
                } else {
                    Gdx.app.error("EnemyController", "No frames loaded for animation '"+animationName+"' for enemy: " + enemyData.getName() + " (texture paths might be all wrong or textures not loaded).");
                }
            }
        }
        if (loadedAnimations.isEmpty() && (animationPaths != null && !animationPaths.isEmpty()) ){
            Gdx.app.error("EnemyController", "CRITICAL: No animations were successfully loaded for " + enemyData.getName() + " even though animation paths were present in JSON. This usually indicates all texture lookups failed or paths are entirely incorrect.");
        }
        return loadedAnimations;
    }

    private void shootEnemyBullet(Enemy shooter, float targetX, float targetY) {
        Vector2 enemyCenter = new Vector2(shooter.getX() + shooter.getBounds().width / 2,
            shooter.getY() + shooter.getBounds().height / 2);
        Vector2 directionToPlayer = new Vector2(targetX - enemyCenter.x, targetY - enemyCenter.y).nor();

        float offsetDistance = shooter.getBounds().width / 2 + 10; // Ensure bullet spawns slightly outside shooter
        float bulletStartX = enemyCenter.x + directionToPlayer.x * offsetDistance - ENEMY_BULLET_DRAW_WIDTH / 2;
        float bulletStartY = enemyCenter.y + directionToPlayer.y * offsetDistance - ENEMY_BULLET_DRAW_HEIGHT / 2;

        int bulletSpeed = 300;
        int bulletDamage = shooter.getEnemyData().getDamage();

        enemyBullets.add(new Bullet(bulletStartX, bulletStartY, bulletSpeed,
            directionToPlayer.x, directionToPlayer.y,
            enemyBulletTextureRegion, ENEMY_BULLET_DRAW_WIDTH, ENEMY_BULLET_DRAW_HEIGHT,
            bulletDamage));
    }

    public void checkBulletCollisions(Array<Bullet> bullets) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet playerBullet = bullets.get(i);
            Rectangle bulletBounds = playerBullet.getBounds();

            for (int j = activeEnemies.size - 1; j >= 0; j--) {
                Enemy enemy = activeEnemies.get(j);
                if (enemy.isAlive() && bulletBounds.overlaps(enemy.getBounds())) {
                    enemy.takeDamage(playerBullet.getDamage());
                    bullets.removeIndex(i); // Bullet is consumed
                    break; // Bullet hits one enemy
                }
            }
        }
    }

    public Array<Enemy> getActiveEnemies() { return activeEnemies; }
    public Array<Bullet> getEnemyBullets() { return enemyBullets; }
    public Array<Seed> getDroppedSeeds() { return droppedSeeds; }
    public int getEnemiesKilled() { return enemiesKilled; }
}
