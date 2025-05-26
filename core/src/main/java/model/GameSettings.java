
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;

public class GameSettings {
    private float musicVolume = 0.5f;
    private boolean sfxEnabled = true;
    private String currentMusicTrackName = null;
    private Music currentMusic;

    private ObjectMap<String, Integer> keyBindings;
    private boolean autoReloadEnabled;
    private boolean blackAndWhiteModeEnabled = false;

    public GameSettings() {
        keyBindings = new ObjectMap<>();
        keyBindings.put("Move Up", Input.Keys.W);
        keyBindings.put("Move Left", Input.Keys.A);
        keyBindings.put("Move Down", Input.Keys.S);
        keyBindings.put("Move Right", Input.Keys.D);
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
        }

        if (trackName == null || trackName.isEmpty()) {
            Gdx.app.log("GameSettings", "No track name provided to play.");
            currentMusicTrackName = null;
            return;
        }

        FileHandle musicFile = Gdx.files.internal("musics/" + trackName + ".ogg");
        if (!musicFile.exists()) {
            musicFile = Gdx.files.internal("musics/" + trackName + ".mp3");
            if (!musicFile.exists()) {
                musicFile = Gdx.files.internal("musics/" + trackName + ".wav");
            }
        }

        if (!musicFile.exists()) {
            Gdx.app.error("GameSettings", "Music file not found: " + musicFile.path());
            currentMusicTrackName = null;
            return;
        }

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
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            Gdx.app.log("GameSettings", "Music stopped.");
        }
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = MathUtils.clamp(musicVolume, 0f, 1f);
        if (currentMusic != null) {
            currentMusic.setVolume(this.musicVolume);
        }
        Gdx.app.log("GameSettings", "Music volume set to: " + this.musicVolume);
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
        if (trackName != null && !trackName.equals(currentMusicTrackName)) {
            playMusicTrack(trackName);
        } else if (trackName == null && currentMusicTrackName != null) {
            stopMusic();
            currentMusicTrackName = null;
        } else if (trackName != null && currentMusic == null) {
            playMusicTrack(trackName);
        }
    }


    public Music getMusic() {
        return currentMusic;
    }

    public ObjectMap<String, Integer> getKeyBindings() {
        return keyBindings;
    }

    public void setKeyBinding(String action, int keyCode) {
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
        }
        keyBindings.put(action, keyCode);
        Gdx.app.log("GameSettings", "Key binding for '" + action + "' set to: " + getKeyName(keyCode));
    }


    public boolean isAutoReloadEnabled() {
        return autoReloadEnabled;
    }

    public void setAutoReloadEnabled(boolean autoReloadEnabled) {
        this.autoReloadEnabled = autoReloadEnabled;
        Gdx.app.log("GameSettings", "Auto-Reload set to: " + autoReloadEnabled);
    }


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
