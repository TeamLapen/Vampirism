package de.teamlapen.vampirism.common.world;

import de.teamlapen.factions.api.event.FactionVillageEvent;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.world.ITotem;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;

public class VillageEventHandler {

    @SubscribeEvent
    public void onVillageAreaChanged(FactionVillageEvent.AreaChangedEvent event) {
        ITotem totem = event.getTotem();
        if (totem.getTileLevel() instanceof Level level) {
            LevelFog levelFog = LevelFog.get(level);

            levelFog.updateArtificialFogBoundingBox(totem.position(), IFaction.is(totem.getControllingFaction(), ModFactions.VAMPIRE) ? event.getArea() : null);
            if (totem.isRaidTriggeredByBadOmen() && IFaction.is(totem.getCapturingFaction(), ModFactions.VAMPIRE)) {
                levelFog.updateTemporaryArtificialFog(totem.position(), event.getArea());
            }
        }
    }

    @SubscribeEvent
    public void onVillageAreaChanged(FactionVillageEvent.RemovedEvent event) {
        ITotem totem = event.getTotem();
        if (totem.getTileLevel() instanceof Level level) {
            LevelFog levelFog = LevelFog.get(level);

            levelFog.updateArtificialFogBoundingBox(totem.position(), null);
        }
    }
}
