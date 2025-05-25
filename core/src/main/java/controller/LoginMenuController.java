package controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.User;
import view.LoginMenuView;
import view.RegisterMenuView;

import java.util.regex.Pattern;

public class LoginMenuController {
    private LoginMenuView view;
    private User currentUserAttemptingPasswordReset;

    public void setView(LoginMenuView view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        view.getLoginButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLoginButtonClick();
            }
        });
        view.getBackButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBackButtonClick();
            }
        });
        view.getForgetPasswordButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleForgotPasswordButtonClick();
            }
        });
        view.getCheckSecurityQuestionButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleCheckSecurityQuestionButtonClick();
            }
        });
        view.getChangePasswordButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleChangePasswordButtonClick();
            }
        });
    }

    private void handleLoginButtonClick() {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();
        view.getErrorLabel().setText("");
        view.getSuccessfulLabel().setText("");

        if (username.isEmpty() || password.isEmpty()) {
            view.getErrorLabel().setText("Username and password cannot be empty.");
            return;
        }

        User user = App.getInstance().getUserByUsername(username);
        if (user == null) {
            view.getErrorLabel().setText("Username not found.");
            return;
        }

        if (user.getPassword().equals(password)) {
            App.getInstance().setCurrentUser(user);
            view.getSuccessfulLabel().setText("Login successful!");
            System.out.println("User " + username + " logged in successfully.");
            Main.getMain().setScreen(new view.MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        }

        else {
            view.getErrorLabel().setText("Incorrect password.");
        }
    }

    private void handleBackButtonClick() {
        System.out.println("Navigating to Register Menu...");
        Main.getMain().setScreen(new RegisterMenuView(new RegisterMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleForgotPasswordButtonClick() {
        String username = view.getUsernameField().getText();
        view.getErrorLabel().setText("");
        view.getSuccessfulLabel().setText("");

        if (username.isEmpty()) {
            view.getErrorLabel().setText("Please enter your username to reset password.");
            return;
        }

        currentUserAttemptingPasswordReset = App.getInstance().getUserByUsername(username);
        if (currentUserAttemptingPasswordReset == null) {
            view.getErrorLabel().setText("Username not found.");
            return;
        }

        String securityQuestion = "What is your favorite animal?";
        view.showSecurityQuestionForm(securityQuestion);
    }

    private void handleCheckSecurityQuestionButtonClick() {
        String answer = view.getAnswerField().getText();
        view.getErrorLabel().setText("");
        view.getSuccessfulLabel().setText("");

        if (answer.isEmpty()) {
            view.getErrorLabel().setText("Please enter your answer.");
            return;
        }

        if (currentUserAttemptingPasswordReset == null) {
            view.getErrorLabel().setText("No user selected for password reset. Go back to login.");
            view.showLoginForm();
            return;
        }

        if (answer.equalsIgnoreCase(currentUserAttemptingPasswordReset.getSecurityAnswer())) {
            view.getSuccessfulLabel().setText("Security question answered correctly!");
            view.showChangePasswordForm();
        } else {
            view.getErrorLabel().setText("Incorrect answer. Please try again.");
        }
    }

    private void handleChangePasswordButtonClick() {
        String newPassword = view.getNewPasswordField().getText();
        view.getErrorLabel().setText("");
        view.getSuccessfulLabel().setText("");

        if (newPassword.isEmpty()) {
            view.getErrorLabel().setText("New password cannot be empty.");
            return;
        }
        if (!isPasswordValid(newPassword)) {
            view.getErrorLabel().setText("New password too weak! (Min 8 chars, 1 upper, 1 lower, 1 special)");
            return;
        }

        if (currentUserAttemptingPasswordReset == null) {
            view.getErrorLabel().setText("Error: User context lost. Please try again from login.");
            view.showLoginForm();
            return;
        }


        currentUserAttemptingPasswordReset.setPassword(newPassword);
        view.getSuccessfulLabel().setText("Password changed successfully! You can now log in.");
        System.out.println("Password for " + currentUserAttemptingPasswordReset.getUsername() + " changed to: " + newPassword);


        view.showLoginForm();
        currentUserAttemptingPasswordReset = null;
    }

    private boolean isPasswordValid(String password){
        Pattern special = Pattern.compile("[^a-zA-Z0-9 ]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern upper = Pattern.compile("[A-Z]");

        return password.length() >= 8 && special.matcher(password).find() && lower.matcher(password).find() && upper.matcher(password).find();
    }
}
