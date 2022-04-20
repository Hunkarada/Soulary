package hunkarada.soulary.common.soul.states;

import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class Surprise implements ISoulState {
    public byte stage;
    private final LivingEntity livingEntity;

    public Surprise(byte stage, LivingEntity livingEntity) {
        this.stage = stage;
        this.livingEntity = livingEntity;
    }

    @Override
    public void setStage(byte stage) {
        this.stage = stage;
    }

    @Override
    public void uniqueEffects(byte stage, LivingEntity livingEntity) {

    }

    @Override
    public void attributeChange(byte stage, LivingEntity livingEntity) {

    }

    @Override
    public void render(byte stage, LivingEntity livingEntity) {

    }
}
