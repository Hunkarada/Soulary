package hunkarada.soulary.network;

import hunkarada.soulary.Soulary;
import hunkarada.soulary.network.packets.SyncSoulPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class SoularyNetwork {
    // Setting protocol version.
    private static final String PROTOCOL_VERSION = "1";
    // Creating new network channel.
    public static final SimpleChannel SOULARY_SIMPLE_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Soulary.MOD_ID,
                    "soulary_network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    // Initialization method, which registries my network channel.
    public static void init() {
        SOULARY_SIMPLE_CHANNEL.registerMessage(0, SyncSoulPacket.class, SyncSoulPacket::encode, SyncSoulPacket::new, SyncSoulPacket::send, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
