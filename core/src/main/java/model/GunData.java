
package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class GunData {
    private String name;
    private int damage;
    private int projectile;
    private float fire_rate;
    private float reload_time;
    private int max_ammo;
    private ObjectMap<String, Array<String>> animations;


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
