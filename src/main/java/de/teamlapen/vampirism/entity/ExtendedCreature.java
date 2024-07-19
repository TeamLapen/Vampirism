package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.storage.Attachment;
import de.teamlapen.lib.lib.storage.UpdateParams;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.world.ModDamageSources;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Extended entity property which every {@link PathfinderMob} has
 */
public class ExtendedCreature extends Attachment implements IExtendedCreatureVampirism {
    public static final ResourceLocation SERIALIZER_ID = VResourceLocation.mod("extended_creature");

    private final static String KEY_BLOOD = "bloodLevel";
    private final static String KEY_MAX_BLOOD = "max_blood";
    private final static String POISONOUS_BLOOD = "poisonousBlood";

    public static @NotNull Optional<ExtendedCreature> getSafe(@NotNull Entity mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            return Optional.of(pathfinderMob.getData(ModAttachments.EXTENDED_CREATURE));
        }
        return Optional.empty();
    }

    private final PathfinderMob entity;
    private final boolean canBecomeVampire;
    private boolean poisonousBlood;
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

    public ExtendedCreature(PathfinderMob entity) {
        this.entity = entity;
        // We need to call getEntry and not getOrCreateEntry because the values can not be calculated until after the entity constructor has finished
        IEntityBlood entry = VampirismAPI.entityRegistry().getEntry(entity);
        if (entry != null && entry.blood() > 0) {
            maxBlood = entry.blood();
            canBecomeVampire = VampirismAPI.entityRegistry().getConverterEntry(entity) != null;
        } else {
            if (entry == null) {
                markForBloodCalculation = true;
            }
            maxBlood = -1;
            canBecomeVampire = false;
        }
        blood = maxBlood;
        poisonousBlood = false;
    }

    @Override
    public @NotNull Entity asEntity() {
        return this.entity;
    }

    @Override
    public boolean canBeBitten(IVampire biter) {
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
    public @NotNull ResourceLocation getAttachedKey() {
        return SERIALIZER_ID;
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

    @Override
    public boolean hasPoisonousBlood() {
        return poisonousBlood;
    }

    @Override
    public void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains(KEY_BLOOD)) {
            setBlood(nbt.getInt(KEY_BLOOD));
        }
        if (nbt.contains(KEY_MAX_BLOOD)) {
            setBlood(nbt.getInt(KEY_MAX_BLOOD));
        }
        if (nbt.contains(POISONOUS_BLOOD)) {
            setPoisonousBlood(nbt.getBoolean(POISONOUS_BLOOD));
        }
    }

    @Nullable
    @Override
    public IConvertedCreature<?> makeVampire() {
        if (canBecomeVampire()) {
            blood = -1;
            IConvertedCreature<?> c = VampirismAPI.entityRegistry().convert(entity);
            if (c != null) {
                UtilLib.replaceEntity(entity, (PathfinderMob) c);
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
            SanguinareEffect.addRandom(entity, false);
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
            DamageHandler.hurtModded(entity, ModDamageSources::noBlood, 1000);
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
    public void setPoisonousBlood(boolean poisonous) {
        if (poisonous == !poisonousBlood) {
            poisonousBlood = poisonous;
            sync();
        }
    }

    public int getRemainingBarkTicks() {
        return remainingBarkTicks;
    }

    public void increaseRemainingBarkTicks(int additionalBarkTicks) {
        this.remainingBarkTicks += additionalBarkTicks;
    }

    @Override
    public void tick() {
        if (!entity.getCommandSenderWorld().isClientSide) {
            /*
             * Make sure all entities with no blood die
             * check for sanguinare as the entity might be converting instead of dying
             */
            if (blood == 0 && entity.tickCount % 20 == 10 && entity.getEffect(ModEffects.SANGUINARE) == null) {
                DamageHandler.hurtModded(entity, ModDamageSources::noBlood, 1000);
            }
            if (blood > 0 && blood < getMaxBlood() && entity.tickCount % 40 == 8) {
                if (blood < getMaxBlood() * 0.5) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 41));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 41, 2));
                }
                if (entity.getRandom().nextInt(BalanceMobProps.mobProps.BLOOD_REGEN_CHANCE) == 0 && LevelAttributeModifier.calculateModifierValue(blood, getMaxBlood(), 1, 0.8) < entity.getRandom().nextDouble()) {
                    setBlood(getBlood() + 1);
                    sync();
                }
            }
        }
        if (markForBloodCalculation) {
            IEntityBlood entry = VampirismAPI.entityRegistry().getOrCreateEntry(entity);
            setMaxBlood(entry.blood());
            markForBloodCalculation = false;
        }
        if (this.remainingBarkTicks > 0) {
            --this.remainingBarkTicks;
        }
    }

    @Override
    public @NotNull String toString() {
        return super.toString() + " for entity (" + entity.toString() + ") [B" + blood + ",MB" + maxBlood + ",CV" + canBecomeVampire + "]";
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        var nbt = new CompoundTag();
        nbt.putInt(KEY_BLOOD, blood);
        nbt.putInt(KEY_MAX_BLOOD, maxBlood);
        nbt.putBoolean(POISONOUS_BLOOD, poisonousBlood);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compound) {
        if (compound.contains(KEY_MAX_BLOOD)) {
            setMaxBlood(compound.getInt(KEY_MAX_BLOOD));
        }
        if (compound.contains(KEY_BLOOD)) {
            setBlood(compound.getInt(KEY_BLOOD));
        }
        if (compound.contains(POISONOUS_BLOOD)) {
            setPoisonousBlood(compound.getBoolean(POISONOUS_BLOOD));
        }
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider, UpdateParams params) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(KEY_BLOOD, getBlood());
        nbt.putInt(KEY_MAX_BLOOD, getBlood());
        nbt.putBoolean(POISONOUS_BLOOD, hasPoisonousBlood());
        return nbt;
    }

    @Override
    public void sync() {
        sync(UpdateParams.forAllPlayer());
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, ExtendedCreature> {

        @Override
        public @NotNull ExtendedCreature read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            if (holder instanceof PathfinderMob mob) {
                var creature = new ExtendedCreature(mob);
                creature.deserializeNBT(provider, tag);
                return creature;
            }
            throw new IllegalArgumentException("Expected PathfinderMob, got " + holder.getClass().getSimpleName());
        }

        @Override
        public CompoundTag write(ExtendedCreature attachment, HolderLookup.@NotNull Provider provider) {
            return attachment.serializeNBT(provider);
        }
    }

    public static class Factory implements Function<IAttachmentHolder, ExtendedCreature> {

        @Override
        public ExtendedCreature apply(IAttachmentHolder holder) {
            if (holder instanceof PathfinderMob mob) {
                return new ExtendedCreature(mob);
            }
            throw new IllegalArgumentException("Cannot create extended creature handler attachment for holder " + holder.getClass() + ". Expected PathfinderMob");
        }
    }
}
