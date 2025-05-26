
package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


public class EnemyData {
    private String name;
    private int damage;
    private float damage_rate;
    private int hp;
    private float speed;
    private ObjectMap<String, Array<String>> animations;


    public EnemyData() {}


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


    public void setName(String name) { this.name = name; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setDamage_rate(float damage_rate) { this.damage_rate = damage_rate; }
    public void setHp(int hp) { this.hp = hp; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setAnimations(ObjectMap<String, Array<String>> animations) { this.animations = animations; }
}
