package hunkarada.soulary.common.soul.states;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

public interface ISoulState {

    default void handle(){
        uniqueEffects();
        if (FMLEnvironment.dist == Dist.CLIENT){
            render();
        }
    }

    void setStage(byte stage);

    void attributeChange();

    void uniqueEffects();

    default void register(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    void render();

}
