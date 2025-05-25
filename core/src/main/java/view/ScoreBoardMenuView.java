package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ScoreBoardMenuController;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.User;


public class ScoreBoardMenuView implements Screen {
    private Stage stage;
    private final TextButton backButton;
    private final TextButton sortByUsernameButton;
    private final TextButton sortByScoreButton;
    private final TextButton sortByKillsButton;
    private final TextButton sortByTimeAliveButton;
    private final Label menuLabel;
    private final Label errorLabel;
    private final Label sortLabel;
    private final Table rootTable;
    private final Table sortButtonsTable;
    private final Table scoreboardContentTable;
    private final ScrollPane scrollPane;
    private Image backgroundImage;
    private final Skin skin;
    private final ScoreBoardMenuController controller;
    private final Color GOLD = new Color(1.0f, 0.843f, 0.0f, 1.0f);
    private final Color SILVER = new Color(0.753f, 0.753f, 0.753f, 1.0f);
    private final Color BRONZE = new Color(0.804f, 0.498f, 0.196f, 1.0f);

    public ScoreBoardMenuView(ScoreBoardMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;

        this.backButton = new TextButton("Back to Main Menu", skin);
        this.sortByUsernameButton = new TextButton("Username", skin);
        this.sortByScoreButton = new TextButton("Score", skin);
        this.sortByKillsButton = new TextButton("Kills", skin);
        this.sortByTimeAliveButton = new TextButton("Time Alive", skin);

        this.sortLabel = new Label("Sort by: ", skin);
        this.sortLabel.setColor(Color.ROYAL);
        this.menuLabel = new Label("Scoreboard", skin);
        this.errorLabel = new Label("", skin);
        this.errorLabel.setColor(Color.RED);

        this.rootTable = new Table(skin);
        this.sortButtonsTable = new Table(skin);
        this.scoreboardContentTable = new Table(skin);

        this.scrollPane = new ScrollPane(scoreboardContentTable, skin);
        this.scrollPane.setFadeScrollBars(false);
        this.scrollPane.setScrollingDisabled(true, false);

        this.controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Texture backgroundTexture = GameAssetManager.getGameAssetManager()
            .getTexture(GameAssetManager.getGameAssetManager()
                .getRandomBackgroundPath());

        if (backgroundTexture != null) {
            backgroundImage = new Image(backgroundTexture);
            backgroundImage.setFillParent(true);
            stage.addActor(backgroundImage);
        }
        rootTable.clear();
        rootTable.setFillParent(true);
        rootTable.center();
        rootTable.add(menuLabel).colspan(5).padBottom(30).row();
        rootTable.add(errorLabel).colspan(5).center().padBottom(10).row();
        rootTable.add(scrollPane).colspan(5).expand().fill().pad(10).row();
        setupSortButtonsTable();
        rootTable.add(sortButtonsTable).colspan(5).padBottom(20).row();
        rootTable.add(backButton).colspan(5).width(500).height(80).padTop(10).row();
        stage.addActor(rootTable);
        rootTable.pack();
        controller.displayScoreboard(null);
    }

    private void setupSortButtonsTable() {
        sortButtonsTable.clear();
        sortButtonsTable.add(sortLabel).width(150).padRight(30);
        sortButtonsTable.add(sortByUsernameButton).width(350).height(80).padRight(10);
        sortButtonsTable.add(sortByScoreButton).width(350).height(80).padRight(10);
        sortButtonsTable.add(sortByKillsButton).width(350).height(80).padRight(10);
        sortButtonsTable.add(sortByTimeAliveButton).width(350).height(80).padRight(10);
        sortButtonsTable.pack();
    }

    public void updateScoreboard(Array<User> users) {
        scoreboardContentTable.clear();
        scoreboardContentTable.defaults().pad(5).left();
        scoreboardContentTable.add(new Label("Rank", skin)).width(60);
        scoreboardContentTable.add(new Label("Username", skin)).width(180);
        scoreboardContentTable.add(new Label("Score", skin)).width(100);
        scoreboardContentTable.add(new Label("Kills", skin)).width(100);
        scoreboardContentTable.add(new Label("Time Alive", skin)).width(120).row();

        if (users == null || users.size == 0) {
            scoreboardContentTable.add(new Label("No players found.", skin)).colspan(5).center().padTop(20).row();
            scoreboardContentTable.pack();
            return;
        }

        User currentUser = App.getInstance().getCurrentUser();

        for (int i = 0; i < users.size; i++) {
            if(i >= 9){
                break;
            }
            User user = users.get(i);
            Color rankColor = Color.WHITE;
            boolean isCurrentUser = (currentUser != null && currentUser.getUsername().equals(user.getUsername()));
            if (i == 0) rankColor = GOLD;
            else if (i == 1) rankColor = SILVER;
            else if (i == 2) rankColor = BRONZE;
            Label rankLabel = new Label(String.valueOf(i + 1), skin);
            rankLabel.setColor(rankColor);
            scoreboardContentTable.add(rankLabel).width(60);
            Label usernameLabel;
            if (isCurrentUser) {
                usernameLabel = new Label("(" + user.getUsername() + ")", skin);
                usernameLabel.setColor(Color.CYAN);
            } else {
                usernameLabel = new Label(user.getUsername(), skin);
                usernameLabel.setColor(rankColor);
            }
            usernameLabel.setAlignment(Align.left);
            scoreboardContentTable.add(usernameLabel).width(180);
            Label scoreLabel = new Label(String.valueOf(user.getScore()), skin);
            scoreLabel.setColor(rankColor);
            scoreboardContentTable.add(scoreLabel).width(100);
            Label killLabel = new Label(String.valueOf(user.getTotalKill()), skin);
            killLabel.setColor(rankColor);
            scoreboardContentTable.add(killLabel).width(100);
            Label timeAliveLabel = new Label(String.valueOf(user.getMaximumTimeAlive()), skin);
            timeAliveLabel.setColor(rankColor);
            scoreboardContentTable.add(timeAliveLabel).width(120).row();
        }
        scoreboardContentTable.pack();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        rootTable.pack();
        rootTable.setPosition(stage.getWidth() / 2 - rootTable.getWidth() / 2,
            stage.getHeight() / 2 - rootTable.getHeight() / 2);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }

    public TextButton getBackButton() { return backButton; }
    public TextButton getSortByUsernameButton() { return sortByUsernameButton; }
    public TextButton getSortByScoreButton() { return sortByScoreButton; }
    public TextButton getSortByKillsButton() { return sortByKillsButton; }
    public TextButton getSortByTimeAliveButton() { return sortByTimeAliveButton; }
    public Label getErrorLabel() { return errorLabel; }
}
