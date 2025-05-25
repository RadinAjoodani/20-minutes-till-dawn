package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.RegisterMenuController;
import graphic.source.Main;
import model.GameAssetManager;

public class RegisterMenuView implements Screen {
    private Stage stage;
    private final TextButton registerButton;
    private final TextButton playAsGuestButton;
    private final TextButton goToLoginMenuButton;
    private final TextButton gotoLoginMenuButton2;
    private final Label menuLabel;
    private final Label usernameLabel;
    private final Label passwordLabel;
    private final Label questionLabel;
    private final Label messageLabel;
    private final Label successMessageLabel;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField answerField;
    private final Table rootTable;
    private final Table registrationFormTable;
    private final Table successMessageTable;
    private Image backgroundImage;
    private final Skin skin;
    private final RegisterMenuController controller;

    public RegisterMenuView(RegisterMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.registerButton = new TextButton("Register", skin);
        this.playAsGuestButton = new TextButton("Play as Guest", skin);
        this.goToLoginMenuButton = new TextButton("Go to Login Menu", skin);
        this.gotoLoginMenuButton2 = new TextButton("Go to Login Menu", skin);
        this.menuLabel = new Label("Register", skin);
        this.usernameLabel = new Label("Username: ", skin);
        this.passwordLabel = new Label("Password: ", skin);
        this.questionLabel = new Label("What is your favorite animal? ", skin);
        this.messageLabel = new Label("", skin);
        this.messageLabel.setColor(Color.RED);
        this.successMessageLabel = new Label("Registration successful!", skin);
        this.successMessageLabel.setColor(Color.GREEN);
        this.usernameField = new TextField("", skin);
        this.passwordField = new TextField("", skin);
        this.answerField = new TextField("",skin);
        this.passwordField.setPasswordMode(true);
        this.passwordField.setPasswordCharacter('*');
        this.rootTable = new Table(skin);
        this.registrationFormTable = new Table(skin);
        this.successMessageTable = new Table(skin);
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
        rootTable.clear();
        rootTable.setFillParent(true);
        rootTable.center();
        rootTable.add(menuLabel).colspan(2).padBottom(30).row();
        rootTable.add(messageLabel).colspan(2).center().padBottom(10).row();
        setupRegistrationFormTable();
        setupSuccessMessageTable();
        rootTable.add().colspan(2).grow().row();
        stage.addActor(rootTable);
        rootTable.pack();
        showRegistrationForm();
    }

    private void setupRegistrationFormTable() {
        registrationFormTable.clear();
        registrationFormTable.defaults().padBottom(20);
        registrationFormTable.add(usernameLabel).right().padRight(10);
        registrationFormTable.add(usernameField).width(300).height(80).row();
        registrationFormTable.add(passwordLabel).right().padRight(10);
        registrationFormTable.add(passwordField).width(300).height(80).row();
        registrationFormTable.add(questionLabel).right().padRight(10);
        registrationFormTable.add(answerField).width(300).height(80).row();
        registrationFormTable.add(registerButton).padRight(10);
        registrationFormTable.add(playAsGuestButton).padLeft(10).row();
        registrationFormTable.add(gotoLoginMenuButton2).colspan(2).center().padTop(20).row();
        registrationFormTable.pack();
    }

    private void setupSuccessMessageTable() {
        successMessageTable.clear();
        successMessageTable.defaults().padBottom(20);
        successMessageTable.add(successMessageLabel).center().row();
        successMessageTable.add(goToLoginMenuButton).center().padTop(20).row();
        successMessageTable.pack();
    }

    private void showOnlyTable(Table tableToShow) {
        Cell<Table> contentCell = rootTable.getCells().get(rootTable.getCells().size - 1);
        contentCell.setActor(tableToShow);
        rootTable.pack();
        rootTable.setPosition(stage.getWidth() / 2 - rootTable.getWidth() / 2,
            stage.getHeight() / 2 - rootTable.getHeight() / 2);
    }

    public void showRegistrationForm() {
        menuLabel.setText("Register");
        messageLabel.setText("");
        usernameField.setText("");
        passwordField.setText("");
        answerField.setText("");
        showOnlyTable(registrationFormTable);
    }

    public void showRegistrationSuccess() {
        menuLabel.setText("Registration Complete!");
        messageLabel.setText("");
        showOnlyTable(successMessageTable);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        rootTable.pack();
        rootTable.setPosition(stage.getWidth() / 2 - rootTable.getWidth() / 2,
            stage.getHeight() / 2 - rootTable.getHeight() / 2);
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
    public TextButton getRegisterButton() {
        return registerButton;
    }
    public TextButton getPlayAsGuestButton() {
        return playAsGuestButton;
    }
    public TextButton getGoToLoginMenuButton() {
        return goToLoginMenuButton;
    }

    public TextButton getGotoLoginMenuButton2() {
        return gotoLoginMenuButton2;
    }

    public Label getMenuLabel() {
        return menuLabel;
    }
    public Label getMessageLabel() {
        return messageLabel;
    }
    public Label getSuccessMessageLabel() {
        return successMessageLabel;
    }
    public TextField getAnswerField() {
        return answerField;
    }
}
