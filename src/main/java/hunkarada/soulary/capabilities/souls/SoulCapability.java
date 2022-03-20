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

package hunkarada.soulary.capabilities.souls;

import hunkarada.soulary.Soulary;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;

import static hunkarada.soulary.Soulary.LOGGER;
import static hunkarada.soulary.capabilities.souls.SoulCapability.Provider.SOUL_CAPABILITY;

/*This class is realizing soul capability.
 *Every LivingEntity must have it with all that stats.
 *Data storages in NBT, for manipulating data use HashMaps, then convert to CompoundTag.*/
@SuppressWarnings({"unchecked", "boxing", "WrapperTypeMayBePrimitive"})
public class SoulCapability {
    /*Name constants for setting default capability data*/
    public static final String[] STAT_NAMES = {"will", "stability"};
    public static final String[] FEEL_NAMES = {"joy", "sadness", "trust", "disgust", "fear", "anger", "surprise", "anticipation"};

    /*That's actually capability data*/
    protected final HashMap<String, Float> soulStats = new HashMap<>();
    protected final HashMap<String, Float> soulFeels = new HashMap<>();
    protected final HashMap<String, Float> soulChaosNumbers = new HashMap<>();
    protected final HashMap<String, Float> soulAdaptations = new HashMap<>();
    protected final HashMap<String, Byte> soulStages = new HashMap<>();
    protected byte tickCounter;

    /*Methods for safety getting data from HashMap (without providing directly change methods)*/
    public float getStat(String key) {
        return soulStats.get(key);
    }
    public float getFeel(String key){
        return soulFeels.get(key);
    }
    public float getChaos(String key){
        return soulChaosNumbers.get(key);
    }
    public byte getStage(String key){
        return soulStages.get(key);
    }

    /*Method to handle tickingCounter and ChaosNumbers*/
    public boolean tickHandler(){
        if (tickCounter < 20){
            tickCounter++;
            return false;
        }
        else {
            tickCounter = 0;
            chaosHandler();
            regenerationHandler();
            return true;
        }
    }

    /*Method, which changing chaos (random) numbers, fired with tickHandler()*/
    private void chaosHandler(){
        Random random = new Random();
        for (String key:FEEL_NAMES){
            soulChaosNumbers.put(key, random.nextFloat(-1, 1));
        }
    }

    /*Method, which regenerates will, stability and adaptation values by a time, fired with tickHandler()*/
    private void regenerationHandler(){
        this.add("will", 1f);
        this.add("stability", -0.1f);
        for (String key : FEEL_NAMES) {
            validateAdaptation(key, soulAdaptations.get(key) + 0.001f);
        }
    }

    /*Methods for safety changing capability data
    * No need to create subtract and divide methods because I can use add and multiply as subtract and divide
    * Also, I can set border to max value with setting stage, check validateStage() method for more info.*/
    public void add(String key, float value, byte stage) {
        if (soulStats.containsKey(key)){
            float result = selectMap(key).get(key) + value;
            safetyCalc(key, result, stage);
        }
        else if (soulFeels.containsKey(key)) {
            float result = selectMap(key).get(key) + (value * soulAdaptations.get(key));
            safetyCalc(key, result, stage);
            validateAdaptation(key, value);
        }
    }
    public void multiply(String key, float value, byte stage) {
        if (soulStats.containsKey(key)){
            float result = selectMap(key).get(key) * value;
            safetyCalc(key, result, stage);
        }
        else if (soulFeels.containsKey(key)) {
            float result = selectMap(key).get(key) * (value * soulAdaptations.get(key));
            safetyCalc(key, result, stage);
            validateAdaptation(key, value);
        }
    }

    /*Calculation methods without setting border with stage byte*/
    public void add(String key, float value){
        add(key, value, (byte) 3);
    }
    public void multiply(String key, float value){
        multiply(key, value, (byte) 3);
    }

    /*Because I'll change only soulStats and soulFeels directly, and they haven't equal keys, so I'll get map from unique key,*/
    private HashMap<String, Float> selectMap(String key){
        if (soulStats.containsKey(key)){
            return soulStats;
        }
        else if (soulFeels.containsKey(key)){
            return soulFeels;
        }
        else throw new RuntimeException("Invalid key for HashMap");
    }

    /*Method, which validating current stage after changing feelings*/
    private void validateStage(String key){
        float value = soulFeels.get(key);
        if (value <= 25) {
            soulStages.put(key, (byte) 0);
        } else if (value > 25 && value <= 50) {
            soulStages.put(key, (byte) 1);
        } else if (value > 50 && value <= 75) {
            soulStages.put(key, (byte) 2);
        } else if (value > 75 && value <= 100) {
            soulStages.put(key, (byte) 3);
        }
    }

    /*Method, which validating adaptation state, based on changed value.*/
    private void validateAdaptation(String key, float value){
        float result = soulAdaptations.get(key) - value/100;
        if (result < 0){
            soulAdaptations.put(key, 0f);
        }
        else if (result > 1){
            soulAdaptations.put(key, 1f);
        }
        else {
            soulAdaptations.put(key, result);
        }
    }

    /*Method, which checking calculations for invalid results.
    If invalid - sets value at 0-stage borders.*/
    private void safetyCalc(String key, float result, byte stage){
        switch (stage){
            case 0 -> {
                if (result > 25){
                    selectMap(key).put(key, 25f);
                    if (soulFeels.containsKey(key)) {
                        validateStage(key);
                    }

                }
            }
            case 1 -> {
                if (result > 50){
                    selectMap(key).put(key, 50f);
                    if (soulFeels.containsKey(key)) {
                        validateStage(key);
                    }

                }
            }
            case 2 -> {
                if (result > 75){
                    selectMap(key).put(key, 75f);
                    if (soulFeels.containsKey(key)) {
                        validateStage(key);
                    }
                }
            }
            case 3 -> {
                if (result > 100){
                    selectMap(key).put(key, 100f);
                    if (soulFeels.containsKey(key)) {
                        validateStage(key);
                    }

                }
            }
        }
        if (result < 0){
            selectMap(key).put(key, 0f);
        }
        else {
            selectMap(key).put(key, result);
        }
        if (soulFeels.containsKey(key)) {
            validateStage(key);
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
        nbt.put("soulFeels", convertHashToNbt(soulFeels));
        nbt.put("soulChaosNumbers", convertHashToNbt(soulChaosNumbers));
        nbt.put("soulAdaptations", convertHashToNbt(soulAdaptations));
        nbt.put("soulStages", convertHashToNbt(soulStages));
        nbt.putByte("tickCounter", tickCounter);
        return nbt;
    }

    /*Converts data from NBT got from disk to HashMap*/
    public void setNbtData(CompoundTag nbt) {
        setFromNbtToHash((CompoundTag) nbt.get("soulStats"), soulStats);
        setFromNbtToHash((CompoundTag) nbt.get("soulFeels"), soulFeels);
        setFromNbtToHash((CompoundTag) nbt.get("soulChaosNumbers"), soulChaosNumbers);
        setFromNbtToHash((CompoundTag) nbt.get("soulAdaptations"), soulAdaptations);
        setFromNbtToHash((CompoundTag) nbt.get("soulStages"), soulStages);
        nbt.getByte("tickCounter");
    }

    /*Sets default data for new instance of capability*/
    private void setDefaults() {
        for (String key : STAT_NAMES) {
            soulStats.put(key, 0f);
        }
        for (String key : FEEL_NAMES) {
            Random random = new Random();
            soulFeels.put(key, 0f);
            soulStages.put(key, (byte) 0);
            soulChaosNumbers.put(key, random.nextFloat(-1, 1));
            soulAdaptations.put(key, 1f);
            validateStage(key);
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
            SoulCapability.Provider provider = new Provider(new SoulCapability());
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
        event.getEntityLiving().getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> soulCapability.setNbtData(event.getOriginal().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).getNbtData()));

    }
    public static void debug(Player player){
        LOGGER.warn(player.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).soulStats);
        LOGGER.warn(player.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).soulFeels);
        LOGGER.warn(player.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).soulChaosNumbers);
        LOGGER.warn(player.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).soulAdaptations);
        LOGGER.warn(player.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability()).soulStages);
    }
}
