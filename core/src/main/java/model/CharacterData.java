package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap; // Changed from java.util.Map to GDX ObjectMap

public class CharacterData {
    private String name;
    private int speed;
    private int hp;
    // Changed to ObjectMap to align with LibGDX's Json parsing default for maps
    private ObjectMap<String, Array<String>> animations;

    public CharacterData(){
        // Default constructor for Json serialization
    }

    // Changed constructor to use ObjectMap
    public CharacterData(String name, int speed, int hp, ObjectMap<String, Array<String>> animations) {
        this.name = name;
        this.speed = speed;
        this.hp = hp;
        this.animations = animations;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHp() {
        return hp;
    }

    // Changed return type to ObjectMap
    public ObjectMap<String, Array<String>> getAnimations() {
        return animations;
    }
    public void setName(String name) { this.name = name; }
    public void setHp(int hp) { this.hp = hp; }
    public void setSpeed(int speed) { this.speed = speed; }

    // Optional: for debugging/logging
    @Override
    public String toString() {
        return "CharacterData{" +
            "name='" + name + '\'' +
            ", speed=" + speed +
            ", hp=" + hp +
            ", animations=" + animations +
            '}';
    }
}
