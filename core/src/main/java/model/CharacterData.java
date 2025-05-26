package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class CharacterData {
    private String name;
    private int speed;
    private int hp;

    private ObjectMap<String, Array<String>> animations;

    public CharacterData(){

    }


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


    public ObjectMap<String, Array<String>> getAnimations() {
        return animations;
    }
    public void setName(String name) { this.name = name; }
    public void setHp(int hp) { this.hp = hp; }
    public void setSpeed(int speed) { this.speed = speed; }


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
