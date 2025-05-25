package controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.User;
import view.LoginMenuView;
import view.MainMenuView;
import view.RegisterMenuView;

import java.util.regex.Pattern;

public class RegisterMenuController {
    private RegisterMenuView view;

    public void setView(RegisterMenuView view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        view.getRegisterButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleRegisterButtonClick();
            }
        });
        view.getPlayAsGuestButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handlePlayAsGuestButtonClick();
            }
        });
        view.getGoToLoginMenuButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleGoToLoginButtonClick();
            }
        });
        view.getGotoLoginMenuButton2().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleGoToLoginButtonClick();
            }
        });
    }

    public void handleRegisterButtonClick() {
        if (view == null) return;
        String username = view.getUsernameField().getText().trim();
        String password = view.getPasswordField().getText();
        String answer = view.getAnswerField().getText().trim();
        view.getMessageLabel().setText("");
        view.getMessageLabel().setColor(com.badlogic.gdx.graphics.Color.RED);
        if (username.isEmpty() || password.isEmpty() || answer.isEmpty()) {
            view.getMessageLabel().setText("All fields must be filled!");
            return;
        }
        if (!isUsernameAvailable(username)) {
            view.getMessageLabel().setText("Username already taken!");
            return;
        }
        if (!isPasswordValid(password)) {
            view.getMessageLabel().setText("Password too weak! (Min 8 chars, 1 upper, 1 lower, 1 special)");
            return;
        }
        User newUser = new User(username, password, answer);
        String randomAvatarPath = GameAssetManager.getGameAssetManager().getRandomAvatarPath();
        if (randomAvatarPath != null) {
            newUser.setAvatarPath(randomAvatarPath);
        }
        else {
            newUser.setAvatarPath(GameAssetManager.getGameAssetManager().DEFAULT_AVATAR_PATH);
            System.err.println("No random avatars available, assigned default.");
        }

        App.getInstance().addUsers(newUser);
        App.getInstance().saveUsers();
        System.out.println("User registered successfully - Username: " + username + ", Password: " + password + ", Avatar: " + newUser.getAvatarPath());
        view.showRegistrationSuccess();
    }

    public void handlePlayAsGuestButtonClick() {
        System.out.println("Playing as guest...");
        App.getInstance().setCurrentUser(null);
        Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    public void handleGoToLoginButtonClick() {
        System.out.println("Navigating to Login Menu...");
        Main.getMain().setScreen(new LoginMenuView(new LoginMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private boolean isUsernameAvailable(String username){
        for(User user : App.getInstance().getUsers()){
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPasswordValid(String password){
        Pattern special = Pattern.compile("[^a-zA-Z0-9 ]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern upper = Pattern.compile("[A-Z]");
        return password.length() >= 8 && special.matcher(password).find() && lower.matcher(password).find() && upper.matcher(password).find();
    }
}
