package hunkarada.soulary.common.soul.states;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

public interface ISoulState {

    default void handle(byte stage, LivingEntity livingEntity){
        attributeChange(stage, livingEntity);
        uniqueEffects(stage, livingEntity);
        if (FMLEnvironment.dist == Dist.CLIENT){
            render(stage, livingEntity);
        }
    }

    void setStage(byte stage);

    void uniqueEffects(byte stage, LivingEntity livingEntity);

    void attributeChange(byte stage, LivingEntity livingEntity);

    default void register(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    void render(byte stage, LivingEntity livingEntity);

}
