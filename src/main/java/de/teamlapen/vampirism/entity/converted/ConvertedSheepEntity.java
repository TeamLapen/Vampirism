package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * {@link IConvertedCreature} for sheep
 * Allows converted sheep to be sheared
 */
public class ConvertedSheepEntity extends ConvertedCreatureEntity<SheepEntity> implements net.minecraftforge.common.IForgeShearable {
    private final static DataParameter<Byte> COAT = EntityDataManager.defineId(ConvertedSheepEntity.class, DataSerializers.BYTE);

    private Boolean lastSheared = null;

    public ConvertedSheepEntity(EntityType<? extends ConvertedSheepEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        boolean t = getSheared();
        if (!nil() && (lastSheared == null || lastSheared != t)) {
            lastSheared = t;
            getOldCreature().setSheared(lastSheared);

        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Sheared", this.getSheared());
    }

    public DyeColor getFleeceColor() {
        return nil() ? DyeColor.WHITE : this.getOldCreature().getColor();
    }

    public boolean getSheared() {
        return (this.entityData.get(COAT) & 16) != 0;
    }

    public void setSheared(boolean sheared) {
        byte b0 = this.entityData.get(COAT);

        if (sheared) {
            this.entityData.set(COAT, (byte) (b0 | 16));
        } else {
            this.entityData.set(COAT, (byte) (b0 & -17));
        }
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, World world, BlockPos pos) {
        return !getSheared() && !isBaby();
    }

    @Override
    public List<ItemStack> onSheared(@Nullable PlayerEntity player, ItemStack item, World world, BlockPos pos, int fortune) {
        java.util.List<ItemStack> ret = new java.util.ArrayList<>();
        if (!world.isClientSide()) {
            this.setSheared(true);
            int i = 1 + this.random.nextInt(3);

            for (int j = 0; j < i; ++j)
                ret.add(new ItemStack(SheepEntity.ITEM_BY_DYE.get(this.getFleeceColor())));

            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
        }
        return ret;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(COAT, (byte) 0);
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<SheepEntity> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public ConvertedCreatureEntity<SheepEntity> createFrom(SheepEntity entity) {
            return Helper.createEntity(ModEntities.CONVERTED_SHEEP.get(), entity.getCommandSenderWorld()).map(creature -> {
                this.copyImportantStuff(creature, entity);
                creature.setSheared(entity.isSheared());
                return creature;
            }).orElse(null);
        }
    }

}
