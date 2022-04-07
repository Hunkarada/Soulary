/*  Mod for minecraft about souls and feelings
    Copyright (C) 2022 by Hunkarada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

package hunkarada.soulary.common.events.ticking;

import hunkarada.soulary.common.soul.SoulCapability;
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

import static hunkarada.soulary.common.soul.SoulCapability.FEEL_NAMES;
import static hunkarada.soulary.common.soul.SoulCapability.Provider.SOUL_CAPABILITY;
import static hunkarada.soulary.network.packets.SyncSoulCapability.sync;

public class TickingSoulEvents {
    public static void tickingSoul(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).tickHandler()){
            soulRegeneration(event);
            biomeExposure(event);
            soulAura(event);
        }
    }
    private static void soulRegeneration(LivingEvent event){
        event.getEntityLiving().getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> {
            soulCapability.add("will", 1f);
            soulCapability.add("stability", -0.1f);
        });
    }
    /*Method, which working, when you have stage >= 2.
     If so, you will share your feelings with any other entities in radius, based on your will.*/
    private static void soulAura(LivingEvent event){
        List<Byte> stages = new ArrayList<>();
        for (String key: FEEL_NAMES){
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
                            for (int index = 0; index < FEEL_NAMES.length; index++) {
                                soulCapability.add(FEEL_NAMES[index], event.getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getFeel(FEEL_NAMES[index]) / 100, (byte) (stages.get(index)-2), true);
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