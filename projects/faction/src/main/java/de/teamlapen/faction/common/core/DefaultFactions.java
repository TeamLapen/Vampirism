package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.Factions;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import de.teamlapen.faction.api.registries.factions.DeferredFactionRegister;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.player.INeutralPlayer;
import de.teamlapen.faction.common.factions.PlayableFactionBuilder;
import net.minecraft.util.ARGB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class DefaultFactions {

    private static final DeferredFactionRegister FACTIONS = DeferredFactionRegister.create(REFERENCE.MOD_ID);


    public static final DeferredFaction<INeutralPlayer, IPlayableFaction<INeutralPlayer>> NEUTRAL = FACTIONS.registerFaction(Factions.Keys.NEUTRAL.getPath(), () -> new PlayableFactionBuilder<>(SafeCast.<Supplier<AttachmentType<INeutralPlayer>>>cast( FactionAttachments.NEUTRAL_PLAYER))
            .color(ARGB.white(1))
            .build());

    static void register(IEventBus bus) {
        FACTIONS.register(bus);
    }
}