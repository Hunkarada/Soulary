package hunkarada.soulary.common.events.soulary;

import hunkarada.soulary.common.soul.SoulCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import static hunkarada.soulary.common.soul.SoulCapability.Provider.SOUL_CAPABILITY;

public class StageChangedEvent extends LivingEvent {
    public StageChangedEvent(LivingEntity entity) {
        super(entity);
    }
    public SoulCapability getSoulCapability(){
        return getEntityLiving().getCapability(SOUL_CAPABILITY).orElse(new SoulCapability());
    }
}
