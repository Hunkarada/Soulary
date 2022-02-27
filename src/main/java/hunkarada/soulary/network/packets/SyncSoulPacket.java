package hunkarada.soulary.network.packets;

import hunkarada.soulary.capabilities.souls.SoulCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Packet for syncing data from server to client
public class SyncSoulPacket {
    CompoundTag data;
    public SyncSoulPacket(SoulCapability instance){
        this.data = instance.getNbtData();
    }
    public SyncSoulPacket(FriendlyByteBuf buffer){
        this.data = buffer.readNbt();
    }
    public void encode(FriendlyByteBuf buffer){
        buffer.writeNbt(data);
    }
    public void send(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                Minecraft.getInstance().player.getCapability(SoulCapability.Provider.SOUL_CAPABILITY).ifPresent(capability -> capability.setNbtData(data)));
        context.get().setPacketHandled(true);

    }
}
