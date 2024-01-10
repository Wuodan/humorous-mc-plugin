package nezd53.sneakfart;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static java.lang.Math.random;
import static nezd53.sneakfart.SneakFart.fartTimeEnd;
import static nezd53.sneakfart.SneakFart.fartTimeStart;

public class FartListener implements Listener {

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking())
            handleSneak(event.getPlayer());
    }

    private static void handleSneak(Player player) {
        int startingTick = player.getStatistic(Statistic.SNEAK_TIME);
        int endTick = startingTick + (int) (random() * (fartTimeEnd - fartTimeStart) + fartTimeStart) * 20;

        player.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SneakFart.class), () -> {
            if (player.getStatistic(Statistic.SNEAK_TIME) >= endTick)
                FartHandler.fart(player);
        }, endTick - startingTick + 5);

    }
}
