package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.entities.extensions.IEntity;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.lord.LordPlayer;
import de.teamlapen.faction.common.factions.neutral.NeutralPlayer;
import de.teamlapen.faction.common.factions.skills.RefinementHandler;
import de.teamlapen.faction.common.factions.tasks.TaskManager;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import de.teamlapen.faction.common.world.attachments.LevelDamage;
import de.teamlapen.sync.api.IAttachmentSync;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FactionAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, REFERENCE.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FactionPlayerHandler>> FACTION_PLAYER_HANDLER = ATTACHMENT_TYPES.register(de.teamlapen.faction.api.FactionAttachments.Keys.FACTION_PLAYER_HANDLER.getPath(), () -> syncAttachment(new FactionPlayerHandler.AttachmentOptions()).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NeutralPlayer>> NEUTRAL_PLAYER = ATTACHMENT_TYPES.register(de.teamlapen.faction.api.FactionAttachments.Keys.NEUTRAL_PLAYER.getPath(), () -> syncAttachment(new NeutralPlayer.AttachmentOptions()).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelDamage>> LEVEL_DAMAGE = ATTACHMENT_TYPES.register(de.teamlapen.faction.api.FactionAttachments.Keys.DAMAGE_HANDLER.getPath(), () -> AttachmentType.builder(new LevelDamage.Factory()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TaskManager>> TASK_MANAGER = ATTACHMENT_TYPES.register(de.teamlapen.faction.api.FactionAttachments.Keys.TASK_MANAGER.getPath(), () -> AttachmentType.builder(new TaskManager.Factory()).serialize(new TaskManager.Serializer()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RefinementHandler>> REFINEMENT_HANDLER = ATTACHMENT_TYPES.register(de.teamlapen.faction.api.FactionAttachments.Keys.REFINEMENT_HANDLER.getPath(), () -> syncAttachment(new RefinementHandler.AttachmentOptions()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LordPlayer>> LORD_PLAYER = ATTACHMENT_TYPES.register(de.teamlapen.faction.api.FactionAttachments.Keys.LORD_PLAYER.getPath(), () -> syncAttachment(new LordPlayer.AttachmentOptions()).copyOnDeath().build());

    static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    private static <T extends IAttachmentSync & IEntity, Z extends IAttachmentHolder> AttachmentType.Builder<T> syncAttachment(AttachmentSynchronization<T, Z> options) {
        return AttachmentType.builder(options).serialize(options).sync(options);
    }
}
