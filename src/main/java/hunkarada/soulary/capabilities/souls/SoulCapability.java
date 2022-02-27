package hunkarada.soulary.capabilities.souls;

// It's default realization of soul capability for player.

import hunkarada.soulary.Soulary;
import hunkarada.soulary.network.SoularyNetwork;
import hunkarada.soulary.network.packets.SyncSoulPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static hunkarada.soulary.capabilities.souls.SoulCapability.Provider.SOUL_CAPABILITY;

/*This class is realizing soul capability.
 *Every LivingEntity must have it with all that stats.
 *Data storages in NBT, for manipulating data use HashMaps, then convert to CompoundTag.*/
@SuppressWarnings({"unchecked", "boxing", "WrapperTypeMayBePrimitive"})
public class SoulCapability {
    /*Constants for setting defaults*/
    public static final String[] STAT_NAMES = {"soulWill", "soulStability"};
    public static final float[] STAT_VALUES = {100, 10};
    public static final String[] FEELINGS_NAMES = {"joy/sadness", "trust/disgust", "fear/anger", "surprise/anticipation"};

    /*That's actually capability data*/
    public HashMap<String, Float> soulStats = new HashMap<>();
    public HashMap<String, Float> soulFeelings = new HashMap<>();

    /*Coverts HashMaps to CompoundTag*/
    public static <T> CompoundTag convertHashToNbt(HashMap<String, T> hashMap){
        T[] values = (T[]) hashMap.values().toArray();
        String[] keys = hashMap.keySet().toArray(new String[]{});
        CompoundTag nbt = new CompoundTag();
        int x = 0;
        if (values[0] instanceof String){
            for (T data:values) {
                nbt.putString(keys[x], (String) data);
                x++;
            }
        }
        else if (values[0] instanceof Byte){
            for (T data:values) {
                nbt.putByte(keys[x], (Byte) data);
                x++;
            }
        }
        else if (values[0] instanceof Short){
            for (T data:values) {
                nbt.putShort(keys[x], (Short) data);
                x++;
            }
        }
        else if (values[0] instanceof Integer){
            for (T data:values) {
                nbt.putInt(keys[x], (Integer) data);
                x++;
            }
        }
        else if (values[0] instanceof Float){
            for (T data:values) {
                nbt.putFloat(keys[x], (Float) data);
                x++;
            }
        }
        else if (values[0] instanceof Long){
            for (T data:values) {
                nbt.putLong(keys[x], (Long) data);
                x++;
            }
        }
        else if (values[0] instanceof Double){
            for (T data:values) {
                nbt.putDouble(keys[x], (Double) data);
                x++;
            }
        }
        else if (values[0] instanceof Boolean){
            for (T data:values) {
                nbt.putBoolean(keys[x], (Boolean) data);
                x++;
            }
        }
        return nbt;
    }
    /*Sets capability data from CompoundTag*/
    public static <T> void setFromNbtToHash(CompoundTag nbt, HashMap<String, T> hashMap){
        String[] keys = hashMap.keySet().toArray(new String[]{});
        T value = (T) hashMap.values().toArray()[0];
        if (value instanceof String){
            for (String key:keys) {
                String data = nbt.getString(key);
                hashMap.put(key, (T) data);
            }
        }
        else if (value instanceof Byte){
            for(String key:keys) {
                Byte data = nbt.getByte(key);
                hashMap.put(key, (T) data);
            }
        }
        else if (value instanceof Short){
            for(String key:keys) {
                Short data = nbt.getShort(key);
                hashMap.put(key, (T) data);
            }
        }
        else if (value instanceof Integer){
            for(String key:keys) {
                Integer data = nbt.getInt(key);
                hashMap.put(key, (T) data);
            }
        }
        else if (value instanceof Float){
            for(String key:keys) {
                Float data = nbt.getFloat(key);
                hashMap.put(key, (T) data);
            }
        }
        else if (value instanceof Long){
            for(String key:keys) {
                Long data = nbt.getLong(key);
                hashMap.put(key, (T) data);
            }
        }
        else if (value instanceof Double){
            for(String key:keys) {
                Double data = nbt.getDouble(key);
                hashMap.put(key, (T) data);
            }
        }
        else if (value instanceof Boolean){
            for(String key:keys) {
                Boolean data = nbt.getBoolean(key);
                hashMap.put(key, (T) data);
            }
        }
    }
    /*Constructor for capability, sets default values*/
    public SoulCapability(){
        setDefaults();
    }

    /*Returns CompoundTag with all capability data, in that state it's ready for saving capability to disk.*/
    public CompoundTag getNbtData() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("soulStats", convertHashToNbt(soulStats));
        nbt.put("soulFeelings", convertHashToNbt(soulFeelings));
        return nbt;
    }

    /*Sets data from CompoundTag (got from readNbt()) to HashMaps*/
    public void setNbtData(CompoundTag nbt){
        setFromNbtToHash((CompoundTag) nbt.get("soulStats"), soulStats);
        setFromNbtToHash((CompoundTag) nbt.get("soulFeelings"), soulFeelings);
    }

    /*Sets default data for instance of capability*/
    public void setDefaults(){
        int x = 0;
        final Random random = new Random();
        for (String key : STAT_NAMES) {
            soulStats.put(key, STAT_VALUES[x]);
            x++;
        }
        for (String key : FEELINGS_NAMES) {
            soulFeelings.put(key, random.nextFloat(0, 100));
        }
    }

    /*Class for registering Serializable capability*/
    public static class Provider implements ICapabilitySerializable<Tag> {
        public static Capability<SoulCapability> SOUL_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

        SoulCapability instance;
        LazyOptional<SoulCapability> lazyOptional = LazyOptional.of(this::getInstance);

        public Provider(SoulCapability instance) {
            this.instance = instance;
        }
        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
            return SOUL_CAPABILITY.orEmpty(capability, lazyOptional);
        }

        @Override
        public Tag serializeNBT() {
            return instance.getNbtData();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            instance.setNbtData((CompoundTag) nbt);
        }

        public void invalidate() {
            lazyOptional.invalidate();
        }

        public @NotNull SoulCapability getInstance() {
            return instance;
        }


    }

    /*Event for attaching capability for any LivingEntity*/
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity){
            SoulCapability.Provider provider = new Provider(new SoulCapability());
            event.addCapability(new ResourceLocation(Soulary.MOD_ID, "soul_capability"), provider);
            event.addListener(provider::invalidate);
        }
    }

    /*Event, which register capability in forge*/
    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.register(SoulCapability.class);
    }

    /*Method, which sending capability to LocalPlayer form server
    * Use every time, when capability on server updates*/
    public static void sync(Player player){
        if (!player.level.isClientSide){
            ServerPlayer serverPlayer = (ServerPlayer) player;
            player.getCapability(SOUL_CAPABILITY).ifPresent(capability ->
                    SoularyNetwork.SOULARY_SIMPLE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncSoulPacket(capability)));
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
