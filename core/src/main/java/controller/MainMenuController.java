package controller;

<<<<<<< HEAD
import com.badlogic.gdx.Gdx;
=======
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import view.*;
<<<<<<< HEAD

=======
// import view.SettingsMenuView;
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86


public class MainMenuController {
    private MainMenuView view;

    public void setView(MainMenuView view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
<<<<<<< HEAD
        if (view == null) {
            Gdx.app.error("MainMenuController", "View is null, cannot setup listeners.");
            return;
        }

        if (view.getContinueGameButton() != null) {
            view.getContinueGameButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleContinueGameButtonClick();
                }
            });
        } else { Gdx.app.error("MainMenuController", "ContinueGameButton is null.");}


        if (view.getPreGameMenuButton() != null) {
            view.getPreGameMenuButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handlePreGameMenuButtonClick();
                }
            });
        } else { Gdx.app.error("MainMenuController", "PreGameMenuButton is null.");}


        if (view.getHintMenuButton() != null) {
            view.getHintMenuButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleHintMenuButtonClick();
                }
            });
        } else { Gdx.app.error("MainMenuController", "HintMenuButton is null.");}


        if (view.getScoreboardMenuButton() != null) {
            view.getScoreboardMenuButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleScoreboardMenuButtonClick();
                }
            });
        } else { Gdx.app.error("MainMenuController", "ScoreboardMenuButton is null.");}


        if (view.getSettingMenuButton() != null) {
            view.getSettingMenuButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleSettingMenuButtonClick();
                }
            });
        } else { Gdx.app.error("MainMenuController", "SettingMenuButton is null.");}


        if (view.getProfileMenuButton() != null) {
            view.getProfileMenuButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleProfileMenuButtonClick();
                }
            });
        } else { Gdx.app.error("MainMenuController", "ProfileMenuButton is null.");}


        if (view.getLogoutButton() != null) {
            view.getLogoutButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleLogoutButtonClick();
                }
            });
        } else { Gdx.app.error("MainMenuController", "LogoutButton is null.");}

    }

    private void handleContinueGameButtonClick() {
        Gdx.app.log("MainMenuController", "Attempting to continue saved game...");
        if (view != null && view.getWelcomeLabel() != null) {
            view.getWelcomeLabel().setText("Feature coming soon!");
        }
    }

    private void handlePreGameMenuButtonClick() {
        Gdx.app.log("MainMenuController", "Navigating to Pre-Game Menu...");
        if (Main.getMain().getScreen() != null) Main.getMain().getScreen().dispose();
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        Main.getMain().setScreen(new PreGameMenuView(new PreGameMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleHintMenuButtonClick() {
<<<<<<< HEAD
        Gdx.app.log("MainMenuController", "Navigating to Hint Menu...");
        if (Main.getMain().getScreen() != null) Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new HintMenuView(new HintMenuController()));
    }

    private void handleScoreboardMenuButtonClick() {
        Gdx.app.log("MainMenuController", "Navigating to Scoreboard Menu...");
        if (Main.getMain().getScreen() != null) Main.getMain().getScreen().dispose();
=======
        System.out.println("Navigating to Hint Menu...");
    }

    private void handleScoreboardMenuButtonClick() {
        System.out.println("Navigating to Scoreboard Menu...");
        Main.getMain().getScreen().dispose();
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        Main.getMain().setScreen(new ScoreBoardMenuView(new ScoreBoardMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleSettingMenuButtonClick() {
<<<<<<< HEAD
        Gdx.app.log("MainMenuController", "Navigating to Settings Menu...");
        if (Main.getMain().getScreen() != null) Main.getMain().getScreen().dispose();
=======
        System.out.println("Navigating to Settings Menu...");
        Main.getMain().getScreen().dispose();
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        Main.getMain().setScreen(new SettingsMenuView(new SettingsMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleProfileMenuButtonClick() {
<<<<<<< HEAD
        Gdx.app.log("MainMenuController", "Navigating to Profile Menu...");
        if (Main.getMain().getScreen() != null) Main.getMain().getScreen().dispose();
=======
        System.out.println("Navigating to Profile Menu...");
        Main.getMain().getScreen().dispose();
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        Main.getMain().setScreen(new ProfileMenuView(new ProfileMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleLogoutButtonClick() {
<<<<<<< HEAD
        Gdx.app.log("MainMenuController", "Logging out...");
        App.getInstance().setCurrentUser(null);
        if (Main.getMain().getScreen() != null) Main.getMain().getScreen().dispose();
=======
        System.out.println("Logging out...");
        App.getInstance().setCurrentUser(null);
        Main.getMain().getScreen().dispose();
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        Main.getMain().setScreen(new LoginMenuView(new LoginMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }
}
