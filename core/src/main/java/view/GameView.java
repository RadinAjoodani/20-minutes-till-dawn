package view;

import com.badlogic.gdx.Gdx;
<<<<<<< HEAD
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import graphic.source.Main;
import model.Ability;
import model.App;
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
import model.GameAssetManager;
import model.GameSettings;
import model.Player;
import model.Gun;
import model.Bullet;
<<<<<<< HEAD
import model.User;
import controller.EnemyController;
import controller.PauseMenuController;
import view.GameOverScreen.GameResult;


=======
import model.Seed;
import controller.EnemyController;
// import com.badlogic.gdx.Game; // No longer needed if using Main.getMain().setScreen

/**
 * The main game screen where gameplay logic and rendering occur.
 */
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
public class GameView implements Screen {
    private final Player player;
    private final Gun gun;
    private final int gameDurationMinutes;
    private final GameSettings gameSettings;
<<<<<<< HEAD
    private final String username;

    private SpriteBatch batch;
    private Texture backgroundTexture;
    private String loadedBackgroundPath;
    private TextureRegion bulletTextureRegion;

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
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

    private EnemyController enemyController;

    private BitmapFont font;
<<<<<<< HEAD
    private BitmapFont uiFont;
    private boolean gameOver;

    private enum GamePlayState { PLAYING, USER_PAUSED, ABILITY_SELECTION, ENDING }
    private GamePlayState currentGameState;
    private long pauseStartTimeNanos;

    private static final float UI_TOP_MARGIN = 15f;
    private static final float UI_ELEMENT_SPACING = 8f;
    private static final float XP_BAR_WIDTH_TOP = 400f;
    private static final float XP_BAR_HEIGHT_TOP = 25f;
    private static final float UI_SIDE_MARGIN = 10f;
    private static final float UI_TEXT_LINE_HEIGHT = 25f;

    private Matrix4 uiProjectionMatrix;

    private GameInputProcessor gameInputProcessor;
    private Stage uiStage;
    private TextButton pauseButton;
    private InputMultiplexer inputMultiplexer;

    private Stage abilitySelectionStage;
    private Table abilityTable;
    private Array<Ability> offeredAbilities;

    private ShaderProgram grayscaleShader;


=======
    private boolean gameOver;

    // NEW: Constants for XP bar drawing
    private static final float XP_BAR_WIDTH = 150f;
    private static final float XP_BAR_HEIGHT = 15f;
    private static final float UI_MARGIN_LEFT = 10f;
    private static final float UI_LINE_HEIGHT = 30f; // Vertical spacing for UI elements

    // Modified constructor to accept the main Game instance and username
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public GameView(Player player, Gun gun, int gameDurationMinutes, GameSettings gameSettings, String username) {
        this.player = player;
        this.gun = gun;
        this.gameDurationMinutes = gameDurationMinutes;
        this.gameSettings = gameSettings;
<<<<<<< HEAD
        this.username = username;
        this.gameOver = false;
        this.currentGameState = GamePlayState.PLAYING;
        this.pauseStartTimeNanos = 0;
        this.offeredAbilities = new Array<>(3);
=======
        this.username = username; // Store the username

        this.playerCenter = new Vector2();
        this.gameOver = false;
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        bullets = new Array<>();
<<<<<<< HEAD

        if ((currentGameState == GamePlayState.USER_PAUSED || currentGameState == GamePlayState.ABILITY_SELECTION) && pauseStartTimeNanos > 0) {
            long pauseDurationNanos = TimeUtils.nanoTime() - pauseStartTimeNanos;
            gameStartTime += pauseDurationNanos;
            pauseStartTimeNanos = 0;
            Gdx.app.log("GameView", "Resumed. Adjusted gameStartTime for pause duration: " + pauseDurationNanos + "ns.");
        } else if (currentGameState != GamePlayState.ABILITY_SELECTION) {
            stateTime = 0f;
            shootTimer = 0f;
            gameStartTime = TimeUtils.nanoTime();
            gameElapsedTimeSeconds = 0f;
            if (player != null) player.resetLevelUpFlag();
        }
        currentGameState = GamePlayState.PLAYING;
=======
        stateTime = 0f;
        shootTimer = 0f;
        gameStartTime = TimeUtils.nanoTime();
        gameElapsedTimeSeconds = 0f; // Initialize elapsed time
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

        font = new BitmapFont();
        font.setColor(Color.WHITE);

<<<<<<< HEAD
        uiFont = new BitmapFont();
        uiFont.setColor(Color.WHITE);
        uiFont.getData().setScale(1.1f);

        float GdxWidth = Gdx.graphics.getWidth();
        float GdxHeight = Gdx.graphics.getHeight();

        if (GdxWidth == 0 || GdxHeight == 0) { Gdx.app.error("GameView.show", "Gdx.graphics.getWidth() or getHeight() is 0."); return; }

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, GdxWidth, GdxHeight);
        uiProjectionMatrix = new Matrix4().setToOrtho2D(0, 0, GdxWidth, GdxHeight);



        try {


            String vertexShader = Gdx.files.internal("shaders/grayscale.vert").readString();
            String fragmentShader = Gdx.files.internal("shaders/grayscale.frag").readString();
            grayscaleShader = new ShaderProgram(vertexShader, fragmentShader);

            if (!grayscaleShader.isCompiled()) {
                Gdx.app.error("ShaderError", "Grayscale shader failed to compile:\n" + grayscaleShader.getLog());
                if (grayscaleShader != null) grayscaleShader.dispose();
                grayscaleShader = null;
            } else {
                Gdx.app.log("ShaderInfo", "Grayscale shader compiled successfully.");
            }
        } catch (Exception e) {
            Gdx.app.error("ShaderError", "Exception loading/compiling grayscale shader.", e);
            if (grayscaleShader != null) grayscaleShader.dispose();
            grayscaleShader = null;
        }


        if (uiStage == null) uiStage = new Stage(new ScreenViewport(), batch);
        else uiStage.getViewport().update((int)GdxWidth, (int)GdxHeight, true);
        uiStage.clear();

        if (abilitySelectionStage == null) abilitySelectionStage = new Stage(new ScreenViewport(), batch);
        else abilitySelectionStage.getViewport().update((int)GdxWidth, (int)GdxHeight, true);
        abilitySelectionStage.clear();

        Skin skin = GameAssetManager.getGameAssetManager().getSkin();
        if (skin == null) { Gdx.app.error("GameView.show", "Skin is null."); skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json")); }

        pauseButton = new TextButton("Pause", skin);
        pauseButton.setSize(100, 50);
        pauseButton.setPosition(GdxWidth - pauseButton.getWidth() - UI_SIDE_MARGIN, GdxHeight - pauseButton.getHeight() - UI_TOP_MARGIN);
        pauseButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentGameState == GamePlayState.PLAYING && !gameOver) togglePause();
            }
        });
        uiStage.addActor(pauseButton);

        loadedBackgroundPath = "backgrounds/game_background.jpg";
        try { backgroundTexture = GameAssetManager.getGameAssetManager().getTexture(loadedBackgroundPath);
            if (backgroundTexture != null) Gdx.app.log("GameView.show", "Loaded: " + loadedBackgroundPath); }
        catch (Exception e) { Gdx.app.error("GameScreen", "Exception getting '" + loadedBackgroundPath + "'.", e); backgroundTexture = null; }
        if (backgroundTexture == null) {
            Gdx.app.log("GameScreen", "Failed specific bg. Fallback...");
            loadedBackgroundPath = GameAssetManager.getGameAssetManager().getRandomBackgroundPath();
            if (loadedBackgroundPath != null) {
                try { backgroundTexture = GameAssetManager.getGameAssetManager().getTexture(loadedBackgroundPath); }
                catch (Exception e) { Gdx.app.error("GameScreen", "Failed random bg: " + loadedBackgroundPath, e); backgroundTexture = null; }
            }
            if (backgroundTexture == null) {
                loadedBackgroundPath = "backgrounds/default_background.png";
                try { backgroundTexture = new Texture(Gdx.files.internal(loadedBackgroundPath)); }
                catch (Exception e) { Gdx.app.error("GameScreen", "CRITICAL: Failed default_background.png.", e); loadedBackgroundPath = null;}
            }
        }
        if (backgroundTexture != null) backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        else Gdx.app.error("GameScreen", "CRITICAL: No background texture loaded.");

        Texture bulletTex = GameAssetManager.getGameAssetManager().getTexture(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH);
        if (bulletTex != null) bulletTextureRegion = new TextureRegion(bulletTex);
        else bulletTextureRegion = new TextureRegion(new Texture(1,1,com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));

        if (worldPlayerPosition == null) worldPlayerPosition = new Vector2(0, 0);
        if (player != null) { player.setX(worldPlayerPosition.x); player.setY(worldPlayerPosition.y); }
        else { Gdx.app.error("GameView.show","Player is null!"); return; }

        if (enemyController == null && GameAssetManager.getGameAssetManager() != null && player != null) {
            enemyController = new EnemyController(GameAssetManager.getGameAssetManager(), player, gameDurationMinutes);
        } else if (enemyController == null) { Gdx.app.error("GameView.show","EnemyController is null!"); return; }

        gameInputProcessor = new GameInputProcessor();
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(abilitySelectionStage);
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(gameInputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void togglePause() {
        if (currentGameState == GamePlayState.PLAYING) {
            currentGameState = GamePlayState.USER_PAUSED;
            pauseStartTimeNanos = TimeUtils.nanoTime();
            Main.getMain().setScreen(new PauseMenuView(new PauseMenuController(this)));
        }
    }
    private void enterAbilitySelection() {
        if (currentGameState != GamePlayState.ABILITY_SELECTION) {
            currentGameState = GamePlayState.ABILITY_SELECTION;
            pauseStartTimeNanos = TimeUtils.nanoTime();
            prepareAbilitySelectionUI();
            Gdx.input.setInputProcessor(inputMultiplexer);
        }
    }
    private void prepareAbilitySelectionUI() {
        abilitySelectionStage.clear();
        offeredAbilities.clear();
        Skin skin = GameAssetManager.getGameAssetManager().getSkin();
        if (skin == null) skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        if (abilityTable == null) abilityTable = new Table(skin); else abilityTable.clear();
        abilityTable.setFillParent(true); abilityTable.center();
        Label title = new Label("Level Up! Choose an Ability:", skin, "title");
        abilityTable.add(title).colspan(3).padBottom(30).row();
        Array<Ability> allAbilities = new Array<>(Ability.values());
        allAbilities.shuffle();
        for (int i = 0; i < Math.min(3, allAbilities.size); i++) offeredAbilities.add(allAbilities.get(i));
        for (final Ability ability : offeredAbilities) {
            TextButton abilityButton = new TextButton(ability.getDisplayName() + "\n" + ability.getDescription(), skin);
            abilityButton.getLabel().setWrap(true); abilityButton.getLabel().setAlignment(Align.center);
            abilityButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { applyAbility(ability); exitAbilitySelection(); }
            });
            abilityTable.add(abilityButton).width(Gdx.graphics.getWidth() / 3.5f).height(120f).pad(10);
        }
        abilitySelectionStage.addActor(abilityTable);
    }
    private void applyAbility(Ability ability) {
        if (player == null || gun == null) return;
        switch (ability) {
            case VITALITY: player.increaseMaxHealth(1); break;
            case DAMAGER: gun.applyDamageBuff(0.25f, 10f); break;
            case PROCREASE: gun.increaseProjectiles(1); break;
            case AMOCREASE: gun.increaseMaxAmmo(5); break;
            case SPEEDY: player.applySpeedBuff(2f, 10f); break;
        }
    }
    private void exitAbilitySelection() {
        if (currentGameState == GamePlayState.ABILITY_SELECTION) {
            long pauseDurationNanos = TimeUtils.nanoTime() - pauseStartTimeNanos;
            gameStartTime += pauseDurationNanos; pauseStartTimeNanos = 0;
            currentGameState = GamePlayState.PLAYING;
            if(player != null) player.resetLevelUpFlag();
            abilitySelectionStage.clear();
            Gdx.input.setInputProcessor(inputMultiplexer);
        }
    }
    public void triggerGiveUp() {
        if (!gameOver) {
            endGameAndShowScreen(GameResult.GAVE_UP);
        } else {
            Gdx.app.log("GameView", "triggerGiveUp called but game is already over or ending.");
        }
    }
    private void endGameAndShowScreen(GameResult result) {
        if (this.currentGameState == GamePlayState.ENDING && this.gameOver) {
            Gdx.app.log("GameView", "endGameAndShowScreen called again while already ending. Ignoring. Result: " + result.name());
            return;
        }

        this.gameOver = true;
        this.currentGameState = GamePlayState.ENDING;
        Gdx.app.log("GameView", "Ending game with result: " + result.name() + ". Saving stats.");

        int finalScoreValue = 0;
        int kills = (enemyController != null) ? enemyController.getEnemiesKilled() : 0;
        float timeAlive = gameElapsedTimeSeconds;

        User currentUser = App.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.setTotalKill(currentUser.getTotalKill() + kills);
            if ((int)timeAlive > currentUser.getMaximumTimeAlive()) {
                currentUser.setMaximumTimeAlive((int)timeAlive);
            }
            if (result == GameResult.WIN || result == GameResult.DIED) {
                int sessionScore = (int)(timeAlive * kills * 0.1f) + kills * 10;
                currentUser.setScore(currentUser.getScore() + sessionScore);
            }
            App.getInstance().saveUsers();
            finalScoreValue = currentUser.getScore();
        } else if (player != null) {
            finalScoreValue = (int)(timeAlive * kills * 0.1f) + kills * 10;
        }

        final GameResult finalResultToPass = result;
        final int finalScoreForScreen = finalScoreValue;
        final String playerUsernameToShow = (this.username == null || this.username.trim().isEmpty()) ? "Player" : this.username;

        Gdx.app.postRunnable(() -> {
            Gdx.app.log("GameView", "Executing postRunnable to set GameOverScreen. Current main screen: " + (Main.getMain().getScreen() != null ? Main.getMain().getScreen().getClass().getSimpleName() : "null"));
            Main.getMain().setScreen(new view.GameOverScreen(playerUsernameToShow, timeAlive, kills, finalScoreForScreen, finalResultToPass));
        });
    }


    @Override
    public void render(float delta) {
        if (currentGameState == GamePlayState.USER_PAUSED && Main.getMain().getScreen() != this) return;
        if (player == null || worldPlayerPosition == null || enemyController == null || gameCamera == null || batch == null || uiProjectionMatrix == null || uiStage == null || abilitySelectionStage == null) {
            Gdx.app.error("GameView.render", "Critical component is null, skipping render loop.");
            Gdx.gl.glClearColor(0,0,0,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player.hasJustLeveledUp() && currentGameState == GamePlayState.PLAYING && !gameOver) {
            enterAbilitySelection();
        }

        if (!gameOver && player != null) {
            player.update(delta);
        }

        if (currentGameState == GamePlayState.PLAYING && !gameOver) {
            stateTime += delta;
            shootTimer += delta;
            gameElapsedTimeSeconds = (TimeUtils.nanoTime() - gameStartTime) / 1_000_000_000.0f;
            if (gun != null) gun.updateBuffs(delta);
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
            player.setX(worldPlayerPosition.x); player.setY(worldPlayerPosition.y);
            enemyController.update(delta, gameElapsedTimeSeconds);
            enemyController.checkBulletCollisions(bullets);
        } else if (currentGameState == GamePlayState.ABILITY_SELECTION || currentGameState == GamePlayState.USER_PAUSED) {
            stateTime += delta;
        }

        gameCamera.position.x = worldPlayerPosition.x;
        gameCamera.position.y = worldPlayerPosition.y;
        gameCamera.update();


        if (gameSettings != null && gameSettings.isBlackAndWhiteModeEnabled() && grayscaleShader != null && grayscaleShader.isCompiled()) {
            batch.setShader(grayscaleShader);
        } else {
            batch.setShader(null);
        }

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();

        if (backgroundTexture != null) {
            float cameraWorldLeft = gameCamera.position.x - gameCamera.viewportWidth / 2;
            float cameraWorldBottom = gameCamera.position.y - gameCamera.viewportHeight / 2;
            float u = cameraWorldLeft / backgroundTexture.getWidth(); float v = cameraWorldBottom / backgroundTexture.getHeight();
            float u2 = (cameraWorldLeft + gameCamera.viewportWidth) / backgroundTexture.getWidth();
            float v2 = (cameraWorldBottom + gameCamera.viewportHeight) / backgroundTexture.getHeight();
            batch.draw(backgroundTexture, cameraWorldLeft, cameraWorldBottom,
                gameCamera.viewportWidth, gameCamera.viewportHeight, u, v, u2, v2);
        }

        TextureRegion playerCurrentFrameToDraw = player.getCurrentFrame(stateTime);
        if (playerCurrentFrameToDraw != null) {
            float playerScaledWidth = playerCurrentFrameToDraw.getRegionWidth() * Player.PLAYER_SCALE_FACTOR;
            float playerScaledHeight = playerCurrentFrameToDraw.getRegionHeight() * Player.PLAYER_SCALE_FACTOR;
            batch.draw(playerCurrentFrameToDraw, worldPlayerPosition.x - playerScaledWidth / 2f,
                worldPlayerPosition.y - playerScaledHeight / 2f, playerScaledWidth, playerScaledHeight);
        }

        if (!gameOver && gun != null && gun.getAnimations() != null) {
            Vector3 mouseScreenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 mouseWorldPos = gameCamera.unproject(new Vector3(mouseScreenPos));
            Vector2 playerWorldCenter = new Vector2(worldPlayerPosition.x, worldPlayerPosition.y);
            Vector2 directionToMouse = new Vector2(mouseWorldPos.x - playerWorldCenter.x, mouseWorldPos.y - playerWorldCenter.y);
            gunRotationAngle = directionToMouse.angleDeg();
            TextureRegion gunCurrentFrame = null;
            Animation<TextureRegion> currentGunAnimation = gun.isReloading() ? gun.getAnimations().get("reload") : gun.getAnimations().get("still");
            if (currentGunAnimation == null) currentGunAnimation = gun.getAnimations().get("idle");
            if (currentGunAnimation != null) gunCurrentFrame = currentGunAnimation.getKeyFrame(gun.isReloading() ? gun.getReloadStateTime() : stateTime, !gun.isReloading());
            if (gunCurrentFrame != null) {
                float gunDrawWidth = gunCurrentFrame.getRegionWidth() * Player.PLAYER_SCALE_FACTOR;
                float gunDrawHeight = gunCurrentFrame.getRegionHeight() * Player.PLAYER_SCALE_FACTOR;
                float gunOriginX = gunDrawWidth * 0.1f; float gunOriginY = gunDrawHeight * 0.5f;
                batch.draw(gunCurrentFrame, playerWorldCenter.x - gunOriginX, playerWorldCenter.y - gunOriginY,
                    gunOriginX, gunOriginY, gunDrawWidth, gunDrawHeight, 1, 1, gunRotationAngle);
            }
        }

        if (currentGameState == GamePlayState.PLAYING && !gameOver) {
            for (int i = bullets.size - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i); bullet.update(delta);
                float cullMargin = 200f;
                float camViewLeft = gameCamera.position.x - gameCamera.viewportWidth / 2f - cullMargin;
                float camViewRight = gameCamera.position.x + gameCamera.viewportWidth / 2f + cullMargin;
                float camViewBottom = gameCamera.position.y - gameCamera.viewportHeight / 2f - cullMargin;
                float camViewTop = gameCamera.position.y + gameCamera.viewportHeight / 2f + cullMargin;
                if (bullet.getX() < camViewLeft || bullet.getX() > camViewRight || bullet.getY() < camViewBottom || bullet.getY() > camViewTop) bullets.removeIndex(i);
                else bullet.draw(batch);
            }
        } else if (!bullets.isEmpty()){ for (Bullet bullet : bullets) bullet.draw(batch); }
        enemyController.draw(batch);
        batch.end();

        batch.setShader(null);

        batch.setProjectionMatrix(uiProjectionMatrix);
        batch.begin();
        if (uiFont != null && player != null && player.getCharacterData() != null && gun != null && gun.getGunData() != null && enemyController != null && bulletTextureRegion != null) {
            float screenWidth = Gdx.graphics.getWidth(); float screenHeight = Gdx.graphics.getHeight();
            String levelText = "Level: " + player.getLevel();
            GlyphLayout levelLayout = new GlyphLayout(uiFont, levelText);
            float levelTextX = (screenWidth - levelLayout.width) / 2; float levelTextY = screenHeight - UI_TOP_MARGIN;
            uiFont.draw(batch, levelLayout, levelTextX, levelTextY);
            float xpBarY = levelTextY - levelLayout.height - UI_ELEMENT_SPACING; float xpBarX = (screenWidth - XP_BAR_WIDTH_TOP) / 2;
            batch.setColor(Color.DARK_GRAY); batch.draw(bulletTextureRegion, xpBarX, xpBarY - XP_BAR_HEIGHT_TOP, XP_BAR_WIDTH_TOP, XP_BAR_HEIGHT_TOP);
            float xpProgress = 0; if (player.getXpToNextLevel() > 0) xpProgress = (float) player.getXp() / player.getXpToNextLevel();
            xpProgress = MathUtils.clamp(xpProgress, 0f, 1f); batch.setColor(Color.LIME);
            batch.draw(bulletTextureRegion, xpBarX, xpBarY - XP_BAR_HEIGHT_TOP, XP_BAR_WIDTH_TOP * xpProgress, XP_BAR_HEIGHT_TOP);
            String xpText = String.format("%d / %d XP", player.getXp(), player.getXpToNextLevel());
            GlyphLayout xpLayout = new GlyphLayout(font, xpText);
            float xpTextX = xpBarX + (XP_BAR_WIDTH_TOP - xpLayout.width) / 2; float xpTextY = xpBarY - XP_BAR_HEIGHT_TOP / 2 + xpLayout.height / 2;
            font.draw(batch, xpLayout, xpTextX, xpTextY); batch.setColor(Color.WHITE);
            float currentSideUIY = screenHeight - UI_TOP_MARGIN;
            font.draw(batch, "HP: " + player.getCurrentHp() + "/" + player.getMaxHp(), UI_SIDE_MARGIN, currentSideUIY); currentSideUIY -= UI_TEXT_LINE_HEIGHT;
            font.draw(batch, "Ammo: " + gun.getCurrentAmmo() + "/" + gun.getCurrentMaxAmmo(), UI_SIDE_MARGIN, currentSideUIY); currentSideUIY -= UI_TEXT_LINE_HEIGHT;
            if (gun.isReloading()) { font.draw(batch, "RELOADING...", UI_SIDE_MARGIN, currentSideUIY); currentSideUIY -= UI_TEXT_LINE_HEIGHT; }
            font.draw(batch, "Kills: " + enemyController.getEnemiesKilled(), UI_SIDE_MARGIN, currentSideUIY); currentSideUIY -= UI_TEXT_LINE_HEIGHT;
            font.draw(batch, String.format("Time: %02d:%02d", (int)(gameElapsedTimeSeconds / 60), (int)(gameElapsedTimeSeconds % 60)), UI_SIDE_MARGIN, currentSideUIY);
        }
        batch.end();

        if (!gameOver && currentGameState != GamePlayState.ABILITY_SELECTION && currentGameState != GamePlayState.USER_PAUSED) {
            uiStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            uiStage.draw();
        }
        if (currentGameState == GamePlayState.ABILITY_SELECTION) {
            abilitySelectionStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            abilitySelectionStage.draw();
        }

        if (!gameOver && currentGameState == GamePlayState.PLAYING) {
            boolean timeUp = (gameDurationMinutes > 0 && gameElapsedTimeSeconds >= gameDurationMinutes * 60);
            if (!player.isAlive() || timeUp) {
                GameResult result = player.isAlive() ? GameResult.WIN : GameResult.DIED;
                Gdx.app.log("GameScreen", "Game Over condition met. Result: " + result);
                endGameAndShowScreen(result);
            }
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }
    }

    @Override
<<<<<<< HEAD
    public void resize(int width, int height) {
        if (width == 0 || height == 0) return;
        if (gameCamera != null) { gameCamera.viewportWidth = width; gameCamera.viewportHeight = height; gameCamera.update(); }
        if (uiProjectionMatrix != null) uiProjectionMatrix.setToOrtho2D(0, 0, width, height);
        if (uiStage != null) { uiStage.getViewport().update(width, height, true);
            if (pauseButton != null) pauseButton.setPosition(width - pauseButton.getWidth() - UI_SIDE_MARGIN, height - pauseButton.getHeight() - UI_TOP_MARGIN);
        }
        if (abilitySelectionStage != null) {
            abilitySelectionStage.getViewport().update(width, height, true);
            if (abilityTable != null && abilityTable.getStage() == abilitySelectionStage) {
                abilityTable.invalidateHierarchy();
                abilityTable.pack();
            }
        }
    }
    @Override
    public void pause() {
        if (!gameOver && currentGameState == GamePlayState.PLAYING) {
            currentGameState = GamePlayState.USER_PAUSED;
            pauseStartTimeNanos = TimeUtils.nanoTime();
        }
    }
    @Override
    public void resume() {
        if (inputMultiplexer != null && Gdx.input.getInputProcessor() != inputMultiplexer) Gdx.input.setInputProcessor(inputMultiplexer);
    }
    @Override
    public void hide() {
        if (currentGameState == GamePlayState.PLAYING && !gameOver && pauseStartTimeNanos == 0) {
            currentGameState = GamePlayState.USER_PAUSED;
            pauseStartTimeNanos = TimeUtils.nanoTime();
        }
    }
    @Override
    public void dispose() {
        Gdx.app.log("GameView", "dispose() called.");
        if (batch != null) { batch.dispose(); batch = null; }
        if (font != null) { font.dispose(); font = null; }
        if (uiFont != null) { uiFont.dispose(); uiFont = null; }
        if (uiStage != null) { uiStage.dispose(); uiStage = null; }
        if (abilitySelectionStage != null) { abilitySelectionStage.dispose(); abilitySelectionStage = null; }
        if (grayscaleShader != null) { grayscaleShader.dispose(); grayscaleShader = null; }

        if (bulletTextureRegion != null && bulletTextureRegion.getTexture() != null) {
            Texture tex = bulletTextureRegion.getTexture();
            if (tex.getWidth() == 1 && tex.getHeight() == 1) {
                boolean isManaged = false;
                if (GameAssetManager.getGameAssetManager() != null && GameAssetManager.getGameAssetManager().getAssetManager() != null) {
                    if(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH != null &&
                        GameAssetManager.getGameAssetManager().getAssetManager().isLoaded(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH)){
                        if(GameAssetManager.getGameAssetManager().getAssetManager().get(GameAssetManager.getGameAssetManager().BULLET_TEXTURE_PATH, Texture.class) == tex)
                            isManaged = true;
                    }
                }
                if (!isManaged) tex.dispose();
            }
            bulletTextureRegion = null;
        }
        if (backgroundTexture != null) {
            boolean isManaged = false;
            if (GameAssetManager.getGameAssetManager() != null &&
                GameAssetManager.getGameAssetManager().getAssetManager() != null &&
                loadedBackgroundPath != null && GameAssetManager.getGameAssetManager().getAssetManager().isLoaded(loadedBackgroundPath)) {
                if (GameAssetManager.getGameAssetManager().getAssetManager().get(loadedBackgroundPath, Texture.class) == backgroundTexture)
                    isManaged = true;
            }
            if (!isManaged) backgroundTexture.dispose();
            backgroundTexture = null;
        }
    }

    private class GameInputProcessor implements com.badlogic.gdx.InputProcessor {
        private Vector3 unprojectVec = new Vector3();
        @Override
        public boolean keyDown(int keycode) {
            if (gameOver) return true;
            if (currentGameState == GamePlayState.ABILITY_SELECTION) return true;
            if (gameSettings == null) return false;
            Integer pauseKey = gameSettings.getKeyBindings().get("Pause");
            if (pauseKey != null && keycode == pauseKey) { if (currentGameState == GamePlayState.PLAYING) togglePause(); return true; }

            if(currentGameState == GamePlayState.USER_PAUSED && Main.getMain().getScreen() != GameView.this) return false;

            if (currentGameState == GamePlayState.PLAYING) {
                if (keycode == Input.Keys.NUM_1) {
                    long timeToAdjustNanos = 60 * 1_000_000_000L;
                    gameStartTime -= timeToAdjustNanos;
                    gameElapsedTimeSeconds = (TimeUtils.nanoTime() - gameStartTime) / 1_000_000_000.0f;
                    Gdx.app.log("CHEAT", "Time advanced by 1 minute. New elapsed: " + gameElapsedTimeSeconds); return true;
                } else if (keycode == Input.Keys.NUM_2 && player != null) {
                    player.addXp(player.getXpToNextLevel() - player.getXp() + 1);
                    Gdx.app.log("CHEAT", "Player leveled up. New level: " + player.getLevel()); return true;
                } else if (keycode == Input.Keys.NUM_3 && player != null) {
                    player.increaseMaxHealth(1);
                    Gdx.app.log("CHEAT", "Player HP increased. Current: " + player.getCurrentHp() + "/" + player.getMaxHp()); return true;
                } else if (keycode == Input.Keys.NUM_4 && enemyController != null) { enemyController.spawnBossNow(); return true;
                } else if (keycode == Input.Keys.NUM_5 && enemyController != null) { enemyController.killRandomEnemies(10); return true; }
            }
            Integer moveUpKey = gameSettings.getKeyBindings().get("Move Up");
            Integer moveDownKey = gameSettings.getKeyBindings().get("Move Down");
            Integer moveLeftKey = gameSettings.getKeyBindings().get("Move Left");
            Integer moveRightKey = gameSettings.getKeyBindings().get("Move Right");
            Integer shootKey = gameSettings.getKeyBindings().get("Shoot");
            Integer reloadKey = gameSettings.getKeyBindings().get("Reload");
            if (currentGameState == GamePlayState.PLAYING) {
                if (moveUpKey != null && keycode == moveUpKey) moveUp = true;
                else if (moveDownKey != null && keycode == moveDownKey) moveDown = true;
                else if (moveLeftKey != null && keycode == moveLeftKey) moveLeft = true;
                else if (moveRightKey != null && keycode == moveRightKey) moveRight = true;
                else if (shootKey != null && keycode == shootKey && !(shootKey >= Input.Buttons.LEFT && shootKey <= Input.Buttons.MIDDLE)) {
                    if (gameCamera != null) { unprojectVec.set(Gdx.input.getX(), Gdx.input.getY(), 0); gameCamera.unproject(unprojectVec); attemptShoot(unprojectVec.x, unprojectVec.y); }
                } else if (reloadKey != null && keycode == reloadKey) { if (gun != null && !gun.isReloading()) gun.startReload(); }
            }
            return false;
        }
        @Override public boolean keyUp(int keycode) {
            if (gameOver || currentGameState != GamePlayState.PLAYING ) return false;
            if (gameSettings == null) return false;
            Integer moveUpKey = gameSettings.getKeyBindings().get("Move Up");
            Integer moveDownKey = gameSettings.getKeyBindings().get("Move Down");
            Integer moveLeftKey = gameSettings.getKeyBindings().get("Move Left");
            Integer moveRightKey = gameSettings.getKeyBindings().get("Move Right");
            if (moveUpKey != null && keycode == moveUpKey) moveUp = false;
            else if (moveDownKey != null && keycode == moveDownKey) moveDown = false;
            else if (moveLeftKey != null && keycode == moveLeftKey) moveLeft = false;
            else if (moveRightKey != null && keycode == moveRightKey) moveRight = false;
            return false;
        }
        @Override public boolean keyTyped(char character) { return currentGameState != GamePlayState.PLAYING || gameOver; }
        @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (gameOver || currentGameState != GamePlayState.PLAYING ) return false;
            if (gameSettings == null || gameCamera == null) return false;
            Integer shootBinding = gameSettings.getKeyBindings().get("Shoot");
            if (shootBinding != null && button == shootBinding && (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) ) {
                unprojectVec.set(screenX, screenY, 0); gameCamera.unproject(unprojectVec); attemptShoot(unprojectVec.x, unprojectVec.y); return true;
            } return false;
        }
        private void attemptShoot(float targetWorldX, float targetWorldY) {
            if (currentGameState != GamePlayState.PLAYING || gameOver) return;
            if (gun == null || gun.getGunData() == null || player == null || player.getIdleAnimation() == null || worldPlayerPosition == null || bulletTextureRegion == null) return;
            if (!gun.isReloading() && gun.getCurrentAmmo() > 0 && shootTimer >= gun.getGunData().getFire_rate()) {
                gun.shoot(); shootTimer = 0f; Vector2 playerWorldCenter = new Vector2(worldPlayerPosition.x, worldPlayerPosition.y);
                Animation<TextureRegion> playerIdleAnim = player.getIdleAnimation();
                for (int i = 0; i < gun.getCurrentProjectiles(); i++) {
                    Vector2 bulletDirection = new Vector2(targetWorldX - playerWorldCenter.x, targetWorldY - playerWorldCenter.y);
                    if (gun.getCurrentProjectiles() > 1) {
                        float baseSpreadAngle = 5f; float totalSpread = baseSpreadAngle * (gun.getCurrentProjectiles() -1) ;
                        float angleStep = (gun.getCurrentProjectiles() > 1) ? totalSpread / (gun.getCurrentProjectiles() -1) : 0;
                        float startAngle = -totalSpread / 2f; float currentSpread = startAngle + (i * angleStep);
                        if (gun.getCurrentProjectiles() == 1) currentSpread = 0;
                        bulletDirection.rotateDeg(currentSpread);
                    }
                    bulletDirection.nor(); float playerRadiusEstimate = 0f;
                    TextureRegion frame = playerIdleAnim.getKeyFrame(0, true);
                    if(frame != null) playerRadiusEstimate = (frame.getRegionWidth() * Player.PLAYER_SCALE_FACTOR / 2) * 0.7f;
                    float bulletStartX = playerWorldCenter.x + bulletDirection.x * (playerRadiusEstimate + 5) - BULLET_DRAW_WIDTH / 2;
                    float bulletStartY = playerWorldCenter.y + bulletDirection.y * (playerRadiusEstimate + 5) - BULLET_DRAW_HEIGHT / 2;
                    bullets.add(new Bullet(bulletStartX, bulletStartY, 500, bulletDirection.x, bulletDirection.y, bulletTextureRegion, BULLET_DRAW_WIDTH, BULLET_DRAW_HEIGHT, gun.getCurrentDamage()));
                }
            }
        }
        @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return currentGameState != GamePlayState.PLAYING || gameOver; }
        @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return currentGameState != GamePlayState.PLAYING || gameOver; }
        @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return currentGameState != GamePlayState.PLAYING || gameOver; }
        @Override public boolean mouseMoved(int screenX, int screenY) { return currentGameState != GamePlayState.PLAYING || gameOver; }
        @Override public boolean scrolled(float amountX, float amountY) { return currentGameState != GamePlayState.PLAYING || gameOver; }
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }
}
