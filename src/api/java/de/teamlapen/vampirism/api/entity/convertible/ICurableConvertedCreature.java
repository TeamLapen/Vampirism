package de.teamlapen.vampirism.api.entity.convertible;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface ICurableConvertedCreature<T extends CreatureEntity> extends IConvertedCreature<T> {

    DataParameter<Boolean> CONVERTING = EntityDataManager.createKey(ZombieVillagerEntity.class, DataSerializers.BOOLEAN);

    /**
     * called in {@link #interactWithCureItem(PlayerEntity, ItemStack, CreatureEntity)} override to save values to attributes
     *
     * @param conversionStarterIn uuid of the player that started the curing process
     * @param conversionTimeIn ticks the conversion should takes
     * @param entity the entity that extends this interface
     */
    default void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @Nonnull CreatureEntity entity){
        entity.getDataManager().set(CONVERTING, true);
        entity.removePotionEffect(Effects.WEAKNESS);
        entity.world.setEntityState(entity, (byte)16);
    }

    /**
     * call in {@link Entity#registerData()}
     *
     * @param entity the entity that extends this interface
     */
    default void registerConvertingData(@Nonnull CreatureEntity entity) {
        entity.getDataManager().register(CONVERTING, false);
    }

    /**
     * @param entity the entity that extends this interface
     * @return if the entity is in progress of converting
     */
    default boolean isConverting(CreatureEntity entity) {
        return entity.getDataManager().get(CONVERTING);
    }

    /**
     * call in {@link MobEntity#func_230254_b_(PlayerEntity, Hand)}
     *
     * @param player the interacting player
     * @param stack the itemstack in the players hand
     * @param entity the entity that extends this interface
     * @return the action result
     */
    default ActionResultType interactWithCureItem(PlayerEntity player, ItemStack stack, CreatureEntity entity){
        if (!entity.isPotionActive(Effects.WEAKNESS)) return ActionResultType.CONSUME;
        if (!player.abilities.isCreativeMode){
            stack.shrink(1);
        }
        if (!entity.world.isRemote){
            this.startConverting(player.getUniqueID(), entity.getRNG().nextInt(100)+100, entity);
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * call in {@link Entity#handleStatusUpdate(byte)}
     *
     * @param id the status id
     * @param entity the entity that extends this interface
     * @return if the staus update was handled
     */
    default boolean handleSound(byte  id, CreatureEntity entity) {
        if (id == 16) {
            if (!entity.isSilent()) {
                entity.world.playSound(entity.getPosX(), entity.getPosYEye(), entity.getPosZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, entity.getSoundCategory(), 1.0F + entity.getRNG().nextFloat(), entity.getRNG().nextFloat() * 0.7F + 0.3F, false);
            }
            return true;
        }
        return false;
    }

    /**
     * @see ZombieVillagerEntity#getConversionProgress()
     *
     * @param entity the entity that extends this interface
     * @return the progress
     */
    default int getConversionProgress(CreatureEntity entity) {
        int i = 1;
        if (entity.getRNG().nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            for(int k = (int)entity.getPosX() - 4; k < (int)entity.getPosX() + 4 && j < 14; ++k) {
                for(int l = (int)entity.getPosY() - 4; l < (int)entity.getPosY() + 4 && j < 14; ++l) {
                    for(int i1 = (int)entity.getPosZ() - 4; i1 < (int)entity.getPosZ() + 4 && j < 14; ++i1) {
                        Block block = entity.world.getBlockState(blockpos$mutable.setPos(k, l, i1)).getBlock();
                        if (block == Blocks.IRON_BARS || block instanceof BedBlock) {
                            if (entity.getRNG().nextFloat() < 0.3F) {
                                ++i;
                            }
                            ++j;
                        }
                    }
                }
            }
        }
        return i;
    }

    /**
     * creates the new entity <p>
     *
     * override to use already an already existing entity
     *
     * @param entity the entity that extends this interface
     * @param newType the entity type of the cured entity
     * @return the new entity
     */
    default T createCuredEntity(CreatureEntity entity, EntityType<T> newType) {
        return entity.func_233656_b_(newType, false);
    }

    /**
     * creates the cured entity and copies attributes <p>
     * adds/removes the entities to/from the world <p>
     *
     * override to copy additionally attributes
     *
     * @param world the world of the curing process
     * @param entity the entity that extends this interface
     * @param newType the entity type of the cured entity
     * @return the new cured entity
     */
    default T cureEntity(ServerWorld world, CreatureEntity entity, EntityType<T> newType) {
        T newEntity = createCuredEntity(entity, newType);
        newEntity.renderYawOffset = entity.renderYawOffset;
        newEntity.rotationYawHead = entity.rotationYawHead;
        newEntity.onInitialSpawn(world, world.getDifficultyForLocation(newEntity.getPosition()), SpawnReason.CONVERSION, null, null);
        newEntity.addPotionEffect(new EffectInstance(Effects.NAUSEA, 200, 0));
        if (!entity.isSilent()) {
            world.playEvent(null, 1027, entity.getPosition(), 0);
        }
        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(entity, newEntity);
        return newEntity;
    }
}
