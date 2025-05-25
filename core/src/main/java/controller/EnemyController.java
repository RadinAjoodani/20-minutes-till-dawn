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

    // General spawn timer (for any enemies not covered by specific rules)
    private float generalSpawnTimer;
    private float generalSpawnInterval = 3.0f; // Time between general enemy spawns

    // NEW: Tentacle Monster specific spawn variables
    private float tentacleMonsterSpawnTimer;
    private float tentacleMonsterSpawnInterval = 3.0f; // Each 3 seconds
    private final String TENTACLE_MONSTER_NAME = "TentacleMonster"; // Ensure this matches JSON

    // NEW: Eye Bat specific spawn variables
    private float eyeBatSpawnTimer;
    private float eyeBatSpawnInterval = 10.0f; // Each 10 seconds
    private final String EYE_BAT_NAME = "EyeBat"; // Ensure this matches JSON
    private float gameTotalDurationSeconds; // Received from GameView
    private float eyeBatSpawnStartTime; // t/4 seconds

    // NEW: Tree enemy specific spawn variables
    private float treeSpawnTimer;
    private float treeSpawnInterval = 20.0f; // Initial interval for trees
    private int maxTreesOnMap = 3; // Maximum number of trees active at once
    private final String TREE_ENEMY_NAME = "Tree"; // Ensure this matches JSON

    private Array<Bullet> enemyBullets;
    private TextureRegion enemyBulletTextureRegion;
    private static final float ENEMY_BULLET_DRAW_WIDTH = 15f;
    private static final float ENEMY_BULLET_DRAW_HEIGHT = 15f;

    private Array<Seed> droppedSeeds;
    private TextureRegion seedTextureRegion;

    private int enemiesKilled = 0; // NEW: Kills counter

    public EnemyController(GameAssetManager assetManager, Player player, float gameTotalDurationMinutes) {
        this.assetManager = assetManager;
        this.player = player;
        this.activeEnemies = new Array<>();
        this.generalSpawnTimer = 0f;
        this.tentacleMonsterSpawnTimer = 0f;
        this.eyeBatSpawnTimer = 0f;
        this.treeSpawnTimer = 0f; // Initialize tree timer
        this.enemyBullets = new Array<>();
        this.droppedSeeds = new Array<>();

        this.gameTotalDurationSeconds = gameTotalDurationMinutes * 60f; // Convert minutes to seconds
        this.eyeBatSpawnStartTime = this.gameTotalDurationSeconds / 4f;

        // Load enemy bullet texture
        Texture bulletTexture = assetManager.getTexture(assetManager.BULLET_TEXTURE_PATH);
        if (bulletTexture != null) {
            enemyBulletTextureRegion = new TextureRegion(bulletTexture);
        } else {
            Gdx.app.error("EnemyController", "Failed to load enemy bullet texture. Using default white square.");
            enemyBulletTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
        }

        // Load seed texture
        Texture seedTexture = assetManager.getTexture(assetManager.ENEMY_SEED_TEXTURE_PATH);
        if (seedTexture != null) {
            seedTextureRegion = new TextureRegion(seedTexture);
        } else {
            Gdx.app.error("EnemyController", "Failed to load seed texture. Using default white square.");
            seedTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
        }
    }

    public void update(float delta, float gameElapsedTimeSeconds) { // Pass elapsed time from GameView
        // --- General Enemy Spawning (if any remain, or as a fallback) ---
        generalSpawnTimer += delta;
        if (generalSpawnTimer >= generalSpawnInterval) {
            // If you only want rule-based spawning, you can remove this general spawn.
            // For now, let's keep it but ensure it doesn't spawn the specific types.
            // spawnRandomEnemyExcept(TENTACLE_MONSTER_NAME, EYE_BAT_NAME, TREE_ENEMY_NAME);
            generalSpawnTimer = 0f;
        }

        // NEW: Tentacle Monster Spawning Logic
        tentacleMonsterSpawnTimer += delta;
        if (tentacleMonsterSpawnTimer >= tentacleMonsterSpawnInterval) {
            int numToSpawn = (int) (gameElapsedTimeSeconds / 30.0f); // i/30 of this enemy
            numToSpawn = Math.max(1, numToSpawn); // Ensure at least 1 spawns after first interval
            if (numToSpawn > 0) {
                Gdx.app.log("EnemyController", "Spawning " + numToSpawn + " TentacleMonsters.");
                for (int i = 0; i < numToSpawn; i++) {
                    spawnSpecificEnemy(TENTACLE_MONSTER_NAME);
                }
            }
            tentacleMonsterSpawnTimer = 0f;
        }

        // NEW: Eye Bat Spawning Logic
        eyeBatSpawnTimer += delta;
        if (gameElapsedTimeSeconds >= eyeBatSpawnStartTime && eyeBatSpawnTimer >= eyeBatSpawnInterval) {
            // Formula: (4i - t + 30) / 30, where i is gameElapsedTimeSeconds, t is gameTotalDurationSeconds
            float formulaResult = (4 * gameElapsedTimeSeconds - gameTotalDurationSeconds + 30) / 30.0f;
            int numToSpawn = Math.max(0, MathUtils.floor(formulaResult)); // Ensure non-negative and floor
            numToSpawn = Math.max(1, numToSpawn); // Ensure at least 1 spawns after first interval
            if (numToSpawn > 0) {
                Gdx.app.log("EnemyController", "Spawning " + numToSpawn + " EyeBats.");
                for (int i = 0; i < numToSpawn; i++) {
                    spawnSpecificEnemy(EYE_BAT_NAME);
                }
            }
            eyeBatSpawnTimer = 0f;
        }

        // NEW: Tree Spawning Logic
        treeSpawnTimer += delta;
        // Count current number of trees
        int currentTrees = 0;
        for (Enemy enemy : activeEnemies) {
            if (enemy.getName().equals(TREE_ENEMY_NAME)) {
                currentTrees++;
            }
        }

        if (currentTrees < maxTreesOnMap && treeSpawnTimer >= treeSpawnInterval) {
            // Make trees spawn more frequently over time, up to a certain point
            float dynamicTreeSpawnInterval = Math.max(5.0f, treeSpawnInterval * (1.0f - (gameElapsedTimeSeconds / gameTotalDurationSeconds * 0.5f)));
            if (treeSpawnTimer >= dynamicTreeSpawnInterval) {
                spawnSpecificEnemy(TREE_ENEMY_NAME);
                treeSpawnTimer = 0f;
                Gdx.app.log("EnemyController", "Spawned a Tree enemy. Current trees: " + (currentTrees + 1));
            }
        }


        // Update existing enemies and handle interactions
        for (int i = activeEnemies.size - 1; i >= 0; i--) {
            Enemy enemy = activeEnemies.get(i);
            float playerCenterX = player.getX() + player.getIdleAnimation().getKeyFrame(0).getRegionWidth() * Player.PLAYER_SCALE_FACTOR / 2;
            float playerCenterY = player.getY() + player.getIdleAnimation().getKeyFrame(0).getRegionHeight() * Player.PLAYER_SCALE_FACTOR / 2;

            // Only move if not a static enemy like a Tree
            if (!enemy.getName().equals(TREE_ENEMY_NAME)) {
                enemy.update(delta, playerCenterX, playerCenterY);
            } else {
                enemy.update(delta, enemy.getX(), enemy.getY()); // Trees don't move towards player
            }

            // Enemy attack logic - collision with player (only for non-Tree enemies)
            if (player.isAlive() && enemy.getBounds().overlaps(player.getBounds(0)) && enemy.getEnemyData().getDamage() > 0) {
                if (enemy.canAttack(delta)) {
                    player.takeDamage(enemy.getEnemyData().getDamage());
                }
            }

            // EyeBat shooting logic (already in place)
            if (enemy.getName().equals(EYE_BAT_NAME) && enemy.isAlive()) {
                if (enemy.canShoot(delta)) {
                    shootEnemyBullet(enemy, playerCenterX, playerCenterY);
                }
            }

            if (!enemy.isAlive()) {
                Gdx.app.log("EnemyController", enemy.getName() + " defeated!");
                // Drop a seed when enemy is defeated
                // Trees also drop seeds, maybe adjust XP value for trees?
                droppedSeeds.add(new Seed(enemy.getX() + enemy.getBounds().width / 2,
                    enemy.getY() + enemy.getBounds().height / 2,
                    seedTextureRegion, enemy.getName().equals(TREE_ENEMY_NAME) ? 5 : 3)); // Trees give more XP
                activeEnemies.removeIndex(i);
                enemiesKilled++; // Increment kills counter
            }
        }

        // Update enemy bullets (already in place)
        for (int i = enemyBullets.size - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            bullet.update(delta);
            // Check if enemy bullet hits player
            if (player.isAlive() && bullet.getBounds().overlaps(player.getBounds(0))) {
                player.takeDamage(bullet.getDamage());
                enemyBullets.removeIndex(i);
            } else if (bullet.getX() < -bullet.getWidth() || bullet.getX() > Gdx.graphics.getWidth() ||
                bullet.getY() < -bullet.getHeight() || bullet.getY() > Gdx.graphics.getHeight()) {
                enemyBullets.removeIndex(i);
            }
        }

        // Check for player collecting seeds (already in place)
        if (player.isAlive()) {
            Rectangle playerBounds = player.getBounds(stateTime); // Use player's stateTime for accurate bounds
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

    // NEW: Helper method to spawn a specific enemy by name
    private void spawnSpecificEnemy(String enemyName) {
        EnemyData enemyData = assetManager.getEnemyDataByName(enemyName);
        if (enemyData == null) {
            Gdx.app.error("EnemyController", "Enemy data not found for: " + enemyName);
            return;
        }
        spawnEnemy(enemyData);
    }

    // Modified spawnEnemy to accept a specific EnemyData, or pick random if null
    private void spawnEnemy(EnemyData specificEnemyData) {
        EnemyData enemyDataToSpawn = specificEnemyData;
        if (enemyDataToSpawn == null) {
            Array<EnemyData> allEnemyData = assetManager.getAllEnemyData();
            if (allEnemyData.size == 0) {
                Gdx.app.error("EnemyController", "No enemy data available to spawn!");
                return;
            }
            enemyDataToSpawn = allEnemyData.random();
        }

        ObjectMap<String, Animation<TextureRegion>> enemyAnimations = loadAllAnimationsForEnemy(enemyDataToSpawn);

        float spawnX, spawnY;
        int edge = MathUtils.random(0, 3);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        TextureRegion firstFrame = enemyAnimations.containsKey("show") ?
            enemyAnimations.get("show").getKeyFrame(0) : null;
        float enemyWidth = (firstFrame != null) ? firstFrame.getRegionWidth() * Enemy.ENEMY_SCALE_FACTOR : 50 * Enemy.ENEMY_SCALE_FACTOR;
        float enemyHeight = (firstFrame != null) ? firstFrame.getRegionHeight() * Enemy.ENEMY_SCALE_FACTOR : 50 * Enemy.ENEMY_SCALE_FACTOR;

        // Add padding from screen edges
        float padding = 20f; // Small padding to avoid enemies spawning exactly on edge

        switch (edge) {
            case 0: // Top edge
                spawnX = MathUtils.random(padding, screenWidth - enemyWidth - padding);
                spawnY = screenHeight + enemyHeight / 2; // Spawn slightly off-screen
                break;
            case 1: // Bottom edge
                spawnX = MathUtils.random(padding, screenWidth - enemyWidth - padding);
                spawnY = -enemyHeight - enemyHeight / 2; // Spawn slightly off-screen
                break;
            case 2: // Left edge
                spawnX = -enemyWidth - enemyWidth / 2; // Spawn slightly off-screen
                spawnY = MathUtils.random(padding, screenHeight - enemyHeight - padding);
                break;
            case 3: // Right edge
                spawnX = screenWidth + enemyWidth / 2; // Spawn slightly off-screen
                spawnY = MathUtils.random(padding, screenHeight - enemyHeight - padding);
                break;
            default:
                spawnX = MathUtils.random(padding, screenWidth - enemyWidth - padding); // Default to top if something goes wrong
                spawnY = screenHeight + enemyHeight / 2;
                break;
        }

        Enemy newEnemy = new Enemy(enemyDataToSpawn, enemyAnimations, spawnX, spawnY);
        activeEnemies.add(newEnemy);
        Gdx.app.log("EnemyController", "Spawned " + newEnemy.getName() + " at (" + (int)spawnX + ", " + (int)spawnY + ")");
    }

    // Original loadAllAnimationsForEnemy method remains the same
    private ObjectMap<String, Animation<TextureRegion>> loadAllAnimationsForEnemy(EnemyData enemyData) {
        ObjectMap<String, Animation<TextureRegion>> loadedAnimations = new ObjectMap<>();
        ObjectMap<String, Array<String>> animationPaths = enemyData.getAnimations();

        if (animationPaths != null) {
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
                            Gdx.app.error("EnemyController", "Failed to load texture for enemy animation frame: " + path);
                        }
                    }
                    if (frames.size > 0) {
                        Animation.PlayMode playMode = Animation.PlayMode.LOOP;
                        float frameDuration = 0.1f;

                        if (animationName.equals("spawn")) {
                            playMode = Animation.PlayMode.NORMAL;
                            frameDuration = 0.15f;
                        } else if (animationName.equals("damaged")) { // Example for tree damaged animation
                            playMode = Animation.PlayMode.NORMAL;
                            frameDuration = 0.1f;
                        }

                        loadedAnimations.put(animationName, new Animation<>(frameDuration, frames, playMode));
                    }
                }
            }
        }
        return loadedAnimations;
    }

    private void shootEnemyBullet(Enemy shooter, float targetX, float targetY) {
        Vector2 enemyCenter = new Vector2(shooter.getX() + shooter.getBounds().width / 2,
            shooter.getY() + shooter.getBounds().height / 2);
        Vector2 directionToPlayer = new Vector2(targetX - enemyCenter.x, targetY - enemyCenter.y).nor();

        float offsetDistance = shooter.getBounds().width / 2 + 10;
        float bulletStartX = enemyCenter.x + directionToPlayer.x * offsetDistance - ENEMY_BULLET_DRAW_WIDTH / 2;
        float bulletStartY = enemyCenter.y + directionToPlayer.y * offsetDistance - ENEMY_BULLET_DRAW_HEIGHT / 2;

        int bulletSpeed = 300;
        int bulletDamage = shooter.getEnemyData().getDamage();

        enemyBullets.add(new Bullet(bulletStartX, bulletStartY, bulletSpeed,
            directionToPlayer.x, directionToPlayer.y,
            enemyBulletTextureRegion, ENEMY_BULLET_DRAW_WIDTH, ENEMY_BULLET_DRAW_HEIGHT,
            bulletDamage));
        Gdx.app.log("EnemyController", shooter.getName() + " shot a bullet!");
    }


    public void checkBulletCollisions(Array<Bullet> bullets) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet playerBullet = bullets.get(i);
            Rectangle bulletBounds = playerBullet.getBounds();

            for (int j = activeEnemies.size - 1; j >= 0; j--) {
                Enemy enemy = activeEnemies.get(j);
                // Don't damage if enemy is already dying or dead
                if (enemy.isAlive() && bulletBounds.overlaps(enemy.getBounds())) {
                    boolean enemyDied = enemy.takeDamage(playerBullet.getDamage());
                    bullets.removeIndex(i); // Remove player bullet on hit
                    break; // Only hit one enemy per bullet
                }
            }
        }
    }

    public Array<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    public Array<Bullet> getEnemyBullets() {
        return enemyBullets;
    }

    public Array<Seed> getDroppedSeeds() {
        return droppedSeeds;
    }

    public int getEnemiesKilled() { // NEW: Getter for kills
        return enemiesKilled;
    }

    // You might want to pass player's stateTime to enemy.update for more accurate bounds
    private float stateTime = 0f; // Add this and ensure it's updated in render/update from GameView
    // For now, I'll use the '0' for simplicity as in your original code
    // If you plan more complex player animations affecting bounds, you'll need to pass this
}
