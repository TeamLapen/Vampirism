package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && (event.entity instanceof IAdjustableLevel)) {
            IAdjustableLevel entity = (IAdjustableLevel) event.entity;
            if (entity.getLevel() == -1) {
                Difficulty d = DifficultyCalculator.findDifficultyForPos(event.world, event.entity.getPosition(), 30);
                int l = entity.suggestLevel(d);
                if (l > entity.getMaxLevel()) {
                    l = entity.getMaxLevel();
                } else if (l < 0) {
                    event.setCanceled(true);
                }
                entity.setLevel(l);
            }
        }
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityCreature) {
            ExtendedCreature.get((EntityCreature) event.entity).onUpdate();
        }
    }
}
