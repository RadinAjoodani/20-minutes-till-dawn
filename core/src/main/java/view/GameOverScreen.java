package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import controller.MainMenuController;
import graphic.source.Main; // Assuming your main game class is `graphic.source.Main`
import model.GameAssetManager;

/**
 * Screen displayed when the game ends, showing final stats.
 */
public class GameOverScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private Skin skin; // For general UI if needed, but for simple text, BitmapFont is fine.

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
        skin = GameAssetManager.getGameAssetManager().getSkin(); // Get the shared skin

        // It's good practice to get fonts from the skin if possible for consistency
        font = skin.getFont("default-font"); // Use a font defined in your skin.json
        if (font == null) {
            Gdx.app.error("GameOverScreen", "Could not find 'default-font' in skin. Using new BitmapFont.");
            font = new BitmapFont(); // Fallback if skin font not found
            font.setColor(Color.WHITE);
        } else {
            font.setColor(Color.WHITE); // Ensure font color is white
        }


        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Return to Main Menu on any key press
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
        Gdx.gl.glClearColor(0, 0, 0, 1); // Black background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Use GlyphLayout for centering text
        GlyphLayout layout = new GlyphLayout();

        // Game Over Title
        font.getData().setScale(2.0f); // Make title larger
        String gameOverTitle = "GAME OVER!";
        layout.setText(font, gameOverTitle);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() * 0.85f);

        // Reset font scale for stats
        font.getData().setScale(1.2f);
        float startY = Gdx.graphics.getHeight() * 0.65f; // Starting Y position for stats
        float lineSpacing = 50f; // Vertical spacing between lines

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
        font.getData().setScale(1.0f); // Reset to default size
        String instructionText = "Press Any Key or Touch to Return to Main Menu";
        layout.setText(font, instructionText);
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() * 0.15f);

        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        dispose();
    }
    @Override
    public void dispose() {
        batch.dispose();
        // Do NOT dispose font if it's from the skin; the skin manages it.
        // If you create `new BitmapFont()` in show(), then dispose it here.
//        if (font != null && !skin.getFonts().containsValue(font, true)) { // Heuristic check if font is from skin
//            font.dispose();
//        }
        // No need to dispose skin itself, as GameAssetManager manages it.
    }
}
