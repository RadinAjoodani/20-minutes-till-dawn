package controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import view.*;
// import view.SettingsMenuView;


public class MainMenuController {
    private MainMenuView view;

    public void setView(MainMenuView view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        view.getContinueGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleContinueGameButtonClick();
            }
        });
        view.getPreGameMenuButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handlePreGameMenuButtonClick();
            }
        });
        view.getHintMenuButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleHintMenuButtonClick();
            }
        });
        view.getScoreboardMenuButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleScoreboardMenuButtonClick();
            }
        });
        view.getSettingMenuButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleSettingMenuButtonClick();
            }
        });
        view.getProfileMenuButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleProfileMenuButtonClick();
            }
        });
        view.getLogoutButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLogoutButtonClick();
            }
        });
    }

    private void handleContinueGameButtonClick() {
        System.out.println("Attempting to continue saved game...");
        view.getWelcomeLabel().setText("Feature coming soon!");
    }

    private void handlePreGameMenuButtonClick() {
        System.out.println("Navigating to Pre-Game Menu...");
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new PreGameMenuView(new PreGameMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleHintMenuButtonClick() {
        System.out.println("Navigating to Hint Menu...");
    }

    private void handleScoreboardMenuButtonClick() {
        System.out.println("Navigating to Scoreboard Menu...");
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new ScoreBoardMenuView(new ScoreBoardMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleSettingMenuButtonClick() {
        System.out.println("Navigating to Settings Menu...");
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new SettingsMenuView(new SettingsMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleProfileMenuButtonClick() {
        System.out.println("Navigating to Profile Menu...");
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new ProfileMenuView(new ProfileMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleLogoutButtonClick() {
        System.out.println("Logging out...");
        App.getInstance().setCurrentUser(null);
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new LoginMenuView(new LoginMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }
}
