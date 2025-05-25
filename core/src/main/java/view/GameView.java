package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import controller.MainMenuController;
import graphic.source.Main;
import model.GameAssetManager;
import model.GameSettings;
import model.Player;
import model.Gun;
import model.Bullet;
import model.Seed;
import controller.EnemyController;
// import com.badlogic.gdx.Game; // No longer needed if using Main.getMain().setScreen

/**
 * The main game screen where gameplay logic and rendering occur.
 */
public class GameView implements Screen {
    private final Player player;
    private final Gun gun;
    private final int gameDurationMinutes;
    private final GameSettings gameSettings;
    private final String username; // NEW: To pass username to game over screen

    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextureRegion bulletTextureRegion; // Used for bullets and general UI drawing like XP bar

    private Array<Bullet> bullets; // Player bullets
    private float stateTime; // General animation time
    private float shootTimer; // Cooldown for shooting
    private long gameStartTime;
    private float gameElapsedTimeSeconds; // NEW: Track elapsed time in seconds

    // Player movement flags
    private boolean moveUp, moveDown, moveLeft, moveRight;

    // Constants for scaling
    private static final float BULLET_DRAW_WIDTH = 20f;
    private static final float BULLET_DRAW_HEIGHT = 20f;

    // Variables for gun rotation
    private float gunRotationAngle;
    private Vector2 playerCenter;

    private EnemyController enemyController;

    private BitmapFont font;
    private boolean gameOver;

    // NEW: Constants for XP bar drawing
    private static final float XP_BAR_WIDTH = 150f;
    private static final float XP_BAR_HEIGHT = 15f;
    private static final float UI_MARGIN_LEFT = 10f;
    private static final float UI_LINE_HEIGHT = 30f; // Vertical spacing for UI elements

    // Modified constructor to accept the main Game instance and username
    public GameView(Player player, Gun gun, int gameDurationMinutes, GameSettings gameSettings, String username) {
        this.player = player;
        this.gun = gun;
        this.gameDurationMinutes = gameDurationMinutes;
        this.gameSettings = gameSettings;
        this.username = username; // Store the username

        this.playerCenter = new Vector2();
        this.gameOver = false;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        bullets = new Array<>();
        stateTime = 0f;
        shootTimer = 0f;
        gameStartTime = TimeUtils.nanoTime();
        gameElapsedTimeSeconds = 0f; // Initialize elapsed time

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        String randomBgPath = GameAssetManager.getGameAssetManager().getRandomBackgroundPath();
        if (randomBgPath != null) {
            backgroundTexture = GameAssetManager.getGameAssetManager().getTexture(randomBgPath);
            if (backgroundTexture == null) {
                Gdx.app.error("GameScreen", "Failed to load background texture: " + randomBgPath + ". Using default.");
                backgroundTexture = new Texture(Gdx.files.internal("backgrounds/default_background.png"));
            }
        } else {
            Gdx.app.error("GameScreen", "No random background path available. Using default.");
            backgroundTexture = new Texture(Gdx.files.internal("backgrounds/default_background.png"));
        }

        Texture bulletTexture = GameAssetManager.getGameAssetManager().getTexture(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH);
        if (bulletTexture != null) {
            bulletTextureRegion = new TextureRegion(bulletTexture);
        } else {
            Gdx.app.error("GameScreen", "Failed to load bullet texture. Using default white square.");
            bulletTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
            // Set color to white for visibility if using default square
            bulletTextureRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        TextureRegion playerInitialFrame = player.getIdleAnimation().getKeyFrame(0);
        float playerScaledWidth = playerInitialFrame.getRegionWidth() * Player.PLAYER_SCALE_FACTOR;
        float playerScaledHeight = playerInitialFrame.getRegionHeight() * Player.PLAYER_SCALE_FACTOR;
        player.setX((Gdx.graphics.getWidth() - playerScaledWidth) / 2);
        player.setY((Gdx.graphics.getHeight() - playerScaledHeight) / 2);

        // Pass total game duration to EnemyController
        enemyController = new EnemyController(GameAssetManager.getGameAssetManager(), player, gameDurationMinutes);

        Gdx.input.setInputProcessor(new GameInputProcessor());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Game Logic Update Phase ---
        stateTime += delta;
        shootTimer += delta;
        gameElapsedTimeSeconds = TimeUtils.nanosToMillis(TimeUtils.nanoTime() - gameStartTime) / 1000f; // Update elapsed time

        TextureRegion playerCurrentFrameForCalc = player.getIdleAnimation().getKeyFrame(stateTime, true);
        float playerCurrentFrameWidth = playerCurrentFrameForCalc.getRegionWidth() * Player.PLAYER_SCALE_FACTOR;
        float playerCurrentFrameHeight = playerCurrentFrameForCalc.getRegionHeight() * Player.PLAYER_SCALE_FACTOR;
        playerCenter.set(player.getX() + playerCurrentFrameWidth / 2, player.getY() + playerCurrentFrameHeight / 2);

        Vector2 currentMousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        Vector2 directionToMouse = new Vector2(currentMousePos.x - playerCenter.x, currentMousePos.y - playerCenter.y);
        gunRotationAngle = directionToMouse.angleDeg();

        if (!gameOver) {
            gun.updateReload(delta);

            if (gameSettings.isAutoReloadEnabled() && gun.getCurrentAmmo() == 0 && !gun.isReloading()) {
                gun.startReload();
            }

            float playerSpeed = player.getSpeed() * delta;
            if (moveUp) player.setY(player.getY() + playerSpeed);
            if (moveDown) player.setY(player.getY() - playerSpeed);
            if (moveLeft) player.setX(player.getX() - playerSpeed);
            if (moveRight) player.setX(player.getX() + playerSpeed);

            player.setX(Math.max(0, Math.min(player.getX(), Gdx.graphics.getWidth() - playerCurrentFrameWidth)));
            player.setY(Math.max(0, Math.min(player.getY(), Gdx.graphics.getHeight() - playerCurrentFrameHeight)));

            // Pass elapsed game time to enemy controller
            enemyController.update(delta, gameElapsedTimeSeconds);
            enemyController.checkBulletCollisions(bullets);

            for (int i = bullets.size - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                bullet.update(delta);
                if (bullet.getX() < -bullet.getWidth() || bullet.getX() > Gdx.graphics.getWidth() ||
                    bullet.getY() < -bullet.getHeight() || bullet.getY() > Gdx.graphics.getHeight()) {
                    bullets.removeIndex(i);
                }
            }

            // Check if player is defeated (game over)
            if (!player.isAlive()) {
                gameOver = true;
                Gdx.app.log("GameScreen", "Game Over! Player defeated.");
            }

            // Check game duration
            if (gameElapsedTimeSeconds >= gameDurationMinutes * 60) {
                gameOver = true;
                Gdx.app.log("GameScreen", "Game Over! Time's up.");
            }
        }

        // --- Drawing Phase ---
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!gameOver) {
            TextureRegion playerCurrentFrame = player.getIdleAnimation().getKeyFrame(stateTime, true);
            batch.draw(playerCurrentFrame, player.getX(), player.getY(), playerCurrentFrameWidth, playerCurrentFrameHeight);

            TextureRegion gunCurrentFrame = null;
            Animation<TextureRegion> currentGunAnimation;

            if (gun.isReloading()) {
                currentGunAnimation = gun.getAnimations().get("reload");
                if (currentGunAnimation == null) {
                    currentGunAnimation = gun.getAnimations().get("still");
                    if (currentGunAnimation == null) currentGunAnimation = gun.getAnimations().get("idle");
                }
                if (currentGunAnimation != null) {
                    gunCurrentFrame = currentGunAnimation.getKeyFrame(gun.getReloadStateTime(), false);
                }
            } else {
                currentGunAnimation = gun.getAnimations().get("still");
                if (currentGunAnimation == null) {
                    currentGunAnimation = gun.getAnimations().get("idle");
                }
                if (currentGunAnimation != null) {
                    gunCurrentFrame = currentGunAnimation.getKeyFrame(stateTime, true);
                }
            }

            if (gunCurrentFrame != null) {
                float gunDrawWidth = gunCurrentFrame.getRegionWidth() * Player.PLAYER_SCALE_FACTOR;
                float gunDrawHeight = gunCurrentFrame.getRegionHeight() * Player.PLAYER_SCALE_FACTOR;

                float gunOriginX = gunDrawWidth * 0.1f;
                float gunOriginY = gunDrawHeight * 0.5f;

                float gunPosX = playerCenter.x - gunOriginX + playerCurrentFrameWidth * 0.2f;
                float gunPosY = playerCenter.y - gunOriginY + playerCurrentFrameHeight * 0.1f;

                batch.draw(gunCurrentFrame,
                    gunPosX, gunPosY,
                    gunOriginX, gunOriginY,
                    gunDrawWidth, gunDrawHeight,
                    1, 1,
                    gunRotationAngle);
            } else {
                Gdx.app.error("GameScreen", "No appropriate gun animation found for gun: " + gun.getGunData().getName());
            }

            for (Bullet bullet : bullets) {
                bullet.draw(batch);
            }
        }

        enemyController.draw(batch);

        // --- Draw UI ---
        float currentUIY = Gdx.graphics.getHeight() - UI_MARGIN_LEFT; // Start from top-left

        // HP
        font.draw(batch, "HP: " + player.getCurrentHp() + "/" + player.getCharacterData().getHp(), UI_MARGIN_LEFT, currentUIY);
        currentUIY -= UI_LINE_HEIGHT;

        // Ammo
        font.draw(batch, "Ammo: " + gun.getCurrentAmmo() + "/" + gun.getGunData().getMax_ammo(), UI_MARGIN_LEFT, currentUIY);
        currentUIY -= UI_LINE_HEIGHT;

        // Reloading Status
        if (gun.isReloading()) {
            font.draw(batch, "RELOADING...", UI_MARGIN_LEFT, currentUIY);
        }
        currentUIY -= UI_LINE_HEIGHT;

        // Level
        font.draw(batch, "Level: " + player.getLevel(), UI_MARGIN_LEFT, currentUIY);
        currentUIY -= UI_LINE_HEIGHT;

        // XP Bar
        // Draw XP Bar Background
        batch.setColor(Color.DARK_GRAY);
        batch.draw(bulletTextureRegion, UI_MARGIN_LEFT, currentUIY - XP_BAR_HEIGHT + 5, XP_BAR_WIDTH, XP_BAR_HEIGHT); // Adjusted Y for bar
        batch.setColor(Color.WHITE); // Reset color before drawing fill

        // Draw XP Bar Fill
        float xpProgress = (float) player.getXp() / player.getXpToNextLevel();
        xpProgress = MathUtils.clamp(xpProgress, 0f, 1f); // Ensure it stays between 0 and 1
        batch.setColor(Color.GREEN);
        batch.draw(bulletTextureRegion, UI_MARGIN_LEFT, currentUIY - XP_BAR_HEIGHT + 5, XP_BAR_WIDTH * xpProgress, XP_BAR_HEIGHT);
        batch.setColor(Color.WHITE); // Reset color for other drawings

        // XP Text over the bar
        font.draw(batch, String.format("XP: %d/%d", player.getXp(), player.getXpToNextLevel()), UI_MARGIN_LEFT, currentUIY);
        currentUIY -= UI_LINE_HEIGHT; // Move past the XP bar and its text

        // Kills
        font.draw(batch, "Kills: " + enemyController.getEnemiesKilled(), UI_MARGIN_LEFT, currentUIY);
        currentUIY -= UI_LINE_HEIGHT;

        // Display elapsed time
        font.draw(batch, String.format("Time: %02d:%02d", (int)(gameElapsedTimeSeconds / 60), (int)(gameElapsedTimeSeconds % 60)),
            UI_MARGIN_LEFT, currentUIY);


        if (gameOver) {
            // Remove the old game over text from GameView
            // The new GameOverScreen will handle this
        }

        batch.end();

        // --- Game Over Transition ---
        if (gameOver && !(Main.getMain().getScreen() instanceof GameOverScreen)) {
            // Calculate score when game ends
            int finalScore = (int)(gameElapsedTimeSeconds * enemyController.getEnemiesKilled());
            // Need a way to get the username here. If it's passed into GameView constructor, use it.
            // For now, assuming `username` field is correctly populated from PreGame or similar.
            Gdx.app.postRunnable(() -> Main.getMain().setScreen(new GameOverScreen(username, gameElapsedTimeSeconds, enemyController.getEnemiesKilled(), finalScore)));
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        // Check if bulletTextureRegion's texture is unique to this screen before disposing
        // If it's from GameAssetManager, don't dispose it here.
        if (bulletTextureRegion != null && bulletTextureRegion.getTexture() != null) {
            // This is a heuristic to check if it's the 1x1 default texture created here
            if (bulletTextureRegion.getTexture().getWidth() == 1 && bulletTextureRegion.getTexture().getHeight() == 1) {
                bulletTextureRegion.getTexture().dispose();
            }
        }
        font.dispose();
        // Do NOT dispose enemyController here; it might have textures managed by AssetManager
        // which will be disposed by GameAssetManager.
    }

    private class GameInputProcessor implements com.badlogic.gdx.InputProcessor {
        @Override
        public boolean keyDown(int keycode) {
            if (gameOver) {
                // Input is handled by GameOverScreen once the transition occurs.
                // This prevents multiple screen changes or input during transition.
                return true;
            }

            if (keycode == gameSettings.getKeyBindings().get("Move Up")) {
                moveUp = true;
            } else if (keycode == gameSettings.getKeyBindings().get("Move Down")) {
                moveDown = true;
            } else if (keycode == gameSettings.getKeyBindings().get("Move Left")) {
                moveLeft = true;
            } else if (keycode == gameSettings.getKeyBindings().get("Move Right")) {
                moveRight = true;
            } else if (keycode == gameSettings.getKeyBindings().get("Shoot")) {
                Vector2 shootMousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
                attemptShoot(shootMousePos);
            } else if (keycode == gameSettings.getKeyBindings().get("Reload")) {
                if (!gun.isReloading()) {
                    gun.startReload();
                }
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (gameOver) return false;

            if (keycode == gameSettings.getKeyBindings().get("Move Up")) {
                moveUp = false;
            } else if (keycode == gameSettings.getKeyBindings().get("Move Down")) {
                moveDown = false;
            } else if (keycode == gameSettings.getKeyBindings().get("Move Left")) {
                moveLeft = false;
            } else if (keycode == gameSettings.getKeyBindings().get("Move Right")) {
                moveRight = false;
            }
            return true;
        }

        @Override public boolean keyTyped(char character) { return false; }

        @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (gameOver) {
                // Input is handled by GameOverScreen once the transition occurs.
                return true;
            }

            if (button == gameSettings.getKeyBindings().get("Shoot")) {
                Vector2 shootMousePos = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
                attemptShoot(shootMousePos);
            }
            return false;
        }

        private void attemptShoot(Vector2 targetMousePos) {
            if (!gun.isReloading() && gun.getCurrentAmmo() > 0 && shootTimer >= gun.getGunData().getFire_rate()) {
                gun.shoot();

                for (int i = 0; i < gun.getGunData().getProjectile(); i++) {
                    Vector2 bulletDirection = new Vector2(targetMousePos.x - playerCenter.x, targetMousePos.y - playerCenter.y);
                    if (gun.getGunData().getProjectile() > 1) {
                        float spreadAngle = MathUtils.random(-5f, 5f);
                        bulletDirection.rotateDeg(spreadAngle);
                    }
                    bulletDirection.nor();

                    float offsetDistance = (player.getIdleAnimation().getKeyFrame(0).getRegionWidth() * Player.PLAYER_SCALE_FACTOR / 2) + 10;
                    float bulletStartX = playerCenter.x + bulletDirection.x * offsetDistance - BULLET_DRAW_WIDTH / 2;
                    float bulletStartY = playerCenter.y + bulletDirection.y * offsetDistance - BULLET_DRAW_HEIGHT / 2;

                    bullets.add(new Bullet(bulletStartX, bulletStartY, 500,
                        bulletDirection.x, bulletDirection.y,
                        bulletTextureRegion, BULLET_DRAW_WIDTH, BULLET_DRAW_HEIGHT,
                        gun.getGunData().getDamage()));
                }
                shootTimer = 0f;
            }
        }

        @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
        @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
        @Override public boolean scrolled(float amountX, float amountY) { return false; }
    }
}
