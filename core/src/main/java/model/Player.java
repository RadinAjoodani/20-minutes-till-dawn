<<<<<<< HEAD

=======
// model/Player.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
<<<<<<< HEAD
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.Gdx;


class ActiveBuff {
    public Ability ability;
    public float durationRemaining;
    public float originalValue;

    public ActiveBuff(Ability ability, float duration, float originalValue) {
        this.ability = ability;
        this.durationRemaining = duration;
        this.originalValue = originalValue;
    }
}

public class Player {
    private CharacterData characterData;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> damageAnimation;

    private float x, y;
    private float baseSpeed;
    private float currentSpeed;
    private int maxHp;
    private int currentHp;
    private int xp;
    private int level;
    public static final float PLAYER_SCALE_FACTOR = 4f;

    private boolean justLeveledUp = false;
    private Array<ActiveBuff> activeSpeedBuffs;


    private boolean isTakingDamage = false;
    private float damageAnimationStateTime = 0f;

    public Player(CharacterData characterData, Animation<TextureRegion> idleAnimation) {
        this.characterData = characterData;
        this.idleAnimation = idleAnimation;

        if (GameAssetManager.getGameAssetManager() != null) {
            this.damageAnimation = GameAssetManager.getGameAssetManager().getPlayerDamageAnimation("damage");
            if (this.damageAnimation == null) {
                Gdx.app.log("Player", "Player 'damage' animation not found in GameAssetManager.");
            }
        } else {
            Gdx.app.error("Player", "GameAssetManager is null, cannot load damage animation.");
        }


        if (characterData != null) {
            this.baseSpeed = characterData.getSpeed();
            this.currentSpeed = this.baseSpeed;
            this.maxHp = characterData.getHp();
            this.currentHp = this.maxHp;
        } else {
            Gdx.app.error("Player", "CharacterData is null, cannot initialize player stats.");
            this.baseSpeed = 1.0f;
            this.currentSpeed = 1.0f;
            this.maxHp = 10;
            this.currentHp = 10;
        }
        this.xp = 0;
        this.level = 1;
        this.activeSpeedBuffs = new Array<>();
    }


    public void update(float delta) {
        updateBuffs(delta);
        if (isTakingDamage) {
            damageAnimationStateTime += delta;
            if (damageAnimation != null && damageAnimation.isAnimationFinished(damageAnimationStateTime)) {
                isTakingDamage = false;
                damageAnimationStateTime = 0f;
            }
        }
    }


    public CharacterData getCharacterData() { return characterData; }
    public Animation<TextureRegion> getIdleAnimation() { return idleAnimation; }
    public Animation<TextureRegion> getDamageAnimation() { return damageAnimation; }

    public TextureRegion getCurrentFrame(float stateTime) {
        if (isTakingDamage && damageAnimation != null) {
            return damageAnimation.getKeyFrame(damageAnimationStateTime, false);
        }
        if (idleAnimation != null) {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
        Gdx.app.log("Player", "Returning null frame for player.");
        return null;
    }


    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getSpeed() { return currentSpeed * 20; }
    public float getRawCurrentSpeed() { return currentSpeed; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }

    public void takeDamage(int amount) {
        if (!isAlive() || isTakingDamage) return;

=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        this.currentHp -= amount;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
<<<<<<< HEAD
        Gdx.app.log("Player", "Player took " + amount + " damage. HP: " + currentHp + "/" + maxHp);

        if (damageAnimation != null) {
            isTakingDamage = true;
            damageAnimationStateTime = 0f;
        }
    }

    public boolean isTakingDamage() {
        return isTakingDamage;
    }

    public boolean isAlive() { return currentHp > 0; }
    public Rectangle getBounds(float stateTime) {
        TextureRegion currentFrameForBounds = null;
        if (isTakingDamage && damageAnimation != null) {
            currentFrameForBounds = damageAnimation.getKeyFrame(0);
        } else if (idleAnimation != null) {
            currentFrameForBounds = idleAnimation.getKeyFrame(stateTime, true);
        }

        if (currentFrameForBounds == null) {
            Gdx.app.log("Player.getBounds", "Could not get a valid frame for bounds calculation.");
            return new Rectangle(x,y,0,0);
        }

        float playerScaledWidth = currentFrameForBounds.getRegionWidth() * PLAYER_SCALE_FACTOR;
        float playerScaledHeight = currentFrameForBounds.getRegionHeight() * PLAYER_SCALE_FACTOR;
        return new Rectangle(x - playerScaledWidth/2f, y - playerScaledHeight/2f, playerScaledWidth, playerScaledHeight);
    }

    public int getXp() { return xp; }
    public int getLevel() { return level; }
    public int getXpToNextLevel() { return 20 * level; }

    public void addXp(int amount) {
        if (!isAlive()) return;
        this.xp += amount;
        Gdx.app.log("Player", "Gained " + amount + " XP. Total XP: " + xp + "/" + getXpToNextLevel());
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (xp >= getXpToNextLevel() && getXpToNextLevel() > 0) {
            xp -= getXpToNextLevel();
            level++;
            justLeveledUp = true;
            Gdx.app.log("Player", "LEVEL UP! New Level: " + level + ". Remaining XP for next: " + xp);
        }
    }

    public boolean hasJustLeveledUp() { return justLeveledUp; }
    public void resetLevelUpFlag() { justLeveledUp = false; }

    public void increaseMaxHealth(int amount) {
        this.maxHp += amount;
        this.currentHp += amount;
        if (this.currentHp > this.maxHp) this.currentHp = this.maxHp;
        Gdx.app.log("PlayerAbility", "Vitality: Max HP increased to " + this.maxHp + ". Current HP: " + this.currentHp);
    }

    public void applySpeedBuff(float multiplier, float duration) {
        for (int i = activeSpeedBuffs.size - 1; i >= 0; i--) {
            ActiveBuff buff = activeSpeedBuffs.get(i);
            if (buff.ability == Ability.SPEEDY) {
                currentSpeed = buff.originalValue;
                activeSpeedBuffs.removeIndex(i);
                Gdx.app.log("PlayerAbility", "Removed old Speedy buff. Speed reverted to: " + currentSpeed);
            }
        }
        this.currentSpeed = this.baseSpeed * multiplier;
        activeSpeedBuffs.add(new ActiveBuff(Ability.SPEEDY, duration, this.baseSpeed));
        Gdx.app.log("PlayerAbility", "Speedy: Player speed multiplied by " + multiplier + " for " + duration + "s. New currentSpeed: " + this.currentSpeed);
    }

    public void updateBuffs(float delta) {
        for (int i = activeSpeedBuffs.size - 1; i >= 0; i--) {
            ActiveBuff buff = activeSpeedBuffs.get(i);
            buff.durationRemaining -= delta;
            if (buff.durationRemaining <= 0) {
                if (buff.ability == Ability.SPEEDY) {
                    currentSpeed = buff.originalValue;
                    Gdx.app.log("PlayerAbility", "Speedy expired. Player speed reverted to " + currentSpeed);
                }
                activeSpeedBuffs.removeIndex(i);
            }
=======
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
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        }
    }
}
