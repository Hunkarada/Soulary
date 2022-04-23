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
import hunkarada.soulary.common.soul.states.*;
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
import java.util.Arrays;
import java.util.HashMap;

import static hunkarada.soulary.Soulary.LOGGER;
import static hunkarada.soulary.common.soul.SoulCapability.Provider.SOUL_CAPABILITY;

/*This class is implementing soul capability.*/
@SuppressWarnings({"unchecked", "boxing", "WrapperTypeMayBePrimitive"})
public class SoulCapability {
    /*Name constants for setting default capability data*/
    public static final String[] STAT_NAMES = {"will", "stability"};
    public static final String[] FEEL_NAMES = {"joy", "trust", "fear", "surprise", "sadness", "disgust", "anger", "anticipation"};
    public static final String[] COMPLEX_FEEL_NAMES = {"optimism", "love", "submission", "awe", "disapproval", "remorse", "contempt", "aggressiveness"};
    public static final String[] REVERSED_FEEL_NAMES = {"sadness", "disgust", "anger", "anticipation", "joy", "trust", "fear", "surprise"};
    public static final String[] STATE_NAMES = {"joy", "trust", "fear", "surprise", "sadness", "disgust", "anger", "anticipation", "optimism", "love", "submission", "awe", "disapproval", "remorse", "contempt", "aggressiveness"};
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
    // Enums for type-safety
    public enum Feels{
        JOY("joy"),
        TRUST("trust"),
        FEAR("fear"),
        SURPRISE("surprise"),
        SADNESS("sadness"),
        DISGUST("disgust"),
        ANGER("anger"),
        ANTICIPATION("anticipation");

        private final String key;
        Feels(String key){
            this.key = key;
        }
        public static Feels getFromKey(String key){
            Feels feels;
            switch (key){
                case "joy" -> feels = JOY;
                case "trust" -> feels = TRUST;
                case "fear" -> feels = FEAR;
                case  "surprise" -> feels = SURPRISE;
                case "sadness" -> feels = SADNESS;
                case "disgust" -> feels = DISGUST;
                case "anger" -> feels = ANGER;
                case  "anticipation" -> feels = ANTICIPATION;
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
            return feels;
        }
    }

    public enum Stats{
        WILL("will"),
        STABILITY("stability");
        private final String key;
        Stats(String key){
            this.key = key;
        }
        public static Stats getFromKey(String key){
            Stats stats;
            switch (key){
                case "will" -> stats = WILL;
                case "stability" -> stats = STABILITY;
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
            return stats;
        }
    }

    /*Methods for safety changing capability data
    * No need to create subtract and divide methods because I can use addFeel and multiplyFeel as subtract and divide
    * Also, I can set border to max value with setting state, or disable changing reversed adaptation (for example if I want to change all feelings at once), check validateState() method for more info.*/
    public void addStat(Stats stat, float value){
        float result = soulStats.get(stat.key) + value;
        validateStatCalculation(stat.key, result);
    }
    public void multiplyStat(Stats stat, float value){
        float result = soulStats.get(stat.key) * value;
        validateStatCalculation(stat.key, result);
    }


    public void addFeel(Feels feel, float value, byte state, boolean changeReversedAdaptation) {
        validateFeelsCalculation(feel.key, calculateAdaptation(feel.key, value, changeReversedAdaptation), state);
    }

    public void multiplyFeel(String key, float value, byte state, boolean changeReversedAdaptation) {
        float result = soulFeels.get(key) * value - soulFeels.get(key);
        validateFeelsCalculation(key, calculateAdaptation(key, result, changeReversedAdaptation), state);
    }

    /*Simplified calculation methods*/
    public void addFeel(Feels key, float value){
        addFeel(key, value, (byte) 3, true);
    }

    public void multiplyFeel(String key, float value){
        multiplyFeel(key, value, (byte) 3, true);
    }

    public void addFeel(Feels key, float value, boolean changeReversedAdaptation){
        addFeel(key, value, (byte) 3, changeReversedAdaptation);
    }

    public void multiplyFeel(String key, float value, boolean changeReversedAdaptation){
        multiplyFeel(key, value, (byte) 3, changeReversedAdaptation);
    }

    public void addFeel(Feels key, float value, byte state){
        addFeel(key, value, state, true);
    }

    public void multiplyFeel(String key, float value, byte state){
        multiplyFeel(key, value, state, true);
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
                    soulCapability.multiplyFeel(key, 0.5f, false);
                }
                soulCapability.addStat(Stats.WILL, -100);
                soulCapability.addStat(Stats.STABILITY, 100);
            });
        }
    }

    /*Method, which validating adaptation state, based on changed value.
     * Made by Brilliance
     * https://www.desmos.com/calculator/glrhteb7z9*/
    private float calculateAdaptation(String key, float value, boolean changeReversedAdaptation){
        int index = Arrays.stream(FEEL_NAMES).toList().indexOf(key);
        float adaptation = soulAdaptations.get(FEEL_NAMES[index]);
        float bufferAdaptation = soulAdaptations.get(FEEL_NAMES[index]) + value;
        float reversedBufferAdaptation = soulAdaptations.get(REVERSED_FEEL_NAMES[index]) - value;
        float resultAdaptation = 0;
        if (bufferAdaptation < 0 || adaptation < 0){
            float BA;
            float ADA;
            if (bufferAdaptation < -50){
                BA = -50;
                resultAdaptation +=(bufferAdaptation+50)*2;
            }
            else if (bufferAdaptation > 0){
                BA = 0;
            }
            else {
                BA = bufferAdaptation;
            }
            if (adaptation > 0){
                ADA = 0;
            }
            else {
                ADA = adaptation;
            }
            // when adaptation becomes negative
            resultAdaptation += (BA-ADA)*(-0.02*BA+1);
            if (bufferAdaptation < -50){
                bufferAdaptation = -50;
            }
            if (reversedBufferAdaptation > 50){
                reversedBufferAdaptation = 50;
            }
            else if (reversedBufferAdaptation < -50){
                reversedBufferAdaptation = -50;
            }
        }
        if (bufferAdaptation > 0 || adaptation > 0){
            float BA;
            float ADA;
            if (bufferAdaptation > 50){
                BA = 50;
                resultAdaptation +=(bufferAdaptation-50)*0.5;
            }
            else if (bufferAdaptation < 0){
                BA = 0;
            }
            else {
                BA = bufferAdaptation;
            }
            if (adaptation < 0){
                ADA = 0;
            }
            else {
                ADA = adaptation;
            }
            //when adaptation becomes positive
            resultAdaptation += (BA-ADA)*(-0.01*BA+1);
            if (bufferAdaptation > 50){
                bufferAdaptation = 50;
            }
            if (reversedBufferAdaptation > 50){
                reversedBufferAdaptation = 50;
            }
            else if (reversedBufferAdaptation < -50){
                reversedBufferAdaptation = -50;
            }
        }
        soulAdaptations.put(FEEL_NAMES[index], bufferAdaptation);
        if (changeReversedAdaptation){
            soulAdaptations.put(REVERSED_FEEL_NAMES[index], reversedBufferAdaptation);
        }
        return soulFeels.get(FEEL_NAMES[index]) + resultAdaptation;
    }

    /*Method, which checking calculations for invalid results.
            If invalid - sets value at 0-state borders.*/
    private void validateFeelsCalculation(String key, float result, byte state){
        float currentFeel = soulFeels.get(key);
        float border = 25 + (25*state);
        if (result < 0){
            soulFeels.put(key, 0f);
        }
        else if (result > border && currentFeel <= border){
            soulFeels.put(key, border);
        }
        else {
            soulFeels.put(key, result);
        }
        validateState(key);
    }

    private void validateStatCalculation(String key, float result){
        if (result > 100){
            soulStats.put(key, 100f);
        }
        else if (result < 0){
            soulStats.put(key, 0f);
        }
        else {
            soulStats.put(key, result);
        }
    }

    /*This method changing current state of entity, depending on feelings of this entity.*/
    public void validateState(String key){
        byte previousState = soulStates.get(key);
        byte newState = 0;
        float value = soulFeels.get(key);

        if (value > 25 && value <= 50) {
            newState = 1;
        } else if (value > 50 && value <= 75) {
            newState = 2;
        } else if (value > 75 && value <= 100) {
            newState = 3;
        }
        if (previousState != newState){
            soulStates.put(key, newState);
        }

        int basicIndex = Arrays.stream(FEEL_NAMES).toList().indexOf(key);
        int firstIndex = basicIndex-1;
        int secondIndex = basicIndex+1;

        if (firstIndex < 0){
            firstIndex = 7;
        }
        if (secondIndex > 7){
            secondIndex = 0;
        }

        byte firstState = soulStates.get(FEEL_NAMES[firstIndex]);
        byte secondState = soulStates.get(FEEL_NAMES[secondIndex]);

        if (newState >= firstState){
            soulStates.put(COMPLEX_FEEL_NAMES[basicIndex], firstState);
        }
        else {
            soulStates.put(COMPLEX_FEEL_NAMES[basicIndex], newState);
        }

        if (newState >= secondState){
            soulStates.put(COMPLEX_FEEL_NAMES[secondIndex], secondState);
        }
        else {
            soulStates.put(COMPLEX_FEEL_NAMES[secondIndex], newState);
        }
    }

    public static ISoulState generateState(String key, byte state, LivingEntity livingEntity){
        switch (key){
            case "aggressiveness" -> {return new Aggressiveness(state, livingEntity);}
            case "anger" -> {return new Anger(state, livingEntity);}
            case "anticipation" -> {return new Anticipation(state, livingEntity);}
            case "awe" -> {return new Awe(state, livingEntity);}
            case "contempt" -> {return new Contempt(state, livingEntity);}
            case "disapproval" -> {return new Disapproval(state, livingEntity);}
            case "disgust" -> {return new Disgust(state, livingEntity);}
            case "fear" -> {return new Fear(state, livingEntity);}
            case "joy" -> {return new Joy(state, livingEntity);}
            case "love" -> {return new Love(state, livingEntity);}
            case "optimism" -> {return new Optimism(state, livingEntity);}
            case "remorse" -> {return new Remorse(state, livingEntity);}
            case "sadness" -> {return new Sadness(state, livingEntity);}
            case "submission" -> {return new Submission(state, livingEntity);}
            case "surprise" -> {return new Surprise(state, livingEntity);}
            case "trust" -> {return new Trust(state, livingEntity);}
        }
        return null;
    }

    public static void debug(LivingEntity livingEntity){
        LOGGER.warn("BEFORE");
        LOGGER.warn(livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStats);
        LOGGER.warn(livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels);
        LOGGER.warn(livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates);
        livingEntity.getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> {
            soulCapability.addStat(Stats.WILL, 10);
            soulCapability.addStat(Stats.STABILITY, 10);
            soulCapability.addFeel(Feels.JOY, 10);
            soulCapability.addFeel(Feels.TRUST, 5, (byte) 1);
        });
        LOGGER.warn("AFTER");
        LOGGER.warn(livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStats);
        LOGGER.warn(livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels);
        LOGGER.warn(livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates);
    }
}
