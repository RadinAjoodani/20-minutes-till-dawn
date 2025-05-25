package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.PreGameMenuController;
import model.GameAssetManager;

public class PreGameMenuView implements Screen {
    private Stage stage;
    private final Label menuLabel;
    private final Label chooseHeroLabel;
    private final SelectBox<String> heroSelectBox;
    private final Label chooseGunLabel;
    private final SelectBox<String> gunSelectBox;
    private final Label chooseDurationLabel;
    private final SelectBox<Integer> durationSelectBox;
    private final TextButton backButton;
    private final TextButton playGameButton; // NEW
    private final Table table;
    private final Skin skin;

    public PreGameMenuView(PreGameMenuController controller, Skin skin) {
        this.skin = skin;
        this.menuLabel = new Label("Pre Game Menu!", skin);
        this.chooseHeroLabel = new Label("Choose Your Hero: ", skin);
        this.heroSelectBox = new SelectBox<>(skin);
        this.chooseGunLabel = new Label("Choose Your Gun: ", skin);
        this.gunSelectBox = new SelectBox<>(skin);
        this.chooseDurationLabel = new Label("Game Duration: ", skin);
        this.durationSelectBox = new SelectBox<>(skin);
        this.backButton = new TextButton("Back", skin);
        this.playGameButton = new TextButton("Play Game", skin); // NEW

        this.table = new Table(skin);

        // Populate hero names
        Array<String> heroNames = GameAssetManager.getGameAssetManager().getAllCharacterNames();
        if (heroNames.size > 0) {
            heroSelectBox.setItems(heroNames);
            heroSelectBox.setSelected(heroNames.first());
        } else {
            Gdx.app.error("PreGameMenuView", "No hero names loaded from GameAssetManager!");
            heroSelectBox.setItems("No Heroes Available");
        }

        // Populate gun names
        Array<String> gunNames = GameAssetManager.getGameAssetManager().getAllGunNames();
        if (gunNames.size > 0) {
            gunSelectBox.setItems(gunNames);
            gunSelectBox.setSelected(gunNames.first());
        } else {
            Gdx.app.error("PreGameMenuView", "No gun names loaded from GameAssetManager!");
            gunSelectBox.setItems("No Guns Available");
        }

        // Populate duration options
        Array<Integer> durations = new Array<>();
        durations.add(2);
        durations.add(5);
        durations.add(10);
        durations.add(20);
        durationSelectBox.setItems(durations);
        durationSelectBox.setSelected(5); // Default to 5 minutes

        controller.setView(this);
    }

    @Override public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.clear();
        table.setFillParent(true);
        table.center();

        table.add(menuLabel).colspan(3).padBottom(30).row();

        table.add(chooseHeroLabel).padRight(10).left();
        table.add(heroSelectBox).width(200).colspan(2).row();

        table.add(chooseGunLabel).padRight(10).left().padTop(15);
        table.add(gunSelectBox).width(200).colspan(2).row();

        table.add(chooseDurationLabel).padRight(10).left().padTop(15);
        table.add(durationSelectBox).width(200).colspan(2).row();

        // NEW: Add Play Game button
        table.add(playGameButton).width(150).height(40).padTop(30).colspan(3).row();
        table.add(backButton).width(100).height(40).padTop(10).colspan(3).row();

        stage.addActor(table);
        table.pack();
        table.setPosition(stage.getWidth() / 2 - table.getWidth() / 2,
            stage.getHeight() / 2 - table.getHeight() / 2);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        table.pack();
        table.setPosition(stage.getWidth() / 2 - table.getWidth() / 2,
            stage.getHeight() / 2 - table.getHeight() / 2);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        dispose();
    }
    @Override public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }

    public TextButton getBackButton() { return backButton; }
    public TextButton getPlayGameButton() { return playGameButton; } // NEW
    public SelectBox<String> getHeroSelectBox() { return heroSelectBox; }
    public SelectBox<String> getGunSelectBox() { return gunSelectBox; }
    public SelectBox<Integer> getDurationSelectBox() { return durationSelectBox; }
}
