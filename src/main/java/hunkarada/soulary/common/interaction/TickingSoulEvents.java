package hunkarada.soulary.common.interaction;

import hunkarada.soulary.capabilities.souls.SoulCapability;
import net.minecraft.core.BlockPos;
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
            soulRegeneration(event);
            biomeExposure(event);
            soulAura(event);
            if (event.getEntityLiving() instanceof Player){
                SoulCapability.debug((Player) event.getEntityLiving());
            }
        }
    }
    private static void soulRegeneration(LivingEvent event){
        event.getEntityLiving().getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> {
        });
    }
    private static void soulAura(LivingEvent event){
        float auraRadius = event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("will")/100*8;
        AABB sqrZone = new AABB(
                new BlockPos(event.getEntityLiving().getOnPos().getX()+auraRadius, event.getEntityLiving().getOnPos().getY()+auraRadius, event.getEntityLiving().getOnPos().getZ()+auraRadius),
                new BlockPos(event.getEntityLiving().getOnPos().getX()+auraRadius*-1, event.getEntityLiving().getOnPos().getY()+auraRadius*-1, event.getEntityLiving().getOnPos().getZ()+auraRadius*-1));
        List<Entity> entities = event.getEntityLiving().getLevel().getEntities(event.getEntityLiving(), sqrZone);
        List<LivingEntity> filteredEntities = new ArrayList<>();
        for (Entity entitiy:entities){
            if (Math.sqrt(event.getEntityLiving().getOnPos().distSqr(entitiy.getOnPos())) <= auraRadius && entitiy instanceof LivingEntity livingEntity) {
                filteredEntities.add(livingEntity);
            }
        }
        for (Entity entity:filteredEntities){
            entity.getCapability(SOUL_CAPABILITY).ifPresent(
                    soulCapability -> {
                        soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("joy")/100);
                        soulCapability.add("sadness", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("sadness")/100);
                        soulCapability.add("trust", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("trust")/100);
                        soulCapability.add("disgust", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("disgust")/100);
                        soulCapability.add("fear", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("fear")/100);
                        soulCapability.add("anger", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("anger")/100);
                        soulCapability.add("surprise", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("surprise")/100);
                        soulCapability.add("anticipation", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getSoulStat("anticipation")/100);
                    });
            if (entity instanceof Player){
                sync((Player) entity);
            }
        }
    }
    private static void biomeExposure(LivingEvent event){
        Collection<Biome> list = ForgeRegistries.BIOMES.getValues();
        Biome biome = event.getEntityLiving().getLevel().getBiomeManager().getBiome(event.getEntityLiving().getOnPos()).value();
    }
}