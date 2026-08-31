package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.api.world.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class VampirismAttachments {

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IExtendedCreatureVampirism>> EXTENDED_CREATURE = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.EXTENDED_CREATURE);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IVampirePlayer>> VAMPIRE_PLAYER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.VAMPIRE_PLAYER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IHunterPlayer>> HUNTER_PLAYER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.HUNTER_PLAYER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IGarlicChunkHandler>> GARLIC_HANDLER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.GARLIC_HANDLER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IFogHandler>> FOG_HANDLER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.FOG_HANDLER);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IFogHandler>> DAMAGE_HANDLER = DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Keys.DAMAGE_HANDLER);

    public static class Keys {
        public static final Identifier EXTENDED_CREATURE = VIdentifier.mod("extended_creature");
        public static final Identifier VAMPIRE_PLAYER = VIdentifier.mod("vampire_player");
        public static final Identifier HUNTER_PLAYER = VIdentifier.mod("hunter_player");
        public static final Identifier GARLIC_HANDLER = VIdentifier.mod("garlic_handler");
        public static final Identifier FOG_HANDLER = VIdentifier.mod("fog_handler");
        public static final Identifier DAMAGE_HANDLER = VIdentifier.mod("damage_handler");
        public static final Identifier VAMPIRE_BAT = VIdentifier.mod("vampire_bat");
        public static final Identifier DRACULA_PLAYER = VIdentifier.mod("dracula_player");
        public static final Identifier MARSHALL_PLAYER = VIdentifier.mod("marshall_player");
        public static final Identifier INFECTION_STATUS = VIdentifier.mod("infection_status");
        public static final Identifier QUARREL_HURT_BYPASS = VIdentifier.mod("quarrel_hurt_bypass");
        public static final Identifier NEAREST_VILLAGE = VIdentifier.mod("nearest_village");
        public static final Identifier VAMPIRE_MINION_DATA = VIdentifier.mod("vampire_minion_data");
        public static final Identifier HUNTER_MINION_DATA = VIdentifier.mod("hunter_minion_data");
        public static final Identifier VELMORRA_PORTAL = VIdentifier.mod("velmorra_portal");
        public static final Identifier DRACULA_FIGHT_DATA = VIdentifier.mod("dracula_fight_data");
        public static final Identifier MARKER = VIdentifier.mod("marker");
        public static final Identifier HERITAGE = VIdentifier.mod("heritage");
    }
}
