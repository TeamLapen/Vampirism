package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.List;

/**
 * {@link IConvertedCreature} for sheep
 * Allows converted sheep to be sheared
 */
public class EntityConvertedSheep extends EntityConvertedCreature implements IShearable {


    private final static DataParameter<Byte> COAT = EntityDataManager.createKey(EntityConvertedSheep.class, DataSerializers.BYTE);
    private Boolean lastSheared = null;

    public EntityConvertedSheep(World world) {
        super(world);
    }

    public EnumDyeColor getFleeceColor() {
        return nil() ? EnumDyeColor.WHITE : ((EntitySheep) this.getOldCreature()).getFleeceColor();
    }


    public boolean getSheared() {
        return ((this.dataManager.get(COAT)).byteValue() & 16) != 0;
    }

    public void setSheared(boolean sheared) {
        byte b0 = this.dataManager.get(COAT).byteValue();

        if (sheared) {
            this.dataManager.set(COAT, Byte.valueOf((byte) (b0 | 16)));
        } else {
            this.dataManager.set(COAT, Byte.valueOf((byte) (b0 & -17)));
        }
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return !getSheared();
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        boolean t = getSheared();
        if (!nil() && (lastSheared == null || lastSheared.booleanValue() != t)) {
            lastSheared = t;
            ((EntitySheep) getOldCreature()).setSheared(lastSheared);

        }
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {

        this.setSheared(true);
        int i = 1 + this.rand.nextInt(3);

        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        for (int j = 0; j < i; ++j)
            ret.add(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()));

        this.playSound(SoundEvents.entity_sheep_shear, 1.0F, 1.0F);
        return ret;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Sheared", this.getSheared());
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(COAT, Byte.valueOf((byte) 0));
    }

    public static class ConvertingSheepHandler extends DefaultConvertingHandler<EntitySheep> {
        public ConvertingSheepHandler() {
            super(null);
        }

        @Override
        public EntityConvertedCreature createFrom(EntitySheep entity) {
            EntityConvertedSheep creature = new EntityConvertedSheep(entity.worldObj);
            this.copyImportantStuff(creature, entity);
            creature.setSheared(entity.getSheared());
            return creature;
        }
    }

}
