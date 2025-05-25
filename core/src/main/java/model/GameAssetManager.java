// model/GameAssetManager.java
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
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

    public final String BULLET_TEXTURE_PATH = "guns/bullet.png";
    public final String ENEMY_SEED_TEXTURE_PATH = "guns/enemy_seed.png"; // NEW: Seed texture path

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

        loadOtherAssets();
        loadHeroes();
        loadGuns();
        loadEnemies();
        loadGameAssets(); // This now includes the seed texture

        assetManager.finishLoading();
        Gdx.app.log("GameAssetManager", "All assets finished loading.");
    }

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null) {
            gameAssetManager = new GameAssetManager();
        }
        return gameAssetManager;
    }

    private void loadOtherAssets() {
        Json json = new Json();

        Array<String> avatarFiles = json.fromJson(Array.class, Gdx.files.internal("resources/avatars.json"));
        for (String filePath : avatarFiles) {
            String fullPath = AVATAR_FOLDER + filePath;
            assetManager.load(fullPath, Texture.class);
            avatarImagePaths.add(fullPath);
        }

        Array<String> backgroundFiles = json.fromJson(Array.class, Gdx.files.internal("resources/backgrounds.json"));
        for (String filePath : backgroundFiles) {
            String fullPath = BACKGROUND_FOLDER + filePath;
            assetManager.load(fullPath, Texture.class);
            backgroundImagePaths.add(fullPath);
        }

        Array<String> musicFiles = json.fromJson(Array.class, Gdx.files.internal("resources/musics.json"));
        for (String filePath : musicFiles) {
            String fullPath = MUSIC_FOLDER + filePath;
            assetManager.load(fullPath, Music.class);
            musicPaths.add(fullPath);
        }

        assetManager.load(DEFAULT_AVATAR_PATH, Texture.class);
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
                            assetManager.load(path, Texture.class);
                            Gdx.app.log("AssetLoading", "Queued hero texture: " + path);
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
                            assetManager.load(path, Texture.class);
                            Gdx.app.log("AssetLoading", "Queued gun texture: " + path);
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
                            assetManager.load(path, Texture.class);
                            Gdx.app.log("AssetLoading", "Queued enemy texture: " + path);
                        }
                    }
                }
            }
        }
    }

    private void loadGameAssets() {
        assetManager.load(BULLET_TEXTURE_PATH, Texture.class);
        Gdx.app.log("AssetLoading", "Queued game asset: " + BULLET_TEXTURE_PATH);
        assetManager.load(ENEMY_SEED_TEXTURE_PATH, Texture.class); // NEW: Load seed texture
        Gdx.app.log("AssetLoading", "Queued game asset: " + ENEMY_SEED_TEXTURE_PATH);
    }


    private void loadCharacterDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) {
            Gdx.app.error("GameAssetManager", "Character data JSON file not found: " + path);
            return;
        }
        String jsonText = fileHandle.readString();
        Json json = new Json();
        allCharacters = json.fromJson(Array.class, CharacterData.class, jsonText);

        for (CharacterData hero : allCharacters) {
            Gdx.app.log("HeroLoaded", "Name: " + hero.getName() +
                ", HP: " + hero.getHp() +
                ", Speed: " + hero.getSpeed() +
                ", Animations: " + hero.getAnimations().keys().toString());
        }
    }

    private void loadGunDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) {
            Gdx.app.error("GameAssetManager", "Gun data JSON file not found: " + path);
            return;
        }
        String jsonText = fileHandle.readString();
        Json json = new Json();
        allGuns = json.fromJson(Array.class, GunData.class, jsonText);

        for (GunData gun : allGuns) {
            Gdx.app.log("GunLoaded", "Name: " + gun.getName() +
                ", Damage: " + gun.getDamage() +
                ", Animations: " + (gun.getAnimations() != null ? gun.getAnimations().keys().toString() : "None"));
        }
    }

    private void loadEnemyDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) {
            Gdx.app.error("GameAssetManager", "Enemy data JSON file not found: " + path);
            return;
        }
        String jsonText = fileHandle.readString();
        Json json = new Json();
        allEnemies = json.fromJson(Array.class, EnemyData.class, jsonText);

        for (EnemyData enemy : allEnemies) {
            Gdx.app.log("EnemyLoaded", "Name: " + enemy.getName() +
                ", HP: " + enemy.getHp() +
                ", Speed: " + enemy.getSpeed() +
                ", Animations: " + (enemy.getAnimations() != null ? enemy.getAnimations().keys().toString() : "None"));
        }
    }

    public Skin getSkin() {
        return skin;
    }

    public String getRandomAvatarPath() {
        if (avatarImagePaths.size == 0) {
            System.err.println("No specific avatar paths available. Returning default.");
            return DEFAULT_AVATAR_PATH;
        }
        return avatarImagePaths.get(random.nextInt(avatarImagePaths.size));
    }

    public String getRandomBackgroundPath() {
        if (backgroundImagePaths.size == 0) {
            System.err.println("No background images loaded.");
            return null;
        }
        return backgroundImagePaths.get(random.nextInt(backgroundImagePaths.size));
    }

    public Array<String> getAllAvatarPaths() {
        return new Array<>(avatarImagePaths);
    }

    public CharacterData getCharacterDataByName(String name) {
        for (CharacterData character : allCharacters) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        Gdx.app.error("GameAssetManager", "CharacterData not found for name: " + name);
        return null;
    }

    public Array<String> getAllCharacterNames() {
        Array<String> names = new Array<>();
        for (CharacterData character : allCharacters) {
            names.add(character.getName());
        }
        return names;
    }

    public GunData getGunDataByName(String name) {
        for (GunData gun : allGuns) {
            if (gun.getName().equalsIgnoreCase(name)) {
                return gun;
            }
        }
        Gdx.app.error("GameAssetManager", "GunData not found for name: " + name);
        return null;
    }

    public Array<String> getAllGunNames() {
        Array<String> names = new Array<>();
        for (GunData gun : allGuns) {
            names.add(gun.getName());
        }
        return names;
    }

    public EnemyData getEnemyDataByName(String name) {
        for (EnemyData enemy : allEnemies) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                return enemy;
            }
        }
        Gdx.app.error("GameAssetManager", "EnemyData not found for name: " + name);
        return null;
    }

    public Array<String> getAllEnemyNames() {
        Array<String> names = new Array<>();
        for (EnemyData enemy : allEnemies) {
            names.add(enemy.getName());
        }
        return names;
    }

    public Music getMusic(String path) {
        return assetManager.get(path, Music.class);
    }

    public String getRandomMusicPath() {
        if (musicPaths.size == 0) return null;
        return musicPaths.random();
    }

    public Array<String> getMusicPaths() {
        return new Array<>(musicPaths);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public <T> T get(String filePath, Class<T> type) {
        if (!assetManager.isLoaded(filePath, type)) {
            Gdx.app.error("GameAssetManager", "Asset not loaded or wrong type: " + filePath + " of type " + type.getSimpleName());
            // Attempt to load and finish loading for missing asset, though it's better to preload
            // assetManager.load(filePath, type);
            // assetManager.finishLoadingAsset(filePath);
            // if (!assetManager.isLoaded(filePath, type)) { // Check again after attempt
            //     Gdx.app.error("GameAssetManager", "Failed to load missing asset at runtime: " + filePath);
            //     return null;
            // }
        }
        return assetManager.get(filePath, type);
    }

    public Texture getTexture(String path) {
        return get(path, Texture.class);
    }

    // NEW: Get TextureRegion for a specific asset path
    public TextureRegion getTextureRegion(String path) {
        Texture texture = getTexture(path);
        return (texture != null) ? new TextureRegion(texture) : null;
    }


    public void dispose() {
        skin.dispose();
        assetManager.dispose();
    }

    public Random getRandom() {
        return random;
    }

    public Array<EnemyData> getAllEnemyData() {
        return new Array<>(allEnemies);
    }
}
