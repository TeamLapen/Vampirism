package de.teamlapen.factions.api;

import de.teamlapen.factions.api.entities.player.INeutralPlayer;
import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.api.util.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import static de.teamlapen.factions.api.registries.ApiRegistryProvider.retrieveAttachmentType;

@SuppressWarnings("unused")
public class FactionAttachments {
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IFactionPlayerHandler>> FACTION_PLAYER_HANDLER = retrieveAttachmentType(Keys.FACTION_PLAYER_HANDLER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<INeutralPlayer>> NEUTRAL_PLAYER = retrieveAttachmentType(Keys.NEUTRAL_PLAYER);


    public static class Keys {
        public static final ResourceLocation FACTION_PLAYER_HANDLER = ResourceLocation.fromNamespaceAndPath(REFERENCE.MOD_ID, "faction_player_handler");
        public static final ResourceLocation NEUTRAL_PLAYER = ResourceLocation.fromNamespaceAndPath(REFERENCE.MOD_ID, "neutral_player");
        public static final ResourceLocation DAMAGE_HANDLER = ResourceLocation.fromNamespaceAndPath(REFERENCE.MOD_ID, "damage_handler");
    }
}
