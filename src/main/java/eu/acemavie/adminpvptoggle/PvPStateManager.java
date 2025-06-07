package eu.acemavie.adminpvptoggle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class PvPStateManager {
    private final Path filePath;
    private final Set<String> pvpDisabledPlayers = new HashSet<>();
    private final Gson gson = new Gson();

    private boolean maceDisabled = false;


    public PvPStateManager(Path filePath) {
        this.filePath = filePath;
    }


    public boolean isMaceDisabled() {
        return maceDisabled;
    }

    public void setMaceDisabled(boolean disabled) {
        this.maceDisabled = disabled;
        if(disabled) { pvpDisabledPlayers.add("macedisabledhhehehheilazy"); } else { pvpDisabledPlayers.remove("macedisabledhhehehheilazy");}

    }

    public boolean toggleMacePvP() {
        maceDisabled = !maceDisabled;
        if(maceDisabled) { pvpDisabledPlayers.add("macedisabledhhehehheilazy"); } else { pvpDisabledPlayers.remove("macedisabledhhehehheilazy");}
        return maceDisabled;
    }


    public boolean isPvPDisabled(String playerName) {
        return pvpDisabledPlayers.contains(playerName.toLowerCase());
    }

    public boolean togglePvP(String playerName) {
        String name = playerName.toLowerCase();
        boolean disabled = !pvpDisabledPlayers.remove(name);
        if (disabled) pvpDisabledPlayers.add(name);
        return disabled;
    }

    public void load() {
        if (!Files.exists(filePath)) return;
        try {
            String json = Files.readString(filePath);
            Type type = new TypeToken<Set<String>>(){}.getType();
            Set<String> loaded = gson.fromJson(json, type);
            pvpDisabledPlayers.clear();
            if (loaded != null){
                pvpDisabledPlayers.addAll(loaded);
                if(pvpDisabledPlayers.contains("macedisabledhhehehheilazy")) {
                    maceDisabled = true;
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to load PvP states: " + e.getMessage());
        }
    }

    public void save() {
        try {
            String json = gson.toJson(pvpDisabledPlayers);
            Files.writeString(filePath, json);
        } catch (IOException e) {
            System.err.println("Failed to save PvP states: " + e.getMessage());
        }
    }
}
