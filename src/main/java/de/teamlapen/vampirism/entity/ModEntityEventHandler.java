package de.teamlapen.vampirism.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handler for all entity related events
 */
public class ModEntityEventHandler {

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityCreature && ExtendedCreature.get((EntityCreature) event.entity) == null) {
            ExtendedCreature.register((EntityCreature) event.entity);
        }
    }
}
