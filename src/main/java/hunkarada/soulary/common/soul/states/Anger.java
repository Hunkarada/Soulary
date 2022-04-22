package hunkarada.soulary.common.soul.states;

import net.minecraft.world.entity.LivingEntity;

public class Anger implements ISoulState {
    public byte stage;
    private final LivingEntity livingEntity;

    public Anger(byte stage, LivingEntity livingEntity) {
        this.stage = stage;
        this.livingEntity = livingEntity;
    }

    @Override
    public void setStage(byte stage) {
        this.stage = stage;
    }

    @Override
    public void uniqueEffects() {

    }

    @Override
    public void attributeChange() {

    }

    @Override
    public void render() {

    }

}
