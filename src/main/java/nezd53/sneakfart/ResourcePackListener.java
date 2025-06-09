package nezd53.sneakfart;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePackListener implements Listener {
    private static final Set<UUID> playersWithPack = ConcurrentHashMap.newKeySet();

    public static boolean hasResourcePack(UUID playerId) {
        return playersWithPack.contains(playerId);
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if(event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            addPlayer(event);
        } else {
            removePlayer(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removePlayer(event);
    }

    private static void addPlayer(PlayerEvent event) {
        playersWithPack.add(event.getPlayer().getUniqueId());
    }

    private static void removePlayer(PlayerEvent event) {
        playersWithPack.remove(event.getPlayer().getUniqueId());
    }

}