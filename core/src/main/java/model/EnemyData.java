// model/EnemyData.java
package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Data class representing an enemy's properties and animation paths from JSON.
 */
public class EnemyData {
    private String name;
    private int damage;
    private float damage_rate; // Renamed from "damage rate" for Java convention
    private int hp;
    private float speed;
    private ObjectMap<String, Array<String>> animations;

    // Default constructor for Json deserialization
    public EnemyData() {}

    // Getters
    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public float getDamage_rate() {
        return damage_rate;
    }

    public int getHp() {
        return hp;
    }

    public float getSpeed() {
        return speed;
    }

    public ObjectMap<String, Array<String>> getAnimations() {
        return animations;
    }

    // Setters (optional, typically not needed for immutable data loaded from JSON)
    public void setName(String name) { this.name = name; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setDamage_rate(float damage_rate) { this.damage_rate = damage_rate; }
    public void setHp(int hp) { this.hp = hp; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setAnimations(ObjectMap<String, Array<String>> animations) { this.animations = animations; }
}
