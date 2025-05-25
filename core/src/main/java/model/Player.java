// model/Player.java
package model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx; // Import Gdx for logging

/**
 * Represents the player character with their chosen hero's data and animations.
 */
public class Player {
    private CharacterData characterData;
    private Animation<TextureRegion> idleAnimation;
    private float x, y; // Player position
    private float speed; // Player movement speed
    private int currentHp;
    private int xp; // NEW: Player's current experience points
    private int level; // NEW: Player's current level
    public static final float PLAYER_SCALE_FACTOR = 4f;

    public Player(CharacterData characterData, Animation<TextureRegion> idleAnimation) {
        this.characterData = characterData;
        this.idleAnimation = idleAnimation;
        this.speed = characterData.getSpeed();
        this.currentHp = characterData.getHp();
        this.xp = 0; // NEW: Initialize XP
        this.level = 1; // NEW: Initialize level
    }

    public CharacterData getCharacterData() {
        return characterData;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return idleAnimation;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed * 20;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void takeDamage(int amount) {
        this.currentHp -= amount;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
        Gdx.app.log("Player", "Player took " + amount + " damage. HP: " + currentHp);
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public Rectangle getBounds(float stateTime) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        float playerScaledWidth = currentFrame.getRegionWidth() * PLAYER_SCALE_FACTOR;
        float playerScaledHeight = currentFrame.getRegionHeight() * PLAYER_SCALE_FACTOR;
        return new Rectangle(x, y, playerScaledWidth, playerScaledHeight);
    }

    // NEW: Get player's current XP
    public int getXp() {
        return xp;
    }

    // NEW: Get player's current Level
    public int getLevel() {
        return level;
    }

    // NEW: Calculate XP needed for next level
    public int getXpToNextLevel() {
        // Formula: 20 * currentLevel XP
        return 20 * level;
    }

    // NEW: Add XP to player
    public void addXp(int amount) {
        this.xp += amount;
        Gdx.app.log("Player", "Gained " + amount + " XP. Total XP: " + xp);
        checkLevelUp();
    }

    // NEW: Check if player can level up
    private void checkLevelUp() {
        while (xp >= getXpToNextLevel()) {
            xp -= getXpToNextLevel(); // Deduct XP for current level
            level++; // Increment level
            // Optionally, increase player stats here (e.g., max HP, speed)
            // characterData.setHp(characterData.getHp() + 10); // Example: increase max HP
            // this.currentHp = characterData.getHp(); // Heal to new max HP
            // this.speed = characterData.getSpeed(); // Update speed if it's dynamic
            Gdx.app.log("Player", "Player Leveled Up! New Level: " + level + ", Remaining XP: " + xp);
        }
    }
}
