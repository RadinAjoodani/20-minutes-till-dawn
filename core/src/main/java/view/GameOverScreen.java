package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import controller.MainMenuController;
import graphic.source.Main;
import model.GameAssetManager;

public class GameOverScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;

    private String username;
    private float aliveTimeSeconds;
    private int kills;
    private int score;
    private GameResult gameResult;


    public enum GameResult {
        WIN,
        DIED,
        GAVE_UP
    }

    public GameOverScreen(String username, float aliveTimeSeconds, int kills, int score, GameResult result) {
        this.username = (username == null || username.trim().isEmpty()) ? "Player" : username;
        this.aliveTimeSeconds = aliveTimeSeconds;
        this.kills = kills;
        this.score = score;
        this.gameResult = result;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
                return true;
            }
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        GlyphLayout layout = new GlyphLayout();
        GlyphLayout userLayout = new GlyphLayout();

        String titleText;
        Color titleColor;
        String userSpecificMessage = "";

        switch (gameResult) {
            case WIN:
                titleText = "YOU WIN!";
                userSpecificMessage = this.username + " is Victorious!";
                titleColor = Color.GREEN;
                break;
            case GAVE_UP:
                titleText = "YOU GAVE UP";
                titleColor = Color.YELLOW;
                break;
            case DIED:
            default:
                titleText = "YOU ARE DEAD!";
                titleColor = Color.RED;
                break;
        }
        titleFont.setColor(titleColor);
        layout.setText(titleFont, titleText);
        titleFont.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() * 0.85f);

        if (!userSpecificMessage.isEmpty()) {
            font.getData().setScale(1.5f);
            userLayout.setText(font, userSpecificMessage);
            font.draw(batch, userLayout, (Gdx.graphics.getWidth() - userLayout.width) / 2, Gdx.graphics.getHeight() * 0.85f - layout.height - 15);
            font.getData().setScale(1.0f);
        }


        font.getData().setScale(1.2f);
        float startY = Gdx.graphics.getHeight() * 0.65f;
        if (!userSpecificMessage.isEmpty() && gameResult == GameResult.WIN) {
            startY -= (userLayout.height + 15);
        } else if (userSpecificMessage.isEmpty() && gameResult != GameResult.WIN) {
            startY = Gdx.graphics.getHeight() * 0.75f - layout.height;
        }


        float lineSpacing = 45f;

        String usernameStatText = "Player: " + this.username;
        layout.setText(font, usernameStatText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY);

        String aliveTimeText = String.format("Time Survived: %02d:%02d", (int)(aliveTimeSeconds / 60), (int)(aliveTimeSeconds % 60));
        layout.setText(font, aliveTimeText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY - lineSpacing);

        String killsText = "Enemies Defeated: " + kills;
        layout.setText(font, killsText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY - 2 * lineSpacing);

        String scoreText = "Final Score: " + score;
        layout.setText(font, scoreText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY - 3 * lineSpacing);

        font.getData().setScale(1.0f);
        String instructionText = "Press Any Key or Touch to Return to Main Menu";
        layout.setText(font, instructionText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() * 0.15f);

        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameOverScreen", "dispose() called");
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (titleFont != null) {
            titleFont.dispose();
            titleFont = null;
        }
    }
}
