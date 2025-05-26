package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import view.GameView;
import view.MainMenuView;
import view.PauseMenuView;
import view.GameOverScreen.GameResult; // Ensure this is imported if GameView uses it

public class PauseMenuController {
    private PauseMenuView view;
    private GameView gameViewInstance;

    public PauseMenuController(GameView gameViewInstance) {
        this.gameViewInstance = gameViewInstance;
    }

    // This method is called by PauseMenuView from its show() method
    public void setViewAndAttachListeners(PauseMenuView view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        if (view == null) {
            Gdx.app.error("PauseMenuController", "View is null, cannot setup listeners.");
            return;
        }

        // Ensure buttons are not null before adding listeners
        // These buttons (resumeButton, giveUpButton) are initialized in PauseMenuView.show()
        // before this method is called.
        if (view.getResumeButton() == null) {
            Gdx.app.error("PauseMenuController", "ResumeButton is null in setupListeners. UI not fully initialized in View's show() before calling setViewAndAttachListeners?");
            return;
        }
        view.getResumeButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleResumeGame();
            }
        });

        if (view.getGiveUpButton() == null) {
            Gdx.app.error("PauseMenuController", "GiveUpButton is null in setupListeners. UI not fully initialized in View's show() before calling setViewAndAttachListeners?");
            return;
        }
        view.getGiveUpButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleGiveUp();
            }
        });

        // The PauseMenuController does NOT manage the toggleHintsButton listener.
        // That listener is set up directly within PauseMenuView.show().
    }

    private void handleResumeGame() {
        if (gameViewInstance != null) {
            Gdx.app.log("PauseMenuController", "Resuming game.");
            Main.getMain().setScreen(gameViewInstance);
            // GameView's show() method will handle restoring its input processor and adjusting timers
        } else {
            Gdx.app.error("PauseMenuController", "Cannot resume game, GameView instance is null. Returning to Main Menu.");
            // Fallback to main menu if gameViewInstance is somehow lost
            if (App.getInstance().getGameSettings().getMusic() != null) {
                App.getInstance().getGameSettings().getMusic().stop();
            }
            Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        }
    }

    private void handleGiveUp() {
        Gdx.app.log("PauseMenuController", "Player chose to Give Up.");
        if (gameViewInstance != null) {
            // Tell GameView to handle the "give up" sequence, which includes saving stats
            // and transitioning to the GameOverScreen with the GAVE_UP result.
            gameViewInstance.triggerGiveUp();
        } else {
            // Fallback if gameViewInstance is null (should not happen in normal flow)
            Gdx.app.error("PauseMenuController", "GameView instance is null during give up. Directly going to Main Menu.");
            if (App.getInstance().getGameSettings().getMusic() != null) {
                App.getInstance().getGameSettings().getMusic().stop();
            }
            Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        }
    }

    // This method might be called by GameView if it needs to re-initialize the controller with a new view instance
    // However, typically a new PauseMenuController is created each time PauseMenu is shown.
    public void setGameViewInstance(GameView gameViewInstance) {
        this.gameViewInstance = gameViewInstance;
    }
}
