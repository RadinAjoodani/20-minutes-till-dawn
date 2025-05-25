// controller/PreGameMenuController.java
package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import graphic.source.Main;
import model.*;
import view.GameView;
import view.PreGameMenuView;

public class PreGameMenuController {
    private PreGameMenuView view;
    private PreGame pregame;

    public void setView(PreGameMenuView view) {
        this.view = view;
        this.pregame = new PreGame();
        setupListeners();

        String initialHero = view.getHeroSelectBox().getSelected();
        if (initialHero != null) {
            CharacterData characterData = GameAssetManager.getGameAssetManager().getCharacterDataByName(initialHero);
            if (characterData != null) {
                Array<String> idleAnimationPaths = characterData.getAnimations().get("idle");
                if (idleAnimationPaths != null && idleAnimationPaths.size > 0) {
                    Animation<TextureRegion> idleAnimation = loadAnimation(idleAnimationPaths, 0.1f);
                    Player player = new Player(characterData, idleAnimation);
                    pregame.setPlayer(player);
                    Gdx.app.log("PreGameMenuController", "Player initialized with default selected hero: " + characterData.getName());
                } else {
                    Gdx.app.error("PreGameMenuController", "No idle animation paths found for default hero: " + initialHero);
                    pregame.setPlayer(null);
                }
            } else {
                Gdx.app.error("PreGameMenuController", "CharacterData not found for default hero: " + initialHero);
                pregame.setPlayer(null);
            }
        } else {
            Gdx.app.log("PreGameMenuController", "No default hero selected in SelectBox.");
            pregame.setPlayer(null);
        }

        String initialGun = view.getGunSelectBox().getSelected();
        if (initialGun != null) {
            GunData gunData = GameAssetManager.getGameAssetManager().getGunDataByName(initialGun);
            if (gunData != null) {
                ObjectMap<String, Array<String>> gunAnimationPaths = gunData.getAnimations();
                if (gunAnimationPaths == null || gunAnimationPaths.isEmpty()) {
                    Gdx.app.error("GunLoad", "No animation paths found for gun: " + initialGun);
                    pregame.setSelectedGun(null);
                    return;
                }
                // This line now correctly loads all animations, including 'reload'
                ObjectMap<String, Animation<TextureRegion>> loadedGunAnimations = loadAllAnimations(gunAnimationPaths, 0.1f);
                Gun gun = new Gun(gunData, loadedGunAnimations); // Gun now initialized with max ammo
                pregame.setSelectedGun(gun);
                Gdx.app.log("PreGameMenuController", "Gun initialized with default selected gun: " + gunData.getName());
            } else {
                Gdx.app.error("PreGameMenuController", "GunData not found for default gun: " + initialGun);
                pregame.setSelectedGun(null);
            }
        } else {
            Gdx.app.log("PreGameMenuController", "No default gun selected in SelectBox.");
            pregame.setSelectedGun(null);
        }

        Integer initialDuration = view.getDurationSelectBox().getSelected();
        if (initialDuration != null) {
            pregame.setGameDurationMinutes(initialDuration);
            Gdx.app.log("PreGameMenuController", "Game duration initialized to: " + initialDuration + " minutes.");
        } else {
            Gdx.app.log("PreGameMenuController", "No default duration selected in SelectBox.");
            pregame.setGameDurationMinutes(5);
        }
    }

    public PreGame getPregame() {
        return pregame;
    }

    private void setupListeners() {
        view.getHeroSelectBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedHero = view.getHeroSelectBox().getSelected();
                CharacterData characterData = GameAssetManager.getGameAssetManager().getCharacterDataByName(selectedHero);
                if (characterData != null) {
                    Array<String> idleAnimationPaths = characterData.getAnimations().get("idle");
                    if (idleAnimationPaths != null && idleAnimationPaths.size > 0) {
                        Animation<TextureRegion> idleAnimation = loadAnimation(idleAnimationPaths, 0.1f);
                        Player player = new Player(characterData, idleAnimation);
                        pregame.setPlayer(player);
                        Gdx.app.log("HeroSelect", "Player set to: " + selectedHero);
                    } else {
                        Gdx.app.error("HeroLoad", "Idle animation paths not found for: " + selectedHero);
                        pregame.setPlayer(null);
                    }
                } else {
                    Gdx.app.error("HeroLoad", "Character data not found in AssetManager for: " + selectedHero);
                    pregame.setPlayer(null);
                }
            }
        });

        view.getGunSelectBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedGunName = view.getGunSelectBox().getSelected();
                GunData gunData = GameAssetManager.getGameAssetManager().getGunDataByName(selectedGunName);
                if (gunData != null) {
                    ObjectMap<String, Array<String>> gunAnimationPaths = gunData.getAnimations();
                    if (gunAnimationPaths == null || gunAnimationPaths.isEmpty()) {
                        Gdx.app.error("GunLoad", "No animation paths found for gun: " + selectedGunName);
                        pregame.setSelectedGun(null);
                        return;
                    }
                    ObjectMap<String, Animation<TextureRegion>> loadedGunAnimations = loadAllAnimations(gunAnimationPaths, 0.1f);
                    Gun gun = new Gun(gunData, loadedGunAnimations);
                    pregame.setSelectedGun(gun);
                    Gdx.app.log("GunSelect", "Gun set to: " + selectedGunName);
                } else {
                    Gdx.app.error("GunLoad", "Gun data not found in AssetManager for: " + selectedGunName);
                    pregame.setSelectedGun(null);
                }
            }
        });

        view.getDurationSelectBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Integer selectedDuration = view.getDurationSelectBox().getSelected();
                if (selectedDuration != null) {
                    pregame.setGameDurationMinutes(selectedDuration);
                } else {
                    Gdx.app.error("DurationSelect", "Selected duration is null.");
                    pregame.setGameDurationMinutes(5);
                }
            }
        });

        view.getPlayGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pregame.getPlayer() != null && pregame.getSelectedGun() != null) {
                    Gdx.app.log("PreGameMenuController", "Starting game...");
                    Main.getMain().setScreen(new GameView(pregame.getPlayer(), pregame.getSelectedGun(), pregame.getGameDurationMinutes(), App.getInstance().getGameSettings(), pregame.getUsername()));
                }
                else {
                    Gdx.app.error("PreGameMenuController", "Cannot start game: Player or Gun not selected!");
                }
            }
        });

        view.getBackButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });
    }

    private Animation<TextureRegion> loadAnimation(Array<String> paths, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        for (String path : paths) {
            Texture texture = GameAssetManager.getGameAssetManager().getTexture(path);
            if (texture != null) {
                frames.add(new TextureRegion(texture));
            } else {
                Gdx.app.error("AnimationLoad", "Failed to load texture for animation frame: " + path);
            }
        }
        if (frames.size == 0) {
            Gdx.app.error("AnimationLoad", "No frames loaded for animation! Returning a placeholder.");
            Texture fallbackTexture = new Texture(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            fallbackTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return new Animation<>(0.1f, new TextureRegion(fallbackTexture));
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    private ObjectMap<String, Animation<TextureRegion>> loadAllAnimations(ObjectMap<String, Array<String>> animationPaths, float defaultFrameDuration) {
        ObjectMap<String, Animation<TextureRegion>> loadedAnimations = new ObjectMap<>();
        if (animationPaths != null) {
            for (ObjectMap.Entry<String, Array<String>> entry : animationPaths.entries()) {
                String animationName = entry.key;
                Array<String> paths = entry.value;
                if (paths != null && paths.size > 0) {
                    float frameDuration = defaultFrameDuration;
                    Animation.PlayMode playMode = Animation.PlayMode.LOOP; // Default to loop

                    if (animationName.equals("still")) {
                        frameDuration = 0.5f; // A longer duration for a 'still' frame
                        playMode = Animation.PlayMode.NORMAL; // Only one frame, no loop needed
                    } else if (animationName.equals("reload")) { // NEW: Handle reload animation
                        // Adjust frameDuration for reload if needed, usually faster than idle
                        frameDuration = 0.08f; // Example: faster frame duration for reload
                        playMode = Animation.PlayMode.NORMAL; // Reload animation usually plays once
                    }
                    loadedAnimations.put(animationName, new Animation<>(frameDuration, loadAnimationFrames(paths), playMode));
                } else {
                    Gdx.app.error("AnimationLoad", "No paths provided for animation: " + animationName);
                }
            }
        }
        return loadedAnimations;
    }

    private Array<TextureRegion> loadAnimationFrames(Array<String> paths) {
        Array<TextureRegion> frames = new Array<>();
        for (String path : paths) {
            Texture texture = GameAssetManager.getGameAssetManager().getTexture(path);
            if (texture != null) {
                frames.add(new TextureRegion(texture));
            } else {
                Gdx.app.error("AnimationLoad", "Failed to load texture for frame: " + path);
            }
        }
        return frames;
    }
}
