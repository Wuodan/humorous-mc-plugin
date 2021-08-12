package nezd53.sneakfart;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

import static java.lang.Math.*;
import static nezd53.sneakfart.SneakFart.*;

public class FartHandler {
    static void handleSneak(Player player) {
        int startingTick = player.getStatistic(Statistic.SNEAK_TIME);
        int endTick = startingTick + (int) (random() * (fartTimeEnd - fartTimeStart) + fartTimeStart) * 20;

        player.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(SneakFart.class), () -> {
            if (player.getStatistic(Statistic.SNEAK_TIME) >= endTick)
                fart(player);
        }, endTick - startingTick + 5);

    }

    static void fart(Player player) {
        Particle.DustOptions options = new Particle.DustOptions(Color.fromRGB(139, 69, 19), (float) fartParticleSize);
        float yaw = player.getLocation().getYaw();
        Vector offset = new Vector(sin(toRadians(yaw)) * fartDistance, 0.25, -cos(toRadians(yaw)) * fartDistance);
        Location l = player.getLocation().add(offset);
        player.getWorld().spawnParticle(Particle.REDSTONE, l,
                25, fartOffset, fartOffset, fartOffset, options);
        player.playSound(l, Sound.BLOCK_WET_GRASS_PLACE, (float) fartVolume, 0.005f);

        if (random() < poopChance)
            poop(player, offset, l);

        if (random() < deadlyPoopChance)
            deadlyPoop(player, l);

        if (random() < nauseaChance)
            inflictNausea(l, player);
    }

    private static void poop(Player player, Vector offset, Location l) {
        Item item = player.getWorld().dropItem(l, new ItemStack(Material.BROWN_DYE));
        item.setPickupDelay(32767);
        item.setTicksLived(5900);
        item.setCustomName(player.getName() + "'s " + poopName);
        item.setCustomNameVisible(true);
        item.setVelocity(offset.clone().divide(new Vector(10, 1, 10)));
    }

    private static void deadlyPoop(Player player, Location l) {
        String command = "summon zombie " + l.getX() + " " + l.getY() + " " + l.getZ() +
                " {ArmorItems:[{},{},{},{id:player_head,Count:1,tag:{SkullOwner:{Id:[I;1918731868,-266323871,-1302493604,-266336159],Properties:{textures:[{Value:\"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWFhMDU5OTBkNjMzYzhmYjFhYWVmYTM1YzcwYzViMWU0YWFiODE2YWI1MmI4YzAyZDU0MzY4ODdhNjI3YTI0MCJ9fX0=\"}]}}}}], " +
                "ActiveEffects:[{Duration:99999999, ShowParticles:0, Id:14}], IsBaby:1, Health:1, DeathLootTable:\"\"}";
        try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void inflictNausea(Location l, Player player) {
        List<Player> players = l.getWorld().getPlayers();
        for (Player p : players)
            if (p.getLocation().distance(l) <= nauseaDistance && !player.equals(p))
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 * 20, 5));
    }
}
