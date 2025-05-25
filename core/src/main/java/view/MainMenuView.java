package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.MainMenuController;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.GameSettings;
import model.User;

import java.util.Random;

public class MainMenuView implements Screen {
    private Stage stage;
    private final TextButton continueGameButton;
    private final TextButton preGameMenuButton;
    private final TextButton hintMenuButton;
    private final TextButton scoreboardMenuButton;
    private final TextButton settingMenuButton;
    private final TextButton profileMenuButton;
    private final TextButton logoutButton;
    private final Label welcomeLabel;
    private final Label scoreLabel;
    private final Image avatarImage;
    private Image backgroundImage;
    private final Skin skin;
    private final MainMenuController controller;

    public MainMenuView(MainMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;

        this.continueGameButton = new TextButton("Continue Game", skin);
        this.preGameMenuButton = new TextButton("New Game", skin);
        this.hintMenuButton = new TextButton("Hint Menu", skin);
        this.scoreboardMenuButton = new TextButton("Scoreboard", skin);
        this.settingMenuButton = new TextButton("Settings", skin);
        this.profileMenuButton = new TextButton("Profile", skin);
        this.logoutButton = new TextButton("Logout", skin);

        this.welcomeLabel = new Label("", skin);
        this.scoreLabel = new Label("", skin);

        this.avatarImage = new Image(new TextureRegionDrawable(
            new TextureRegion(GameAssetManager.getGameAssetManager().getTexture(
                GameAssetManager.getGameAssetManager().DEFAULT_AVATAR_PATH
            ))
        ));

        this.controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        initializeMusic();
        setupBackground();
        setupUI();
    }

    private void initializeMusic() {
        GameSettings settings = App.getInstance().getGameSettings();
        Array<String> availableTracks = GameSettings.getAvailableTracks();

        if (settings.getMusic() != null && settings.getMusic().isPlaying()) {

            settings.getMusic().setVolume(settings.getMusicVolume());
            System.out.println("Continuing current music: " + settings.getCurrentMusicTrackName());
        }
        else {

            if (settings.getCurrentMusicTrackName() != null &&
                availableTracks.contains(settings.getCurrentMusicTrackName(), false)) {

                settings.playMusicTrack(settings.getCurrentMusicTrackName());
                System.out.println("Resuming previous track: " + settings.getCurrentMusicTrackName());
            }
            else if (availableTracks.size > 0) {

                Random random = new Random();
                String randomTrack = availableTracks.get(random.nextInt(availableTracks.size));
                settings.playMusicTrack(randomTrack);
                System.out.println("Playing random track: " + randomTrack);
            }
            else {
                System.out.println("No music tracks available to play.");
            }
        }
    }

    private void setupBackground() {
        Texture backgroundTexture = GameAssetManager.getGameAssetManager()
            .getTexture(GameAssetManager.getGameAssetManager()
                .getRandomBackgroundPath());

        if (backgroundTexture != null) {
            backgroundImage = new Image(backgroundTexture);
            backgroundImage.setFillParent(true);
            stage.addActor(backgroundImage);
        }
    }

    private void setupUI() {
        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();

        User currentUser = App.getInstance().getCurrentUser();
        updateUserInfo(currentUser);

        Table userInfoTable = new Table(skin);
        userInfoTable.add(avatarImage).size(100, 100).pad(10).left();
        userInfoTable.add(welcomeLabel).pad(10).left().row();
        userInfoTable.add(scoreLabel).pad(10).left().colspan(2).row();

        table.add(userInfoTable).padBottom(30).row();
        table.add(continueGameButton).width(500).height(80).padBottom(15).row();
        table.add(preGameMenuButton).width(500).height(80).padBottom(15).row();
        table.add(hintMenuButton).width(500).height(80).padBottom(15).row();
        table.add(scoreboardMenuButton).width(500).height(80).padBottom(15).row();
        table.add(settingMenuButton).width(500).height(80).padBottom(15).row();
        table.add(profileMenuButton).width(500).height(80).padBottom(15).row();
        table.add(logoutButton).width(500).height(80).row();

        stage.addActor(table);
        table.pack();
    }

    private void updateUserInfo(User currentUser) {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            scoreLabel.setText("Score: " + currentUser.getScore());

            Texture userAvatarTexture = currentUser.getAvatarTexture();
            if (userAvatarTexture != null) {
                avatarImage.setDrawable(new TextureRegionDrawable(new TextureRegion(userAvatarTexture)));
            } else {
                setDefaultAvatar();
                System.err.println("User avatar texture not found for " + currentUser.getUsername() + ". Displaying default.");
            }
        } else {
            welcomeLabel.setText("Welcome, Guest!");
            scoreLabel.setText("Score: N/A");
            setDefaultAvatar();
        }
    }

    private void setDefaultAvatar() {
        avatarImage.setDrawable(new TextureRegionDrawable(
            new TextureRegion(GameAssetManager.getGameAssetManager().getTexture(
                GameAssetManager.getGameAssetManager().DEFAULT_AVATAR_PATH
            ))
        ));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }


    public TextButton getContinueGameButton() {
        return continueGameButton;
    }

    public TextButton getPreGameMenuButton() {
        return preGameMenuButton;
    }

    public TextButton getHintMenuButton() {
        return hintMenuButton;
    }

    public TextButton getScoreboardMenuButton() {
        return scoreboardMenuButton;
    }

    public TextButton getSettingMenuButton() {
        return settingMenuButton;
    }

    public TextButton getProfileMenuButton() {
        return profileMenuButton;
    }

    public TextButton getLogoutButton() {
        return logoutButton;
    }

    public Label getWelcomeLabel() {
        return welcomeLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public Image getAvatarImage() {
        return avatarImage;
    }
}
