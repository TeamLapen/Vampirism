package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * {@link IConvertedCreature} for sheep
 * Allows converted sheep to be sheared
 */
public class ConvertedSheepEntity extends ConvertedCreatureEntity<Sheep> implements net.minecraftforge.common.IForgeShearable {
    private final static EntityDataAccessor<Byte> COAT = SynchedEntityData.defineId(ConvertedSheepEntity.class, EntityDataSerializers.BYTE);

    private Boolean lastSheared = null;

    public ConvertedSheepEntity(EntityType<? extends ConvertedSheepEntity> type, Level world) {
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
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
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
    public boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos pos) {
        return !getSheared() && !isBaby();
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
        java.util.List<ItemStack> ret = new java.util.ArrayList<>();
        if (!world.isClientSide()) {
            this.setSheared(true);
            int i = 1 + this.random.nextInt(3);

            for (int j = 0; j < i; ++j)
                ret.add(new ItemStack(Sheep.ITEM_BY_DYE.get(this.getFleeceColor())));

            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
        }
        return ret;
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(COAT, (byte) 0);
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<Sheep> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public ConvertedCreatureEntity<Sheep> createFrom(Sheep entity) {
            return Helper.createEntity(ModEntities.converted_sheep.get(), entity.getCommandSenderWorld()).map(creature -> {
                this.copyImportantStuff(creature, entity);
                creature.setSheared(entity.isSheared());
                return creature;
            }).orElse(null);
        }
    }

}
