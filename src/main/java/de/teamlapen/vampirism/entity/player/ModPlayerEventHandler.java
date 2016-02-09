package de.teamlapen.vampirism.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handler for player related events
 */
public class ModPlayerEventHandler {


    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            /*
            Register ExtendedProperties.
            Could be done via factions, but that might be a little bit overkill for 2-5 factions and might cause trouble with addon mods.
             */
            if (VampirePlayer.get((EntityPlayer) event.entity) == null) {
                VampirePlayer.register((EntityPlayer) event.entity);
            }
            if (HunterPlayer.get((EntityPlayer) event.entity) == null) {
                HunterPlayer.register((EntityPlayer) event.entity);
            }
        }
    }


}
