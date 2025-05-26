// model/Player.java
package model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
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
    private Animation<TextureRegion> damageAnimation; // Player's damage animation

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

    // Damage animation state
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

    // Call this method in GameView's render loop for the player
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

    public TextureRegion getCurrentFrame(float stateTime) { // GameView will call this
        if (isTakingDamage && damageAnimation != null) {
            return damageAnimation.getKeyFrame(damageAnimationStateTime, false); // Play once
        }
        if (idleAnimation != null) {
            return idleAnimation.getKeyFrame(stateTime, true); // Loop idle
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
        if (!isAlive() || isTakingDamage) return; // Already dead or in damage animation

        this.currentHp -= amount;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
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
        }
    }
}
