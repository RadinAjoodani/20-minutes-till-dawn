package model;

public enum Ability {
    // Enum constants with display name and description
    VITALITY("Vitality", "Increases Max Health by 1 HP permanently."),
    DAMAGER("Damager", "Increases current gun damage by 25% for 10 seconds."),
    PROCREASE("Procrease", "Adds 1 projectile to the current gun permanently."),
    AMOCREASE("Amocrease", "Adds 5 to the current gun's max ammo permanently."),
    SPEEDY("Speedy", "Doubles player speed for 10 seconds.");

    private final String displayName;
    private final String description;

    Ability(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // Optional: Add type, duration, etc., if needed for more complex logic later
    // public enum AbilityType { PERMANENT, TIMED_BUFF }
    // private AbilityType type;
    // private float duration; // in seconds, for timed buffs
}
