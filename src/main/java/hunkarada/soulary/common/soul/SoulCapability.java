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

package hunkarada.soulary.common.soul;

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
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;

import static hunkarada.soulary.common.soul.SoulCapability.Provider.SOUL_CAPABILITY;

/*This class is implementing soul capability.*/
@SuppressWarnings({"unchecked", "boxing", "WrapperTypeMayBePrimitive"})
public class SoulCapability {
    /*Name constants for setting default capability data*/
    public static final String[] STAT_NAMES = {"will", "stability"};
    public static final String[] FEEL_NAMES = {"joy", "sadness", "trust", "disgust", "fear", "anger", "surprise", "anticipation"};
    public static final String[] REVERSED_FEEL_NAMES = {"sadness", "joy", "disgust", "trust", "anger", "fear", "anticipation", "surprise"};
    public static final String[] STATE_NAMES = {"joy", "sadness", "trust", "disgust", "fear", "anger", "surprise", "anticipation", "aggressiveness", "awe", "contempt", "disapproval", "love", "optimism", "remorse", "submission"};
    //That's actually capability data

    //soulStats contains will (= mana) and stability (= exhaustion), they have simple calculations and used for casting wills.
    protected final HashMap<String, Float> soulStats = new HashMap<>();
    // soulFeels contains actual basic feels (check in FEEL_NAMES), soulAdaptation contains values for adaptation mechanic (check calculateWithAdaptation()).
    protected final HashMap<String, Float> soulFeels = new HashMap<>();
    protected final HashMap<String, Float> soulAdaptations = new HashMap<>();
    // soulStates contains currently affecting states to LivingEntity, check StatesHandler.class and SoulState.class.
    protected final HashMap<String, Byte> soulStates = new HashMap<>();
    // soulRelation contains UUID:soulFeels keypair to handle relations with other entities.
    protected final HashMap<String, HashMap<String, Float>> soulRelations = new HashMap<>();
    // tickCounter using to handle TickingSoulEvents.class and any other tick-related mechanics.
    protected byte tickCounter;

    /*Reference to LivingEntity with capability*/
    LivingEntity livingEntity;

    /*Constructor for capability, sets default values*/
    public SoulCapability(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
        setDefaults();
    }

    /*Methods for safety getting data from HashMap*/
    public float getStat(String key) {
        return soulStats.get(key);
    }
    public float getFeel(String key){
        return soulFeels.get(key);
    }
    public byte getState(String key){
        return soulStates.get(key);
    }
    /*Method to handle tickingCounter and ChaosNumbers*/
    public boolean tickHandler(){
        if (tickCounter < 20){
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
        else if (value instanceof Float){
            for(String key:keys) {
                Float data = nbt.getFloat(key);
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

    /*Returns CompoundTag with all capability data, in that state data prepared to saving at disk.*/
    public CompoundTag getNbtData() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("soulStats", convertHashToNbt(soulStats));
        nbt.put("soulFeels", convertHashToNbt(soulFeels));
        nbt.put("soulAdaptations", convertHashToNbt(soulAdaptations));
        nbt.put("soulStates", convertHashToNbt(soulStates));
        nbt.putByte("tickCounter", tickCounter);
        return nbt;
    }

    /*Converts data from NBT got from disk to HashMap*/
    public void setNbtData(CompoundTag nbt) {
        setFromNbtToHash((CompoundTag) nbt.get("soulStats"), soulStats);
        setFromNbtToHash((CompoundTag) nbt.get("soulFeels"), soulFeels);
        setFromNbtToHash((CompoundTag) nbt.get("soulAdaptations"), soulAdaptations);
        setFromNbtToHash((CompoundTag) nbt.get("soulStates"), soulStates);
        nbt.getByte("tickCounter");
    }

    /*Sets default data for new instance of capability*/
    private void setDefaults() {
        for (String key : STAT_NAMES) {
            soulStats.put(key, 0f);
        }
        for (String key : FEEL_NAMES) {
            soulFeels.put(key, 0f);
            soulAdaptations.put(key, 0f);
            FeelsHandler.validateBasicState(key, livingEntity);
        }
        for (String key : STATE_NAMES){
            soulStates.put(key, (byte) 0);
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

//        public void invalidate() {
//            lazyOptional.invalidate();
//        }

        public @NotNull SoulCapability getInstance() {
            return instance;
        }


    }

    /*Event for attaching capability for any LivingEntity*/
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            SoulCapability.Provider provider = new Provider(new SoulCapability((LivingEntity) event.getObject()));
            event.addCapability(new ResourceLocation(Soulary.MOD_ID, "soul_capability"), provider);
//            event.addListener(provider::invalidate);
        }
    }

    /*Event, which register capability in forge*/
    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.register(SoulCapability.class);
    }
    /*Event for saving capability when player switching dimension*/
    public static void playerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            event.getEntityLiving().getCapability(SOUL_CAPABILITY).ifPresent(soulCapability ->
                    soulCapability.setNbtData(event.getOriginal().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(event.getEntityLiving())).getNbtData()));
        }
        else if (event.isWasDeath()){
            event.getEntityLiving().getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> {
                soulCapability.setNbtData(event.getOriginal().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(event.getEntityLiving())).getNbtData());
                for (String key:FEEL_NAMES){
                    FeelsHandler.multiplyFeel(key, 0.5f, false, event.getEntityLiving());
                }
                FeelsHandler.addFeel("will", -100, event.getEntityLiving());
                FeelsHandler.addFeel("stability", 100, event.getEntityLiving());
            });
        }
    }
}
