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

package hunkarada.soulary;

import hunkarada.soulary.common.soul.SoulCapability;
import hunkarada.soulary.common.events.ticking.TickingSoulEvents;
import hunkarada.soulary.network.SoularyNetwork;
import hunkarada.soulary.network.packets.SyncSoulCapability;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static hunkarada.soulary.Soulary.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class Soulary {

    public static final String MOD_ID = "soulary";
    public static final Logger LOGGER = LogManager.getLogger();

    public void onCommonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, SoulCapability::attachCapability);
        MinecraftForge.EVENT_BUS.addListener(SyncSoulCapability::syncChangedDimension);
        MinecraftForge.EVENT_BUS.addListener(SyncSoulCapability::syncLoggingIn);
        MinecraftForge.EVENT_BUS.addListener(SyncSoulCapability::syncOnRespawn);
        MinecraftForge.EVENT_BUS.addListener(SoulCapability::playerClone);
        MinecraftForge.EVENT_BUS.addListener(TickingSoulEvents::tickingSoul);
        SoularyNetwork.init();
    }

    public void onClientSetup(FMLClientSetupEvent event){
//        OverlayRegistry.registerOverlayTop("SoularyHUD", new SoulHud());
    }
    public void onServerSetup(FMLDedicatedServerSetupEvent event){
    }

    public Soulary() {
        // Register ourselves for server and other game events we are interested in
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onServerSetup);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(SoulCapability::registerCapability);
    }
}
