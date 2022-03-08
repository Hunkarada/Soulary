package hunkarada.soulary.capabilities.souls;

// It's default realization of soul capability for player.

import hunkarada.soulary.Soulary;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;

/*This class is realizing soul capability.
 *Every LivingEntity must have it with all that stats.
 *Data storages in NBT, for manipulating data use HashMaps, then convert to CompoundTag.*/
@SuppressWarnings({"unchecked", "boxing", "WrapperTypeMayBePrimitive"})
public class SoulCapability {
    /*Name constants for setting default capability data*/
    private static final String[] STAT_NAMES = {"will", "stability", "joy/sadness", "trust/disgust", "fear/anger", "surprise/anticipation"};

    /*That's actually capability data*/
    private final HashMap<String, Float> soulStats = new HashMap<>();
    private byte tickCounter;

    /*Methods for safety getting data from HashMap (without providing change methods)*/
    public float getSoulStat(String key) {
        return soulStats.get(key);
    }

    /*Methods for safety changing capability data
    * No need to create subtract and divide methods because I can use add and multiply as subtract and divide*/
    public void add(String key, float value) {
        float result = soulStats.get(key) + value;
        if (result > 100){
            soulStats.put(key, 100f);
        }
        else if (result < 0 && key.equals("will") || key.equals("stability")){
            soulStats.put(key, 0f);
        }
        else if (result < -100){
            soulStats.put(key, -100f);
        }
        else {
            soulStats.put(key, result);
        }

    }
    public void multiply(String key, float value) {
        float result = soulStats.get(key) * value;
        if (result > 100){
            soulStats.put(key, 100f);
        }
        else if (result < 0 && key.equals("will") || key.equals("stability")){
            soulStats.put(key, 0f);
        }
        else if (result < -100){
            soulStats.put(key, -100f);
        }
        else {
            soulStats.put(key, result);
        }
    }

    /*Method to handle tickingCounter*/
    public boolean tickHandler(){
        if (tickCounter < 100){
            tickCounter++;
            return false;
        }
        else {
            tickCounter = 0;
            return true;
        }
    }
    /*Coverts HashMaps to CompoundTag*/
    private static <T> CompoundTag convertHashToNbt(HashMap<String, T> hashMap){
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
    private static <T> void setFromNbtToHash(CompoundTag nbt, HashMap<String, T> hashMap){
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

    /*Returns CompoundTag with all capability data, in that state data prepared to saving at disk.*/
    public CompoundTag getNbtData() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("soulStats", convertHashToNbt(soulStats));
        nbt.putByte("tickCounter", tickCounter);
        return nbt;
    }

    /*Converts data from NBT got from disk to HashMap*/
    public void setNbtData(CompoundTag nbt) {
        setFromNbtToHash((CompoundTag) nbt.get("soulStats"), soulStats);
        nbt.getByte("tickCounter");
    }

    /*Sets default data for new instance of capability*/
    private void setDefaults() {
        final Random random = new Random();
        for (String key : STAT_NAMES) {
            if (key.equals("will") || key.equals("stability")){
                soulStats.put(key, random.nextFloat(0, 100));
            }
            else {
                soulStats.put(key, random.nextFloat(-100, 100));
            }
        }
        tickCounter = 0;
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
        if (event.getObject() instanceof LivingEntity) {
            SoulCapability.Provider provider = new Provider(new SoulCapability());
            event.addCapability(new ResourceLocation(Soulary.MOD_ID, "soul_capability"), provider);
            event.addListener(provider::invalidate);
        }
    }

    /*Event, which register capability in forge*/
    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.register(SoulCapability.class);
    }
}
