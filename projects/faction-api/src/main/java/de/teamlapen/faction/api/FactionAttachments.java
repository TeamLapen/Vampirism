package de.teamlapen.faction.api;

import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.INeutralPlayer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import static de.teamlapen.faction.api.registries.ApiRegistryProvider.retrieveAttachmentType;

@SuppressWarnings("unused")
public class FactionAttachments {
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IFactionPlayerHandler>> FACTION_PLAYER_HANDLER = retrieveAttachmentType(Keys.FACTION_PLAYER_HANDLER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<INeutralPlayer>> NEUTRAL_PLAYER = retrieveAttachmentType(Keys.NEUTRAL_PLAYER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ITaskManager>> TASK_MANAGER = retrieveAttachmentType(Keys.TASK_MANAGER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IRefinementHandler>> REFINEMENT_HANDLER = retrieveAttachmentType(Keys.REFINEMENT_HANDLER);


    public static class Keys {
        public static final Identifier FACTION_PLAYER_HANDLER = FIdentifier.mod("faction_player_handler");
        public static final Identifier NEUTRAL_PLAYER = FIdentifier.mod("neutral_player");
        public static final Identifier DAMAGE_HANDLER = FIdentifier.mod("damage_handler");
        public static final Identifier TASK_MANAGER = FIdentifier.mod("task_manager");
        public static final Identifier REFINEMENT_HANDLER = FIdentifier.mod("refinement_handler");
    }
}
