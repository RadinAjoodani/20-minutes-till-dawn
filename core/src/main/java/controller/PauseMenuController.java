package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import view.GameOverScreen;
import view.GameView;
import view.MainMenuView;
import view.PauseMenuView;
import view.GameOverScreen.GameResult;

public class PauseMenuController {
    private PauseMenuView view;
    private GameView gameViewInstance;

    public PauseMenuController(GameView gameViewInstance) {
        this.gameViewInstance = gameViewInstance;
        if (this.gameViewInstance == null) {
            Gdx.app.error("PauseMenuController", "Constructor: gameViewInstance is null!");
        }
    }


    public void setViewAndAttachListeners(PauseMenuView view) {
        this.view = view;
        if (this.view == null) {
            Gdx.app.error("PauseMenuController", "setViewAndAttachListeners: view is null!");
            return;
        }
        setupListeners();
    }

    private void setupListeners() {
        if (view == null) {
            Gdx.app.error("PauseMenuController", "View is null, cannot setup listeners.");
            return;
        }

        if (view.getResumeButton() == null) {
            Gdx.app.error("PauseMenuController", "ResumeButton is null in setupListeners.");

            return;
        }
        view.getResumeButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleResumeGame();
            }
        });

        if (view.getGiveUpButton() == null) {
            Gdx.app.error("PauseMenuController", "GiveUpButton is null in setupListeners.");
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
        } else {
            Gdx.app.error("PauseMenuController", "Cannot resume game, GameView instance is null. Returning to Main Menu.");
            if (App.getInstance().getGameSettings().getMusic() != null) {
                App.getInstance().getGameSettings().getMusic().stop();
            }
            Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        }
    }

    private void handleGiveUp() {
        Gdx.app.log("PauseMenuController", "Player chose to Give Up.");
        if(App.getInstance().getCurrentUser() == null || App.getInstance().getCurrentUser().getUsername() == null){
            Main.getMain().setScreen(new GameOverScreen("Guest", 0, 0, 0, GameResult.GAVE_UP));
        }
        else {
            Main.getMain().setScreen(new GameOverScreen(App.getInstance().getCurrentUser().getUsername(), 0, 0, 0, GameResult.GAVE_UP));
        }
    }

    public void setGameViewInstance(GameView gameViewInstance) {
        this.gameViewInstance = gameViewInstance;
    }
}
