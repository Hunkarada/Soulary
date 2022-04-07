package hunkarada.soulary.common.soul;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

public class FeelsHandler {
    public static void feelsHandler(LivingEntity livingEntity, String key){
        HashMap<String, Byte> stages = livingEntity.getCapability(SoulCapability.Provider.SOUL_CAPABILITY).orElse(new SoulCapability()).soulStages;
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
        switch (stage) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> {}
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
}
