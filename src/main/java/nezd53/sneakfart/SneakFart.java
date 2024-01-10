package nezd53.sneakfart;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

public final class SneakFart extends JavaPlugin {

    static boolean sneakFarts, fartCommand;
    static double fartDistance, fartTimeStart, fartTimeEnd,
            fartOffset, fartParticleSize, fartVolume, poopChance, deadlyPoopChance, nauseaChance, nauseaDistance;
    static int fartParticleCount;
    static String poopName;
    static int fartCount;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        sneakFarts = getConfig().getBoolean("EnableFarts", true);
        fartCommand = getConfig().getBoolean("FartCommand", true);
        fartDistance = getConfig().getDouble("FartDistance", 0.8);
        fartTimeStart = getConfig().getDouble("FartTimeStart", 10);
        fartTimeEnd = getConfig().getDouble("FartTimeEnd", 60);
        fartOffset = getConfig().getDouble("FartOffset", 0.25);
        fartParticleCount = getConfig().getInt("FartParticleCount", 25);
        fartParticleSize = getConfig().getDouble("FartParticleSize", 1);
        fartVolume = getConfig().getDouble("FartVolume", 1);
        poopChance = getConfig().getDouble("PoopChance", 0.1);
        deadlyPoopChance = getConfig().getDouble("DeadlyPoopChance", 0.05);
        poopName = getConfig().getString("PoopName", "Poop");
        nauseaChance = getConfig().getDouble("NauseaChance", 1);
        nauseaDistance = getConfig().getDouble("NauseaDistance", 3);

        if (sneakFarts)
            getServer().getPluginManager().registerEvents(new FartListener(), this);

        if (fartCommand)
            this.getCommand("fart").setExecutor(new FartCommandExecutor());

        fartCount = 0;

        // bStats integration
        int pluginId = 12663; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SingleLineChart("fart_count", () -> fartCount));
    }
}
