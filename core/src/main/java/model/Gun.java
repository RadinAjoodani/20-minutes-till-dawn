// model/Gun.java
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array; // Import Gdx Array
import com.badlogic.gdx.utils.ObjectMap;

// Re-use ActiveBuff from Player.java or define a similar one here if preferred
// For this example, assuming ActiveBuff can be used or adapted.
// class ActiveGunBuff {
//     public Ability ability;
//     public float durationRemaining;
//     public float originalValue; // For damage multiplier
//     public ActiveGunBuff(Ability ability, float duration, float originalValue) { /* ... */ }
// }

public class Gun {
    private GunData gunData; // Base stats
    private ObjectMap<String, Animation<TextureRegion>> animations;

    // Current operational stats, can be modified by abilities
    private int currentMaxAmmo;
    private int currentAmmo;
    private int currentProjectiles;
    private float baseDamage; // From GunData
    private float currentDamageMultiplier; // For Damager buff

    private boolean isReloading;
    private float reloadStateTime;

    private Array<ActiveBuff> activeDamageBuffs;

    public Gun(GunData gunData, ObjectMap<String, Animation<TextureRegion>> animations) {
        this.gunData = gunData;
        this.animations = animations;

        if (gunData != null) {
            this.baseDamage = gunData.getDamage();
            this.currentMaxAmmo = gunData.getMax_ammo();
            this.currentAmmo = this.currentMaxAmmo; // Initialize with max ammo
            this.currentProjectiles = gunData.getProjectile();
        } else {
            Gdx.app.error("Gun", "GunData is null, cannot initialize gun stats properly.");
            // Set default/fallback values
            this.baseDamage = 1;
            this.currentMaxAmmo = 10;
            this.currentAmmo = 10;
            this.currentProjectiles = 1;
        }

        this.currentDamageMultiplier = 1.0f; // No buff initially
        this.isReloading = false;
        this.reloadStateTime = 0f; // Initialize to 0, not gunData.getReload_time()
        this.activeDamageBuffs = new Array<>();
    }

    public GunData getGunData() { // Returns base data
        return gunData;
    }

    public ObjectMap<String, Animation<TextureRegion>> getAnimations() {
        return animations;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public int getCurrentMaxAmmo() {
        return currentMaxAmmo;
    }

    public int getCurrentProjectiles() {
        return currentProjectiles;
    }

    public int getCurrentDamage() {
        return (int) (baseDamage * currentDamageMultiplier);
    }


    public void shoot() {
        if (currentAmmo > 0 && !isReloading) {
            currentAmmo--;
            // Gdx.app.log("Gun", "Fired " + gunData.getName() + ", Ammo left: " + currentAmmo);
        } else if (currentAmmo == 0) {
            // Gdx.app.log("Gun", gunData.getName() + " is out of ammo. Reload!");
        } else if (isReloading) {
            // Gdx.app.log("Gun", gunData.getName() + " is reloading...");
        }
    }

    public void startReload() {
        if (!isReloading && currentAmmo < currentMaxAmmo) { // Use currentMaxAmmo
            isReloading = true;
            reloadStateTime = 0f;
            Gdx.app.log("Gun", (gunData != null ? gunData.getName() : "Unknown Gun") + " started reloading.");
        }
    }

    public void updateReload(float delta) {
        if (isReloading) {
            reloadStateTime += delta;
            // Determine reload duration: from animation if present, else from GunData
            float actualReloadTime = gunData != null ? gunData.getReload_time() : 2.0f; // Default reload time if gunData is null
            Animation<TextureRegion> reloadAnimation = animations != null ? animations.get("reload") : null;

            if (reloadAnimation != null) {
                actualReloadTime = reloadAnimation.getAnimationDuration();
            }

            if (reloadStateTime >= actualReloadTime) {
                finishReload();
            }
        }
    }

    private void finishReload() {
        isReloading = false;
        currentAmmo = currentMaxAmmo; // Reload to currentMaxAmmo
        Gdx.app.log("Gun", (gunData != null ? gunData.getName() : "Unknown Gun") + " finished reloading. Ammo: " + currentAmmo);
    }

    public boolean isReloading() {
        return isReloading;
    }

    public float getReloadStateTime() {
        return reloadStateTime;
    }

    // --- Ability Effects ---
    public void applyDamageBuff(float percentageIncrease, float duration) {
        // Simple approach: this buff overrides previous ones.
        // Could be made stackable or to refresh existing.
        for(int i = activeDamageBuffs.size - 1; i >= 0; i--) {
            ActiveBuff oldBuff = activeDamageBuffs.get(i);
            if (oldBuff.ability == Ability.DAMAGER) { // Assuming ActiveBuff has an ability field
                // Revert effect of old Damager buff if it directly set multiplier based on baseDamage
                // Here, we just clear and apply new. If multiplier was additive, revert would be different.
                activeDamageBuffs.removeIndex(i);
            }
        }
        // Ensure previous buffs are truly reverted before applying a new one if they directly modify currentDamageMultiplier
        this.currentDamageMultiplier = 1.0f; // Reset to base before applying new buff

        this.currentDamageMultiplier *= (1.0f + percentageIncrease);
        activeDamageBuffs.add(new ActiveBuff(Ability.DAMAGER, duration, 1.0f)); // Store original multiplier as 1.0f
        Gdx.app.log("GunAbility", "Damager: Gun damage multiplier set to " + this.currentDamageMultiplier + " for " + duration + "s.");
    }

    public void increaseProjectiles(int amount) {
        this.currentProjectiles += amount;
        Gdx.app.log("GunAbility", "Procrease: Gun projectiles increased to " + this.currentProjectiles);
    }

    public void increaseMaxAmmo(int amount) {
        this.currentMaxAmmo += amount;
        this.currentAmmo += amount; // Also add to current ammo, up to new max
        if (this.currentAmmo > this.currentMaxAmmo) {
            this.currentAmmo = this.currentMaxAmmo;
        }
        Gdx.app.log("GunAbility", "Amocrease: Gun max ammo increased to " + this.currentMaxAmmo + ". Current ammo: " + this.currentAmmo);
    }

    public void updateBuffs(float delta) {
        for (int i = activeDamageBuffs.size - 1; i >= 0; i--) {
            ActiveBuff buff = activeDamageBuffs.get(i);
            buff.durationRemaining -= delta;
            if (buff.durationRemaining <= 0) {
                if (buff.ability == Ability.DAMAGER) {
                    this.currentDamageMultiplier = 1.0f; // Revert to no multiplier
                    Gdx.app.log("GunAbility", "Damager expired. Gun damage multiplier reverted to " + this.currentDamageMultiplier);
                }
                activeDamageBuffs.removeIndex(i);
            }
        }
    }
}
