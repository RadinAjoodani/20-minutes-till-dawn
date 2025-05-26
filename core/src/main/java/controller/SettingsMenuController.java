
package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import graphic.source.Main;
import model.App;
import model.GameAssetManager;
import model.GameSettings;
import view.MainMenuView;
import view.SettingsMenuView;

public class SettingsMenuController {
    private SettingsMenuView view;
    private boolean isWaitingForKeyPress = false;
    private String actionToRebind = null;
    private TextButton currentKeyBindingButton = null;

    public void setView(SettingsMenuView view) {
        this.view = view;
    }

    public void attachListeners() {
        view.getMusicVolumeSlider().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                App.getInstance().getGameSettings().setMusicVolume(view.getMusicVolumeSlider().getValue());
            }
        });

        view.getMusicTrackSelectBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                App.getInstance().getGameSettings().setCurrentMusicTrack(view.getMusicTrackSelectBox().getSelected());
            }
        });

        view.getSfxCheckBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                App.getInstance().getGameSettings().setSfxEnabled(view.getSfxCheckBox().isChecked());
            }
        });

        // NEW: Listener for auto-reload checkbox
        view.getAutoReloadCheckBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                App.getInstance().getGameSettings().setAutoReloadEnabled(view.getAutoReloadCheckBox().isChecked());
                Gdx.app.log("Settings", "Auto-Reload set to: " + view.getAutoReloadCheckBox().isChecked());
            }
        });

        view.getBackButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBackButtonClick();
            }
        });

        for (TextButton button : view.getKeyBindingButtons()) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!isWaitingForKeyPress) {
                        actionToRebind = (String) button.getUserObject();
                        currentKeyBindingButton = button;
                        isWaitingForKeyPress = true;
                        button.setText("Press a key...");
                        Gdx.input.setInputProcessor(new KeyCaptureInputProcessor());
                    }
                }
            });
        }
    }

    private void handleBackButtonClick() {
        System.out.println("Navigating back to Main Menu...");
        // Ensure Main.getMain() is correctly set up to get the main game instance.
        // Assuming Main.getMain() exists and returns the game instance.
        Main.getMain().setScreen(new MainMenuView(new MainMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()));
    }

    private class KeyCaptureInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            if (isWaitingForKeyPress) {
                // Allow ESCAPE to cancel rebinding without changing the key
                if (keycode == Input.Keys.ESCAPE) {
                    // Revert button text to original key name
                    view.updateKeyBindingButton(actionToRebind,
                        GameSettings.getKeyName(App.getInstance().getGameSettings().getKeyBindings().get(actionToRebind)));
                } else {
                    // Set the new key binding
                    App.getInstance().getGameSettings().setKeyBinding(actionToRebind, keycode);
                    view.updateKeyBindingButton(actionToRebind, GameSettings.getKeyName(keycode));
                }
                isWaitingForKeyPress = false;
                actionToRebind = null;
                currentKeyBindingButton = null;
                Gdx.input.setInputProcessor(view.getStage()); // Set input processor back to the stage
                return true;
            }
            return false;
        }
    }
}
