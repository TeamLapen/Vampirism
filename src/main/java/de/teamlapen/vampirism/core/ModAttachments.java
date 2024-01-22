package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAttachments;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampireBat;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.world.LevelDamage;
import de.teamlapen.vampirism.world.fog.FogLevel;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.world.entity.ambient.Bat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, REFERENCE.MODID);

    // Level Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FogLevel>> LEVEL_FOG = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.FOG_HANDLER.getPath(), () -> AttachmentType.builder(new FogLevel.Factory()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<GarlicLevel>> LEVEL_GARLIC = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.GARLIC_HANDLER.getPath(), () -> AttachmentType.builder(new GarlicLevel.Factory()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelDamage>> LEVEL_DAMAGE = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.DAMAGE_HANDLER.getPath(), () -> AttachmentType.builder(new LevelDamage.Factory()).build());

    // Entity Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ExtendedCreature>> EXTENDED_CREATURE = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.EXTENDED_CREATURE.getPath(), () -> AttachmentType.builder(new ExtendedCreature.Factory()).serialize(new ExtendedCreature.Serializer()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FactionPlayerHandler>> FACTION_PLAYER_HANDLER = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.FACTION_PLAYER_HANDLER.getPath(), () -> AttachmentType.builder(new FactionPlayerHandler.Factory()).serialize(new FactionPlayerHandler.Serializer()).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HunterPlayer>> HUNTER_PLAYER = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.HUNTER_PLAYER.getPath(), () -> AttachmentType.builder(new HunterPlayer.Factory()).serialize(new HunterPlayer.Serializer()).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<VampirePlayer>> VAMPIRE_PLAYER = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.VAMPIRE_PLAYER.getPath(), () -> AttachmentType.builder(new VampirePlayer.Factory()).serialize(new VampirePlayer.Serializer()).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Bat>> VAMPIRE_BAT = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.VAMPIRE_BAT.getPath(), () -> AttachmentType.builder(new VampireBat.Factory()).build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
