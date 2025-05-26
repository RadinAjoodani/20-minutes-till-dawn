package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.PauseMenuController;
import model.GameAssetManager;

public class PauseMenuView implements Screen {
    private Stage stage;
    private Skin skin;
    private PauseMenuController controller;

    private Label titleLabel;
    private TextButton resumeButton;
    private TextButton giveUpButton; // "Give Up" and exit to Main Menu
    private Image backgroundImage;

    public PauseMenuView(PauseMenuController controller) {
        this.controller = controller;
        this.skin = GameAssetManager.getGameAssetManager().getSkin(); // Get skin from asset manager
        if (this.skin == null) {
            Gdx.app.error("PauseMenuView", "Skin is null. UI might not render correctly.");
            // Fallback or throw error
            this.skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json")); // Or your default skin path
        }
        this.controller.setViewAndAttachListeners(this); // Set this view in the controller
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Background (optional, could be a semi-transparent overlay or game screen snapshot)
        Texture backgroundTex = GameAssetManager.getGameAssetManager().getTexture(GameAssetManager.getGameAssetManager().getRandomBackgroundPath());
        if (backgroundTex != null) {
            backgroundImage = new Image(backgroundTex);
            backgroundImage.setFillParent(true);
            stage.addActor(backgroundImage);
        }


        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();

        titleLabel = new Label("Game Paused", skin, "title"); // Assuming "title" style exists in skin
        if (skin.has("title", Label.LabelStyle.class)) {
            titleLabel.setAlignment(Align.center);
        } else {
            Gdx.app.log("PauseMenuView", "'title' LabelStyle not found in skin. Using default.");
            titleLabel = new Label("Game Paused", skin); // Fallback to default style
            titleLabel.setAlignment(Align.center);
        }


        resumeButton = new TextButton("Resume Game", skin);
        giveUpButton = new TextButton("Give Up (Main Menu)", skin);

        table.add(titleLabel).padBottom(50).colspan(1).row();
        table.add(resumeButton).width(300).height(60).padBottom(20).row();
        table.add(giveUpButton).width(300).height(60).row();

        stage.addActor(table);

        // Controller listeners are attached via controller.setView(this) in constructor
        // If controller needs re-initialization or listeners re-attached:
        if (controller != null) {
            controller.setViewAndAttachListeners(this); // Ensure controller has current view instance
        } else {
            Gdx.app.error("PauseMenuView", "Controller is null in show(). Listeners may not be active.");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1); // Dark background for pause
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {
        // This screen itself doesn't need to do much on pause
    }

    @Override
    public void resume() {
        // This screen itself doesn't need to do much on resume
    }

    @Override
    public void hide() {
        // Gdx.input.setInputProcessor(null); // Clear input processor when hiding
        // No need to dispose stage here if we might come back to it,
        // but if "Give Up" always creates a new MainMenu, then it's fine.
        // For now, let GameView handle restoring its input processor.
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        // Skin is managed by GameAssetManager, so don't dispose it here.
    }

    public TextButton getResumeButton() {
        return resumeButton;
    }

    public TextButton getGiveUpButton() {
        return giveUpButton;
    }


}
