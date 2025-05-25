package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
// import com.badlogic.gdx.scenes.scene2d.ui.Skin; // No longer needed for font
import controller.MainMenuController;
import graphic.source.Main;
import model.GameAssetManager;

/**
 * Screen displayed when the game ends, showing final stats.
 */
public class GameOverScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font; // Will be initialized directly

    private String username;
    private float aliveTimeSeconds;
    private int kills;
    private int score;

    public GameOverScreen(String username, float aliveTimeSeconds, int kills, int score) {
        this.username = username;
        this.aliveTimeSeconds = aliveTimeSeconds;
        this.kills = kills;
        this.score = score;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Initialize BitmapFont directly instead of getting from skin
        font = new BitmapFont();
        font.setColor(Color.WHITE); // Set default color

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Return to Main Menu on any key press
                // Pass the skin from GameAssetManager to MainMenuView as it might need it for other UI elements
                Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
                return true;
            }
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Pass the skin from GameAssetManager to MainMenuView
                Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Black background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        GlyphLayout layout = new GlyphLayout(); // Use GlyphLayout for centering text

        // Game Over Title
        font.getData().setScale(2.0f); // Make title larger
        String gameOverTitle = "GAME OVER!";
        layout.setText(font, gameOverTitle);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() * 0.85f);

        // Reset font scale for stats
        font.getData().setScale(1.2f); // Slightly larger for stats
        float startY = Gdx.graphics.getHeight() * 0.65f;
        float lineSpacing = 50f;

        // Username
        String usernameText = "Username: " + username;
        layout.setText(font, usernameText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY);

        // Alive Time
        String aliveTimeText = String.format("Alive Time: %02d:%02d", (int)(aliveTimeSeconds / 60), (int)(aliveTimeSeconds % 60));
        layout.setText(font, aliveTimeText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY - lineSpacing);

        // Kills
        String killsText = "Kills: " + kills;
        layout.setText(font, killsText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY - 2 * lineSpacing);

        // Score
        String scoreText = "Score: " + score;
        layout.setText(font, scoreText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, startY - 3 * lineSpacing);

        // Instruction to return to main menu
        font.getData().setScale(1.0f); // Reset to default size for instruction
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
        dispose(); // Dispose resources when screen is hidden
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        // Dispose the font since we created it with "new BitmapFont()"
        if (font != null) {
            font.dispose();
        }
    }
}
