package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


public class CrossbowArrowEntity extends AbstractArrow implements IEntityCrossbowArrow {

    /**
     * Create an entity arrow for a shooting entity (with offset)
     *
     * @param heightOffset A height offset for the position the entity is created
     * @param rightHanded  If the entity is right-handed
     * @param arrow        ItemStack of the represented arrow. Is copied.
     * @param centerOffset An offset from the center of the entity
     */
    public static @NotNull CrossbowArrowEntity createWithShooter(@NotNull Level world, @NotNull LivingEntity shooter, double heightOffset, double centerOffset, boolean rightHanded, @NotNull ItemStack arrow) {
        double yaw = ((shooter.getYRot() - 90)) / 180 * Math.PI;
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
    @NotNull
    ItemStack arrowStack = new ItemStack(ModItems.CROSSBOW_ARROW_NORMAL.get());
    private boolean ignoreHurtTimer = false;

    public CrossbowArrowEntity(@NotNull EntityType<? extends CrossbowArrowEntity> type, @NotNull Level world) {
        super(type, world);
    }


    /**
     * @param arrow ItemStack of the represented arrow. Is copied.
     */
    public CrossbowArrowEntity(@NotNull Level worldIn, double x, double y, double z, @NotNull ItemStack arrow) {
        this(ModEntities.CROSSBOW_ARROW.get(), worldIn);
        this.setPos(x, y, z);
        this.arrowStack = arrow.copy();
        arrowStack.setCount(1);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("arrowStack", arrowStack.save(new CompoundTag()));
    }

    public CrossbowArrowItem.EnumArrowType getArrowType() {
        return arrowStack.getItem() instanceof CrossbowArrowItem ? ((CrossbowArrowItem) arrowStack.getItem()).getType() : CrossbowArrowItem.EnumArrowType.NORMAL;
    }

    @NotNull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public @NotNull RandomSource getRNG() {
        return this.random;
    }

    /**
     * Allows the arrow to ignore the hurt timer of the hit entity
     */
    public void setIgnoreHurtTimer() {
        this.ignoreHurtTimer = true;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        arrowStack.deserializeNBT(compound.getCompound("arrowStack"));
    }

    @Override
    protected void doPostHurtEffects(@NotNull LivingEntity living) {
        super.doPostHurtEffects(living);
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArrow) {
            if (ignoreHurtTimer && living.invulnerableTime > 0) {
                living.invulnerableTime = 0;
            }
            ((IVampirismCrossbowArrow<?>) item).onHitEntity(arrowStack, living, this, getOwner());
        }
    }

    @NotNull
    @Override
    protected ItemStack getPickupItem() {
        return arrowStack;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockRayTraceResult) { //onHitBlock
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArrow) {
            ((IVampirismCrossbowArrow<?>) item).onHitBlock(arrowStack, (blockRayTraceResult).getBlockPos(), this, getOwner());
        }
        super.onHitBlock(blockRayTraceResult);
    }
}
