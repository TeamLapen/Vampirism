package de.teamlapen.vampirism.entity.convertible;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.ai.VVillagerAILookAtCustomer;
import de.teamlapen.vampirism.entity.ai.VVillagerAITrade;
import de.teamlapen.vampirism.entity.ai.VampireAIMoveIndoors;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/**
 * Entity for converted villagers. Able to trade with player
 */
public class EntityConvertedVillager extends EntityConvertedCreature implements IMerchant {


    public static class VillagerConvertingHandler extends ConvertingHandler<EntityVillager> {
        @Override
        public EntityConvertedCreature createFrom(EntityVillager entity) {
            Logger.t("Creating converted creature");
            EntityConvertedCreature convertedCreature = new EntityConvertedVillager(entity.worldObj);
            this.copyImportantStuff(convertedCreature, entity);
            return convertedCreature;
        }

        @Override
        public double getConvertedSpeed(EntityVillager entity) {
            return 0.295D;
        }
    }

    private EntityPlayer buyingPlayer;
    private MerchantRecipeList buyingList;
    private int timeUntilReset;
    private boolean needsInitilization;

    public EntityConvertedVillager(World world) {
        super(world);
        this.tasks.addTask(1, new VVillagerAILookAtCustomer(this));
        this.tasks.addTask(1, new VVillagerAITrade(this));
        this.tasks.addTask(3, new VampireAIMoveIndoors(this));
        this.tasks.addTask(10, new EntityAIMoveThroughVillage(this, 0.6, false));

    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound() {
        return this.isTrading() ? "mob.villager.haggle" : "mob.villager.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound() {
        return "mob.villager.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound() {
        return "mob.villager.death";
    }


    public boolean isTrading() {
        return buyingPlayer != null;
    }

    @Override
    public void setCustomer(EntityPlayer player) {
        buyingPlayer = player;
    }

    @Override
    public EntityPlayer getCustomer() {
        return buyingPlayer;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset;

            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    if (this.buyingList.size() > 1) {
                        Iterator iterator = this.buyingList.iterator();

                        while (iterator.hasNext()) {
                            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                            if (merchantrecipe.isRecipeDisabled()) {
                                merchantrecipe.func_82783_a(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                            }
                        }
                    }

                    this.addDefaultEquipmentAndRecipies(1);
                    this.needsInitilization = false;
                }

                this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
            }
        }
    }

    @Override
    public MerchantRecipeList getRecipes(EntityPlayer p_70934_1_) {
        if (this.buyingList == null) {
            this.addDefaultEquipmentAndRecipies(1);
        }

        return this.buyingList;
    }

    private void addDefaultEquipmentAndRecipies(int i) {
        MerchantRecipeList merchantRecipeList = new MerchantRecipeList();

        addRecipe(merchantRecipeList, new ItemStack(ModItems.humanHeart, 9), 2, this.getRNG(), 0.5F);
        addRecipe(merchantRecipeList, 3, new ItemStack(ModItems.humanHeart, 9), this.getRNG(), 0.5F);
        addRecipe(merchantRecipeList, 1, new ItemStack(ModItems.leechSword, 1), this.getRNG(), 0.3F);
        addRecipe(merchantRecipeList, 1, new ItemStack(ModItems.bloodBottle, 3, ItemBloodBottle.MAX_BLOOD), rand, 0.9F);

        Collections.shuffle(merchantRecipeList);

        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        for (int l = 0; l < i && l < merchantRecipeList.size(); ++l) {
            this.buyingList.addToListWithCheck((MerchantRecipe) merchantRecipeList.get(l));
        }
    }

    @Override
    public void setRecipes(MerchantRecipeList p_70930_1_) {
    }

    public static void addRecipe(MerchantRecipeList list, ItemStack item, int emeralds, Random rnd, float prop) {
        if (rnd.nextFloat() < prop) {
            list.add(new MerchantRecipe(item, new ItemStack(Items.emerald, emeralds)));
        }
    }

    public static void addRecipe(MerchantRecipeList list, int emeralds, ItemStack item, Random rnd, float prop) {
        if (rnd.nextFloat() < prop) {
            list.add(new MerchantRecipe(new ItemStack(Items.emerald, emeralds), item));
        }
    }

    @Override
    public void useRecipe(MerchantRecipe p_70933_1_) {
        p_70933_1_.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());

        if (p_70933_1_.hasSameIDsAs((MerchantRecipe) this.buyingList.get(this.buyingList.size() - 1))) {
            this.timeUntilReset = 40;
            this.needsInitilization = true;
        }
    }

    @Override
    public void func_110297_a_(ItemStack p_110297_1_) {
        if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();

            if (p_110297_1_ != null) {
                this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
            } else {
                this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    public boolean interact(EntityPlayer p_70085_1_) {
        Logger.t("Interacting");
        ItemStack itemstack = p_70085_1_.inventory.getCurrentItem();
        boolean flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;

        if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !p_70085_1_.isSneaking()) {
            if (!this.worldObj.isRemote) {
                this.setCustomer(p_70085_1_);
                p_70085_1_.displayGUIMerchant(this, this.getCustomNameTag());
            }

            return true;
        } else {
            return super.interact(p_70085_1_);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("Offers", 10)) {
            NBTTagCompound list = nbt.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(list);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

        if (this.buyingList != null) {
            nbt.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }
    }
}
