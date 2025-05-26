package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class App {
    private static App instance;
    private Array<User> users;
    private User currentUser;
    private GameSettings gameSettings;

    private App() {
        users = new Array<>();
        gameSettings = new GameSettings();
        loadUsers();
    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public Array<User> getUsers() {
        if(users == null) users = new Array<>();
        return users;
    }

    public void addUsers(User user) {
        this.users.add(user);
        saveUsers();
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }


    public boolean removeUser(User user) {
        boolean removed = users.removeValue(user, true);
        if (removed) {
            saveUsers();
        }
        return removed;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void loadUsers() {
        Json json = new Json();
        FileHandle localFile = Gdx.files.local("users.json");

        if (localFile.exists()) {
            users = json.fromJson(Array.class, User.class, localFile);
        } else {

            FileHandle internalFile = Gdx.files.internal("users.json");
            if (internalFile.exists()) {
                users = json.fromJson(Array.class, User.class, internalFile);
                localFile.writeString(internalFile.readString(), false);
            } else {
                users = new Array<>();
            }
        }
    }

    public void saveUsers() {
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);

        FileHandle file = Gdx.files.local("users.json");


        String uglyJson = json.toJson(users);


        String prettyJson = formatJson(uglyJson);

        file.writeString(prettyJson, false);
    }

    private String formatJson(String uglyJson) {
        StringBuilder pretty = new StringBuilder();
        int indent = 0;
        boolean inQuotes = false;

        for (char c : uglyJson.toCharArray()) {
            switch (c) {
                case '"':
                    pretty.append(c);
                    if (c == '"') {
                        inQuotes = !inQuotes;
                    }
                    break;
                case '{':
                case '[':
                    pretty.append(c);
                    if (!inQuotes) {
                        pretty.append('\n');
                        indent++;
                        appendIndent(pretty, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!inQuotes) {
                        pretty.append('\n');
                        indent--;
                        appendIndent(pretty, indent);
                    }
                    pretty.append(c);
                    break;
                case ',':
                    pretty.append(c);
                    if (!inQuotes) {
                        pretty.append('\n');
                        appendIndent(pretty, indent);
                    }
                    break;
                case ':':
                    pretty.append(c);
                    if (!inQuotes) {
                        pretty.append(" ");
                    }
                    break;
                default:
                    pretty.append(c);
            }
        }
        return pretty.toString();
    }

    private void appendIndent(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }



}
