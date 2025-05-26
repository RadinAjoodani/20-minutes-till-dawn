<<<<<<< HEAD

=======
// model/EnemyData.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

<<<<<<< HEAD

public class EnemyData {
    private String name;
    private int damage;
    private float damage_rate;
=======
/**
 * Data class representing an enemy's properties and animation paths from JSON.
 */
public class EnemyData {
    private String name;
    private int damage;
    private float damage_rate; // Renamed from "damage rate" for Java convention
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    private int hp;
    private float speed;
    private ObjectMap<String, Array<String>> animations;

<<<<<<< HEAD

    public EnemyData() {}


=======
    // Default constructor for Json deserialization
    public EnemyData() {}

    // Getters
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
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

<<<<<<< HEAD

=======
    // Setters (optional, typically not needed for immutable data loaded from JSON)
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public void setName(String name) { this.name = name; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setDamage_rate(float damage_rate) { this.damage_rate = damage_rate; }
    public void setHp(int hp) { this.hp = hp; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setAnimations(ObjectMap<String, Array<String>> animations) { this.animations = animations; }
}
