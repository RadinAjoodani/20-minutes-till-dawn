package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.GameSettings;
import model.Player;
import model.Gun;
import model.Bullet;
import model.User;
import controller.EnemyController;

public class GameView implements Screen {
    private final Player player;
    private final Gun gun;
    private final int gameDurationMinutes;
    private final GameSettings gameSettings;
    private final String username;

    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextureRegion bulletTextureRegion; // Also used for UI elements like XP bar

    private OrthographicCamera gameCamera;
    private Vector2 worldPlayerPosition;

    private Array<Bullet> bullets;
    private float stateTime;
    private float shootTimer;
    private long gameStartTime;
    private float gameElapsedTimeSeconds;

    private boolean moveUp, moveDown, moveLeft, moveRight;

    private static final float BULLET_DRAW_WIDTH = 20f;
    private static final float BULLET_DRAW_HEIGHT = 20f;

    private float gunRotationAngle;

    private EnemyController enemyController;

    private BitmapFont font;
    private BitmapFont uiFont; // Potentially a larger font for UI if needed
    private boolean gameOver;

    // UI constants
    private static final float UI_TOP_MARGIN = 15f; // Margin from the top of the screen
    private static final float UI_ELEMENT_SPACING = 8f; // Spacing between UI elements
    private static final float XP_BAR_WIDTH_TOP = 400f; // Significantly wider XP bar
    private static final float XP_BAR_HEIGHT_TOP = 25f; // Thicker XP bar
    private static final float UI_SIDE_MARGIN = 10f;
    private static final float UI_TEXT_LINE_HEIGHT = 25f; // Adjusted line height for side UI text

    private Matrix4 uiProjectionMatrix;

    public GameView(Player player, Gun gun, int gameDurationMinutes, GameSettings gameSettings, String username) {
        this.player = player;
        this.gun = gun;
        this.gameDurationMinutes = gameDurationMinutes;
        this.gameSettings = gameSettings;
        this.username = username;
        this.gameOver = false;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        bullets = new Array<>();
        stateTime = 0f;
        shootTimer = 0f;
        gameStartTime = TimeUtils.nanoTime();
        gameElapsedTimeSeconds = 0f;

        font = new BitmapFont(); // General font
        font.setColor(Color.WHITE);

        uiFont = new BitmapFont(); // Could be a different size/style if needed
        uiFont.setColor(Color.WHITE);
        uiFont.getData().setScale(1.1f); // Slightly larger font for UI elements like Level

        float GdxWidth = Gdx.graphics.getWidth();
        float GdxHeight = Gdx.graphics.getHeight();

        if (GdxWidth == 0 || GdxHeight == 0) {
            Gdx.app.error("GameView.show", "Gdx.graphics.getWidth() or getHeight() is 0. Cannot initialize camera properly.");
            return;
        }

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, GdxWidth, GdxHeight);

        uiProjectionMatrix = new Matrix4().setToOrtho2D(0, 0, GdxWidth, GdxHeight);

        String specificBackgroundPath = "backgrounds/game_background.jpg";
        try {
            backgroundTexture = GameAssetManager.getGameAssetManager().getTexture(specificBackgroundPath);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Exception while trying to get '" + specificBackgroundPath + "' from AssetManager. It might not be loaded.", e);
            backgroundTexture = null;
        }

        if (backgroundTexture == null) {
            Gdx.app.log("GameScreen", "Failed to load specific background: '" + specificBackgroundPath + "'. Attempting fallback...");
            String randomBgPath = GameAssetManager.getGameAssetManager().getRandomBackgroundPath();
            if (randomBgPath != null) {
                try {
                    backgroundTexture = GameAssetManager.getGameAssetManager().getTexture(randomBgPath);
                    Gdx.app.log("GameScreen", "Using random background: " + randomBgPath);
                } catch (Exception e) {
                    Gdx.app.error("GameScreen", "Failed to load random background: " + randomBgPath, e);
                    backgroundTexture = null;
                }
            }
            if (backgroundTexture == null) {
                Gdx.app.error("GameScreen", "Random background also failed. Using hardcoded default placeholder.");
                try {
                    backgroundTexture = new Texture(Gdx.files.internal("backgrounds/default_background.png"));
                } catch (Exception e) {
                    Gdx.app.error("GameScreen", "CRITICAL: Failed to load default_background.png. No background will be shown.", e);
                }
            }
        }

        if (backgroundTexture != null) {
            backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        } else {
            Gdx.app.error("GameScreen", "No background texture could be loaded. The game background will be black.");
        }

        Texture bulletTex = GameAssetManager.getGameAssetManager().getTexture(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH);
        if (bulletTex != null) {
            bulletTextureRegion = new TextureRegion(bulletTex);
        } else {
            Gdx.app.error("GameScreen", "Failed to load bullet texture. Using default white square.");
            bulletTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
        }

        worldPlayerPosition = new Vector2(0, 0);
        if (player != null) {
            player.setX(worldPlayerPosition.x);
            player.setY(worldPlayerPosition.y);
        } else {
            Gdx.app.error("GameView.show", "Player object is null. Cannot set initial position. Game may not function correctly.");
            return;
        }

        if (GameAssetManager.getGameAssetManager() != null && player != null) {
            enemyController = new EnemyController(GameAssetManager.getGameAssetManager(), player, gameDurationMinutes);
        } else {
            Gdx.app.error("GameView.show", "Failed to initialize EnemyController due to null GameAssetManager or Player. Game may not function correctly.");
            return;
        }

        Gdx.input.setInputProcessor(new GameInputProcessor());
    }

    @Override
    public void render(float delta) {
        if (player == null || worldPlayerPosition == null || enemyController == null || gameCamera == null || batch == null || uiProjectionMatrix == null) {
            Gdx.app.error("GameView.render", "Critical component is null, skipping render loop. Check show() method for errors.");
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        shootTimer += delta;
        gameElapsedTimeSeconds = TimeUtils.nanosToMillis(TimeUtils.nanoTime() - gameStartTime) / 1000f;

        if (!gameOver) {
            if (gun != null) {
                gun.updateReload(delta);
                if (gameSettings != null && gameSettings.isAutoReloadEnabled() && gun.getCurrentAmmo() == 0 && !gun.isReloading()) {
                    gun.startReload();
                }
            }

            float playerSpeed = player.getSpeed() * delta;
            if (moveUp) worldPlayerPosition.y += playerSpeed;
            if (moveDown) worldPlayerPosition.y -= playerSpeed;
            if (moveLeft) worldPlayerPosition.x -= playerSpeed;
            if (moveRight) worldPlayerPosition.x += playerSpeed;

            player.setX(worldPlayerPosition.x);
            player.setY(worldPlayerPosition.y);
        }

        gameCamera.position.x = worldPlayerPosition.x;
        gameCamera.position.y = worldPlayerPosition.y;
        gameCamera.update();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();

        if (backgroundTexture != null) {
            float cameraWorldLeft = gameCamera.position.x - gameCamera.viewportWidth / 2;
            float cameraWorldBottom = gameCamera.position.y - gameCamera.viewportHeight / 2;
            float u = cameraWorldLeft / backgroundTexture.getWidth();
            float v = cameraWorldBottom / backgroundTexture.getHeight();
            float u2 = (cameraWorldLeft + gameCamera.viewportWidth) / backgroundTexture.getWidth();
            float v2 = (cameraWorldBottom + gameCamera.viewportHeight) / backgroundTexture.getHeight();
            batch.draw(backgroundTexture, cameraWorldLeft, cameraWorldBottom,
                gameCamera.viewportWidth, gameCamera.viewportHeight, u, v, u2, v2);
        }

        if (!gameOver) {
            Animation<TextureRegion> playerIdleAnim = player.getIdleAnimation();
            if (playerIdleAnim != null) {
                TextureRegion playerCurrentFrame = playerIdleAnim.getKeyFrame(stateTime, true);
                if (playerCurrentFrame != null) {
                    float playerScaledWidth = playerCurrentFrame.getRegionWidth() * Player.PLAYER_SCALE_FACTOR;
                    float playerScaledHeight = playerCurrentFrame.getRegionHeight() * Player.PLAYER_SCALE_FACTOR;
                    batch.draw(playerCurrentFrame, worldPlayerPosition.x - playerScaledWidth / 2,
                        worldPlayerPosition.y - playerScaledHeight / 2, playerScaledWidth, playerScaledHeight);
                }

                Vector3 mouseScreenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                Vector3 mouseWorldPos = gameCamera.unproject(new Vector3(mouseScreenPos));

                Vector2 playerWorldCenter = new Vector2(worldPlayerPosition.x, worldPlayerPosition.y);
                Vector2 directionToMouse = new Vector2(mouseWorldPos.x - playerWorldCenter.x, mouseWorldPos.y - playerWorldCenter.y);
                gunRotationAngle = directionToMouse.angleDeg();

                if (gun != null && gun.getAnimations() != null) {
                    TextureRegion gunCurrentFrame = null;
                    Animation<TextureRegion> currentGunAnimation = gun.isReloading() ? gun.getAnimations().get("reload") : gun.getAnimations().get("still");
                    if (currentGunAnimation == null) currentGunAnimation = gun.getAnimations().get("idle");

                    if (currentGunAnimation != null) {
                        gunCurrentFrame = currentGunAnimation.getKeyFrame(gun.isReloading() ? gun.getReloadStateTime() : stateTime, !gun.isReloading());
                    }

                    if (gunCurrentFrame != null) {
                        float gunDrawWidth = gunCurrentFrame.getRegionWidth() * Player.PLAYER_SCALE_FACTOR;
                        float gunDrawHeight = gunCurrentFrame.getRegionHeight() * Player.PLAYER_SCALE_FACTOR;
                        float gunOriginX = gunDrawWidth * 0.1f;
                        float gunOriginY = gunDrawHeight * 0.5f;
                        float gunRelativeOffsetX = 0;
                        float gunRelativeOffsetY = 0;
                        batch.draw(gunCurrentFrame, playerWorldCenter.x + gunRelativeOffsetX - gunOriginX,
                            playerWorldCenter.y + gunRelativeOffsetY - gunOriginY,
                            gunOriginX, gunOriginY, gunDrawWidth, gunDrawHeight, 1, 1, gunRotationAngle);
                    }
                }
            }

            for (int i = bullets.size - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                bullet.update(delta);
                float cullMargin = 100f;
                float cameraWorldLeft = gameCamera.position.x - gameCamera.viewportWidth / 2 - cullMargin;
                float cameraWorldRight = gameCamera.position.x + gameCamera.viewportWidth / 2 + cullMargin;
                float cameraWorldBottom = gameCamera.position.y - gameCamera.viewportHeight / 2 - cullMargin;
                float cameraWorldTop = gameCamera.position.y + gameCamera.viewportHeight / 2 + cullMargin;

                if (bullet.getX() < cameraWorldLeft || bullet.getX() > cameraWorldRight ||
                    bullet.getY() < cameraWorldBottom || bullet.getY() > cameraWorldTop) {
                    bullets.removeIndex(i);
                } else {
                    bullet.draw(batch);
                }
            }
        }

        enemyController.update(delta, gameElapsedTimeSeconds);
        enemyController.checkBulletCollisions(bullets);
        enemyController.draw(batch);

        batch.end();

        // --- Draw UI (fixed on screen) ---
        batch.setProjectionMatrix(uiProjectionMatrix);
        batch.begin();

        if (uiFont != null && player != null && player.getCharacterData() != null && gun != null && gun.getGunData() != null && enemyController != null && bulletTextureRegion != null) {

            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            // --- Top Center UI: Level and XP Bar ---
            String levelText = "Level: " + player.getLevel();
            GlyphLayout levelLayout = new GlyphLayout(uiFont, levelText);
            float levelTextX = (screenWidth - levelLayout.width) / 2;
            float levelTextY = screenHeight - UI_TOP_MARGIN; // Y pos for the top of the text
            uiFont.draw(batch, levelLayout, levelTextX, levelTextY);

            float xpBarY = levelTextY - levelLayout.height - UI_ELEMENT_SPACING; // Y pos for the top of the XP bar
            float xpBarX = (screenWidth - XP_BAR_WIDTH_TOP) / 2;

            batch.setColor(Color.DARK_GRAY); // Background of XP bar
            batch.draw(bulletTextureRegion, xpBarX, xpBarY - XP_BAR_HEIGHT_TOP, XP_BAR_WIDTH_TOP, XP_BAR_HEIGHT_TOP);

            float xpProgress = 0;
            if (player.getXpToNextLevel() > 0) {
                xpProgress = (float) player.getXp() / player.getXpToNextLevel();
            }
            xpProgress = MathUtils.clamp(xpProgress, 0f, 1f);
            batch.setColor(Color.LIME); // Brighter green for XP fill
            batch.draw(bulletTextureRegion, xpBarX, xpBarY - XP_BAR_HEIGHT_TOP, XP_BAR_WIDTH_TOP * xpProgress, XP_BAR_HEIGHT_TOP);

            // XP Text (e.g., "XP: 50/120") centered within the bar
            String xpText = String.format("%d / %d XP", player.getXp(), player.getXpToNextLevel());
            GlyphLayout xpLayout = new GlyphLayout(font, xpText); // Using regular font for this text
            float xpTextX = xpBarX + (XP_BAR_WIDTH_TOP - xpLayout.width) / 2;
            float xpTextY = xpBarY - XP_BAR_HEIGHT_TOP / 2 + xpLayout.height / 2;
            font.draw(batch, xpLayout, xpTextX, xpTextY);
            batch.setColor(Color.WHITE); // Reset color


            // --- Top-Left UI: HP, Ammo, Kills, Time (Using regular 'font') ---
            float currentSideUIY = screenHeight - UI_TOP_MARGIN;

            font.draw(batch, "HP: " + player.getCurrentHp() + "/" + player.getCharacterData().getHp(), UI_SIDE_MARGIN, currentSideUIY);
            currentSideUIY -= UI_TEXT_LINE_HEIGHT;

            font.draw(batch, "Ammo: " + gun.getCurrentAmmo() + "/" + gun.getGunData().getMax_ammo(), UI_SIDE_MARGIN, currentSideUIY);
            currentSideUIY -= UI_TEXT_LINE_HEIGHT;

            if (gun.isReloading()) {
                font.draw(batch, "RELOADING...", UI_SIDE_MARGIN, currentSideUIY);
                currentSideUIY -= UI_TEXT_LINE_HEIGHT; // Consume space if reloading text is shown
            }
            // If not reloading, the space for reloading text is skipped, so Kills/Time are higher.
            // If consistent positioning is desired regardless of reloading, always decrement currentSideUIY here.


            font.draw(batch, "Kills: " + enemyController.getEnemiesKilled(), UI_SIDE_MARGIN, currentSideUIY);
            currentSideUIY -= UI_TEXT_LINE_HEIGHT;

            font.draw(batch, String.format("Time: %02d:%02d", (int)(gameElapsedTimeSeconds / 60), (int)(gameElapsedTimeSeconds % 60)),
                UI_SIDE_MARGIN, currentSideUIY);
        }
        batch.end();

        if (!gameOver && player != null && enemyController != null &&
            (!player.isAlive() || (gameDurationMinutes > 0 && gameElapsedTimeSeconds >= gameDurationMinutes * 60))) {
            gameOver = true;
            Gdx.app.log("GameScreen", "Game Over! Player alive: " + player.isAlive() + ", Time up: " + (gameDurationMinutes > 0 && gameElapsedTimeSeconds >= gameDurationMinutes * 60));

            int finalScoreValue = 0;
            int kills = enemyController.getEnemiesKilled();
            float timeAlive = gameElapsedTimeSeconds;

            if (App.getInstance().getCurrentUser() != null) {
                User currentUser = App.getInstance().getCurrentUser();
                int sessionScore = (int)(timeAlive * kills * 0.1f) + kills * 10;
                currentUser.setScore(currentUser.getScore() + sessionScore);
                currentUser.setTotalKill(currentUser.getTotalKill() + kills);
                if ((int)timeAlive > currentUser.getMaximumTimeAlive()) {
                    currentUser.setMaximumTimeAlive((int)timeAlive);
                }
                App.getInstance().saveUsers();
                finalScoreValue = currentUser.getScore();
            } else {
                finalScoreValue = (int)(timeAlive * kills * 0.1f) + kills * 10;
            }

            final int finalScoreForScreen = finalScoreValue;
            Gdx.app.postRunnable(() -> Main.getMain().setScreen(new view.GameOverScreen(username, timeAlive, kills, finalScoreForScreen)));
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0) {
            Gdx.app.log("GameView.resize", "Resize called with zero width or height. Skipping camera update.");
            return;
        }
        if (gameCamera != null) {
            gameCamera.setToOrtho(false, width, height);
            gameCamera.update();
        }
        if (uiProjectionMatrix != null) {
            uiProjectionMatrix.setToOrtho2D(0, 0, width, height);
        }
    }

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
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (uiFont != null) { // Dispose the new uiFont
            uiFont.dispose();
            uiFont = null;
        }

        if (bulletTextureRegion != null && bulletTextureRegion.getTexture() != null) {
            Texture tex = bulletTextureRegion.getTexture();
            if (tex.getWidth() == 1 && tex.getHeight() == 1) {
                boolean isManaged = false;
                if (GameAssetManager.getGameAssetManager() != null && GameAssetManager.getGameAssetManager().getAssetManager() != null) {
                    isManaged = GameAssetManager.getGameAssetManager().getAssetManager().isLoaded(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH) &&
                        GameAssetManager.getGameAssetManager().getAssetManager().get(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH, Texture.class) == tex;
                }
                if (!isManaged) {
                    Gdx.app.log("GameView.dispose", "Disposing 1x1 placeholder bullet texture.");
                    tex.dispose();
                }
            }
            bulletTextureRegion = null;
        }

        if (backgroundTexture != null) {
            boolean isManaged = false;
            if (GameAssetManager.getGameAssetManager() != null && GameAssetManager.getGameAssetManager().getAssetManager() != null) {
                isManaged = (GameAssetManager.getGameAssetManager().getAssetManager().isLoaded("backgrounds/game_background.jpg") &&
                    GameAssetManager.getGameAssetManager().getAssetManager().get("backgrounds/game_background.jpg", Texture.class) == backgroundTexture) ||
                    (GameAssetManager.getGameAssetManager().getAssetManager().isLoaded("backgrounds/default_background.png") &&
                        GameAssetManager.getGameAssetManager().getAssetManager().get("backgrounds/default_background.png", Texture.class) == backgroundTexture);
            }
            if (!isManaged && backgroundTexture.getTextureData() != null && !backgroundTexture.getTextureData().isManaged()) {
//                Gdx.app.log("GameView.dispose", "Disposing background texture directly (path: " + (backgroundTexture.getTextureData().getFilePath() != null ? backgroundTexture.getTextureData().getFilePath() : "unknown") + ")");
                backgroundTexture.dispose();
            } else if (!isManaged && backgroundTexture.getTextureData() == null) { // For textures created with new Texture(1,1,Pixmap.Format)
                Gdx.app.log("GameView.dispose", "Disposing non-managed, non-file-backed background texture.");
                backgroundTexture.dispose();
            }
            backgroundTexture = null;
        }
    }

    private class GameInputProcessor implements com.badlogic.gdx.InputProcessor {
        private Vector3 unprojectVec = new Vector3();

        @Override
        public boolean keyDown(int keycode) {
            if (gameOver) return true;
            if (gameSettings == null) {
                Gdx.app.log("GameInputProcessor", "keyDown: gameSettings is null.");
                return false;
            }

            Integer moveUpKey = gameSettings.getKeyBindings().get("Move Up");
            Integer moveDownKey = gameSettings.getKeyBindings().get("Move Down");
            Integer moveLeftKey = gameSettings.getKeyBindings().get("Move Left");
            Integer moveRightKey = gameSettings.getKeyBindings().get("Move Right");
            Integer shootKey = gameSettings.getKeyBindings().get("Shoot");
            Integer reloadKey = gameSettings.getKeyBindings().get("Reload");

            if (moveUpKey != null && keycode == moveUpKey) moveUp = true;
            else if (moveDownKey != null && keycode == moveDownKey) moveDown = true;
            else if (moveLeftKey != null && keycode == moveLeftKey) moveLeft = true;
            else if (moveRightKey != null && keycode == moveRightKey) moveRight = true;
            else if (shootKey != null && keycode == shootKey && !(shootKey >= Input.Buttons.LEFT && shootKey <= Input.Buttons.MIDDLE)) {
                if (gameCamera != null) {
                    unprojectVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                    gameCamera.unproject(unprojectVec);
                    attemptShoot(unprojectVec.x, unprojectVec.y);
                }
            } else if (reloadKey != null && keycode == reloadKey) {
                if (gun != null && !gun.isReloading()) gun.startReload();
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (gameOver) return false;
            if (gameSettings == null) {
                Gdx.app.log("GameInputProcessor", "keyUp: gameSettings is null.");
                return false;
            }

            Integer moveUpKey = gameSettings.getKeyBindings().get("Move Up");
            Integer moveDownKey = gameSettings.getKeyBindings().get("Move Down");
            Integer moveLeftKey = gameSettings.getKeyBindings().get("Move Left");
            Integer moveRightKey = gameSettings.getKeyBindings().get("Move Right");

            if (moveUpKey != null && keycode == moveUpKey) moveUp = false;
            else if (moveDownKey != null && keycode == moveDownKey) moveDown = false;
            else if (moveLeftKey != null && keycode == moveLeftKey) moveLeft = false;
            else if (moveRightKey != null && keycode == moveRightKey) moveRight = false;
            return true;
        }

        @Override public boolean keyTyped(char character) { return false; }

        @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (gameOver) return true;
            if (gameSettings == null || gameCamera == null) {
                Gdx.app.log("GameInputProcessor", "touchDown: gameSettings or gameCamera is null.");
                return false;
            }

            Integer shootBinding = gameSettings.getKeyBindings().get("Shoot");
            if (shootBinding != null && button == shootBinding && (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) ) {
                unprojectVec.set(screenX, screenY, 0);
                gameCamera.unproject(unprojectVec);
                attemptShoot(unprojectVec.x, unprojectVec.y);
                return true;
            }
            return false;
        }

        private void attemptShoot(float targetWorldX, float targetWorldY) {
            if (gun == null || gun.getGunData() == null || player == null || player.getIdleAnimation() == null || worldPlayerPosition == null || bulletTextureRegion == null) {
                Gdx.app.log("GameView.attemptShoot", "Cannot shoot, a critical component for shooting is null.");
                return;
            }

            if (!gun.isReloading() && gun.getCurrentAmmo() > 0 && shootTimer >= gun.getGunData().getFire_rate()) {
                gun.shoot();
                shootTimer = 0f;

                Vector2 playerWorldCenter = new Vector2(worldPlayerPosition.x, worldPlayerPosition.y);
                Animation<TextureRegion> playerIdleAnim = player.getIdleAnimation();

                for (int i = 0; i < gun.getGunData().getProjectile(); i++) {
                    Vector2 bulletDirection = new Vector2(targetWorldX - playerWorldCenter.x, targetWorldY - playerWorldCenter.y);
                    if (gun.getGunData().getProjectile() > 1) {
                        float spreadAngle = MathUtils.random(-5f, 5f);
                        bulletDirection.rotateDeg(spreadAngle);
                    }
                    bulletDirection.nor();

                    float playerRadiusEstimate = 0f;
                    TextureRegion frame = playerIdleAnim.getKeyFrame(0, true);
                    if(frame != null) {
                        playerRadiusEstimate = (frame.getRegionWidth() * Player.PLAYER_SCALE_FACTOR / 2) * 0.7f;
                    } else {
                        Gdx.app.log("GameView.attemptShoot", "Player animation frame is null, cannot estimate radius for bullet spawn.");
                    }

                    float bulletStartX = playerWorldCenter.x + bulletDirection.x * (playerRadiusEstimate + 5) - BULLET_DRAW_WIDTH / 2;
                    float bulletStartY = playerWorldCenter.y + bulletDirection.y * (playerRadiusEstimate + 5) - BULLET_DRAW_HEIGHT / 2;

                    bullets.add(new Bullet(bulletStartX, bulletStartY, 500,
                        bulletDirection.x, bulletDirection.y,
                        bulletTextureRegion, BULLET_DRAW_WIDTH, BULLET_DRAW_HEIGHT,
                        gun.getGunData().getDamage()));
                }
            }
        }

        @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
        @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
        @Override public boolean scrolled(float amountX, float amountY) { return false; }
    }
}
