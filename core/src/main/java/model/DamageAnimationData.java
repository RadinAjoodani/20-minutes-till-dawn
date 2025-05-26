package model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

// This class will represent the structure in your damages.json
public class DamageAnimationData {
    public String name; // "Player" or "Enemy"
    public ObjectMap<String, Array<String>> animations; // e.g., "damage" -> ["path1.png", "path2.png"]

    public DamageAnimationData() {
        // Default constructor for JSON deserialization
    }
}
