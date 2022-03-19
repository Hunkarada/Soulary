package hunkarada.soulary.network.packets;

import hunkarada.soulary.capabilities.souls.SoulCapability;
import hunkarada.soulary.network.SoularyNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

import static hunkarada.soulary.capabilities.souls.SoulCapability.Provider.SOUL_CAPABILITY;

// Packet for syncing capability data with client
public class SyncSoulCapability {
    CompoundTag data;
    public SyncSoulCapability(SoulCapability instance){
        this.data = instance.getNbtData();
    }
    public SyncSoulCapability(FriendlyByteBuf buffer){
        this.data = buffer.readNbt();
    }
    public void encode(FriendlyByteBuf buffer){
        buffer.writeNbt(data);
    }
    public void send(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(SoulCapability.Provider.SOUL_CAPABILITY).ifPresent(capability -> capability.setNbtData(data));
            }
        });
        context.get().setPacketHandled(true);

    }

    /*Method, which sending capability to LocalPlayer form server
     * Use every time, when capability on server updates*/
    public static void sync(Player player){
        if (!player.level.isClientSide){
            ServerPlayer serverPlayer = (ServerPlayer) player;
            player.getCapability(SOUL_CAPABILITY).ifPresent(capability ->
                    SoularyNetwork.SOULARY_SIMPLE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncSoulCapability(capability)));
        }
    }
    // Sync on LogIn
    public static void syncLoggingIn(PlayerEvent.PlayerLoggedInEvent event){
        sync(event.getPlayer());
    }

    // Sync after changing dimension
    public static void syncChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event){
        sync(event.getPlayer());
    }

    // Sync on respawn
    public static void syncOnRespawn(PlayerEvent.PlayerRespawnEvent event){
        sync(event.getPlayer());
    }
}
