package view;

<<<<<<< HEAD
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.PauseMenuController;
import model.Ability;
import model.App;
import model.CharacterData;
import model.GameAssetManager;
import model.GameSettings;

import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class PauseMenuView implements Screen {
    private Stage stage;
    private Skin skin;
    private PauseMenuController controller;

    private Label titleLabel;
    private TextButton resumeButton;
    private TextButton giveUpButton;
    private TextButton toggleHintsButton;
    private Image backgroundImage;

    private ScrollPane hintsScrollPane;
    private Table hintsContentTable;
    private boolean hintsVisible = false;

    public PauseMenuView(PauseMenuController controller) {
        this.controller = controller;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        if (this.skin == null) {
            Gdx.app.error("PauseMenuView", "Skin is null. UI might not render correctly. Loading default.");
            this.skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        }
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        if (this.skin == null) {
            this.skin = GameAssetManager.getGameAssetManager().getSkin();
            if (this.skin == null) {
                Gdx.app.error("PauseMenuView.show", "Skin is still null. Loading default fallback.");
                this.skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
            }
        }

        Texture backgroundTex = null;
        try {
            String bgPath = GameAssetManager.getGameAssetManager().getRandomBackgroundPath();
            if (bgPath != null) {
                backgroundTex = GameAssetManager.getGameAssetManager().getTexture(bgPath);
            }
        } catch (Exception e) {
            Gdx.app.error("PauseMenuView", "Could not load random background for pause menu.", e);
        }
        if (backgroundTex != null) {
            backgroundImage = new Image(backgroundTex);
            backgroundImage.setFillParent(true);
            stage.addActor(backgroundImage);
        }

        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.center();

        titleLabel = new Label("Game Paused", skin, "title");
        if (skin.has("title", Label.LabelStyle.class)) {
            titleLabel.setAlignment(Align.center);
        } else {
            Gdx.app.log("PauseMenuView", "'title' LabelStyle not found in skin. Using default.");
            titleLabel = new Label("Game Paused", skin);
            titleLabel.setAlignment(Align.center);
        }

        resumeButton = new TextButton("Resume Game", skin);
        giveUpButton = new TextButton("Give Up (Main Menu)", skin);
        toggleHintsButton = new TextButton("Show Hints", skin);

        hintsContentTable = new Table(skin);
        hintsContentTable.pad(10f);
        hintsContentTable.top().left();
        hintsContentTable.columnDefaults(0).minWidth(120).prefWidth(Gdx.graphics.getWidth() * 0.25f).left().top().padRight(10);
        hintsContentTable.columnDefaults(1).expandX().fillX().left().top();


        hintsScrollPane = new ScrollPane(hintsContentTable, skin);
        hintsScrollPane.setFadeScrollBars(false);
        hintsScrollPane.setScrollingDisabled(true, false);
        hintsScrollPane.setVisible(false);

        mainTable.add(titleLabel).padBottom(30).colspan(1).row();
        mainTable.add(resumeButton).width(300).height(80).padBottom(15).row();
        mainTable.add(giveUpButton).width(500).height(80).padBottom(15).row();
        mainTable.add(toggleHintsButton).width(300).height(80).padBottom(15).row();
        mainTable.add(hintsScrollPane).expand().fill().pad(10).minHeight(200).maxHeight(Gdx.graphics.getHeight() * 0.4f).row();


        stage.addActor(mainTable);

        toggleHintsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hintsVisible = !hintsVisible;
                hintsScrollPane.setVisible(hintsVisible);
                if (hintsVisible) {
                    toggleHintsButton.setText("Hide Hints");
                    populateHintsPanel();
                } else {
                    toggleHintsButton.setText("Show Hints");
                }
            }
        });

        if (controller != null) {
            controller.setViewAndAttachListeners(this);
        } else {
            Gdx.app.error("PauseMenuView", "Controller is null in show(). Listeners may not be active.");
        }
    }

    private void populateHintsPanel() {
        hintsContentTable.clearChildren();

        LabelStyle sectionTitleStyle = skin.get("subtitle", LabelStyle.class);
        if (sectionTitleStyle == null) sectionTitleStyle = new LabelStyle(skin.getFont("default-font"), skin.getColor("default"));
        LabelStyle defaultStyle = skin.get(LabelStyle.class);
        if (defaultStyle == null) defaultStyle = new LabelStyle(skin.getFont("default-font"), skin.getColor("default"));

        float itemSpacing = 8f;
        float sectionTopPadding = 15f;
        float sectionBottomPadding = 8f;


        LabelStyle finalSectionTitleStyle = sectionTitleStyle;
        Consumer<String> addSectionTitle = (String titleText) -> {
            Label title = new Label(titleText, finalSectionTitleStyle);
            title.setWrap(true);
            hintsContentTable.add(title).left().padTop(sectionTopPadding).padBottom(sectionBottomPadding).colspan(2).row();
        };
        LabelStyle finalDefaultStyle = defaultStyle;
        BiConsumer<String, String> addItem = (String nameText, String descText) -> {
            Label nameLabel = new Label(nameText, finalDefaultStyle); nameLabel.setWrap(true); nameLabel.setAlignment(Align.topLeft);
            Label descLabel = new Label(descText, finalDefaultStyle); descLabel.setWrap(true); descLabel.setAlignment(Align.topLeft);
            hintsContentTable.add(nameLabel).top();
            hintsContentTable.add(descLabel).top().padBottom(itemSpacing).row();
        };


        addSectionTitle.accept("--- Key Bindings ---");
        GameSettings gameSettings = App.getInstance().getGameSettings();
        ObjectMap<String, Integer> keyBindings = gameSettings.getKeyBindings();
        if (keyBindings != null && keyBindings.size > 0) {
            for (ObjectMap.Entry<String, Integer> entry : keyBindings) {
                addItem.accept(entry.key + ":", GameSettings.getKeyName(entry.value));
            }
        } else {
            addItem.accept("Keys:", "No key binding information available.");
        }

        addSectionTitle.accept("--- Abilities ---");
        if (Ability.values().length > 0) {
            for (Ability ability : Ability.values()) {
                addItem.accept(ability.getDisplayName() + ":", ability.getDescription());
            }
        } else {
            addItem.accept("Abilities:", "No ability information available.");
        }

        addSectionTitle.accept("--- Cheat Codes ---");
        addItem.accept("Press '1':", "Reduce game time by 1 minute.");
        addItem.accept("Press '2':", "Advance player to the next level.");
        addItem.accept("Press '3':", "Increase player's Max HP by 1 and heal 1 HP.");
        addItem.accept("Press '4':", "Spawn a Boss enemy near the player.");
        addItem.accept("Press '5':", "Instantly defeat 10 random non-Boss enemies.");

        addSectionTitle.accept("--- Heroes ---");
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();
        Array<String> heroNames = assetManager.getAllCharacterNames();
        if (heroNames != null && !heroNames.isEmpty()) {
            for (String name : heroNames) {
                CharacterData data = assetManager.getCharacterDataByName(name);
                if (data != null) {
                    addItem.accept(data.getName() + ":", "HP: " + data.getHp() + ", Speed: " + data.getSpeed() + ".");
                }
            }
        } else {
            addItem.accept("Heroes:", "No hero information available.");
        }

        hintsContentTable.pack();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (stage != null) {
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
            if (hintsContentTable != null) {
                float firstColWidth = width * 0.3f;
                if (firstColWidth < 120f) firstColWidth = 120f;
                hintsContentTable.columnDefaults(0).prefWidth(firstColWidth).minWidth(120f);
                hintsContentTable.columnDefaults(1).expandX().fillX();
                hintsContentTable.invalidateHierarchy();
            }
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        Gdx.app.log("PauseMenuView", "dispose() called");
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }

    public TextButton getResumeButton() {
        return resumeButton;
    }

    public TextButton getGiveUpButton() {
        return giveUpButton;
    }
=======
public class PauseMenuView {
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
}
