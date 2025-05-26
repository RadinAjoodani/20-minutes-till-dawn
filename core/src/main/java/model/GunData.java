<<<<<<< HEAD

=======
// model/GunData.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class GunData {
    private String name;
    private int damage;
<<<<<<< HEAD
    private int projectile;
    private float fire_rate;
    private float reload_time;
    private int max_ammo;
    private ObjectMap<String, Array<String>> animations;


=======
    private int projectile; // NEW: Number of projectiles per shot
    private float fire_rate; // This field exists in your JSON, but not used in `GameScreen` for shoot timing
    private float reload_time; // This is the cooldown between shots
    private int max_ammo; // NEW: Maximum ammunition for the gun
    private ObjectMap<String, Array<String>> animations;

    // Getters
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getProjectile() {
        return projectile;
    }

    public float getFire_rate() {
        return fire_rate;
    }

    public float getReload_time() {
        return reload_time;
    }

    public int getMax_ammo() {
        return max_ammo;
    }

    public ObjectMap<String, Array<String>> getAnimations() {
        return animations;
    }

<<<<<<< HEAD

=======
    // Setters (if needed for runtime changes, though often not for immutable data)
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setProjectile(int projectile) {
        this.projectile = projectile;
    }

    public void setFire_rate(float fire_rate) {
        this.fire_rate = fire_rate;
    }

    public void setReload_time(float reload_time) {
        this.reload_time = reload_time;
    }

    public void setMax_ammo(int max_ammo) {
        this.max_ammo = max_ammo;
    }

    public void setAnimations(ObjectMap<String, Array<String>> animations) {
        this.animations = animations;
    }
}
