package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.world.entities.extensions.IEntity;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import de.teamlapen.faction.common.world.entities.EntitySyncHolder;
import de.teamlapen.sync.PropertySync;
import de.teamlapen.sync.api.IAttachmentSync;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAttachments;
import de.teamlapen.vampirism.common.world.attachments.LevelDamage;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import de.teamlapen.vampirism.common.world.attachments.NearestVillage;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraDimension;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.QuarrelEntity;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.InfectionStatus;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireBat;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.core.GlobalPos;
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
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<QuarrelEntity.HurtBypassTracker>> QUARREL_HURT_BYPASS = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.QUARREL_HURT_BYPASS.getPath(), () -> AttachmentType.builder(QuarrelEntity.HurtBypassTracker::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntitySyncHolder<VampireMinionEntity, VampireMinionEntity.VampireMinionData>>> VAMPIRE_MINION_DATA = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.VAMPIRE_MINION_DATA.getPath(), () -> syncHolder(new EntitySyncHolder.Factory<>(VampireMinionEntity.class)).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntitySyncHolder<HunterMinionEntity, HunterMinionEntity.HunterMinionData>>> HUNTER_MINION_DATA = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.HUNTER_MINION_DATA.getPath(), () -> syncHolder(new EntitySyncHolder.Factory<>(HunterMinionEntity.class)).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<GlobalPos>> VELMORRA_PORTAL = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.VELMORRA_PORTAL.getPath(), () -> AttachmentType.builder(new VelmorraDimension.VelmorraPortalPos()).serialize(GlobalPos.MAP_CODEC).build());

    //Blocks Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NearestVillage>> NEAREST_VILLAGE = ATTACHMENT_TYPES.register(VampirismAttachments.Keys.NEAREST_VILLAGE.getPath(), () -> AttachmentType.builder(new NearestVillage.Factory()).build());

    static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    private static <T extends IAttachmentSync & IEntity, Z extends IAttachmentHolder> AttachmentType.Builder<T> syncAttachment(AttachmentSynchronization<T, Z> options) {
        return AttachmentType.builder(options).serialize(options).sync(options);
    }

    private static <TEntity extends EntitySyncHolder.ISyncHolder<TData>, TData extends PropertySync> AttachmentType.Builder<EntitySyncHolder<TEntity, TData>> syncHolder(EntitySyncHolder.Factory<TEntity, TData> options) {
        return AttachmentType.builder(options).sync(options);
    }

}
