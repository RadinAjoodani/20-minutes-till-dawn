package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ProfileMenuController;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.User;

public class ProfileMenuView implements Screen {
    private Stage stage;
    private final TextButton backButtonMain;
    private final TextButton backButtonUsernameForm;
    private final TextButton backButtonPasswordForm;
    private final TextButton changeUsernameButton;
    private final TextButton changePasswordButton;
    private final TextButton deleteAccountButton;
    private final TextButton chooseAvatarGalleryButton;
    private final TextButton chooseAvatarFileButton;
    private final TextButton confirmNewUsernameButton;
    private final TextButton confirmNewPasswordButton;
    private final TextButton confirmDeleteAccountButton;
    private final TextButton cancelDeleteAccountButton;

    private final Label menuLabel;
    private final Label currentUsernameLabel;
    private final Label currentAvatarLabel;
    private final Image currentAvatarImage;
    private final Label newUsernameLabel;
    private final Label currentPasswordVerifyLabel;
    private final Label newPasswordLabel;
    private final Label confirmNewPasswordLabel;
    private final Label errorLabel;
    private final Label successfulLabel;
    private final Label deleteConfirmationLabel;

    private final TextField newUsernameField;
    private final TextField currentPasswordVerifyField;
    private final TextField newPasswordField;
    private final TextField confirmNewPasswordField;

    private final Table rootTable;
    private final Table mainOptionsTable;
    private final Table changeUsernameTable;
    private final Table changePasswordTable;
    private final Table avatarGalleryTable;
    private final Table confirmationDialogTable;

    private Image backgroundImage;

    private final Skin skin;
    private final ProfileMenuController controller;

    public ProfileMenuView(ProfileMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;

        this.backButtonMain = new TextButton("Back to Main Menu", skin);
        this.backButtonUsernameForm = new TextButton("Back", skin);
        this.backButtonPasswordForm = new TextButton("Back", skin);

        this.changeUsernameButton = new TextButton("Change Username", skin);
        this.changePasswordButton = new TextButton("Change Password", skin);
        this.deleteAccountButton = new TextButton("Delete Account", skin);
        this.chooseAvatarGalleryButton = new TextButton("Choose from Gallery", skin);
        this.chooseAvatarFileButton = new TextButton("Select from System", skin);
        this.confirmNewUsernameButton = new TextButton("Confirm Username Change", skin);
        this.confirmNewPasswordButton = new TextButton("Confirm Password Change", skin);
        this.confirmDeleteAccountButton = new TextButton("Yes, Delete My Account", skin);
        this.cancelDeleteAccountButton = new TextButton("No, Keep My Account", skin);

        this.menuLabel = new Label("Profile Menu", skin);
        this.currentUsernameLabel = new Label("Username: ", skin);
        this.currentAvatarLabel = new Label("Current Avatar:", skin);
        this.currentAvatarImage = new Image(new TextureRegionDrawable(
            new TextureRegion(GameAssetManager.getGameAssetManager().getTexture(
                GameAssetManager.getGameAssetManager().DEFAULT_AVATAR_PATH
            ))
        ));
        this.newUsernameLabel = new Label("New Username:", skin);
        this.currentPasswordVerifyLabel = new Label("Current Password:", skin);
        this.newPasswordLabel = new Label("New Password:", skin);
        this.confirmNewPasswordLabel = new Label("Confirm New Password:", skin);
        this.errorLabel = new Label("", skin);
        this.errorLabel.setColor(com.badlogic.gdx.graphics.Color.RED);
        this.successfulLabel = new Label("", skin);
        this.successfulLabel.setColor(com.badlogic.gdx.graphics.Color.GREEN);
        this.deleteConfirmationLabel = new Label("Are you sure you want to delete your account? This cannot be undone!", skin);
        this.deleteConfirmationLabel.setWrap(true);

        this.newUsernameField = new TextField("", skin);
        this.currentPasswordVerifyField = new TextField("", skin);
        this.currentPasswordVerifyField.setPasswordMode(true);
        this.currentPasswordVerifyField.setPasswordCharacter('*');
        this.newPasswordField = new TextField("", skin);
        this.newPasswordField.setPasswordMode(true);
        this.newPasswordField.setPasswordCharacter('*');
        this.confirmNewPasswordField = new TextField("", skin);
        this.confirmNewPasswordField.setPasswordMode(true);
        this.confirmNewPasswordField.setPasswordCharacter('*');

        this.rootTable = new Table(skin);
        this.mainOptionsTable = new Table(skin);
        this.changeUsernameTable = new Table(skin);
        this.changePasswordTable = new Table(skin);
        this.avatarGalleryTable = new Table(skin);
        this.confirmationDialogTable = new Table(skin);
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
        rootTable.add(errorLabel).colspan(2).center().padBottom(10).row();
        rootTable.add(successfulLabel).colspan(2).center().padBottom(10).row();
        setupMainOptionsTable();
        setupChangeUsernameTable();
        setupChangePasswordTable();
        setupAvatarGalleryTable();
        setupConfirmationDialogTable();
        rootTable.add().colspan(2).grow().row();
        stage.addActor(rootTable);
        rootTable.pack();
        showMainProfileOptions();
    }



    private void setupMainOptionsTable() {
        mainOptionsTable.clear();
        mainOptionsTable.defaults().width(500).height(100).pad(10);
        Table userInfoRow = new Table(skin);
        userInfoRow.add(currentAvatarLabel).right().padRight(10);
        userInfoRow.add(currentAvatarImage).size(80, 80).pad(10).left().row();
        userInfoRow.add(currentUsernameLabel).colspan(2).left().padBottom(15).row();
        mainOptionsTable.add(userInfoRow).colspan(2).padBottom(20).row();
        mainOptionsTable.add(changeUsernameButton).row();
        mainOptionsTable.add(changePasswordButton).row();
        mainOptionsTable.add(chooseAvatarGalleryButton).row();
        mainOptionsTable.add(chooseAvatarFileButton).row();
        mainOptionsTable.add(deleteAccountButton).row();
        mainOptionsTable.add(backButtonMain).padTop(30).row();
        mainOptionsTable.pack();
    }

    private void setupChangeUsernameTable() {
        changeUsernameTable.clear();
        changeUsernameTable.defaults().pad(10);
        changeUsernameTable.add(newUsernameLabel).right();
        changeUsernameTable.add(newUsernameField).width(500).height(80).row();
        changeUsernameTable.add(confirmNewUsernameButton).colspan(2).width(300).height(80).padTop(20).row();
        changeUsernameTable.add(backButtonUsernameForm).colspan(2).width(300).height(80).padTop(10).row();
        changeUsernameTable.pack();
    }

    private void setupChangePasswordTable() {
        changePasswordTable.clear();
        changePasswordTable.defaults().pad(10);
        changePasswordTable.add(currentPasswordVerifyLabel).right();
        changePasswordTable.add(currentPasswordVerifyField).width(500).height(80).row();
        changePasswordTable.add(newPasswordLabel).right();
        changePasswordTable.add(newPasswordField).width(500).height(80).row();
        changePasswordTable.add(confirmNewPasswordLabel).right();
        changePasswordTable.add(confirmNewPasswordField).width(500).height(80).row();
        changePasswordTable.add(confirmNewPasswordButton).colspan(2).width(500).height(80).padTop(20).row();
        changePasswordTable.add(backButtonPasswordForm).colspan(2).width(500).height(80).padTop(10).row();
        changePasswordTable.pack();
    }

    private void setupAvatarGalleryTable() {
        avatarGalleryTable.pad(20);
        avatarGalleryTable.defaults().pad(10);
        avatarGalleryTable.add(new Label("Select Your Avatar", skin)).colspan(4).padBottom(20).row();
        Array<String> avatarPaths = GameAssetManager.getGameAssetManager().getAllAvatarPaths();
        int i = 0;
        for (String path : avatarPaths) {
            Texture avatarTexture = GameAssetManager.getGameAssetManager().getTexture(path);
            if (avatarTexture != null) {
                ImageButton avatarButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(avatarTexture)));
                avatarButton.setUserObject(path);
                avatarGalleryTable.add(avatarButton).size(80, 80);
                controller.addAvatarSelectionListener(avatarButton);
                i++;
                if (i % 4 == 0) {
                    avatarGalleryTable.row();
                }
            }
        }
        avatarGalleryTable.row();
        TextButton backToProfileButton = new TextButton("Back to Profile", skin);
        controller.addBackFromGalleryListener(backToProfileButton);
        avatarGalleryTable.add(backToProfileButton).colspan(4).padTop(20).row();
        avatarGalleryTable.pack();
    }

    private void setupConfirmationDialogTable() {
        confirmationDialogTable.pad(20);
        confirmationDialogTable.defaults().pad(10);
        confirmationDialogTable.add(deleteConfirmationLabel).width(500).center().pad(20).row();
        confirmationDialogTable.add(confirmDeleteAccountButton).width(500).height(80).pad(10);
        confirmationDialogTable.add(cancelDeleteAccountButton).width(500).height(80).pad(10).row();
        confirmationDialogTable.pack();
    }

    private void showOnlyTable(Table tableToShow) {
        Cell<Table> contentCell = rootTable.getCells().get(rootTable.getCells().size - 1);
        contentCell.setActor(tableToShow);
        tableToShow.setVisible(true);
        rootTable.pack();
        rootTable.setPosition(stage.getWidth() / 2 - rootTable.getWidth() / 2,
            stage.getHeight() / 2 - rootTable.getHeight() / 2);
    }

    public void showMainProfileOptions() {
        menuLabel.setText("Profile Menu");
        errorLabel.setText("");
        successfulLabel.setText("");
        User currentUser = App.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUsernameLabel.setText("Username: " + currentUser.getUsername());
            currentAvatarImage.setDrawable(new TextureRegionDrawable(new TextureRegion(currentUser.getAvatarTexture())));
        } else {
            currentUsernameLabel.setText("Username: N/A (Guest)");
            currentAvatarImage.setDrawable(new TextureRegionDrawable(
                new TextureRegion(GameAssetManager.getGameAssetManager().getTexture(
                    GameAssetManager.getGameAssetManager().DEFAULT_AVATAR_PATH
                ))
            ));
        }
        showOnlyTable(mainOptionsTable);
        newUsernameField.setText("");
        currentPasswordVerifyField.setText("");
        newPasswordField.setText("");
        confirmNewPasswordField.setText("");
    }

    public void showChangeUsernameForm() {
        menuLabel.setText("Change Username");
        errorLabel.setText("");
        successfulLabel.setText("");
        showOnlyTable(changeUsernameTable);
        newUsernameField.setText("");
    }

    public void showChangePasswordForm() {
        menuLabel.setText("Change Password");
        errorLabel.setText("");
        successfulLabel.setText("");
        showOnlyTable(changePasswordTable);
        currentPasswordVerifyField.setText("");
        newPasswordField.setText("");
        confirmNewPasswordField.setText("");
    }

    public void showAvatarGallery() {
        menuLabel.setText("Choose Avatar");
        errorLabel.setText("");
        successfulLabel.setText("");
        showOnlyTable(avatarGalleryTable);
        avatarGalleryTable.setPosition(stage.getWidth() / 2 - avatarGalleryTable.getWidth() / 2,
            stage.getHeight() / 2 - avatarGalleryTable.getHeight() / 2);
    }

    public void showDeleteConfirmationDialog() {
        errorLabel.setText("");
        successfulLabel.setText("");
        showOnlyTable(confirmationDialogTable);
        confirmationDialogTable.setPosition(stage.getWidth() / 2 - confirmationDialogTable.getWidth() / 2,
            stage.getHeight() / 2 - confirmationDialogTable.getHeight() / 2);
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
        if (avatarGalleryTable.isVisible()) {
            avatarGalleryTable.setPosition(stage.getWidth() / 2 - avatarGalleryTable.getWidth() / 2,
                stage.getHeight() / 2 - avatarGalleryTable.getHeight() / 2);
        }
        if (confirmationDialogTable.isVisible()) {
            confirmationDialogTable.setPosition(stage.getWidth() / 2 - confirmationDialogTable.getWidth() / 2,
                stage.getHeight() / 2 - confirmationDialogTable.getHeight() / 2);
        }
        rootTable.pack();
        rootTable.setPosition(stage.getWidth() / 2 - rootTable.getWidth() / 2,
            stage.getHeight() / 2 - rootTable.getHeight() / 2);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }

    public TextButton getBackButtonMain() { return backButtonMain; }
    public TextButton getBackButtonUsernameForm() { return backButtonUsernameForm; }
    public TextButton getBackButtonPasswordForm() { return backButtonPasswordForm; }

    public TextButton getChangeUsernameButton() { return changeUsernameButton; }
    public TextButton getChangePasswordButton() { return changePasswordButton; }
    public TextButton getDeleteAccountButton() { return deleteAccountButton; }
    public TextButton getChooseAvatarGalleryButton() { return chooseAvatarGalleryButton; }
    public TextButton getChooseAvatarFileButton() { return chooseAvatarFileButton; }
    public TextButton getConfirmNewUsernameButton() { return confirmNewUsernameButton; }
    public TextButton getConfirmNewPasswordButton() { return confirmNewPasswordButton; }
    public TextButton getConfirmDeleteAccountButton() { return confirmDeleteAccountButton; }
    public TextButton getCancelDeleteAccountButton() { return cancelDeleteAccountButton; }

    public Label getErrorLabel() { return errorLabel; }
    public Label getSuccessfulLabel() { return successfulLabel; }
    public TextField getNewUsernameField() { return newUsernameField; }
    public TextField getCurrentPasswordVerifyField() { return currentPasswordVerifyField; }
    public TextField getNewPasswordField() { return newPasswordField; }
    public TextField getConfirmNewPasswordField() { return confirmNewPasswordField; }
    public Image getCurrentAvatarImage() { return currentAvatarImage; }
}
