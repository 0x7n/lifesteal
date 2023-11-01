package me.limpan.lifesteal;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class LifeSteal extends JavaPlugin {
    //public static Map<UUID, Integer> playerKills = new HashMap<>();

    public static JSONObject settingsJSON;

    public static int minHearts = 2;
    public static int maxHearts = 20;
    public static int adjustableDeathHearts = 2;
    public static int adjustableKillHearts = 2;
    @Override
    public void onEnable() {
        // Plugin startup logic
        loadJSONFile();
        startup();
        getLogger().info("LifeSteal is enabled");
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getCommand("giveHearts").setExecutor(new AdminCommands());
        getCommand("takeHearts").setExecutor(new AdminCommands());
        getCommand("resetHearts").setExecutor(new AdminCommands());
        getCommand("setMaxHearts").setExecutor(new AdminCommands());
        getCommand("setMinHearts").setExecutor(new AdminCommands());
        getCommand("giveHeartItem").setExecutor(new AdminCommands());
        getCommand("setAdjustableDeathHearts").setExecutor(new AdminCommands());
        getCommand("setAdjustableKillHearts").setExecutor(new AdminCommands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.updateJSONFile();
    }

    public void startup()
    {
        if(settingsJSON.containsKey("minHearts"))
        {
            minHearts = ((Long)settingsJSON.get("minHearts")).intValue();
        }
        if(settingsJSON.containsKey("maxHearts"))
        {
            maxHearts = ((Long)settingsJSON.get("maxHearts")).intValue();
        }
        if(settingsJSON.containsKey("adjustableKillHearts"))
        {
            adjustableKillHearts = ((Long)settingsJSON.get("adjustableKillHearts")).intValue();
        }
        if(settingsJSON.containsKey("adjustableDeathHearts"))
        {
            int value = ((Long)settingsJSON.get("adjustableDeathHearts")).intValue();
            getLogger().info(Integer.toString(value));
            adjustableDeathHearts = ((Long)settingsJSON.get("adjustableDeathHearts")).intValue();
        }
    }

    public static int getMinHearts() { return minHearts; }
    public static int getMaxHearts() {
        return maxHearts;
    }

    public static void setMinHearts(int minimumHearts) {
        minHearts = minimumHearts;
        settingsJSON.put("minHearts", minimumHearts);
    }

    public static void setMaxHearts(int maximumHearts) {
        maxHearts = maximumHearts;
        settingsJSON.put("maxHearts", maximumHearts);
    }

    public static int getAdjustableKillHearts() { return adjustableKillHearts * 2; }
    public static int getAdjustableDeathHearts() { return adjustableDeathHearts * 2; }
    public static void setAdjustableKillHearts(int amount) {
        adjustableKillHearts = amount;
        settingsJSON.put("adjustableKillHearts", amount);
    }
    public static void setAdjustableDeathHearts(int amount) {
        adjustableDeathHearts = amount;
        settingsJSON.put("adjustableDeathHearts", amount);
    }

    public void updateJSONFile()
    {
        try {
            FileWriter file = new FileWriter("lifesteal.json");
            file.write(LifeSteal.settingsJSON.toJSONString());
            file.close();
        } catch (IOException e) {
            getLogger().info("When trying to write the json file the FileWriter encountered and IOException. Please make sure that the directory is not read only and that there is not already a directory called lifesteal.json");
            e.printStackTrace();
        }
    }

    public void loadJSONFile()
    {
        try {
            JSONParser jsonParser = new JSONParser();
            LifeSteal.settingsJSON = (JSONObject) jsonParser.parse(new FileReader("lifesteal.json"));
        } catch(IOException | ParseException e) {
            getLogger().info("The file lifesteal.json does not exist or has been tampered with");
            try{
                FileWriter file = new FileWriter("lifesteal.json");
                LifeSteal.settingsJSON = new JSONObject();
                file.write(LifeSteal.settingsJSON.toJSONString());
                file.close();
            } catch(IOException e2)
            {
                getLogger().info("Can't create the json file, something must be wrong. For now settings wont be saved.");
                e2.printStackTrace();
            }
        }
    }

    public static LifeSteal getInstance() {
        return getPlugin(LifeSteal.class);
    }

}
