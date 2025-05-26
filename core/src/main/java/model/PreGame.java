
package model;


public class PreGame {
    private Player player;
    private Gun selectedGun;
    private int gameDurationMinutes;
    private String username;

    public PreGame() {
        this.gameDurationMinutes = 5;

        if (App.getInstance().getCurrentUser() != null ){
            this.username = App.getInstance().getCurrentUser().getUsername();
        }
        else {
            this.username = "Guest";
        }
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


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        System.out.println("PreGame: Username set to " + username);
    }
}
