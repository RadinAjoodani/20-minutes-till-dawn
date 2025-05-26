<<<<<<< HEAD

=======
// model/Gun.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
<<<<<<< HEAD
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;










public class Gun {
    private GunData gunData;
    private ObjectMap<String, Animation<TextureRegion>> animations;


    private int currentMaxAmmo;
    private int currentAmmo;
    private int currentProjectiles;
    private float baseDamage;
    private float currentDamageMultiplier;

    private boolean isReloading;
    private float reloadStateTime;

    private Array<ActiveBuff> activeDamageBuffs;
=======
import com.badlogic.gdx.utils.ObjectMap;

public class Gun {
    private GunData gunData;
    private ObjectMap<String, Animation<TextureRegion>> animations;
    private int currentAmmo; // NEW: Current ammo in the magazine
    private boolean isReloading; // NEW: State for reloading
    private float reloadStateTime; // NEW: Time accumulator for reload animation
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

    public Gun(GunData gunData, ObjectMap<String, Animation<TextureRegion>> animations) {
        this.gunData = gunData;
        this.animations = animations;
<<<<<<< HEAD

        if (gunData != null) {
            this.baseDamage = gunData.getDamage();
            this.currentMaxAmmo = gunData.getMax_ammo();
            this.currentAmmo = this.currentMaxAmmo;
            this.currentProjectiles = gunData.getProjectile();
        } else {
            Gdx.app.error("Gun", "GunData is null, cannot initialize gun stats properly.");

            this.baseDamage = 1;
            this.currentMaxAmmo = 10;
            this.currentAmmo = 10;
            this.currentProjectiles = 1;
        }

        this.currentDamageMultiplier = 1.0f;
        this.isReloading = false;
        this.reloadStateTime = 0f;
        this.activeDamageBuffs = new Array<>();
=======
        this.currentAmmo = gunData.getMax_ammo(); // Initialize with max ammo
        this.isReloading = false;
        this.reloadStateTime = gunData.getReload_time();
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    }

    public GunData getGunData() {
        return gunData;
    }

    public ObjectMap<String, Animation<TextureRegion>> getAnimations() {
        return animations;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

<<<<<<< HEAD
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

        } else if (currentAmmo == 0) {

        } else if (isReloading) {

        }
    }

    public void startReload() {
        if (!isReloading && currentAmmo < currentMaxAmmo) {
            isReloading = true;
            reloadStateTime = 0f;
            Gdx.app.log("Gun", (gunData != null ? gunData.getName() : "Unknown Gun") + " started reloading.");
        }
    }

    public void updateReload(float delta) {
        if (isReloading) {
            reloadStateTime += delta;

            float actualReloadTime = gunData != null ? gunData.getReload_time() : 2.0f;
            Animation<TextureRegion> reloadAnimation = animations != null ? animations.get("reload") : null;

            if (reloadAnimation != null) {
                actualReloadTime = reloadAnimation.getAnimationDuration();
            }

            if (reloadStateTime >= actualReloadTime) {
=======
    // NEW: Method to reduce ammo when shooting
    public void shoot() {
        if (currentAmmo > 0 && !isReloading) {
            currentAmmo--;
            Gdx.app.log("Gun", "Fired " + gunData.getName() + ", Ammo left: " + currentAmmo);
        } else if (currentAmmo == 0) {
            Gdx.app.log("Gun", gunData.getName() + " is out of ammo. Reload!");
        } else if (isReloading) {
            Gdx.app.log("Gun", gunData.getName() + " is reloading...");
        }
    }

    // NEW: Method to start reloading
    public void startReload() {
        if (!isReloading && currentAmmo < gunData.getMax_ammo()) {
            isReloading = true;
            reloadStateTime = 0f; // Reset reload animation time
            Gdx.app.log("Gun", gunData.getName() + " started reloading.");
        }
    }

    // NEW: Method to update reload state
    public void updateReload(float delta) {
        if (isReloading) {
            reloadStateTime += delta;
            Animation<TextureRegion> reloadAnimation = animations.get("reload");
            if (reloadAnimation != null && reloadStateTime >= reloadAnimation.getAnimationDuration()) {
                finishReload(); // Finish reload once animation completes
            } else if (reloadAnimation == null && reloadStateTime >= gunData.getReload_time()) {
                // If no specific reload animation, use fire_rate as a simple reload time
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
                finishReload();
            }
        }
    }

<<<<<<< HEAD
    private void finishReload() {
        isReloading = false;
        currentAmmo = currentMaxAmmo;
        Gdx.app.log("Gun", (gunData != null ? gunData.getName() : "Unknown Gun") + " finished reloading. Ammo: " + currentAmmo);
    }

=======
    // NEW: Method to finish reloading
    private void finishReload() {
        isReloading = false;
        currentAmmo = gunData.getMax_ammo();
        Gdx.app.log("Gun", gunData.getName() + " finished reloading. Ammo: " + currentAmmo);
    }

    // NEW: Check if gun is currently reloading
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public boolean isReloading() {
        return isReloading;
    }

<<<<<<< HEAD
=======
    // NEW: Get current frame of reload animation
    public TextureRegion getReloadAnimationFrame(float stateTime) {
        Animation<TextureRegion> reloadAnimation = animations.get("reload");
        if (reloadAnimation != null) {
            return reloadAnimation.getKeyFrame(stateTime, false); // Not looping
        }
        return null;
    }

>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public float getReloadStateTime() {
        return reloadStateTime;
    }


<<<<<<< HEAD
    public void applyDamageBuff(float percentageIncrease, float duration) {


        for(int i = activeDamageBuffs.size - 1; i >= 0; i--) {
            ActiveBuff oldBuff = activeDamageBuffs.get(i);
            if (oldBuff.ability == Ability.DAMAGER) {


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
=======
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
}
