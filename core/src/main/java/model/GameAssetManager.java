// model/GameAssetManager.java
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation; // Import LibGDX Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Random;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager;
    private final Skin skin;
    private final AssetManager assetManager;
    private final Random random;
    private final String AVATAR_FOLDER = "avatars/";
    private Array<String> avatarImagePaths;
    public final String DEFAULT_AVATAR_PATH = AVATAR_FOLDER + "avatar_lion.png";
    private final String BACKGROUND_FOLDER = "backgrounds/";
    private Array<String> backgroundImagePaths;
    private String MUSIC_FOLDER = "musics/";
    private Array<String> musicPaths;

    private Array<CharacterData> allCharacters;
    private Array<GunData> allGuns;
    private Array<EnemyData> allEnemies;
    private Array<DamageAnimationData> allDamageAnimationsData; // For damages.json

    // Store loaded animations
    private ObjectMap<String, Animation<TextureRegion>> playerDamageAnimations; // Keyed by type, e.g., "Player_damage"
    private ObjectMap<String, Animation<TextureRegion>> enemyDamageAnimations;  // Keyed by type, e.g., "Enemy_damage"


    public final String BULLET_TEXTURE_PATH = "guns/bullet.png";
    public final String ENEMY_SEED_TEXTURE_PATH = "guns/enemy_seed.png";

    private GameAssetManager() {
        this.skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        this.assetManager = new AssetManager();
        this.random = new Random();
        this.avatarImagePaths = new Array<>();
        this.backgroundImagePaths = new Array<>();
        this.musicPaths = new Array<>();
        this.allCharacters = new Array<>();
        this.allGuns = new Array<>();
        this.allEnemies = new Array<>();
        this.allDamageAnimationsData = new Array<>();
        this.playerDamageAnimations = new ObjectMap<>();
        this.enemyDamageAnimations = new ObjectMap<>();

        loadOtherAssets();
        loadHeroes();
        loadGuns();
        loadEnemies();
        loadDamageAnimationsFromJson("resources/damages.json"); // Load damage animations
        loadGameAssets();

        assetManager.finishLoading(); // Important: ensure all assets are loaded
        Gdx.app.log("GameAssetManager", "All assets finished loading.");

        // After loading, create actual Animation objects
        createLoadedPlayerDamageAnimations();
        createLoadedEnemyDamageAnimations();
    }

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null) {
            gameAssetManager = new GameAssetManager();
        }
        return gameAssetManager;
    }

    private void loadOtherAssets() {
        Json json = new Json();
        // Load avatars, backgrounds, musics as before...
        FileHandle avatarsFile = Gdx.files.internal("resources/avatars.json");
        if (avatarsFile.exists()) {
            Array<String> avatarFiles = json.fromJson(Array.class, avatarsFile);
            if (avatarFiles != null) {
                for (String filePath : avatarFiles) {
                    String fullPath = AVATAR_FOLDER + filePath;
                    if (Gdx.files.internal(fullPath).exists()) {
                        assetManager.load(fullPath, Texture.class);
                        avatarImagePaths.add(fullPath);
                    } else { Gdx.app.log("AssetLoading", "Avatar texture not found: " + fullPath); }
                }
            }
        } else { Gdx.app.log("AssetLoading", "avatars.json not found.");}


        FileHandle backgroundsFile = Gdx.files.internal("resources/backgrounds.json");
        if(backgroundsFile.exists()){
            Array<String> backgroundFiles = json.fromJson(Array.class, backgroundsFile);
            if (backgroundFiles != null) {
                for (String filePath : backgroundFiles) {
                    String fullPath = BACKGROUND_FOLDER + filePath;
                    if (Gdx.files.internal(fullPath).exists()) {
                        assetManager.load(fullPath, Texture.class);
                        backgroundImagePaths.add(fullPath);
                    } else { Gdx.app.log("AssetLoading", "Background texture not found: " + fullPath); }
                }
            }
        } else { Gdx.app.log("AssetLoading", "backgrounds.json not found.");}


        FileHandle musicsFile = Gdx.files.internal("resources/musics.json");
        if(musicsFile.exists()){
            Array<String> musicFiles = json.fromJson(Array.class, musicsFile);
            if (musicFiles != null) {
                for (String filePath : musicFiles) {
                    String fullPath = MUSIC_FOLDER + filePath;
                    // For music, type is Music.class, not Texture.class
                    if (Gdx.files.internal(fullPath).exists()) {
                        assetManager.load(fullPath, Music.class);
                        musicPaths.add(fullPath);
                    } else { Gdx.app.log("AssetLoading", "Music file not found: " + fullPath); }
                }
            }
        } else { Gdx.app.log("AssetLoading", "musics.json not found.");}


        if (Gdx.files.internal(DEFAULT_AVATAR_PATH).exists()) {
            assetManager.load(DEFAULT_AVATAR_PATH, Texture.class);
        } else {Gdx.app.log("AssetLoading", "Default avatar not found: " + DEFAULT_AVATAR_PATH);}
    }

    private void loadHeroes() {
        loadCharacterDataFromJson("resources/heroes.json");
        for (CharacterData character : allCharacters) {
            ObjectMap<String, Array<String>> animations = character.getAnimations();
            if (animations != null) {
                for (ObjectMap.Entry<String, Array<String>> entry : animations.entries()) {
                    Array<String> animationPaths = entry.value;
                    if (animationPaths != null) {
                        for (String path : animationPaths) {
                            if (Gdx.files.internal(path).exists()) assetManager.load(path, Texture.class);
                            else Gdx.app.log("AssetLoading", "Hero texture not found: " + path);
                        }
                    }
                }
            }
        }
    }

    private void loadGuns() {
        loadGunDataFromJson("resources/guns.json");
        for (GunData gun : allGuns) {
            ObjectMap<String, Array<String>> animations = gun.getAnimations();
            if (animations != null) {
                for (ObjectMap.Entry<String, Array<String>> entry : animations.entries()) {
                    Array<String> animationPaths = entry.value;
                    if (animationPaths != null) {
                        for (String path : animationPaths) {
                            if (Gdx.files.internal(path).exists()) assetManager.load(path, Texture.class);
                            else Gdx.app.log("AssetLoading", "Gun texture not found: " + path);
                        }
                    }
                }
            }
        }
    }

    private void loadEnemies() {
        loadEnemyDataFromJson("resources/enemies.json");
        for (EnemyData enemy : allEnemies) {
            ObjectMap<String, Array<String>> animations = enemy.getAnimations();
            if (animations != null) {
                for (ObjectMap.Entry<String, Array<String>> entry : animations.entries()) {
                    Array<String> animationPaths = entry.value;
                    if (animationPaths != null) {
                        for (String path : animationPaths) {
                            if (Gdx.files.internal(path).exists()) assetManager.load(path, Texture.class);
                            else Gdx.app.log("AssetLoading", "Enemy texture not found: " + path);
                        }
                    }
                }
            }
        }
    }

    private void loadDamageAnimationsFromJson(String jsonPath) {
        FileHandle fileHandle = Gdx.files.internal(jsonPath);
        if (!fileHandle.exists()) {
            Gdx.app.error("GameAssetManager", "Damage animations JSON file not found: " + jsonPath);
            return;
        }
        String jsonText = fileHandle.readString();
        Json json = new Json();
        allDamageAnimationsData = json.fromJson(Array.class, DamageAnimationData.class, jsonText);

        for (DamageAnimationData dad : allDamageAnimationsData) {
            if (dad.animations != null) {
                for (ObjectMap.Entry<String, Array<String>> entry : dad.animations.entries()) { // e.g., key "damage"
                    Array<String> animationPaths = entry.value;
                    if (animationPaths != null) {
                        for (String path : animationPaths) {
                            if (Gdx.files.internal(path).exists()) {
                                assetManager.load(path, Texture.class);
                                Gdx.app.log("AssetLoading", "Queued damage anim texture: " + path);
                            } else {
                                Gdx.app.log("AssetLoading", "Damage animation texture not found: " + path);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createLoadedPlayerDamageAnimations() {
        for (DamageAnimationData dad : allDamageAnimationsData) {
            if (dad.name.equals("Player") && dad.animations != null) {
                for (ObjectMap.Entry<String, Array<String>> animEntry : dad.animations.entries()) {
                    String animType = animEntry.key; // e.g., "damage"
                    Array<String> paths = animEntry.value;
                    Array<TextureRegion> frames = new Array<>();
                    for (String path : paths) {
                        if (assetManager.isLoaded(path, Texture.class)) {
                            frames.add(new TextureRegion(assetManager.get(path, Texture.class)));
                        }
                    }
                    if (frames.size > 0) {
                        // Assuming 0.1f frame duration, non-looping for damage animation
                        Animation<TextureRegion> animation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);
                        playerDamageAnimations.put(animType, animation); // Store as "damage"
                        Gdx.app.log("GameAssetManager", "Created Player damage animation: " + animType);
                    }
                }
            }
        }
    }

    private void createLoadedEnemyDamageAnimations() {
        for (DamageAnimationData dad : allDamageAnimationsData) {
            if (dad.name.equals("Enemy") && dad.animations != null) { // Generic "Enemy" type
                for (ObjectMap.Entry<String, Array<String>> animEntry : dad.animations.entries()) {
                    String animType = animEntry.key; // e.g., "damage" (which is death explosion)
                    Array<String> paths = animEntry.value;
                    Array<TextureRegion> frames = new Array<>();
                    for (String path : paths) {
                        if (assetManager.isLoaded(path, Texture.class)) {
                            frames.add(new TextureRegion(assetManager.get(path, Texture.class)));
                        }
                    }
                    if (frames.size > 0) {
                        // Assuming 0.1f frame duration, non-looping for death animation
                        Animation<TextureRegion> animation = new Animation<>(0.08f, frames, Animation.PlayMode.NORMAL); // Faster for explosion
                        enemyDamageAnimations.put(animType, animation); // Store as "damage" for generic enemy death
                        Gdx.app.log("GameAssetManager", "Created generic Enemy death animation: " + animType);
                    }
                }
            }
        }
    }

    public Animation<TextureRegion> getPlayerDamageAnimation(String type) { // e.g., type = "damage"
        return playerDamageAnimations.get(type);
    }

    public Animation<TextureRegion> getEnemyDeathAnimation(String type) { // e.g., type = "damage" (for the explosion)
        return enemyDamageAnimations.get(type);
    }


    private void loadGameAssets() {
        if(Gdx.files.internal(BULLET_TEXTURE_PATH).exists()) assetManager.load(BULLET_TEXTURE_PATH, Texture.class);
        else Gdx.app.log("AssetLoading", "Bullet texture not found: " + BULLET_TEXTURE_PATH);

        if(Gdx.files.internal(ENEMY_SEED_TEXTURE_PATH).exists()) assetManager.load(ENEMY_SEED_TEXTURE_PATH, Texture.class);
        else Gdx.app.log("AssetLoading", "Enemy seed texture not found: " + ENEMY_SEED_TEXTURE_PATH);
    }


    private void loadCharacterDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) { Gdx.app.error("GameAssetManager", "Character data JSON not found: " + path); return; }
        allCharacters = new Json().fromJson(Array.class, CharacterData.class, fileHandle.readString());
    }

    private void loadGunDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) { Gdx.app.error("GameAssetManager", "Gun data JSON not found: " + path); return; }
        allGuns = new Json().fromJson(Array.class, GunData.class, fileHandle.readString());
    }

    private void loadEnemyDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) { Gdx.app.error("GameAssetManager", "Enemy data JSON not found: " + path); return; }
        allEnemies = new Json().fromJson(Array.class, EnemyData.class, fileHandle.readString());
    }

    public Skin getSkin() { return skin; }
    public String getRandomAvatarPath() { return avatarImagePaths.size > 0 ? avatarImagePaths.random() : DEFAULT_AVATAR_PATH; }
    public String getRandomBackgroundPath() { return backgroundImagePaths.size > 0 ? backgroundImagePaths.random() : null; }
    public Array<String> getAllAvatarPaths() { return new Array<>(avatarImagePaths); }
    public CharacterData getCharacterDataByName(String name) {
        for (CharacterData character : allCharacters) if (character.getName().equalsIgnoreCase(name)) return character;
        return null;
    }
    public Array<String> getAllCharacterNames() {
        Array<String> names = new Array<>();
        for (CharacterData character : allCharacters) names.add(character.getName());
        return names;
    }
    public GunData getGunDataByName(String name) {
        for (GunData gun : allGuns) if (gun.getName().equalsIgnoreCase(name)) return gun;
        return null;
    }
    public Array<String> getAllGunNames() {
        Array<String> names = new Array<>();
        for (GunData gun : allGuns) names.add(gun.getName());
        return names;
    }
    public EnemyData getEnemyDataByName(String name) {
        for (EnemyData enemy : allEnemies) if (enemy.getName().equalsIgnoreCase(name)) return enemy;
        return null;
    }
    public Array<String> getAllEnemyNames() {
        Array<String> names = new Array<>();
        for (EnemyData enemy : allEnemies) names.add(enemy.getName());
        return names;
    }
    public Music getMusic(String path) { return assetManager.isLoaded(path, Music.class) ? assetManager.get(path, Music.class) : null; }
    public String getRandomMusicPath() { return musicPaths.size > 0 ? musicPaths.random() : null; }
    public Array<String> getMusicPaths() { return new Array<>(musicPaths); }
    public AssetManager getAssetManager() { return assetManager; }

    public <T> T get(String filePath, Class<T> type) {
        if (!assetManager.isLoaded(filePath, type)) {
            Gdx.app.error("GameAssetManager", "Asset not loaded or wrong type requested: " + filePath + " of type " + type.getSimpleName());
            // Attempting to load it now (this is not ideal for managed loading, should be preloaded)
            if (Gdx.files.internal(filePath).exists()) {
                Gdx.app.log("GameAssetManager", "Attempting to load missing asset at runtime: " + filePath);
                assetManager.load(filePath, type);
                assetManager.finishLoadingAsset(filePath); // Block until loaded
                if (assetManager.isLoaded(filePath, type)) {
                    return assetManager.get(filePath, type);
                }
            }
            Gdx.app.error("GameAssetManager", "Failed to load missing asset at runtime: " + filePath);
            return null;
        }
        return assetManager.get(filePath, type);
    }

    public Texture getTexture(String path) {
        return get(path, Texture.class);
    }

    public TextureRegion getTextureRegion(String path) {
        Texture texture = getTexture(path);
        return (texture != null) ? new TextureRegion(texture) : null;
    }

    public void dispose() {
        Gdx.app.log("GameAssetManager", "Disposing assets.");
        if (skin != null) skin.dispose();
        if (assetManager != null) assetManager.dispose();
        // Clear collections
        allCharacters.clear();
        allGuns.clear();
        allEnemies.clear();
        allDamageAnimationsData.clear();
        playerDamageAnimations.clear();
        enemyDamageAnimations.clear();
        avatarImagePaths.clear();
        backgroundImagePaths.clear();
        musicPaths.clear();
        gameAssetManager = null; // Allow re-initialization if needed
    }

    public Random getRandom() { return random; }
    public Array<EnemyData> getAllEnemyData() { return new Array<>(allEnemies); }
}
