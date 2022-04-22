package hunkarada.soulary.common.soul;


import hunkarada.soulary.common.soul.states.*;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

import static hunkarada.soulary.common.soul.SoulCapability.Provider.SOUL_CAPABILITY;

public class StatesHandler {
    /*This method changing current state of entity, depending on feelings of this entity.*/
    public static void stateHandler(String key, LivingEntity livingEntity){
        HashMap<String, Byte> states = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates;
        switch (key) {
            case "joy" -> joy(states.get("joy"), livingEntity);
            case "sadness" -> sadness(states.get("sadness"), livingEntity);
            case "trust" -> trust(states.get("trust"), livingEntity);
            case "disgust" -> disgust(states.get("disgust"), livingEntity);
            case "fear" -> fear(states.get("fear"), livingEntity);
            case "anger" -> anger(states.get("anger"), livingEntity);
            case "surprise" -> surprise(states.get("surprise"), livingEntity);
            case "anticipation" -> anticipation(states.get("anticipation"), livingEntity);
        }
    }
    private static void joy(byte state, LivingEntity livingEntity){
        byte anticipationState = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates.get("anticipation");
        byte trustState = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates.get("trust");
    }
    private static void sadness(byte state, LivingEntity livingEntity){

    }
    private static void trust(byte state, LivingEntity livingEntity){

    }
    private static void disgust(byte state, LivingEntity livingEntity){

    }
    private static void fear(byte state, LivingEntity livingEntity){

    }
    private static void anger(byte state, LivingEntity livingEntity){

    }
    private static void surprise(byte state, LivingEntity livingEntity){

    }
    private static void anticipation(byte state, LivingEntity livingEntity){

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

}
