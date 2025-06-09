package nezd53.sneakfart;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class SneakFart extends JavaPlugin {

    static boolean sneakFarts, fartCommand;
    static double fartDistance, fartTimeStart, fartTimeEnd,
            fartOffset, fartParticleSize, fartVolume, fartPitch, fartVolumeCustom, fartPitchCustom, poopChance, deadlyPoopChance, nauseaChance, nauseaDistance;
    static int fartParticleCount;
    static String fartSoundName, fartSoundNameCustom, poopName;
    static int fartCount;
    static ResourcePackListener resourcePackListener = new ResourcePackListener();

    @Override
    public void onEnable() {
        loadConfig();

        getServer().getPluginManager().registerEvents(resourcePackListener, this);

        if (sneakFarts)
            getServer().getPluginManager().registerEvents(new FartListener(), this);

        if (fartCommand)
            Optional.ofNullable(this.getCommand("fart"))
                    .ifPresent(pluginCommand -> pluginCommand.setExecutor(new FartCommandExecutor()));

        fartCount = 0;

        // bStats integration
        int pluginId = 12663; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SingleLineChart("fart_count", () -> fartCount));
    }

    private void loadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        sneakFarts = config.getBoolean("EnableFarts", true);
        fartCommand = config.getBoolean("FartCommand", true);
        fartDistance = config.getDouble("FartDistance", 0.8);
        fartTimeStart = config.getDouble("FartTimeStart", 10);
        fartTimeEnd = config.getDouble("FartTimeEnd", 60);
        fartOffset = config.getDouble("FartOffset", 0.25);
        fartParticleCount = config.getInt("FartParticleCount", 25);
        fartParticleSize = config.getDouble("FartParticleSize", 1);
        fartSoundName = config.getString("FartSoundName", "minecraft:block.wet_grass.place");
        fartVolume = config.getDouble("FartVolume", 1);
        fartPitch = config.getDouble("FartPitch", 1);
        fartVolumeCustom = config.getDouble("FartVolumeCustom", 1);
        fartPitchCustom = config.getDouble("FartPitchCustom", 1);
        poopChance = config.getDouble("PoopChance", 0.1);
        deadlyPoopChance = config.getDouble("DeadlyPoopChance", 0.05);
        poopName = config.getString("PoopName", "Poop");
        nauseaChance = config.getDouble("NauseaChance", 1);
        nauseaDistance = config.getDouble("NauseaDistance", 3);
        // load from environment variable or config
        fartSoundNameCustom = Optional.ofNullable(System.getenv("FART_SOUND_NAME"))
            .orElse(config.getString("FART_SOUND_NAME", null));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
