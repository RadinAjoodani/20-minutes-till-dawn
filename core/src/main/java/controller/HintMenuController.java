package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import graphic.source.Main;
import model.Ability;
import model.App;
import model.CharacterData;
import model.GameAssetManager;
import model.GameSettings;
import view.HintMenuView;
import view.MainMenuView;

import java.util.HashMap;
import java.util.Map;

public class HintMenuController {
    private HintMenuView view;
    private GameAssetManager assetManager;
    private GameSettings gameSettings;

    public HintMenuController() {
        this.assetManager = GameAssetManager.getGameAssetManager();
        this.gameSettings = App.getInstance().getGameSettings();
    }

    public void setView(HintMenuView view) {
        this.view = view;
        loadDataIntoView();
        setupListeners();
    }

    private void setupListeners() {
        if (view == null || view.getBackButton() == null) {
            Gdx.app.error("HintMenuController", "View or BackButton is null. Cannot attach listeners.");
            return;
        }
        view.getBackButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBackButtonClick();
            }
        });
    }

    private void handleBackButtonClick() {
        Gdx.app.log("HintMenuController", "Navigating back to Main Menu.");
        Main.getMain().setScreen(new MainMenuView(new MainMenuController(), assetManager.getSkin()));
    }

    private void loadDataIntoView() {
        if (view == null) {
            Gdx.app.error("HintMenuController", "View is null. Cannot load data.");
            return;
        }

        // 1. Hero Descriptions
        Array<Map<String, String>> heroInfos = new Array<>();
        Array<String> heroNames = assetManager.getAllCharacterNames();
        if (heroNames != null) {
            for (String name : heroNames) {
                CharacterData data = assetManager.getCharacterDataByName(name);
                if (data != null) {
                    Map<String, String> heroInfo = new HashMap<>();
                    heroInfo.put("name", data.getName());
                    // Assuming CharacterData has methods like getHp() and getSpeed()
                    // If CharacterData has a description field, use that. Otherwise, construct one.
                    heroInfo.put("description", "HP: " + data.getHp() + ", Speed: " + data.getSpeed() + ". A valiant hero."); // Placeholder description
                    heroInfos.add(heroInfo);
                }
            }
        }
        view.setHeroInfo(heroInfos);

        // 2. Game Keys
        ObjectMap<String, Integer> keyBindings = gameSettings.getKeyBindings();
        Array<Map<String, String>> keyInfos = new Array<>();
        if (keyBindings != null) {
            for (ObjectMap.Entry<String, Integer> entry : keyBindings) {
                Map<String, String> keyInfo = new HashMap<>();
                keyInfo.put("action", entry.key);
                keyInfo.put("key", GameSettings.getKeyName(entry.value));
                keyInfos.add(keyInfo);
            }
        }
        view.setKeyBindingInfo(keyInfos);

        // 3. Cheat Codes
        Array<Map<String, String>> cheatInfos = new Array<>();
        cheatInfos.add(createCheatInfo("Press '1'", "Reduce game time by 1 minute."));
        cheatInfos.add(createCheatInfo("Press '2'", "Advance player to the next level."));
        cheatInfos.add(createCheatInfo("Press '3'", "Increase player's Max HP by 1 and heal 1 HP."));
        cheatInfos.add(createCheatInfo("Press '4'", "Spawn a Boss enemy near the player."));
        cheatInfos.add(createCheatInfo("Press '5'", "Instantly defeat 10 random non-Boss enemies."));
        view.setCheatCodeInfo(cheatInfos);

        // 4. Abilities
        Array<Map<String, String>> abilityInfos = new Array<>();
        for (Ability ability : Ability.values()) {
            Map<String, String> abilityInfo = new HashMap<>();
            abilityInfo.put("name", ability.getDisplayName());
            abilityInfo.put("description", ability.getDescription());
            abilityInfos.add(abilityInfo);
        }
        view.setAbilityInfo(abilityInfos);

        view.populateHints(); // Tell the view to update its tables
    }

    private Map<String, String> createCheatInfo(String key, String description) {
        Map<String, String> info = new HashMap<>();
        info.put("key", key);
        info.put("description", description);
        return info;
    }
}
