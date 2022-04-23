package hunkarada.soulary.common.soul;

import net.minecraft.world.entity.LivingEntity;

public class FeelsHandler {

    /*Functions for internal calculations of adaptation
        Positive means adaptation > 1, negative means adaptation < 1*/
    private static float onAdaptationPositive(float x){
        return (float) (-0.01*x+1);
    }

    private static float onAdaptationNegative(float x){
        return (float) (-0.02*x+1);
    }

}
