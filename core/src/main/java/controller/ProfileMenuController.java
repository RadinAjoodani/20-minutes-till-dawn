package controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.User;
import view.LoginMenuView;
import view.MainMenuView;
import view.ProfileMenuView;

import javax.swing.*;
import java.io.*;
import java.util.regex.Pattern;

public class ProfileMenuController {
    private ProfileMenuView view;

    public void setView(ProfileMenuView view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        view.getBackButtonMain().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBackButtonClick();
            }
        });

        view.getChangeUsernameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showChangeUsernameForm();
            }
        });
        view.getConfirmNewUsernameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleChangeUsername();
            }
        });

        view.getBackButtonUsernameForm().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showMainProfileOptions();
            }
        });


        view.getChangePasswordButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showChangePasswordForm();
            }
        });
        view.getConfirmNewPasswordButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleChangePassword();
            }
        });

        view.getBackButtonPasswordForm().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showMainProfileOptions();
            }
        });


        view.getDeleteAccountButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showDeleteConfirmationDialog();
            }
        });
        view.getConfirmDeleteAccountButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleDeleteAccount();
            }
        });
        view.getCancelDeleteAccountButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showMainProfileOptions();
            }
        });

        view.getChooseAvatarGalleryButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showAvatarGallery();
            }
        });

        view.getChooseAvatarFileButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleChooseAvatarFromFileSystem();
            }
        });
    }


    public void addAvatarSelectionListener(ImageButton avatarButton) {
        avatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selectedAvatarPath = (String) avatarButton.getUserObject();
                handleAvatarSelected(selectedAvatarPath);
            }
        });
    }


    public void addBackFromGalleryListener(TextButton backButton) {
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.showMainProfileOptions();
            }
        });
    }




    private void handleBackButtonClick() {
        System.out.println("Navigating back to Main Menu...");
        Main.getMain().setScreen(new MainMenuView(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    private void handleChangeUsername() {
        User currentUser = App.getInstance().getCurrentUser();
        if (currentUser == null) {
            view.getErrorLabel().setText("No user logged in.");
            return;
        }

        String newUsername = view.getNewUsernameField().getText().trim();

        view.getErrorLabel().setText("");
        view.getSuccessfulLabel().setText("");

        if (newUsername.isEmpty()) {
            view.getErrorLabel().setText("New username cannot be empty.");
            return;
        }

        if (newUsername.equals(currentUser.getUsername())) {
            view.getErrorLabel().setText("New username is the same as current username.");
            return;
        }

        User existingUserWithNewName = App.getInstance().getUserByUsername(newUsername);
        if (existingUserWithNewName != null) {
            view.getErrorLabel().setText("Username '" + newUsername + "' is already taken.");
            return;
        }

        currentUser.setUsername(newUsername);
        App.getInstance().saveUsers();

        view.getSuccessfulLabel().setText("Username changed successfully to '" + newUsername + "'!");
        System.out.println("Username changed for " + currentUser.getUsername());
        view.showMainProfileOptions();
    }

    private void handleChangePassword() {
        User currentUser = App.getInstance().getCurrentUser();
        if (currentUser == null) {
            view.getErrorLabel().setText("No user logged in.");
            return;
        }

        String currentPasswordVerify = view.getCurrentPasswordVerifyField().getText();
        String newPassword = view.getNewPasswordField().getText();
        String confirmNewPassword = view.getConfirmNewPasswordField().getText();

        view.getErrorLabel().setText("");
        view.getSuccessfulLabel().setText("");

        if (!currentUser.getPassword().equals(currentPasswordVerify)) {
            view.getErrorLabel().setText("Incorrect current password.");
            return;
        }

        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            view.getErrorLabel().setText("New password fields cannot be empty.");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            view.getErrorLabel().setText("New passwords do not match.");
            return;
        }

        if (!isPasswordValid(newPassword)) {
            view.getErrorLabel().setText("New password too weak! (Min 8 chars, 1 upper, 1 lower, 1 special)");
            return;
        }

        currentUser.setPassword(newPassword);
        App.getInstance().saveUsers();

        view.getSuccessfulLabel().setText("Password changed successfully!");
        System.out.println("Password changed for " + currentUser.getUsername());
        view.showMainProfileOptions();
    }

    private void handleDeleteAccount() {
        User currentUser = App.getInstance().getCurrentUser();
        if (currentUser == null) {
            view.getErrorLabel().setText("No user logged in to delete.");
            view.showMainProfileOptions();
            return;
        }

        boolean removed = App.getInstance().removeUser(currentUser);

        if (removed) {
            App.getInstance().setCurrentUser(null);
            view.getSuccessfulLabel().setText("Account deleted successfully!");
            System.out.println("Account for " + currentUser.getUsername() + " deleted.");
            Main.getMain().setScreen(new LoginMenuView(new LoginMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        } else {
            view.getErrorLabel().setText("Failed to delete account.");
            System.err.println("Failed to delete account for " + currentUser.getUsername() + ". User not found in App list.");
            view.showMainProfileOptions();
        }
    }

    private void handleAvatarSelected(String avatarPath) {
        User currentUser = App.getInstance().getCurrentUser();
        if (currentUser == null) {
            view.getErrorLabel().setText("No user logged in to change avatar.");
            view.showMainProfileOptions();
            return;
        }

        currentUser.setAvatarPath(avatarPath);
        App.getInstance().saveUsers();

        view.getCurrentAvatarImage().setDrawable(
            new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
                new com.badlogic.gdx.graphics.g2d.TextureRegion(
                    GameAssetManager.getGameAssetManager().getTexture(avatarPath)
                )
            )
        );

        view.getSuccessfulLabel().setText("Avatar changed successfully!");
        System.out.println("Avatar changed for " + currentUser.getUsername() + " to " + avatarPath);
        view.showMainProfileOptions();
    }

    private void handleChooseAvatarFromFileSystem() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose Avatar Image");
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            String fileName = selectedFile.getName().toLowerCase();
            if (!(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))) {
                view.getErrorLabel().setText("Only PNG and JPG images are supported.");
                return;
            }

            try {
                File destFolder = new File("avatars/customs");
                if (!destFolder.exists()) destFolder.mkdirs();

                File destFile = new File(destFolder, selectedFile.getName());
                try (InputStream in = new FileInputStream(selectedFile);
                     OutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }

                String avatarPath = "avatars/customs/" + selectedFile.getName();

                GameAssetManager.getGameAssetManager().getAssetManager().load(avatarPath, Texture.class);
                GameAssetManager.getGameAssetManager().getAssetManager().finishLoading();

                App.getInstance().getCurrentUser().setAvatarPath(avatarPath);
                App.getInstance().saveUsers();

                view.getErrorLabel().setText("Avatar successfully set.");

            } catch (IOException e) {
                e.printStackTrace();
                view.getErrorLabel().setText("Error copying the selected image.");
            }

        } else {
            view.getErrorLabel().setText("Avatar selection cancelled.");
        }
    }


    public void handleDragAndDropAvatar(String filePath) {
        view.getErrorLabel().setText("Drag and drop avatar is a platform-specific feature and is not yet implemented.");
        System.out.println("Drag and drop avatar requested with file: " + filePath);
    }

    private boolean isPasswordValid(String password){
        Pattern special = Pattern.compile("[^a-zA-Z0-9 ]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern upper = Pattern.compile("[A-Z]");

        return password.length() >= 8 && special.matcher(password).find() && lower.matcher(password).find() && upper.matcher(password).find();
    }
}
