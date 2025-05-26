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

public class PauseMenuController {
    private PauseMenuView view;
    private GameView gameViewInstance; // To resume the specific game instance

    public PauseMenuController(GameView gameViewInstance) {
        this.gameViewInstance = gameViewInstance;
    }

    // This method will be called by PauseMenuView from its show() method
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
        if (view.getResumeButton() == null) {
            Gdx.app.error("PauseMenuController", "ResumeButton is null in setupListeners. UI not fully initialized?");
            return;
        }
        view.getResumeButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleResumeGame();
            }
        });

        if (view.getGiveUpButton() == null) {
            Gdx.app.error("PauseMenuController", "GiveUpButton is null in setupListeners. UI not fully initialized?");
            return;
        }
        view.getGiveUpButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleGiveUp();
            }
        });
    }

    private void handleResumeGame() {
        if (gameViewInstance != null) {
            Gdx.app.log("PauseMenuController", "Resuming game.");
            Main.getMain().setScreen(gameViewInstance);
            // GameView's show() method should handle restoring input processor and adjusting timers
        } else {
            Gdx.app.error("PauseMenuController", "Cannot resume game, GameView instance is null. Returning to Main Menu.");
            handleGiveUp(); // Fallback to main menu
        }
    }

    private void handleGiveUp() {
        Gdx.app.log("PauseMenuController", "Giving up and returning to Main Menu.");
        if (gameViewInstance != null) {
            // It's good practice to dispose the GameView screen if we are not returning to it.
            // However, LibGDX's setScreen usually handles disposing the previous screen's hide() method.
            // If GameView's dispose is not being called, you might need to explicitly call it here.
            // gameViewInstance.dispose(); // Consider if this is needed based on your screen lifecycle.
        }
        if (App.getInstance().getGameSettings().getMusic() != null) {
            App.getInstance().getGameSettings().getMusic().stop();
        }
        Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    public void setGameViewInstance(GameView gameViewInstance) {
        this.gameViewInstance = gameViewInstance;
    }
}
