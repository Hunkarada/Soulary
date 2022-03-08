package hunkarada.soulary.common.interaction;

import hunkarada.soulary.capabilities.souls.SoulCapability;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static hunkarada.soulary.Soulary.LOGGER;
import static hunkarada.soulary.capabilities.souls.SoulCapability.Provider.SOUL_CAPABILITY;
import static hunkarada.soulary.network.packets.SyncSoulCapability.sync;

public class TickingSoulEvents {
    public static void tickingSoul(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).tickHandler()){
            soulAura(event);
            biomeExposure(event);
        }
    }
    private static void soulAura(LivingEvent event){
        float coefficient = event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("will")/100*4;
        AABB aabb;
        if (coefficient == 0) {
            aabb = new AABB(event.getEntityLiving().getOnPos());
        }
        else {
            aabb = new AABB(
                    event.getEntityLiving().getOnPos().subtract(new Vec3i(event.getEntityLiving().getOnPos().getX(), event.getEntityLiving().getOnPos().getY(), event.getEntityLiving().getOnPos().getZ()).offset(coefficient * -1, coefficient * -1, coefficient * -1)),
                    event.getEntityLiving().getOnPos().subtract(new Vec3i(event.getEntityLiving().getOnPos().getX(), event.getEntityLiving().getOnPos().getY(), event.getEntityLiving().getOnPos().getZ()).offset(coefficient, coefficient, coefficient)));
            event.getEntityLiving().getLevel().getEntities(event.getEntityLiving(), aabb);
        }
        List<Entity> entities = event.getEntityLiving().getLevel().getEntities(event.getEntityLiving(), aabb);
        List<Entity> filteredEntities = new ArrayList<>();
        for (Entity entitiy:entities){
            LOGGER.warn(event.getEntityLiving().getOnPos().distSqr(entitiy.getOnPos()));
            if (event.getEntityLiving().getOnPos().distSqr(entitiy.getOnPos()) <= coefficient) {
                filteredEntities.add(entitiy);
            }
        }
        LOGGER.warn(filteredEntities);
        for (Entity entity:filteredEntities){
            entity.getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> soulCapability.add("joy/sadness", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("joy/sadness")/10));
            entity.getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> soulCapability.add("trust/disgust", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("trust/disgust")/10));
            entity.getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> soulCapability.add("fear/anger", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("fear/anger")/10));
            entity.getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> soulCapability.add("surprise/anticipation", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("surprise/anticipation")/10));
            if (entity instanceof Player){
                sync((Player) entity);
            }
        }
        LOGGER.warn("AURA OF " + event.getEntityLiving() + "IMPULSED");
    }
    private static void biomeExposure(LivingEvent event){
        Collection<Biome> list = ForgeRegistries.BIOMES.getValues();
        Biome biome = event.getEntityLiving().getLevel().getBiomeManager().getBiome(event.getEntityLiving().getOnPos()).value();
    }
}