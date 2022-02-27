package hunkarada.soulary.common.will;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;


public abstract class BasicWill {
    protected String id;
    protected ResourceLocation model;
    protected ResourceLocation texture;
    protected ResourceLocation sound;
    protected Entity caster;
    protected double willCost;
    protected double stabilityCost;
    protected double feelCost;
    protected abstract void cast();
    protected abstract void clientCast();

}
