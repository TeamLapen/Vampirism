package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.block.IGarlic;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.SunDmgHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

/**
 * Base class for all vampires
 */
public abstract class EntityVampireBase extends EntityVampirism {
    private boolean sundamageCache;
    protected float sundamage = 0.5f;
    protected int resitsGarlic = 0;

    public EntityVampireBase(World p_i1595_1_) {
        super(p_i1595_1_);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
    }

    public int isInGarlic() {
        if (worldObj != null) {
            Block b = worldObj.getBlock((int) posX, (int) posY + 1, (int) posZ);
            if (b instanceof IGarlic) {
                return ((IGarlic) b).isWeakGarlic() ? 1 : 2;
            }
        }
        return 0;
    }

    public int getResitsGarlic() {
        return resitsGarlic;
    }

    public boolean isGarlicOkAt(int x, int y, int z) {
        Block b = worldObj.getBlock(x, y, z);
        int i = (b instanceof IGarlic ? ((IGarlic) b).isWeakGarlic() ? 1 : 2 : 0);
        return resitsGarlic >= i;
    }

    public boolean isGettingSundamage() {
        return isGettingSundamage(false);
    }

    public boolean isGettingSundamage(boolean forceRefresh) {
        if (this.ticksExisted % 8 != 0 && !forceRefresh) return sundamageCache;
        return (sundamageCache = SunDmgHelper.gettingSundamge(this));
    }

    protected void attackedEntityAsMob(EntityLivingBase entity) {
        if (this.rand.nextInt(3) == 0) {
            (entity).addPotionEffect(new PotionEffect(Potion.weakness.id, 200));
            (entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100));
        }

    }

    protected boolean isValidLightLevel() {
        if (Helper.isEntityInVampireBiome(this)) return true;
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32)) {
            return false;
        } else {
            int l = this.worldObj.getBlockLightValue(i, j, k);

            if (this.worldObj.isThundering()) {
                int i1 = this.worldObj.skylightSubtracted;
                this.worldObj.skylightSubtracted = 10;
                l = this.worldObj.getBlockLightValue(i, j, k);
                this.worldObj.skylightSubtracted = i1;
            }

            return l <= this.rand.nextInt(8);
        }
    }

    public boolean wantsBlood() {
        return false;
    }

    public void addBlood(int amt) {
        addPotionEffect(new PotionEffect(Potion.regeneration.id, amt * 20));
    }

    @Override
    public boolean getCanSpawnHere() {
        if (!isValidLightLevel()) return false;
        if (isInGarlic() > resitsGarlic) return false;
        return super.getCanSpawnHere();
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote) {
            if (isGettingSundamage()) {
                float dmg = sundamage;
                if (this.isPotionActive(ModPotion.sunscreen)) {
                    dmg = dmg / 2;
                }
                this.attackEntityFrom(VampirismMod.sunDamage, dmg);
            }
            if (this.ticksExisted % 10 == 0) {
                if (isInGarlic() > resitsGarlic) {
                    this.addPotionEffect(new PotionEffect(ModPotion.garlic.id, 50, 0));
                }
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        boolean t = isGarlicOkAt(x, y, z);
        return super.getBlockPathWeight(x, y, z) / (t ? 1 : 3);
    }
}
