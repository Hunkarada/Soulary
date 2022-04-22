package hunkarada.soulary.common.soul;

import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;

import static hunkarada.soulary.common.soul.SoulCapability.Provider.SOUL_CAPABILITY;
import static hunkarada.soulary.common.soul.StatesHandler.stateHandler;

public class FeelsHandler {

    /*Methods for safety changing capability data
    * No need to create subtract and divide methods because I can use addFeel and multiplyFeel as subtract and divide
    * Also, I can set border to max value with setting state, or disable changing reversed adaptation (for example if I want to change all feelings at once), check validateBasicState() method for more info.*/
    public static void addFeel(String key, float value, byte state, boolean changeReversedAdaptation, LivingEntity livingEntity) {
        validateFeelsCalculation(key, calculateWithAdaptation(key, value, changeReversedAdaptation, livingEntity), state, livingEntity);
    }

    public static void multiplyFeel(String key, float value, byte state, boolean changeReversedAdaptation, LivingEntity livingEntity) {
        float result = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.get(key) * value - livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.get(key);
        validateFeelsCalculation(key, calculateWithAdaptation(key, result, changeReversedAdaptation, livingEntity), state, livingEntity);
    }

    /*Simplified calculation methods*/
    public static void addFeel(String key, float value, LivingEntity livingEntity){
        addFeel(key, value, (byte) 3, true, livingEntity);
    }

    public static void multiplyFeel(String key, float value, LivingEntity livingEntity){
        multiplyFeel(key, value, (byte) 3, true, livingEntity);
    }

    public static void addFeel(String key, float value, boolean changeReversedAdaptation, LivingEntity livingEntity){
        addFeel(key, value, (byte) 3, changeReversedAdaptation, livingEntity);
    }

    public static void multiplyFeel(String key, float value, boolean changeReversedAdaptation, LivingEntity livingEntity){
        multiplyFeel(key, value, (byte) 3, changeReversedAdaptation,livingEntity);
    }

    public static void addFeel(String key, float value, byte state, LivingEntity livingEntity){
        addFeel(key, value, state, true, livingEntity);
    }

    public static void multiplyFeel(String key, float value, byte state, LivingEntity livingEntity){
        multiplyFeel(key, value, state, true, livingEntity);
    }

    /*Method, which validating current state after changing feelings
         works only for basic states (states, which you got directly from feeling values),complex states handling separately*/
    static void validateBasicState(String key, LivingEntity livingEntity){
        byte previousState = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates.get(key);
        byte newState = 0;
        float value = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.get(key);
        if (value > 25 && value <= 50) {
            newState = 1;
        } else if (value > 50 && value <= 75) {
            newState = 2;
        } else if (value > 75 && value <= 100) {
            newState = 3;
        }
        if (previousState != newState){
            livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates.put(key, newState);
            stateHandler(key, livingEntity);
        }
    }

    /*Method, which validating adaptation state, based on changed value.
    * Made by Brilliance
    * https://www.desmos.com/calculator/glrhteb7z9*/
    private static float calculateWithAdaptation(String key, float value, boolean changeReversedAdaptation, LivingEntity livingEntity){
        int index = Arrays.stream(SoulCapability.FEEL_NAMES).toList().indexOf(key);
        float adaptation = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulAdaptations.get(SoulCapability.FEEL_NAMES[index]);
        float bufferAdaptation = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulAdaptations.get(SoulCapability.FEEL_NAMES[index]) + value;
        float reversedBufferAdaptation = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulAdaptations.get(SoulCapability.REVERSED_FEEL_NAMES[index]) - value;
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
            resultAdaptation += (BA-ADA)*onAdaptationNegative(BA);
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
            resultAdaptation += (BA-ADA)*onAdaptationPositive(BA);
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
        livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulAdaptations.put(SoulCapability.FEEL_NAMES[index], bufferAdaptation);
        if (changeReversedAdaptation){
            livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulAdaptations.put(SoulCapability.REVERSED_FEEL_NAMES[index], reversedBufferAdaptation);
        }
        return livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.get(SoulCapability.FEEL_NAMES[index]) + resultAdaptation;
    }

    /*Functions for internal calculations of adaptation
        Positive means adaptation > 1, negative means adaptation < 1*/
    private static float onAdaptationPositive(float x){
        return (float) (-0.01*x+1);
    }

    private static float onAdaptationNegative(float x){
        return (float) (-0.02*x+1);
    }

    /*Method, which checking calculations for invalid results.
        If invalid - sets value at 0-state borders.*/
    private static void validateFeelsCalculation(String key, float result, byte state, LivingEntity livingEntity){
        switch (state){
            case 0 -> {
                if (result > 25){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 25f);
                }
                else if (result < 0){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 0f);
                }
                else {
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, result);
                }
            }
            case 1 -> {
                if (result > 50){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 50f);
                }
                else if (result < 0){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 0f);
                }
                else {
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, result);
                }
            }
            case 2 -> {
                if (result > 75){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 75f);
                }
               else if (result < 0){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 0f);
                }
                else {
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, result);
                }
            }
            case 3 -> {
                if (result > 100){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 100f);
                }
                else if (result < 0){
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, 0f);
                }
                else {
                    livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.put(key, result);
                }
            }
        }
        if (livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulFeels.containsKey(key)) {
            validateBasicState(key, livingEntity);
        }
    }
}
