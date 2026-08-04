package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionConsumer {

    public static final DeferredRegister<FactionPlayerConsumer> CONSUMER = DeferredRegister.create(FactionRegistries.Keys.FACTION_PLAYER_CONSUMER, REFERENCE.MOD_ID);

    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> ENABLE_MINION_INCREASED_STATS = CONSUMER.register("enable_minion_increased_stats", () -> factionPlayer -> factionPlayer.getExtension(ILordPlayer.class).ifPresent(x -> x.updateMinionAttributes(true)));
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> DISABLE_MINION_INCREASED_STATS = CONSUMER.register("disable_minion_increased_stats", () -> factionPlayer -> factionPlayer.getExtension(ILordPlayer.class).ifPresent(x -> x.updateMinionAttributes(false)));

    static void register(IEventBus bus) {
        CONSUMER.register(bus);
    }
}
