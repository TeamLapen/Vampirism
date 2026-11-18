package de.teamlapen.library.common.world.entities;

import de.teamlapen.library.api.difficulty.Difficulty;
import de.teamlapen.library.api.difficulty.IAdjustableLevel;
import de.teamlapen.library.common.difficulty.DifficultyCalculator;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class LibraryEntityEventHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof IAdjustableLevel entity) {
                int l2 = entity.getEntityLevel();
                if (entity.getEntityLevel() == -1) {
                    Difficulty d = DifficultyCalculator.findDifficultyForPos(event.getLevel(), event.getEntity().blockPosition(), 30);
                    int l = entity.suggestEntityLevel(d);
                    if (l > entity.getMaxEntityLevel()) {
                        l = entity.getMaxEntityLevel();
                    } else if (l < 0) {
                        event.setCanceled(true);
                    }
                    entity.setEntityLevel(l);
                    if (entity instanceof PathfinderMob) {
                        ((PathfinderMob) entity).setHealth(((PathfinderMob) entity).getMaxHealth());
                    }
                }
            }
        }
    }
}
