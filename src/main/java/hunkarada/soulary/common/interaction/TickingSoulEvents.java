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
import java.util.HashMap;
import java.util.List;

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
    /*Method, which working, when you have stage >= 2 */
    private static void soulAura(LivingEvent event){
        List<Byte> stages = new ArrayList<>();
        for (String key: SoulCapability.FEEL_NAMES){
            stages.add(event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStage(key));
        }
        boolean stageChecker = false;
        for (byte stage:stages){
            if (stage >= 2){
                stageChecker = true;
                break;
            }
        }
        if (stageChecker) {
            float auraRadius = event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("will")/100*8;
            AABB sqrZone = new AABB(
                    new BlockPos(event.getEntityLiving().getOnPos().getX() + auraRadius, event.getEntityLiving().getOnPos().getY() + auraRadius, event.getEntityLiving().getOnPos().getZ() + auraRadius),
                    new BlockPos(event.getEntityLiving().getOnPos().getX() + auraRadius * -1, event.getEntityLiving().getOnPos().getY() + auraRadius * -1, event.getEntityLiving().getOnPos().getZ() + auraRadius * -1));
            List<Entity> entities = event.getEntityLiving().getLevel().getEntities(event.getEntityLiving(), sqrZone);
            List<LivingEntity> filteredEntities = new ArrayList<>();
            for (Entity entitiy : entities) {
                if (Math.sqrt(event.getEntityLiving().getOnPos().distSqr(entitiy.getOnPos())) <= auraRadius && entitiy instanceof LivingEntity livingEntity) {
                    filteredEntities.add(livingEntity);
                }
            }
            for (Entity entity : filteredEntities) {
                entity.getCapability(SOUL_CAPABILITY).ifPresent(
                        soulCapability -> {
                            if (stages.get(0) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("joy") / 100, (byte) (stages.get(0)-1));
                            }
                            if (stages.get(1) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("sadness") / 100, (byte) (stages.get(1)-1));
                            }
                            if (stages.get(2) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("trust") / 100, (byte) (stages.get(2)-1));
                            }
                            if (stages.get(3) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("disgust") / 100, (byte) (stages.get(3)-1));
                            }
                            if (stages.get(4) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("fear") / 100, (byte) (stages.get(4)-1));
                            }
                            if (stages.get(5) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("anger") / 100, (byte) (stages.get(5)-1));
                            }
                            if (stages.get(6) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("surprise") / 100, (byte) (stages.get(6)-1));
                            }
                            if (stages.get(7) >= 2){
                                soulCapability.add("joy", event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getStat("anticipation") / 100, (byte) (stages.get(7)-1));
                            }
                        });
                if (entity instanceof Player) {
                    sync((Player) entity);
                }
            }
        }
    }
    private static void biomeExposure(LivingEvent event){
        Collection<Biome> list = ForgeRegistries.BIOMES.getValues();
        Biome biome = event.getEntityLiving().getLevel().getBiomeManager().getBiome(event.getEntityLiving().getOnPos()).value();
    }
}