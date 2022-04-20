package hunkarada.soulary.common.soul;


import hunkarada.soulary.common.soul.states.*;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

import static hunkarada.soulary.common.soul.SoulCapability.Provider.SOUL_CAPABILITY;

public class StatesHandler {
    /*This method changing current state of entity, depending on feelings of this entity.*/
    public static void stateHandler(LivingEntity livingEntity, String key){
        HashMap<String, Byte> stages = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStages;
        switch (key) {
            case "all" -> {
                joy(stages.get("joy"), livingEntity);
                sadness(stages.get("sadness"), livingEntity);
                trust(stages.get("trust"), livingEntity);
                disgust(stages.get("disgust"), livingEntity);
                fear(stages.get("fear"), livingEntity);
                anger(stages.get("anger"), livingEntity);
                surprise(stages.get("surprise"), livingEntity);
                anticipation(stages.get("anticipation"), livingEntity);
            }
            case "joy" -> joy(stages.get("joy"), livingEntity);
            case "sadness" -> sadness(stages.get("sadness"), livingEntity);
            case "trust" -> trust(stages.get("trust"), livingEntity);
            case "disgust" -> disgust(stages.get("disgust"), livingEntity);
            case "fear" -> fear(stages.get("fear"), livingEntity);
            case "anger" -> anger(stages.get("anger"), livingEntity);
            case "surprise" -> surprise(stages.get("surprise"), livingEntity);
            case "anticipation" -> anticipation(stages.get("anticipation"), livingEntity);
        }
    }
    private static void joy(byte stage, LivingEntity livingEntity){
        byte anticipationStage = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStages.get("anticipation");
        byte trustStage = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStages.get("trust");
        switch (stage) {
            case 0 -> {
            }
            case 1 -> {
                changeState("joy", stage, livingEntity);
            }
            case 2 -> {
                if (anticipationStage == stage){
                }
            }
            case 3 -> {}
        }
    }
    private static void sadness(byte stage, LivingEntity livingEntity){
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }
    }
    private static void trust(byte stage, LivingEntity livingEntity){
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }
    }
    private static void disgust(byte stage, LivingEntity livingEntity){
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }
    }
    private static void fear(byte stage, LivingEntity livingEntity){
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }
    }
    private static void anger(byte stage, LivingEntity livingEntity){
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }
    }
    private static void surprise(byte stage, LivingEntity livingEntity){
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }
    }
    private static void anticipation(byte stage, LivingEntity livingEntity){
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }
    }
    public static ISoulState generateState(String key, byte stage, LivingEntity livingEntity){
        switch (key){
            case "aggressiveness" -> {return new Aggressiveness(stage, livingEntity);}
            case "anger" -> {return new Anger(stage, livingEntity);}
            case "anticipation" -> {return new Anticipation(stage, livingEntity);}
            case "awe" -> {return new Awe(stage, livingEntity);}
            case "contempt" -> {return new Contempt(stage, livingEntity);}
            case "disapproval" -> {return new Disapproval(stage, livingEntity);}
            case "disgust" -> {return new Disgust(stage, livingEntity);}
            case "fear" -> {return new Fear(stage, livingEntity);}
            case "joy" -> {return new Joy(stage, livingEntity);}
            case "love" -> {return new Love(stage, livingEntity);}
            case "optimism" -> {return new Optimism(stage, livingEntity);}
            case "remorse" -> {return new Remorse(stage, livingEntity);}
            case "sadness" -> {return new Sadness(stage, livingEntity);}
            case "submission" -> {return new Submission(stage, livingEntity);}
            case "surprise" -> {return new Surprise(stage, livingEntity);}
            case "trust" -> {return new Trust(stage, livingEntity);}
        }
        return null;
    }

    private static void changeState(String key, byte stage, LivingEntity livingEntity){
        ISoulState newState = livingEntity.getCapability(SOUL_CAPABILITY).orElse(new SoulCapability(livingEntity)).soulStates.get(key);
        newState.setStage(stage);
        livingEntity.getCapability(SOUL_CAPABILITY).ifPresent(soulCapability -> soulCapability.soulStates.put(key, newState));
    }
}
