package model;

import com.badlogic.gdx.utils.Array;
<<<<<<< HEAD
import com.badlogic.gdx.utils.ObjectMap;
=======
import com.badlogic.gdx.utils.ObjectMap; // Changed from java.util.Map to GDX ObjectMap
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

public class CharacterData {
    private String name;
    private int speed;
    private int hp;
<<<<<<< HEAD

    private ObjectMap<String, Array<String>> animations;

    public CharacterData(){

    }


=======
    // Changed to ObjectMap to align with LibGDX's Json parsing default for maps
    private ObjectMap<String, Array<String>> animations;

    public CharacterData(){
        // Default constructor for Json serialization
    }

    // Changed constructor to use ObjectMap
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
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

<<<<<<< HEAD

=======
    // Changed return type to ObjectMap
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
    public ObjectMap<String, Array<String>> getAnimations() {
        return animations;
    }
    public void setName(String name) { this.name = name; }
    public void setHp(int hp) { this.hp = hp; }
    public void setSpeed(int speed) { this.speed = speed; }

<<<<<<< HEAD

=======
    // Optional: for debugging/logging
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
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
