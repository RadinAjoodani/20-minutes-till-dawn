package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.HintMenuController;
import model.GameAssetManager;

import java.util.Map;

public class HintMenuView implements Screen {
    private Stage stage;
    private Skin skin;
    private HintMenuController controller;

    private TextButton backButton;
    private ScrollPane scrollPane;
    private Table contentTable;
    private Image backgroundImage;

    private Array<Map<String, String>> heroInfo;
    private Array<Map<String, String>> keyBindingInfo;
    private Array<Map<String, String>> cheatCodeInfo;
    private Array<Map<String, String>> abilityInfo;


    public HintMenuView(HintMenuController controller) {
        this.controller = controller;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        if (this.skin == null) {
            Gdx.app.error("HintMenuView", "Skin is null. UI might not render correctly. Loading default.");
            this.skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        }
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        try {
            String bgPath = GameAssetManager.getGameAssetManager().getRandomBackgroundPath();
            if (bgPath != null) {
                Texture backgroundTex = GameAssetManager.getGameAssetManager().getTexture(bgPath);
                if (backgroundTex != null) {
                    backgroundImage = new Image(backgroundTex);
                    backgroundImage.setFillParent(true);
                    stage.addActor(backgroundImage);
                }
            }
        } catch (Exception e) {
            Gdx.app.error("HintMenuView", "Could not load background for hint menu.", e);
        }

        contentTable = new Table(skin);
        contentTable.pad(20f);
        contentTable.top().left();




        contentTable.columnDefaults(0).minWidth(150).left().top().padRight(15);
        contentTable.columnDefaults(1).expandX().fillX().left().top();


        scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setOverscroll(false, false);
        scrollPane.setVariableSizeKnobs(false);

        Label titleLabel = new Label("Game Hints & Information", skin, "title");
        if (!skin.has("title", LabelStyle.class)) {
            Gdx.app.log("HintMenuView", "'title' LabelStyle not found in skin. Using default for main title.");
            titleLabel = new Label("Game Hints & Information", skin);
        }
        titleLabel.setAlignment(Align.center);

        backButton = new TextButton("Back to Main Menu", skin);

        Table mainLayoutTable = new Table(skin);
        mainLayoutTable.setFillParent(true);
        mainLayoutTable.pad(10f);
        mainLayoutTable.add(titleLabel).expandX().padTop(10).padBottom(20).row();
        mainLayoutTable.add(scrollPane).expand().fill().pad(10).row();
        mainLayoutTable.add(backButton).width(500).height(80).padTop(20).padBottom(10).row();

        stage.addActor(mainLayoutTable);


        if (controller != null) {
            controller.setView(this);
        } else {
            Gdx.app.error("HintMenuView", "Controller is null in show(). Cannot initialize data or listeners.");
        }
    }

    public void populateHints() {
        contentTable.clearChildren();

        LabelStyle sectionTitleStyle = skin.get("subtitle", LabelStyle.class);
        if (sectionTitleStyle == null) {
            Gdx.app.log("HintMenuView", "'subtitle' LabelStyle not found. Using default for section titles.");
            sectionTitleStyle = new LabelStyle(skin.getFont("default-font"), skin.getColor("default"));
        }
        LabelStyle defaultStyle = skin.get(LabelStyle.class);
        if (defaultStyle == null) {
            Gdx.app.log("HintMenuView", "Default LabelStyle not found. Using basic fallback.");
            defaultStyle = new LabelStyle(skin.getFont("default-font"), skin.getColor("default"));
        }

        float itemSpacing = 8f;
        float sectionTopPadding = 20f;
        float sectionBottomPadding = 10f;


        Label heroesTitle = new Label("--- Heroes ---", sectionTitleStyle);
        contentTable.add(heroesTitle).left().padTop(sectionTopPadding).padBottom(sectionBottomPadding).colspan(2).row();
        if (heroInfo != null && !heroInfo.isEmpty()) {
            for (Map<String, String> hero : heroInfo) {
                Label nameLabel = new Label(hero.get("name") + ":", defaultStyle);
                nameLabel.setWrap(true);
                Label descLabel = new Label(hero.get("description"), defaultStyle);
                descLabel.setWrap(true);
                contentTable.add(nameLabel).top();
                contentTable.add(descLabel).top().padBottom(itemSpacing).row();
            }
        } else {
            contentTable.add(new Label("No hero information available.", defaultStyle)).left().colspan(2).padBottom(itemSpacing).row();
        }


        Label keysTitle = new Label("--- Key Bindings ---", sectionTitleStyle);
        contentTable.add(keysTitle).left().padTop(sectionTopPadding).padBottom(sectionBottomPadding).colspan(2).row();
        if (keyBindingInfo != null && !keyBindingInfo.isEmpty()) {
            for (Map<String, String> keyInfo : keyBindingInfo) {
                Label actionLabel = new Label(keyInfo.get("action") + ":", defaultStyle);
                actionLabel.setWrap(true);
                Label keyNameLabel = new Label(keyInfo.get("key"), defaultStyle);
                keyNameLabel.setWrap(true);
                contentTable.add(actionLabel).top();
                contentTable.add(keyNameLabel).top().padBottom(itemSpacing).row();
            }
        } else {
            contentTable.add(new Label("No key binding information available.", defaultStyle)).left().colspan(2).padBottom(itemSpacing).row();
        }


        Label cheatsTitle = new Label("--- Cheat Codes ---", sectionTitleStyle);
        contentTable.add(cheatsTitle).left().padTop(sectionTopPadding).padBottom(sectionBottomPadding).colspan(2).row();
        if (cheatCodeInfo != null && !cheatCodeInfo.isEmpty()) {
            for (Map<String, String> cheat : cheatCodeInfo) {
                Label keyLabel = new Label(cheat.get("key") + ":", defaultStyle);
                keyLabel.setWrap(true);
                Label descLabel = new Label(cheat.get("description"), defaultStyle);
                descLabel.setWrap(true);
                contentTable.add(keyLabel).top();
                contentTable.add(descLabel).top().padBottom(itemSpacing).row();
            }
        } else {
            contentTable.add(new Label("No cheat code information available.", defaultStyle)).left().colspan(2).padBottom(itemSpacing).row();
        }


        Label abilitiesTitle = new Label("--- Abilities (Level Up Rewards) ---", sectionTitleStyle);
        contentTable.add(abilitiesTitle).left().padTop(sectionTopPadding).padBottom(sectionBottomPadding).colspan(2).row();
        if (abilityInfo != null && !abilityInfo.isEmpty()) {
            for (Map<String, String> ability : abilityInfo) {
                Label nameLabel = new Label(ability.get("name") + ":", defaultStyle);
                nameLabel.setWrap(true);
                Label descLabel = new Label(ability.get("description"), defaultStyle);
                descLabel.setWrap(true);
                contentTable.add(nameLabel).top();
                contentTable.add(descLabel).top().padBottom(itemSpacing).row();
            }
        } else {
            contentTable.add(new Label("No ability information available.", defaultStyle)).left().colspan(2).padBottom(itemSpacing).row();
        }


    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
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
            if (contentTable != null) {

                contentTable.columnDefaults(0).minWidth(width * 0.25f).prefWidth(width * 0.3f).left().top().padRight(15);
                contentTable.columnDefaults(1).expandX().fillX().left().top();
                contentTable.invalidateHierarchy();
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
        Gdx.app.log("HintMenuView", "dispose() called");
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public void setHeroInfo(Array<Map<String, String>> heroInfo) {
        this.heroInfo = heroInfo;
    }

    public void setKeyBindingInfo(Array<Map<String, String>> keyBindingInfo) {
        this.keyBindingInfo = keyBindingInfo;
    }

    public void setCheatCodeInfo(Array<Map<String, String>> cheatCodeInfo) {
        this.cheatCodeInfo = cheatCodeInfo;
    }

    public void setAbilityInfo(Array<Map<String, String>> abilityInfo) {
        this.abilityInfo = abilityInfo;
    }
}
