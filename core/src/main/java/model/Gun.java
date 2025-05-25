// model/Gun.java
package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class Gun {
    private GunData gunData;
    private ObjectMap<String, Animation<TextureRegion>> animations;
    private int currentAmmo; // NEW: Current ammo in the magazine
    private boolean isReloading; // NEW: State for reloading
    private float reloadStateTime; // NEW: Time accumulator for reload animation

    public Gun(GunData gunData, ObjectMap<String, Animation<TextureRegion>> animations) {
        this.gunData = gunData;
        this.animations = animations;
        this.currentAmmo = gunData.getMax_ammo(); // Initialize with max ammo
        this.isReloading = false;
        this.reloadStateTime = gunData.getReload_time();
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
                finishReload();
            }
        }
    }

    // NEW: Method to finish reloading
    private void finishReload() {
        isReloading = false;
        currentAmmo = gunData.getMax_ammo();
        Gdx.app.log("Gun", gunData.getName() + " finished reloading. Ammo: " + currentAmmo);
    }

    // NEW: Check if gun is currently reloading
    public boolean isReloading() {
        return isReloading;
    }

    // NEW: Get current frame of reload animation
    public TextureRegion getReloadAnimationFrame(float stateTime) {
        Animation<TextureRegion> reloadAnimation = animations.get("reload");
        if (reloadAnimation != null) {
            return reloadAnimation.getKeyFrame(stateTime, false); // Not looping
        }
        return null;
    }

    public float getReloadStateTime() {
        return reloadStateTime;
    }


}
