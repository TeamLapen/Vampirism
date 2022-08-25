package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Random;


public class CrossbowArrowEntity extends AbstractArrowEntity implements IEntityCrossbowArrow {

    /**
     * Create a entity arrow for a shooting entity (with offset)
     *
     * @param heightOffset An height offset for the position the entity is created
     * @param rightHanded  If the entiy is right handed
     * @param arrow        ItemStack of the represented arrow. Is copied.
     * @param centerOffset An offset from the center of the entity
     * @deprecated use {@link net.minecraft.item.ArrowItem#createArrow(net.minecraft.world.World, net.minecraft.item.ItemStack, net.minecraft.entity.LivingEntity)}
     */
    @Deprecated
    public static CrossbowArrowEntity createWithShooter(World world, LivingEntity shooter, double heightOffset, double centerOffset, boolean rightHanded, ItemStack arrow) {
        double yaw = ((shooter.yRot - 90)) / 180 * Math.PI;
        if (rightHanded) {
            yaw += Math.PI;
        }
        double posX = shooter.getX() - Math.sin(yaw) * centerOffset;
        double posZ = shooter.getZ() + Math.cos(yaw) * centerOffset;
        CrossbowArrowEntity entityArrow = new CrossbowArrowEntity(world, posX, shooter.getY() + (double) shooter.getEyeHeight() - 0.10000000149011612D + heightOffset, posZ, arrow);
        entityArrow.setOwner(shooter);
        return entityArrow;
    }

    private
    @Nonnull
    ItemStack arrowStack = new ItemStack(ModItems.CROSSBOW_ARROW_NORMAL.get());
    private boolean ignoreHurtTimer = false;

    public CrossbowArrowEntity(EntityType<? extends CrossbowArrowEntity> type, World world) {
        super(type, world);
    }

    public CrossbowArrowEntity(World level, LivingEntity entity, ItemStack stack) {
        super(ModEntities.CROSSBOW_ARROW.get(), entity, level);
        this.arrowStack = stack.copy();
        this.arrowStack.setCount(1);
    }


    /**
     * @param arrow ItemStack of the represented arrow. Is copied.
     */
    public CrossbowArrowEntity(World worldIn, double x, double y, double z, ItemStack arrow) {
        this(ModEntities.CROSSBOW_ARROW.get(), worldIn);
        this.setPos(x, y, z);
        this.arrowStack = arrow.copy();
        arrowStack.setCount(1);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.put("arrowStack", arrowStack.save(new CompoundNBT()));
    }

    public CrossbowArrowItem.EnumArrowType getArrowType() {
        return arrowStack.getItem() instanceof CrossbowArrowItem ? ((CrossbowArrowItem) arrowStack.getItem()).getType() : CrossbowArrowItem.EnumArrowType.NORMAL;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public Random getRNG() {
        return this.random;
    }

    /**
     * Allows the arrow to ignore the hurt timer of the hit entity
     */
    public void setIgnoreHurtTimer() {
        this.ignoreHurtTimer = true;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        arrowStack.deserializeNBT(compound.getCompound("arrowStack"));
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArrow) {
            if (ignoreHurtTimer && living.invulnerableTime > 0) {
                living.invulnerableTime = 0;
            }
            ((IVampirismCrossbowArrow) item).onHitEntity(arrowStack, living, this, getOwner());
        }
    }

    @Nonnull
    @Override
    protected ItemStack getPickupItem() {
        return arrowStack;
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult blockRayTraceResult) { //onHitBlock
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArrow) {
            ((IVampirismCrossbowArrow) item).onHitBlock(arrowStack, (blockRayTraceResult).getBlockPos(), this, getOwner());
        }
        super.onHitBlock(blockRayTraceResult);
    }

    public void setEffectsFromItem(ItemStack p_200887_2_) {
    }
}
