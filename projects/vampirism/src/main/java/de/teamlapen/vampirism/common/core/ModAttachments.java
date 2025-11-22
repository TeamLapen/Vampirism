package de.teamlapen.vampirism.common.core;

import de.teamlapen.factions.api.extensions.IEntity;
import de.teamlapen.factions.common.util.AttachmentSynchronization;
import de.teamlapen.sync.api.IAttachment;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAttachments;
import de.teamlapen.vampirism.common.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.entity.player.vampire.InfectionStatus;
import de.teamlapen.vampirism.common.entity.player.vampire.VampireBat;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.attachments.LevelDamage;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.world.entity.ambient.Bat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, REFERENCE.MODID);

    // Level Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelFog>> LEVEL_FOG = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.FOG_HANDLER.getPath(), () -> AttachmentType.builder(new LevelFog.Factory()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelGarlic>> LEVEL_GARLIC = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.GARLIC_HANDLER.getPath(), () -> AttachmentType.builder(new LevelGarlic.Factory()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelDamage>> LEVEL_DAMAGE = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.DAMAGE_HANDLER.getPath(), () -> AttachmentType.builder(new LevelDamage.Factory()).build());

    // Entity Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ExtendedCreature>> EXTENDED_CREATURE = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.EXTENDED_CREATURE.getPath(), () -> syncAttachment(new ExtendedCreature.AttachmentOptions()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HunterPlayer>> HUNTER_PLAYER = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.HUNTER_PLAYER.getPath(), () -> syncAttachment(new HunterPlayer.AttachmentOptions()).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<VampirePlayer>> VAMPIRE_PLAYER = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.VAMPIRE_PLAYER.getPath(), () -> syncAttachment(new VampirePlayer.AttachmentOptions()).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Bat>> VAMPIRE_BAT = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.VAMPIRE_BAT.getPath(), () -> AttachmentType.builder(new VampireBat.Factory()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<InfectionStatus>> INFECTION_STATUS = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.INFECTION_STATUS.getPath(), () -> AttachmentType.builder(new InfectionStatus.Factory()).build());

    static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    private static <T extends IAttachment & IEntity, Z extends IAttachmentHolder> AttachmentType.Builder<T> syncAttachment(AttachmentSynchronization<T, Z> options) {
        return AttachmentType.builder(options).serialize(options).sync(options);
    }
}
