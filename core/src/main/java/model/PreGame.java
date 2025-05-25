// model/PreGame.java
package model;

/**
 * Manages the state and selections made in the pre-game menu.
 */
public class PreGame {
    private Player player; // The player chosen in the pre-game menu
    private Gun selectedGun; // The gun chosen in the pre-game menu
    private int gameDurationMinutes; // The selected game duration in minutes
    private String username; // NEW: The username entered by the player

    public PreGame() {
        this.gameDurationMinutes = 5; // Default duration
        this.username = "Guest"; // Default username
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (player != null) {
            System.out.println("PreGame: Player set to " + player.getCharacterData().getName());
        } else {
            System.out.println("PreGame: Player set to null.");
        }
    }

    public Gun getSelectedGun() {
        return selectedGun;
    }

    public void setSelectedGun(Gun selectedGun) {
        this.selectedGun = selectedGun;
        if (selectedGun != null) {
            System.out.println("PreGame: Gun set to " + selectedGun.getGunData().getName());
        } else {
            System.out.println("PreGame: Gun set to null.");
        }
    }

    public int getGameDurationMinutes() {
        return gameDurationMinutes;
    }

    public void setGameDurationMinutes(int gameDurationMinutes) {
        this.gameDurationMinutes = gameDurationMinutes;
        System.out.println("PreGame: Game duration set to " + gameDurationMinutes + " minutes.");
    }

    // NEW: Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        System.out.println("PreGame: Username set to " + username);
    }
}
