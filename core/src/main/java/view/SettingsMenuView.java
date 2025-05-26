
package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.SettingsMenuController;
import model.App;
import model.GameAssetManager;
import model.GameSettings;

public class SettingsMenuView implements Screen {
    private Stage stage;
    private final Skin skin;
    private final SettingsMenuController controller;

    private final Label menuLabel;
    private final Slider musicVolumeSlider;
    private final SelectBox<String> musicTrackSelectBox;
    private final CheckBox sfxCheckBox;
    private final TextButton backButton;


    private final CheckBox autoReloadCheckBox;

    private Image backgroundImage;
    private final Table rootTable;
    private final Table keyBindingsTable;
    private final Array<TextButton> keyBindingButtons;

    public SettingsMenuView(SettingsMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;

        this.menuLabel = new Label("Settings", skin);
        this.musicVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        this.musicVolumeSlider.setValue(App.getInstance().getGameSettings().getMusicVolume());

        this.musicTrackSelectBox = new SelectBox<>(skin);
        Array<String> gdxAvailableTracks = GameSettings.getAvailableTracks();

        if (gdxAvailableTracks != null) {
            this.musicTrackSelectBox.setItems(gdxAvailableTracks);
            String currentTrack = App.getInstance().getGameSettings().getCurrentMusicTrackName();

            if (currentTrack != null && gdxAvailableTracks.contains(currentTrack, false)) {
                this.musicTrackSelectBox.setSelected(currentTrack);
            } else if (gdxAvailableTracks.size > 0) {
                this.musicTrackSelectBox.setSelected(gdxAvailableTracks.first());
            }
        }

        this.sfxCheckBox = new CheckBox("SFX On/Off", skin);
        this.sfxCheckBox.setChecked(App.getInstance().getGameSettings().isSfxEnabled());

        this.autoReloadCheckBox = new CheckBox("Auto-Reload On/Off", skin);
        this.autoReloadCheckBox.setChecked(App.getInstance().getGameSettings().isAutoReloadEnabled());

        this.backButton = new TextButton("Back to Main Menu", skin);
        this.keyBindingsTable = new Table(skin);
        this.rootTable = new Table(skin);
        this.keyBindingButtons = new Array<>();

        this.controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        setupBackground();
        rootTable.setFillParent(true);
        rootTable.center();

        rootTable.add(menuLabel).colspan(2).padBottom(30).row();
        rootTable.add(new Label("Music Volume:", skin)).padRight(10);
        rootTable.add(musicVolumeSlider).width(300).padBottom(15).row();
        rootTable.add(new Label("Music Track:", skin)).padRight(10);
        rootTable.add(musicTrackSelectBox).width(300).padBottom(15).row();
        rootTable.add(new Label("SFX:", skin)).padRight(10);
        rootTable.add(sfxCheckBox).padBottom(15).row();
        rootTable.add(new Label("Auto-Reload:", skin)).padRight(10);
        rootTable.add(autoReloadCheckBox).padBottom(15).row();

        rootTable.add(new Label("Key Bindings:", skin)).colspan(2).padBottom(10).row();

        setupKeyBindingsTable();
        rootTable.add(keyBindingsTable).colspan(2).expandX().fillX().padBottom(40).row();
        rootTable.add(backButton).center().width(500).height(80).padTop(20).row();

        stage.addActor(rootTable);
        rootTable.pack();
        rootTable.setPosition(stage.getWidth() / 2 - rootTable.getWidth() / 2,
            stage.getHeight() / 2 - rootTable.getHeight() / 2);

        controller.attachListeners();
    }

    private void setupKeyBindingsTable() {
        keyBindingsTable.clear();
        keyBindingButtons.clear();
        keyBindingsTable.defaults().pad(5).left();

        GameSettings settings = App.getInstance().getGameSettings();

        for (String action : settings.getKeyBindings().keys()) {
            keyBindingsTable.add(new Label(action + ":", skin)).width(150);
            TextButton keyButton = new TextButton(GameSettings.getKeyName(settings.getKeyBindings().get(action)), skin);
            keyButton.setUserObject(action);
            keyBindingsTable.add(keyButton).width(120).pad(15,5,5,5);
            keyBindingButtons.add(keyButton);
        }
        keyBindingsTable.pack();
    }

    public void updateKeyBindingButton(String action, String newKeyName) {
        for (TextButton button : keyBindingButtons) {

            if (button.getUserObject() != null && button.getUserObject().equals(action)) {
                button.setText(newKeyName);
                break;
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
        if (rootTable != null) {
            rootTable.pack();
            rootTable.setPosition(
                stage.getWidth() / 2 - rootTable.getWidth() / 2,
                stage.getHeight() / 2 - rootTable.getHeight() / 2
            );
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }

    public Slider getMusicVolumeSlider() { return musicVolumeSlider; }
    public SelectBox<String> getMusicTrackSelectBox() { return musicTrackSelectBox; }
    public CheckBox getSfxCheckBox() { return sfxCheckBox; }
    public TextButton getBackButton() { return backButton; }
    public Array<TextButton> getKeyBindingButtons() { return keyBindingButtons; }
    public Stage getStage() { return stage; }
    public CheckBox getAutoReloadCheckBox() { return autoReloadCheckBox; }
}
