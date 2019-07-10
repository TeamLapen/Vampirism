package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * {@link IConvertedCreature} for sheep
 * Allows converted sheep to be sheared
 */
public class ConvertedSheepEntity extends ConvertedCreatureEntity<SheepEntity> implements IShearable {


    private final static DataParameter<Byte> COAT = EntityDataManager.createKey(ConvertedSheepEntity.class, DataSerializers.BYTE);
    private Boolean lastSheared = null;

    public ConvertedSheepEntity(EntityType<? extends ConvertedSheepEntity> type, World world) {
        super(type, world);
    }

    public DyeColor getFleeceColor() {
        return nil() ? DyeColor.WHITE : this.getOldCreature().getFleeceColor();
    }


    public boolean getSheared() {
        return (this.dataManager.get(COAT) & 16) != 0;
    }

    public void setSheared(boolean sheared) {
        byte b0 = this.dataManager.get(COAT);

        if (sheared) {
            this.dataManager.set(COAT, (byte) (b0 | 16));
        } else {
            this.dataManager.set(COAT, (byte) (b0 & -17));
        }
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos) {
        return !getSheared() && !isChild();
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
    public List<ItemStack> onSheared(ItemStack item, IWorld world, BlockPos pos, int fortune) {

        this.setSheared(true);
        int i = 1 + this.rand.nextInt(3);

        java.util.List<ItemStack> ret = new java.util.ArrayList<>();
        for (int j = 0; j < i; ++j)
            ret.add(new ItemStack(SheepEntity.WOOL_BY_COLOR.get(this.getFleeceColor())));//TODO make public (is static final)

        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putBoolean("Sheared", this.getSheared());
    }

    @Override
    protected void registerData() {
        super.registerData();

        this.dataManager.register(COAT, (byte) 0);
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<SheepEntity> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public ConvertedCreatureEntity createFrom(SheepEntity entity) {
            ConvertedSheepEntity creature = ModEntities.converted_sheep.create(entity.getEntityWorld());
            this.copyImportantStuff(creature, entity);
            creature.setSheared(entity.getSheared());
            return creature;
        }
    }

}
