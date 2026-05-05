package de.teamlapen.vampirism.common.world;

import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaFightData;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;


public class LevelEventHandler {


    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event)  {
        if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.dimensionTypeRegistration().is(ModDimensions.VELMORRA_DIMENSION_TYPE)) {
            DraculaFightData.get(serverLevel).tick();
        }
    }
}
