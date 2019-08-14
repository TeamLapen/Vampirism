package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Similar to EntityXPOrb
 */
public class SoulOrbEntity extends Entity implements IRendersAsItem {

    public static final DataParameter<String> TYPE_PARAMETER = EntityDataManager.createKey(SoulOrbEntity.class, DataSerializers.STRING);
    private int delayBeforePickup;
    private PlayerEntity player;
    private int age;
    @Nullable
    private ItemStack soulItemStack;

    public SoulOrbEntity(World worldIn, double x, double y, double z, VARIANT type) {
        super(ModEntities.soul_orb, worldIn);
        this.setVariant(type);
        delayBeforePickup = 10;
        this.setPosition(x, y, z);
        this.rotationYaw = (float) (Math.random() * 360.0D);
        this.setMotion((double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F), (double) ((float) (Math.random() * 0.2D) * 2.0F), (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F));
    }

    public SoulOrbEntity(EntityType<? extends SoulOrbEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public ItemStack getItem() {
        return soulItemStack;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        float f = 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightnessForRender();
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f * 15.0F * 16.0F);

        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Nonnull
    public ItemStack getSoulItemStack() {
        if (soulItemStack == null) {
            soulItemStack = createSoulItemStack();
        }
        return soulItemStack;
    }

    @Override
    public boolean handleWaterMovement() {
        return this.world.isMaterialInBB(this.getBoundingBox(), Material.WATER);
    }

    @Override
    public boolean isInvisibleToPlayer(@Nonnull PlayerEntity player) {
        switch (getVariant()) {
            case VAMPIRE:
                return !Helper.isHunter(player) || player.isSpectator();
            default:
                return true;
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        if (!this.world.isRemote) {
            if (delayBeforePickup == 0) {
                if (Helper.isHunter(entityIn)) {
                    if (entityIn.inventory.addItemStackToInventory(getSoulItemStack())) {
                        entityIn.onItemPickup(this, 1);
                        this.remove();
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (delayBeforePickup > 0) {
            delayBeforePickup--;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().subtract(0D, 0.03D, 0D));
        }

        if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
            this.setMotion((double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), 0.2D, (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
        }

        this.pushOutOfBlocks(this.posX, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.posZ);

        if (this.age % 10 == 5 & (this.player == null || !this.player.isAlive() || this.player.getDistanceSq(this) > 64)) {
            this.player = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 8, EntityPredicates.NOT_SPECTATING.and(Helper::isHunter));
        }

        if (this.player != null) {
            //Calculate relative (to the 8 block max) distance to the player
            double relDiffX = (this.player.posX - this.posX) / 8.0D;
            double relDiffY = (this.player.posY + (double) this.player.getEyeHeight() / 2.0D - this.posY) / 8.0D;
            double relDiffZ = (this.player.posZ - this.posZ) / 8.0D;
            double relDist = Math.sqrt(relDiffX * relDiffX + relDiffY * relDiffY + relDiffZ * relDiffZ);
            double d5 = 1.0D - relDist;

            if (d5 > 0.0D) {
                d5 = d5 * d5;
                this.setMotion(this.getMotion().add(relDiffX / relDist * d5 * 0.08D, relDiffY / relDist * d5 * 0.08D, relDiffZ / relDist * d5 * 0.08D));
            }
        }

        this.move(MoverType.SELF, this.getMotion());
        float f = 0.98F;

        if (this.onGround) {
            BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
            BlockState underState = this.world.getBlockState(underPos);
            f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.98F;
        }

        this.setMotion(this.getMotion().mul(f, 0.9800000190734863D, f));

        if (this.onGround) {
            this.setMotion(this.getMotion().mul(1D, -0.8999999761581421D, 1D));
        }


        this.age++;

        if (this.age >= 6000) {
            this.remove();
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);//TODO 1.14 check
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.setVariant(VARIANT.valueOf(compound.getString("type")));
        this.age = compound.getInt("age");
        soulItemStack = null;//Reset item just in case a item of a different type has been created beforehand
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(TYPE_PARAMETER, VARIANT.NONE.name());
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putString("type", this.getVariant().name());
        compound.putInt("age", age);
    }

    private ItemStack createSoulItemStack() {
        switch (getVariant()) {
            case VAMPIRE:
                return new ItemStack(ModItems.soul_orb_vampire);
            default:
                return new ItemStack(ModItems.soul_orb_vampire);
        }
    }

    public VARIANT getVariant() {
        return VARIANT.valueOf(getDataManager().get(TYPE_PARAMETER));
    }

    private void setVariant(VARIANT type) {
        getDataManager().set(TYPE_PARAMETER, type.name());
    }


    public enum VARIANT {
        NONE, VAMPIRE
    }
}
