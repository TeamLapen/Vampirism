package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class VampirismAttachments {

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IExtendedCreatureVampirism>> EXTENDED_CREATURE = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.EXTENDED_CREATURE);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IFactionPlayerHandler>> FACTION_PLAYER_HANDLER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.FACTION_PLAYER_HANDLER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IVampirePlayer>> VAMPIRE_PLAYER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.VAMPIRE_PLAYER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IHunterPlayer>> HUNTER_PLAYER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.HUNTER_PLAYER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IGarlicChunkHandler>> GARLIC_HANDLER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.GARLIC_HANDLER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IFogHandler>> FOG_HANDLER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.FOG_HANDLER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IFogHandler>> DAMAGE_HANDLER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.DAMAGE_HANDLER);

    public static class Keys {
        public static final ResourceLocation EXTENDED_CREATURE = new ResourceLocation(VReference.MODID, "extended_creature");
        public static final ResourceLocation FACTION_PLAYER_HANDLER = new ResourceLocation(VReference.MODID, "faction_player_handler");
        public static final ResourceLocation VAMPIRE_PLAYER = new ResourceLocation(VReference.MODID, "vampire_player");
        public static final ResourceLocation HUNTER_PLAYER = new ResourceLocation(VReference.MODID, "hunter_player");
        public static final ResourceLocation GARLIC_HANDLER = new ResourceLocation(VReference.MODID, "garlic_handler");
        public static final ResourceLocation FOG_HANDLER = new ResourceLocation(VReference.MODID, "fog_handler");
        public static final ResourceLocation DAMAGE_HANDLER = new ResourceLocation(VReference.MODID, "damage_handler");
        public static final ResourceLocation VAMPIRE_BAT = new ResourceLocation(VReference.MODID, "vampire_bat");
    }
}
