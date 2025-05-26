package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.io.File;

public class User {
    private String username;
    private String password;
    private String avatarPath;
    private String securityAnswer;
    private int score;
    private int totalKill;
    private int maximumTimeAlive;


    public User(String username, String password, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.securityAnswer = securityAnswer;
        this.score = 0;
        this.totalKill = 0;
        this.maximumTimeAlive = 0;
    }

    public User(){

    }


    public User(String username, String password, String avatarPath, String securityQuestion, String securityAnswer, int score) {
        this.username = username;
        this.password = password;
        this.avatarPath = avatarPath;
        this.securityAnswer = securityAnswer;
        this.score = score;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public int getScore() {
        return score;
    }

    public int getTotalKill() {
        return totalKill;
    }

    public int getMaximumTimeAlive() {
        return maximumTimeAlive;
    }

    public Texture getAvatarTexture() {
        if (avatarPath != null) {
            try {
                FileHandle fileHandle;

                if (Gdx.files.internal(avatarPath).exists()) {
                    fileHandle = Gdx.files.internal(avatarPath);
                }
                else if (Gdx.files.local(avatarPath).exists()) {
                    fileHandle = Gdx.files.local(avatarPath);
                }
                else if (new File(avatarPath).exists()) {
                    fileHandle = Gdx.files.absolute(avatarPath);
                } else {
                    return GameAssetManager.getGameAssetManager().getTexture(GameAssetManager.getGameAssetManager().DEFAULT_AVATAR_PATH);
                }

                return new Texture(fileHandle);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return GameAssetManager.getGameAssetManager().getTexture(GameAssetManager.getGameAssetManager().DEFAULT_AVATAR_PATH);
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalKill(int totalKill) {
        this.totalKill = totalKill;
    }

    public void setMaximumTimeAlive(int maximumTimeAlive) {
        this.maximumTimeAlive = maximumTimeAlive;
    }
}
