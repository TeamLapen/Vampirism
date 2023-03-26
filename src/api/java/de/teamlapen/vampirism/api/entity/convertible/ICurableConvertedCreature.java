package de.teamlapen.vampirism.api.entity.convertible;

import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Enables the option to cure a {@link IConvertedCreature}
 * <p>
 * to implement this feature there are some requirements:<p>
 * - override {@link #startConverting} to save the conversion started and conversion time <p>
 * - create a {@link EntityDataAccessor<Boolean> } the is returned by {@link #getConvertingDataParam()}<p>
 * - call {@link #registerConvertingData(PathfinderMob)} in {@link net.minecraft.world.entity.Mob#registerData()}<p>
 * - call {@link #interactWithCureItem(Player, ItemStack, PathfinderMob)} in {@link net.minecraft.world.entity.Mob#mobInteract(Player, InteractionHand)} if the cure item is in the players hand<p>
 * - call {@link #handleSound(byte, PathfinderMob)} in {@link net.minecraft.world.entity.Mob#handleEntityEvent(byte)}<p>
 * - check in {@link net.minecraft.world.entity.Mob#aiStep()} if the conversion timer has ended. If so call {@link #cureEntity(ServerLevel, PathfinderMob, EntityType)} <p>
 */
@SuppressWarnings("JavadocReference")
public interface ICurableConvertedCreature<T extends PathfinderMob> extends IConvertedCreature<T> {

    byte CURE_EVENT_ID = (byte) 40;

    /**
     * creates the new entity <p>
     * <p>
     * override to use already an already existing entity
     *
     * @param entity  the entity that extends this interface
     * @param newType the entity type of the cured entity
     * @return the new entity
     */
    default T createCuredEntity(@NotNull PathfinderMob entity, @NotNull EntityType<T> newType) {
        T newEntity = newType.create(entity.level);
        assert newEntity != null;
        newEntity.load(entity.saveWithoutId(new CompoundTag()));
        newEntity.yBodyRot = entity.yBodyRot;
        newEntity.yHeadRot = entity.yHeadRot;
        newEntity.setUUID(UUID.randomUUID());
        return newEntity;
    }

    /**
     * creates the cured entity and copies attributes <p>
     * adds/removes the entities to/from the world <p>
     * <p>
     * override to copy additionally attributes
     *
     * @param world   the world of the curing process
     * @param entity  the entity that extends this interface
     * @param newType the entity type of the cured entity
     * @return the new cured entity
     */
    default T cureEntity(@NotNull ServerLevel world, @NotNull PathfinderMob entity, @NotNull EntityType<T> newType) {
        T newEntity = createCuredEntity(entity, newType);
        entity.remove(Entity.RemovalReason.DISCARDED);
        entity.level.addFreshEntity(newEntity);
        newEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!entity.isSilent()) {
            world.levelEvent(null, 1027, entity.blockPosition(), 0);
        }
        VampirismAPI.getExtendedCreatureVampirism(newEntity).ifPresent(creature -> creature.setBlood(1));
        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(entity, newEntity);
        return newEntity;
    }

    EntityDataAccessor<Boolean> getConvertingDataParam();

    /**
     * call in {@link Entity#handleEntityEvent(byte)}
     *
     * @param id     the status id
     * @param entity the entity that extends this interface
     * @return if the staus update was handled
     */
    default boolean handleSound(byte id, @NotNull PathfinderMob entity) {
        if (id == CURE_EVENT_ID) {
            if (!entity.isSilent()) {
                entity.level.playLocalSound(entity.getX(), entity.getEyeY(), entity.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, entity.getSoundSource(), 1.0F + entity.getRandom().nextFloat(), entity.getRandom().nextFloat() * 0.7F + 0.3F, false);
            }
            return true;
        }
        return false;
    }

    /**
     * call in {@link net.minecraft.world.entity.Mob#mobInteract(Player, InteractionHand)}
     *
     * @param player the interacting player
     * @param stack  the itemstack in the players hand
     * @param entity the entity that extends this interface
     * @return the action result
     */
    default @NotNull InteractionResult interactWithCureItem(@NotNull Player player, @NotNull ItemStack stack, @NotNull PathfinderMob entity) {
        if(isConverting(entity)) return InteractionResult.CONSUME;
        if (!entity.hasEffect(MobEffects.WEAKNESS)) return InteractionResult.CONSUME;
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (!entity.level.isClientSide) {
            this.startConverting(player.getUUID(), entity.getRandom().nextInt(2400) + 2400, entity);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * @param entity the entity that extends this interface
     * @return if the entity is in progress of converting
     */
    default boolean isConverting(@NotNull PathfinderMob entity) {
        return entity.getEntityData().get(this.getConvertingDataParam());
    }

    /**
     * call in {@link Entity#defineSynchedData()}
     *
     * @param entity the entity that extends this interface
     */
    default void registerConvertingData(@NotNull PathfinderMob entity) {
        entity.getEntityData().define(this.getConvertingDataParam(), false);
    }

    /**
     * called in {@link #interactWithCureItem(Player, ItemStack, PathfinderMob)} override to save values to attributes
     *
     * @param conversionStarterIn uuid of the player that started the curing process
     * @param conversionTimeIn    ticks the conversion should take
     * @param entity              the entity that extends this interface
     */
    default void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @NotNull PathfinderMob entity) {
        entity.getEntityData().set(this.getConvertingDataParam(), true);
        entity.removeEffect(MobEffects.WEAKNESS);
        entity.level.broadcastEntityEvent(entity, (CURE_EVENT_ID));
    }
}
