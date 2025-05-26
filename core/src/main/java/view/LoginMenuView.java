package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.LoginMenuController;
import graphic.source.Main;
import model.GameAssetManager;

public class LoginMenuView implements Screen {
    private Stage stage;
    private final TextButton loginButton;
    private final TextButton backButton;
    private final TextButton forgetPasswordButton;
    private final TextButton checkSecurityQuestionButton;
    private final TextButton changePasswordButton;
    private final Label menuLabel;
    private final Label usernameLabel;
    private final Label passwordLabel;
    private final Label questionLabel;
    private final Label newPasswordLabel;
    private final Label errorLabel;
    private final Label successfulLabel;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField answerField;
    private final TextField newPasswordField;
    private final Table table;
    private Image backgroundImage;
    private final LoginMenuController controller;

    public LoginMenuView(LoginMenuController controller, Skin skin){
        this.controller = controller;

        this.loginButton = new TextButton("Login", skin);
        this.backButton = new TextButton("Back to Register", skin);
        this.forgetPasswordButton = new TextButton("Forgot password?",skin);
        this.checkSecurityQuestionButton = new TextButton("Check Answer", skin);
        this.changePasswordButton = new TextButton("Change Password", skin);

        this.menuLabel = new Label("Login Menu", skin);
        this.usernameLabel = new Label("Username:" , skin);
        this.passwordLabel = new Label("Password:", skin);
        this.questionLabel = new Label("Security Question:", skin);
        this.newPasswordLabel = new Label("New Password:", skin);
        this.errorLabel = new Label("", skin);
        this.errorLabel.setColor(Color.RED);
        this.successfulLabel = new Label("", skin);
        this.successfulLabel.setColor(Color.GREEN);

        this.usernameField = new TextField("", skin);
        this.passwordField = new TextField("", skin);
        this.passwordField.setPasswordMode(true);
        this.passwordField.setPasswordCharacter('*');

        this.answerField = new TextField("", skin);
        this.newPasswordField = new TextField("", skin);
        this.newPasswordField.setPasswordMode(true);
        this.newPasswordField.setPasswordCharacter('*');

        this.table = new Table();
        this.controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Texture backgroundTexture = GameAssetManager.getGameAssetManager()
            .getTexture(GameAssetManager.getGameAssetManager()
                .getRandomBackgroundPath());

        if (backgroundTexture != null) {
            backgroundImage = new Image(backgroundTexture);
            backgroundImage.setFillParent(true);
            stage.addActor(backgroundImage);
        }
        table.clear();
        table.setFillParent(true);
        table.center();
        table.add(menuLabel).colspan(2).padBottom(30).row();
        table.add(errorLabel).colspan(2).center().padBottom(10).row();
        table.add(successfulLabel).colspan(2).center().padBottom(10).row();
        table.add(usernameLabel).right().padRight(10);
<<<<<<< HEAD
        table.add(usernameField).width(300).height(70).padBottom(20).row();
        table.add(passwordLabel).right().padRight(10);
        table.add(passwordField).width(300).height(70).padBottom(20).row();
        table.add(questionLabel).right().padRight(10);
        table.add(answerField).width(300).height(70).padBottom(20).row();
        table.add(newPasswordLabel).right().padRight(10);
        table.add(newPasswordField).width(300).height(70).padBottom(20).row();
=======
        table.add(usernameField).width(300).height(50).padBottom(20).row();
        table.add(passwordLabel).right().padRight(10);
        table.add(passwordField).width(300).height(50).padBottom(20).row();
        table.add(questionLabel).right().padRight(10);
        table.add(answerField).width(300).height(50).padBottom(20).row();
        table.add(newPasswordLabel).right().padRight(10);
        table.add(newPasswordField).width(300).height(50).padBottom(20).row();
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        table.add(loginButton).padRight(10);
        table.add(forgetPasswordButton).padLeft(10).row();
        table.add(checkSecurityQuestionButton).colspan(2).center().padTop(20).row();
        table.add(changePasswordButton).colspan(2).center().padTop(20).row();
<<<<<<< HEAD
        table.add(backButton).colspan(2).center().padTop(20).row();
=======
        table.add(backButton).colspan(2).center().padTop(30).row();
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        stage.addActor(table);
        table.pack();
        showLoginForm();
    }

    public void showLoginForm(){
        menuLabel.setText("Login Menu");
        errorLabel.setText("");
        successfulLabel.setText("");
        usernameLabel.setVisible(true);
        usernameField.setVisible(true);
        passwordLabel.setVisible(true);
        passwordField.setVisible(true);
        loginButton.setVisible(true);
        loginButton.setTouchable(Touchable.enabled);
        forgetPasswordButton.setVisible(true);
        forgetPasswordButton.setTouchable(Touchable.enabled);
        questionLabel.setVisible(false);
        answerField.setVisible(false);
        checkSecurityQuestionButton.setVisible(false);
        checkSecurityQuestionButton.setTouchable(Touchable.disabled);
        newPasswordLabel.setVisible(false);
        newPasswordField.setVisible(false);
        changePasswordButton.setVisible(false);
        changePasswordButton.setTouchable(Touchable.disabled);
        backButton.setVisible(true);
        backButton.setTouchable(Touchable.enabled);
        usernameField.setText("");
        passwordField.setText("");
        answerField.setText("");
        newPasswordField.setText("");
    }

    public void showSecurityQuestionForm(String question){
        menuLabel.setText("Forgot Password");
        errorLabel.setText("");
        successfulLabel.setText("");
        passwordLabel.setVisible(false);
        passwordField.setVisible(false);
        loginButton.setVisible(false);
        loginButton.setTouchable(Touchable.disabled);
        forgetPasswordButton.setVisible(false);
        forgetPasswordButton.setTouchable(Touchable.disabled);
        questionLabel.setText(question);
        questionLabel.setVisible(true);
        answerField.setVisible(true);
        checkSecurityQuestionButton.setVisible(true);
        checkSecurityQuestionButton.setTouchable(Touchable.enabled);
        newPasswordLabel.setVisible(false);
        newPasswordField.setVisible(false);
        changePasswordButton.setVisible(false);
        changePasswordButton.setTouchable(Touchable.disabled);
        backButton.setVisible(true);
        backButton.setTouchable(Touchable.enabled);
        answerField.setText("");
    }

    public void showChangePasswordForm(){
        menuLabel.setText("Change Password");
        errorLabel.setText("");
        successfulLabel.setText("");
        questionLabel.setVisible(false);
        answerField.setVisible(false);
        checkSecurityQuestionButton.setVisible(false);
        checkSecurityQuestionButton.setTouchable(Touchable.disabled);
        newPasswordLabel.setVisible(true);
        newPasswordField.setVisible(true);
        changePasswordButton.setVisible(true);
        changePasswordButton.setTouchable(Touchable.enabled);
        passwordLabel.setVisible(false);
        passwordField.setVisible(false);
        loginButton.setVisible(false);
        loginButton.setTouchable(Touchable.disabled);
        forgetPasswordButton.setVisible(false);
        forgetPasswordButton.setTouchable(Touchable.disabled);
        backButton.setVisible(true);
        backButton.setTouchable(Touchable.enabled);
        newPasswordField.setText("");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();

    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }

    public TextField getAnswerField() {
        return answerField;
    }

    public TextField getNewPasswordField() {
        return newPasswordField;
    }

    public TextButton getLoginButton() {
        return loginButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public TextButton getForgetPasswordButton() {
        return forgetPasswordButton;
    }

    public TextButton getCheckSecurityQuestionButton() {
        return checkSecurityQuestionButton;
    }

    public TextButton getChangePasswordButton() {
        return changePasswordButton;
    }



    public Label getMenuLabel() {
        return menuLabel;
    }

    public Label getQuestionLabel() {
        return questionLabel;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public Label getSuccessfulLabel() {
        return successfulLabel;
    }
}
