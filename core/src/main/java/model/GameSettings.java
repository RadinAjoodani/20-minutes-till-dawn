<<<<<<< HEAD

=======
// model/GameSettings.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
<<<<<<< HEAD
import com.badlogic.gdx.math.MathUtils;
=======
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;

public class GameSettings {
    private float musicVolume = 0.5f;
    private boolean sfxEnabled = true;
    private String currentMusicTrackName = null;
    private Music currentMusic;

    private ObjectMap<String, Integer> keyBindings;
<<<<<<< HEAD
    private boolean autoReloadEnabled;
    private boolean blackAndWhiteModeEnabled = false;

    public GameSettings() {
=======
    private boolean autoReloadEnabled; // NEW: Auto reload setting

    public GameSettings() {
        // Initialize key bindings
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        keyBindings = new ObjectMap<>();
        keyBindings.put("Move Up", Input.Keys.W);
        keyBindings.put("Move Left", Input.Keys.A);
        keyBindings.put("Move Down", Input.Keys.S);
        keyBindings.put("Move Right", Input.Keys.D);
<<<<<<< HEAD
        keyBindings.put("Shoot", Input.Buttons.LEFT);
        keyBindings.put("Reload", Input.Keys.R);
        keyBindings.put("Pause", Input.Keys.ESCAPE);

        this.autoReloadEnabled = false;

        Array<String> availableTracks = getAvailableTracks();
        if (currentMusicTrackName == null && availableTracks.size > 0) {
            currentMusicTrackName = availableTracks.first();
        }
    }

    public void playMusicTrack(String trackName) {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
=======
        keyBindings.put("Shoot", Input.Keys.SPACE); // Common for shooting (mouse click)
        keyBindings.put("Jump", Input.Keys.ENTER); // Changed Jump to SPACE as it's common
        keyBindings.put("Reload", Input.Keys.R); // NEW: Default Reload Key

        // Initialize auto-reload setting
        this.autoReloadEnabled = false; // NEW: Default to auto-reload off

        // Initialize music track if not set, using first available if any
        Array<String> availableTracks = getAvailableTracks();
        if (currentMusicTrackName == null && availableTracks.size > 0) {
            currentMusicTrackName = availableTracks.first();
            playMusicTrack(currentMusicTrackName); // Start playing default music on startup
        }
    }

    // Note: The App.getInstance().getMusicManager() calls from previous examples are removed
    // as you've integrated music management directly into GameSettings.

    public void playMusicTrack(String trackName) {
        // Dispose of previous music to prevent memory leaks if switching tracks
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null; // Set to null after disposing
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }

        if (trackName == null || trackName.isEmpty()) {
            Gdx.app.log("GameSettings", "No track name provided to play.");
            currentMusicTrackName = null;
            return;
        }

<<<<<<< HEAD
        FileHandle musicFile = Gdx.files.internal("musics/" + trackName + ".ogg");
        if (!musicFile.exists()) {
=======
        // Changed 'musics/' to match your provided path if it's "musics/filename.ogg"
        FileHandle musicFile = Gdx.files.internal("musics/" + trackName + ".ogg");
        if (!musicFile.exists()) {
            // Also check for .mp3 or .wav if your files are mixed
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
            musicFile = Gdx.files.internal("musics/" + trackName + ".mp3");
            if (!musicFile.exists()) {
                musicFile = Gdx.files.internal("musics/" + trackName + ".wav");
            }
        }

<<<<<<< HEAD
=======

>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        if (!musicFile.exists()) {
            Gdx.app.error("GameSettings", "Music file not found: " + musicFile.path());
            currentMusicTrackName = null;
            return;
        }

<<<<<<< HEAD
        try {
            currentMusic = Gdx.audio.newMusic(musicFile);
            currentMusic.setLooping(true);
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
            currentMusicTrackName = trackName;
            Gdx.app.log("GameSettings", "Playing music: " + trackName);
        } catch (Exception e) {
            Gdx.app.error("GameSettings", "Error loading or playing music: " + trackName, e);
            if (currentMusic != null) {
                currentMusic.dispose();
                currentMusic = null;
            }
            currentMusicTrackName = null;
        }
=======
        currentMusic = Gdx.audio.newMusic(musicFile);
        currentMusic.setLooping(true);
        currentMusic.setVolume(musicVolume);
        currentMusic.play();
        currentMusicTrackName = trackName;
        Gdx.app.log("GameSettings", "Playing music: " + trackName);
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            Gdx.app.log("GameSettings", "Music stopped.");
        }
    }

    public void setMusicVolume(float musicVolume) {
<<<<<<< HEAD
        this.musicVolume = MathUtils.clamp(musicVolume, 0f, 1f);
        if (currentMusic != null) {
            currentMusic.setVolume(this.musicVolume);
        }
        Gdx.app.log("GameSettings", "Music volume set to: " + this.musicVolume);
=======
        this.musicVolume = musicVolume;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
        Gdx.app.log("GameSettings", "Music volume set to: " + musicVolume);
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void setSfxEnabled(boolean sfxEnabled) {
        this.sfxEnabled = sfxEnabled;
        Gdx.app.log("GameSettings", "SFX enabled: " + sfxEnabled);
    }

    public String getCurrentMusicTrackName() {
        return currentMusicTrackName;
    }

    public void setCurrentMusicTrack(String trackName) {
<<<<<<< HEAD
        if (trackName != null && !trackName.equals(currentMusicTrackName)) {
            playMusicTrack(trackName);
        } else if (trackName == null && currentMusicTrackName != null) {
            stopMusic();
            currentMusicTrackName = null;
        } else if (trackName != null && currentMusic == null) {
            playMusicTrack(trackName);
        }
    }


=======
        playMusicTrack(trackName);
    }

>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public Music getMusic() {
        return currentMusic;
    }

    public ObjectMap<String, Integer> getKeyBindings() {
        return keyBindings;
    }

    public void setKeyBinding(String action, int keyCode) {
<<<<<<< HEAD
        if (action.equals("Shoot")) {
            if (keyCode != Input.Buttons.LEFT && keyCode != Input.Buttons.RIGHT && keyCode != Input.Buttons.MIDDLE) {
                Gdx.app.error("GameSettings", "Cannot set non-mouse key for 'Shoot' action. Current key: " + getKeyName(keyCode));
                return;
            }
        } else {
            if (keyCode == Input.Buttons.LEFT || keyCode == Input.Buttons.RIGHT || keyCode == Input.Buttons.MIDDLE) {
                Gdx.app.error("GameSettings", "Cannot set mouse button for keyboard action '" + action + "'. Key: " + getKeyName(keyCode));
                return;
            }
=======
        // Prevent setting a key binding to a mouse button for keyboard actions, and vice-versa
        if (action.equals("Shoot")) {
            // For "Shoot", allow only mouse buttons
            if (keyCode != Input.Buttons.LEFT && keyCode != Input.Buttons.RIGHT && keyCode != Input.Buttons.MIDDLE) {
                Gdx.app.error("GameSettings", "Cannot set non-mouse key for 'Shoot' action.");
                return;
            }
        } else {
            // For other actions, allow only keyboard keys
//            if (keyCode >= Input.Keys.BUTTON_0) { // Mouse buttons start from Input.Keys.BUTTON_0
//                Gdx.app.error("GameSettings", "Cannot set mouse key for keyboard action '" + action + "'.");
//                return;
//            }
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }
        keyBindings.put(action, keyCode);
        Gdx.app.log("GameSettings", "Key binding for '" + action + "' set to: " + getKeyName(keyCode));
    }


<<<<<<< HEAD
=======
    // NEW: Getter and Setter for autoReloadEnabled
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public boolean isAutoReloadEnabled() {
        return autoReloadEnabled;
    }

    public void setAutoReloadEnabled(boolean autoReloadEnabled) {
        this.autoReloadEnabled = autoReloadEnabled;
        Gdx.app.log("GameSettings", "Auto-Reload set to: " + autoReloadEnabled);
    }

<<<<<<< HEAD

    public boolean isBlackAndWhiteModeEnabled() {
        return blackAndWhiteModeEnabled;
    }

    public void setBlackAndWhiteModeEnabled(boolean blackAndWhiteModeEnabled) {
        this.blackAndWhiteModeEnabled = blackAndWhiteModeEnabled;
        Gdx.app.log("GameSettings", "Black and White Mode set to: " + blackAndWhiteModeEnabled);
    }


    public static String getKeyName(int keyCode) {
        if (keyCode == Input.Buttons.LEFT) return "Mouse Left";
        if (keyCode == Input.Buttons.RIGHT) return "Mouse Right";
        if (keyCode == Input.Buttons.MIDDLE) return "Mouse Middle";
        if (keyCode >= Input.Keys.BUTTON_A && keyCode <= Input.Keys.BUTTON_MODE) return "Mouse Button " + (keyCode - Input.Keys.BUTTON_A);

        String keyName = Input.Keys.toString(keyCode);
        return (keyName != null && !keyName.isEmpty()) ? keyName : "Unknown";
    }


    public static Array<String> getAvailableTracks() {
        Array<String> tracks = new Array<>();
        FileHandle musicDir = Gdx.files.internal("musics/");
        if (musicDir.exists() && musicDir.isDirectory()) {
            for (FileHandle file : musicDir.list()) {
                String extension = file.extension();
=======
    // Modified getKeyName to handle mouse buttons correctly
    public static String getKeyName(int keyCode) {
//        if (keyCode >= 0 && keyCode < Input.Keys.BUTTON_0) { // Standard keyboard keys
//            return Input.Keys.toString(keyCode);
//        }
        if (keyCode == Input.Buttons.LEFT) {
            return "Mouse Left";
        } else if (keyCode == Input.Buttons.RIGHT) {
            return "Mouse Right";
        } else if (keyCode == Input.Buttons.MIDDLE) {
            return "Mouse Middle";
        }
        return "Unknown";
    }

    public static Array<String> getAvailableTracks() {
        Array<String> tracks = new Array<>();
        FileHandle musicDir = Gdx.files.internal("musics/"); // Your music directory
        if (musicDir.exists() && musicDir.isDirectory()) {
            for (FileHandle file : musicDir.list()) {
                String extension = file.extension();
                // Check for common music file extensions
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
                if (extension.equalsIgnoreCase("mp3") ||
                    extension.equalsIgnoreCase("ogg") ||
                    extension.equalsIgnoreCase("wav")) {
                    tracks.add(file.nameWithoutExtension());
                }
            }
        } else {
            Gdx.app.error("GameSettings", "Music directory 'musics/' not found or is not a directory!");
        }
        return tracks;
    }

    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
            currentMusic = null;
        }
    }
}
