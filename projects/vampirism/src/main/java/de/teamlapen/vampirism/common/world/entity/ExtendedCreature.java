package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.factions.common.util.AttachmentSynchronization;
import de.teamlapen.factions.common.util.SpawnUtil;
import de.teamlapen.sync.AttachmentSync;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.api.world.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.world.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.BloodResourceHandler;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.effects.SanguinareMobEffect;
import de.teamlapen.vampirism.common.world.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Extended entity property which every {@link PathfinderMob} has
 */
public class ExtendedCreature extends AttachmentSync implements IExtendedCreatureVampirism, BloodResourceHandler {
    public static final ResourceLocation SERIALIZER_ID = VResourceLocation.mod("extended_creature");

    public static final int POISONOUS_BLOOD_DOSE_DURATION = 72000; // 3 in-game days

    private final static String KEY_BLOOD = "bloodLevel";
    private final static String KEY_MAX_BLOOD = "max_blood";
    private final static String KEY_POISONOUS_BLOOD = "poisonousBlood";

    public static @NotNull Optional<ExtendedCreature> getSafe(@NotNull Entity mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            return Optional.of(pathfinderMob.getData(ModAttachments.EXTENDED_CREATURE));
        }
        return Optional.empty();
    }

    @Override
    public AttachmentType<?> getType() {
        return ModAttachments.EXTENDED_CREATURE.get();
    }

    private final PathfinderMob entity;
    private final boolean canBecomeVampire;
    private int poisonousBlood;
    /**
     * If the blood value of these creatures should be calculated
     */
    private boolean markForBloodCalculation = false;
    private int maxBlood;
    /**
     * Stores the current blood value.
     * If this is -1, this entity never had any blood and this value cannot be changed
     */
    private int blood;
    private int remainingBarkTicks;
    private final BloodJournal journal = new BloodJournal();


    public ExtendedCreature(PathfinderMob entity) {
        this.entity = entity;
        // We need to call getEntry and not getOrCreateEntry because the values can not be calculated until after the entity constructor has finished
        IEntityBlood entry = VampirismApi.services().entityRegistry().getEntry(entity);
        if (entry != null && entry.blood() > 0) {
            maxBlood = entry.blood();
            canBecomeVampire = VampirismApi.services().entityRegistry().getConverterEntry(entity) != null;
        } else {
            if (entry == null) {
                markForBloodCalculation = true;
            }
            maxBlood = -1;
            canBecomeVampire = false;
        }
        blood = maxBlood;
        poisonousBlood = 0;
    }

    @Override
    public @NotNull Entity asEntity() {
        return this.entity;
    }

    @Override
    public boolean canBeBitten(@Nullable IVampire biter) {
        return getBlood() > 0;
    }

    @Override
    public boolean canBecomeVampire() {
        return canBecomeVampire;
    }

    @Override
    public int getBlood() {
        return blood;
    }

    @Override
    public void setBlood(int blood) {
        if (blood >= 0 && blood <= getMaxBlood()) {
            if (getBlood() != -1) {
                this.blood = blood;
            }
        }
    }

    @Override
    public float getBloodLevelRelative() {
        return getBlood() / (float) getMaxBlood();
    }

    @Override
    public float getBloodSaturation() {
        return 1.0F;
    }

    @Override
    public PathfinderMob getEntity() {
        return entity;
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    /**
     * Set's maximum blood and current blood
     */
    private void setMaxBlood(int blood) {
        if (this.maxBlood <= 0) {
            blood = -1;
        }
        this.maxBlood = blood;
        this.blood = blood;
    }

    @Nullable
    @Override
    public IConvertedCreature<?> makeVampire() {
        if (canBecomeVampire()) {
            blood = -1;
            IConvertedCreature<?> c = VampirismApi.services().entityRegistry().convert(entity);
            if (c != null) {
                SpawnUtil.replaceEntity(entity, (PathfinderMob) c);
            }
            return c;
        }
        return null;
    }

    @Override
    public boolean canBeInfected(IVampire vampire) {
        return canBecomeVampire && !hasPoisonousBlood() && !entity.hasEffect(ModEffects.SANGUINARE);
    }

    @Override
    public boolean tryInfect(IVampire vampire) {
        if (canBeInfected(vampire)) {
            SanguinareMobEffect.addRandom(entity, false);
            return true;
        }
        return false;
    }

    @Override
    public int onBite(IVampire biter) {
        if (getBlood() <= 0) return 0;
        int amt = Math.max(1, (getMaxBlood() / (biter instanceof VampirePlayer ? 6 : 2)));
        if (amt >= blood) {
            if (blood > 1 && biter.isAdvancedBiter()) {
                amt = blood - 1;
            } else {
                amt = blood;
            }
        }
        blood -= amt;
        if (blood == 0) {
            DamageHandler.hurtModded(((ServerLevel)entity.level()), entity, ModDamageSources::noBlood, 1000);
        }

        this.sync();
        entity.setLastHurtByMob(biter.asEntity());

        // If entity is a child only give 1/3 blood
        if (entity instanceof AgeableMob) {
            if (((AgeableMob) entity).getAge() < 0) {
                amt = Math.round((float) amt / 3f);
            }
        }
        //If advanced biter, sometimes return twice the blood amount
        if (biter.isAdvancedBiter()) {
            if (entity.getRandom().nextInt(4) == 0) {
                amt = 2 * amt;
            }
        }
        if (this.entity instanceof Villager villager) {
            ((ServerLevel) villager.level()).onReputationEvent(ReputationEventType.VILLAGER_HURT, biter.asEntity(), villager);
        }

        return amt;
    }

    @Override
    public int onSyringeUse(int amount) {
        int available = getBlood();

        boolean isChild = entity instanceof AgeableMob ageableMob && ageableMob.getAge() < 0;
        if (isChild) available /= 3;

        // Must have strictly more than maxAmount blood to allow draining
        if (available <= amount) {
            return 0;
        }

        blood -= amount / (isChild ? 3 : 1);
        this.sync();

        return amount;
    }

    @Override
    public boolean hasPoisonousBlood() {
        return poisonousBlood > 0;
    }

    @Override
    public int getPoisonousBloodDuration() {
        return poisonousBlood;
    }

    @Override
    public void setPoisonousBlood(int poisonous) {
        if (Helper.isVampire(entity)) return;

        poisonousBlood = poisonous;
        this.sync();
    }

    public int getRemainingBarkTicks() {
        return remainingBarkTicks;
    }

    public void increaseRemainingBarkTicks(int additionalBarkTicks) {
        this.remainingBarkTicks += additionalBarkTicks;
    }

    @Override
    public void tick() {
        if (entity.level() instanceof ServerLevel level) {
            /*
             * Make sure all entities with no blood die
             * check for sanguinare as the entity might be converting instead of dying
             */
            if (blood == 0 && entity.tickCount % 20 == 10 && entity.getEffect(ModEffects.SANGUINARE) == null) {
                DamageHandler.hurtModded(level, entity, ModDamageSources::noBlood, 1000);
            }
            if (blood > 0 && blood < getMaxBlood() && entity.tickCount % 40 == 8) {
                if (blood < getMaxBlood() * 0.5) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 41));
                    entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 41, 2));
                }
                if (entity.getRandom().nextInt(BalanceMobProps.mobProps.BLOOD_REGEN_CHANCE) == 0 && LevelAttributeModifier.calculateModifierValue(blood, getMaxBlood(), 1, 0.8) < entity.getRandom().nextDouble()) {
                    setBlood(getBlood() + 1);
                    sync();
                }
            }
            if (poisonousBlood > 0) {
                poisonousBlood--;
                if (poisonousBlood == 0) {
                    sync();
                }
            }
            if (Helper.isVampire(entity)) {
                poisonousBlood = 0;
            }
        }
        if (markForBloodCalculation) {
            IEntityBlood entry = VampirismApi.services().entityRegistry().getOrCreateEntry(entity);
            setMaxBlood(entry.blood());
            markForBloodCalculation = false;
        }
        if (this.remainingBarkTicks > 0) {
            --this.remainingBarkTicks;
        }
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(VResourceLocation.mod("blood")).simple(0, () -> this.blood, b -> this.blood = b);
        this.registerProperty(VResourceLocation.mod("max_blood")).simple(0, () -> this.maxBlood, b -> this.maxBlood = b);
        this.registerProperty(VResourceLocation.mod("poisonous_blood")).simple(0, () -> this.poisonousBlood, b -> this.poisonousBlood = b);
    }

    public static class AttachmentOptions extends AttachmentSynchronization<ExtendedCreature, PathfinderMob> {
        @Override
        protected @NotNull ExtendedCreature create(@NotNull PathfinderMob mob) {
            return new ExtendedCreature(mob);
        }

        @Override
        public ExtendedCreature apply(IAttachmentHolder holder) {
            if (holder instanceof PathfinderMob mob) {
                return create(mob);
            }
            throw new IllegalArgumentException("Cannot create attachment for holder " + holder.getClass() + ". Expected PathfinderMob");
        }
    }

    @Override
    public int getAmount() {
        return this.blood;
    }

    @Override
    public int addBlood(int amount) {
        return 0;
    }

    @Override
    public int extractBlood(int amount) {
        this.blood -= amount;
        return amount;
    }

    @Override
    public int getCapacity() {
        return this.maxBlood;
    }

    @Override
    public SnapshotJournal<Integer> getJournal() {
        return this.journal;
    }

    public class BloodJournal extends SnapshotJournal<Integer> {
        @Override
        protected Integer createSnapshot() {
            return blood;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            blood = snapshot;
        }

        @Override
        protected void onRootCommit(Integer originalState) {
            if (blood == 0 && entity.level() instanceof ServerLevel level) {
                DamageHandler.hurtModded(level, entity, ModDamageSources::noBlood, 1000);
            }
        }
    }
}
