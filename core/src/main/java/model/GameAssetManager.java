<<<<<<< HEAD

=======
// model/GameAssetManager.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
<<<<<<< HEAD
import com.badlogic.gdx.graphics.g2d.Animation;
=======
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
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
<<<<<<< HEAD
    private Array<DamageAnimationData> allDamageAnimationsData;


    private ObjectMap<String, Animation<TextureRegion>> playerDamageAnimations;
    private ObjectMap<String, Animation<TextureRegion>> enemyDamageAnimations;


    public final String BULLET_TEXTURE_PATH = "guns/bullet.png";
    public final String ENEMY_SEED_TEXTURE_PATH = "guns/enemy_seed.png";
=======

    public final String BULLET_TEXTURE_PATH = "guns/bullet.png";
    public final String ENEMY_SEED_TEXTURE_PATH = "guns/enemy_seed.png"; // NEW: Seed texture path
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

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
<<<<<<< HEAD
        this.allDamageAnimationsData = new Array<>();
        this.playerDamageAnimations = new ObjectMap<>();
        this.enemyDamageAnimations = new ObjectMap<>();
=======
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

        loadOtherAssets();
        loadHeroes();
        loadGuns();
        loadEnemies();
<<<<<<< HEAD
        loadDamageAnimationsFromJson("resources/damages.json");
        loadGameAssets();

        assetManager.finishLoading();
        Gdx.app.log("GameAssetManager", "All assets finished loading.");


        createLoadedPlayerDamageAnimations();
        createLoadedEnemyDamageAnimations();
=======
        loadGameAssets(); // This now includes the seed texture

        assetManager.finishLoading();
        Gdx.app.log("GameAssetManager", "All assets finished loading.");
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null) {
            gameAssetManager = new GameAssetManager();
        }
        return gameAssetManager;
    }

    private void loadOtherAssets() {
        Json json = new Json();

<<<<<<< HEAD
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
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    private void loadHeroes() {
        loadCharacterDataFromJson("resources/heroes.json");
<<<<<<< HEAD
=======

>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        for (CharacterData character : allCharacters) {
            ObjectMap<String, Array<String>> animations = character.getAnimations();
            if (animations != null) {
                for (ObjectMap.Entry<String, Array<String>> entry : animations.entries()) {
                    Array<String> animationPaths = entry.value;
                    if (animationPaths != null) {
                        for (String path : animationPaths) {
<<<<<<< HEAD
                            if (Gdx.files.internal(path).exists()) assetManager.load(path, Texture.class);
                            else Gdx.app.log("AssetLoading", "Hero texture not found: " + path);
=======
                            assetManager.load(path, Texture.class);
                            Gdx.app.log("AssetLoading", "Queued hero texture: " + path);
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
                        }
                    }
                }
            }
        }
    }

    private void loadGuns() {
        loadGunDataFromJson("resources/guns.json");
<<<<<<< HEAD
=======

>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        for (GunData gun : allGuns) {
            ObjectMap<String, Array<String>> animations = gun.getAnimations();
            if (animations != null) {
                for (ObjectMap.Entry<String, Array<String>> entry : animations.entries()) {
                    Array<String> animationPaths = entry.value;
                    if (animationPaths != null) {
                        for (String path : animationPaths) {
<<<<<<< HEAD
                            if (Gdx.files.internal(path).exists()) assetManager.load(path, Texture.class);
                            else Gdx.app.log("AssetLoading", "Gun texture not found: " + path);
=======
                            assetManager.load(path, Texture.class);
                            Gdx.app.log("AssetLoading", "Queued gun texture: " + path);
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
                        }
                    }
                }
            }
        }
    }

    private void loadEnemies() {
        loadEnemyDataFromJson("resources/enemies.json");
<<<<<<< HEAD
=======

>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        for (EnemyData enemy : allEnemies) {
            ObjectMap<String, Array<String>> animations = enemy.getAnimations();
            if (animations != null) {
                for (ObjectMap.Entry<String, Array<String>> entry : animations.entries()) {
                    Array<String> animationPaths = entry.value;
                    if (animationPaths != null) {
                        for (String path : animationPaths) {
<<<<<<< HEAD
                            if (Gdx.files.internal(path).exists()) assetManager.load(path, Texture.class);
                            else Gdx.app.log("AssetLoading", "Enemy texture not found: " + path);
=======
                            assetManager.load(path, Texture.class);
                            Gdx.app.log("AssetLoading", "Queued enemy texture: " + path);
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
                        }
                    }
                }
            }
        }
    }

<<<<<<< HEAD
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
                for (ObjectMap.Entry<String, Array<String>> entry : dad.animations.entries()) {
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
=======
    private void loadGameAssets() {
        assetManager.load(BULLET_TEXTURE_PATH, Texture.class);
        Gdx.app.log("AssetLoading", "Queued game asset: " + BULLET_TEXTURE_PATH);
        assetManager.load(ENEMY_SEED_TEXTURE_PATH, Texture.class); // NEW: Load seed texture
        Gdx.app.log("AssetLoading", "Queued game asset: " + ENEMY_SEED_TEXTURE_PATH);
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }


    private void loadCharacterDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
<<<<<<< HEAD
        if (!fileHandle.exists()) { Gdx.app.error("GameAssetManager", "Character data JSON not found: " + path); return; }
        allCharacters = new Json().fromJson(Array.class, CharacterData.class, fileHandle.readString());
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    private void loadGunDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
<<<<<<< HEAD
        if (!fileHandle.exists()) { Gdx.app.error("GameAssetManager", "Gun data JSON not found: " + path); return; }
        allGuns = new Json().fromJson(Array.class, GunData.class, fileHandle.readString());
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    private void loadEnemyDataFromJson(String path) {
        FileHandle fileHandle = Gdx.files.internal(path);
<<<<<<< HEAD
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
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }
        return assetManager.get(filePath, type);
    }

    public Texture getTexture(String path) {
        return get(path, Texture.class);
    }

<<<<<<< HEAD
=======
    // NEW: Get TextureRegion for a specific asset path
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public TextureRegion getTextureRegion(String path) {
        Texture texture = getTexture(path);
        return (texture != null) ? new TextureRegion(texture) : null;
    }

<<<<<<< HEAD
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
=======

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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
}
