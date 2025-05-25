package controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.User;
import view.MainMenuView;
import view.ScoreBoardMenuView;

import java.util.Comparator;

public class ScoreBoardMenuController {
    private ScoreBoardMenuView view;

    public enum SortOrder {
        USERNAME, SCORE, KILLS, TIME_ALIVE
    }

    private SortOrder currentSortOrder = SortOrder.SCORE;

    public void setView(ScoreBoardMenuView view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        view.getBackButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBackButtonClick();
            }
        });

        view.getSortByUsernameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentSortOrder = SortOrder.USERNAME;
                displayScoreboard(currentSortOrder);
            }
        });

        view.getSortByScoreButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentSortOrder = SortOrder.SCORE;
                displayScoreboard(currentSortOrder);
            }
        });

        view.getSortByKillsButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentSortOrder = SortOrder.KILLS;
                displayScoreboard(currentSortOrder);
            }
        });

        view.getSortByTimeAliveButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentSortOrder = SortOrder.TIME_ALIVE;
                displayScoreboard(currentSortOrder);
            }
        });
    }

    private void handleBackButtonClick() {
        System.out.println("Navigating back to Main Menu...");
        Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    public void displayScoreboard(SortOrder sortOrder) {
        if (sortOrder == null) {
            sortOrder = currentSortOrder;
        }

        Array<User> allUsers = App.getInstance().getUsers();
        if (allUsers == null || allUsers.size == 0) {
            view.getErrorLabel().setText("No user data available.");
            view.updateScoreboard(new Array<User>());
            return;
        }

        Array<User> usersToSort = new Array<>(allUsers);

        switch (sortOrder) {
            case USERNAME:
                usersToSort.sort(new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return u1.getUsername().compareToIgnoreCase(u2.getUsername());
                    }
                });
                break;
            case SCORE:

                usersToSort.sort(new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return Integer.compare(u2.getScore(), u1.getScore());
                    }
                });
                break;
            case KILLS:

                usersToSort.sort(new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return Integer.compare(u2.getTotalKill(), u1.getTotalKill());
                    }
                });
                break;
            case TIME_ALIVE:

                usersToSort.sort(new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return Integer.compare(u2.getMaximumTimeAlive(), u1.getMaximumTimeAlive());
                    }
                });
                break;
        }

        Array<User> topUsers = new Array<>();
        for (int i = 0; i < Math.min(usersToSort.size, 10); i++) {
            topUsers.add(usersToSort.get(i));
        }
        view.updateScoreboard(topUsers);
        view.getErrorLabel().setText("");
    }
}
