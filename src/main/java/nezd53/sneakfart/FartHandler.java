package nezd53.sneakfart;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static java.lang.Math.cos;
import static java.lang.Math.random;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static nezd53.sneakfart.SneakFart.deadlyPoopChance;
import static nezd53.sneakfart.SneakFart.fartDistance;
import static nezd53.sneakfart.SneakFart.fartOffset;
import static nezd53.sneakfart.SneakFart.fartParticleSize;
import static nezd53.sneakfart.SneakFart.fartPitch;
import static nezd53.sneakfart.SneakFart.fartPitchCustom;
import static nezd53.sneakfart.SneakFart.fartSoundName;
import static nezd53.sneakfart.SneakFart.fartSoundNameCustom;
import static nezd53.sneakfart.SneakFart.fartVolume;
import static nezd53.sneakfart.SneakFart.fartVolumeCustom;
import static nezd53.sneakfart.SneakFart.nauseaChance;
import static nezd53.sneakfart.SneakFart.nauseaDistance;
import static nezd53.sneakfart.SneakFart.poopChance;
import static nezd53.sneakfart.SneakFart.poopName;

public class FartHandler {
    static void fart(Player player) {
        Particle.DustOptions options = new Particle.DustOptions(Color.fromRGB(139, 69, 19), (float) fartParticleSize);
        float yaw = player.getLocation().getYaw();
        Vector offset = new Vector(sin(toRadians(yaw)) * fartDistance, 0.25, -cos(toRadians(yaw)) * fartDistance);
        Location l = player.getLocation().add(offset);
      player.getWorld().spawnParticle(SpigotCompat.DUST_PARTICLE, l,
                25, fartOffset, fartOffset, fartOffset, options);
        playFartSound(player, l);

        if (random() < poopChance)
            poop(player, offset, l);

        if (random() < deadlyPoopChance)
            deadlyPoop(l);

        if (random() < nauseaChance)
            inflictNausea(l, player);

        SneakFart.fartCount++;
    }

    private static void playFartSound(Player player, Location l) {
        if (fartSoundNameCustom != null && ResourcePackListener.hasResourcePack(player.getUniqueId())) {
            // use custom sound from resource pack
            player.playSound(l, fartSoundNameCustom, SoundCategory.PLAYERS, (float) fartVolumeCustom, (float) fartPitchCustom);
        } else {
            // play default sound
            player.playSound(l, fartSoundName, (float) fartVolume, (float) fartPitch);
        }
    }

    private static void poop(Player player, Vector offset, Location l) {
        Item item = player.getWorld().dropItem(l, new ItemStack(Material.BROWN_DYE));
        item.setPickupDelay(32767);
        item.setTicksLived(5900);
        item.setCustomName(player.getName() + "'s " + poopName);
        item.setCustomNameVisible(true);
        item.setVelocity(offset.clone().divide(new Vector(10, 1, 10)));
    }

    private static void deadlyPoop(Location l) {
        //String textureStr = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWFhMDU5OTBkNjMzYzhmYjFhYWVmYTM1YzcwYzViMWU0YWFiODE2YWI1MmI4YzAyZDU0MzY4ODdhNjI3YTI0MCJ9fX0=";
        String textureLink = "http://textures.minecraft.net/texture/aaa05990d633c8fb1aaefa35c70c5b1e4aab816ab52b8c02d5436887a627a240";
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);

        // https://blog.jeff-media.com/creating-custom-heads-in-spigot-1-18-1/
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(textureLink);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);


        SkullMeta meta = (SkullMeta) head.getItemMeta();
        Objects.requireNonNull(meta).setOwnerProfile(profile);
        head.setItemMeta(meta);


        Optional.ofNullable(l.getWorld())
                .ifPresent(world -> {
                    Zombie zombie = (Zombie) world.spawnEntity(l, EntityType.ZOMBIE);
                    zombie.setBaby();
                    Optional.ofNullable(zombie.getEquipment())
                            .ifPresent(equipment -> equipment.setHelmet(head));
                    zombie.setInvisible(true);
                    zombie.setSilent(true);
                    zombie.setHealth(1);
                    zombie.setLootTable(SpigotCompat.EMPTY_LOOT_TABLE);
                    zombie.setInvisible(true);
                    zombie.setCustomName("Deadly Poop");
                });
    }

    private static void inflictNausea(Location l, Player player) {
        Optional.ofNullable(l.getWorld())
                .stream()
                .map(World::getPlayers)
                .flatMap(List::stream)
                .filter(p -> p.getLocation().distance(l) <= nauseaDistance)
                .filter(Predicate.not(player::equals))
                .forEach(p -> p.addPotionEffect(new PotionEffect(SpigotCompat.NAUSEA_EFFECT, 5 * 20, 5)));
    }
}
