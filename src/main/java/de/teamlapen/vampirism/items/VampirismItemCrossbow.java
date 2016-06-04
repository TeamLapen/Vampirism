package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Base class for crossbows
 */
public abstract class VampirismItemCrossbow extends VampirismItem implements IFactionLevelItem<IHunterPlayer> {
    protected double heightOffset = 0.0;

    public VampirismItemCrossbow(String regName) {
        super(regName);
        this.maxStackSize = 1;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return 0;
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(ItemStack stack) {
        return null;
    }

    @Nullable
    @Override
    public IPlayableFaction<IHunterPlayer> getUsingFaction(ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        shoot(playerIn, heightOffset, worldIn, itemStackIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    /**
     * Can be between 0.1 and 5
     *
     * @return The velocity of a shot arrow.
     */
    protected float getArrowVelocity() {
        return 1F;
    }

    /**
     * How long the item cooldown should be after shooting an arrow
     *
     * @return Ticks
     */
    protected int getCooldown(EntityPlayer player, ItemStack stack) {
        return 20;
    }

    /**
     * Can be overridden to use other items as arrows. Could cause problems though.
     */
    protected boolean isArrow(@Nullable ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemCrossbowArrow;
    }

    /**
     * @return If the shot arrow should do critical damage
     */
    protected boolean isCritical(Random random) {
        return false;
    }

    /**
     * @return If the crossbow can shoot without an arrow in the players inventory
     */
    protected boolean isCrossbowInfinite(ItemStack stack, EntityPlayer player) {
        int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, stack);
        return enchant > 0;
    }

    /**
     * Shoots an arrow.
     *
     * @param player       The shooting player
     * @param stack        The crossbow item stack
     * @param heightOffset An height offset for the position the entity is created
     * @return If successful
     */
    protected boolean shoot(EntityPlayer player, double heightOffset, World world, ItemStack stack) {
        boolean flag = player.capabilities.isCreativeMode || isCrossbowInfinite(stack, player);
        ItemStack itemstack = this.findAmmo(player);


        if (itemstack != null || flag) {
            if (itemstack == null) {
                itemstack = new ItemStack(Items.ARROW);
            }

            float f = getArrowVelocity();

            if ((double) f >= 0.1D) {
                boolean flag1 = player.capabilities.isCreativeMode || canArrowBeInfinite(itemstack);

                if (!world.isRemote) {
                    ItemCrossbowArrow itemarrow = itemstack.getItem() instanceof ItemCrossbowArrow ? (ItemCrossbowArrow) itemstack.getItem() : ModItems.crossbowArrow;
                    EntityArrow entityarrow = itemarrow.createEntity(itemstack, world, player, heightOffset);
                    entityarrow.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

                    if (isCritical(player.getRNG())) {
                        entityarrow.setIsCritical(true);
                    }

                    int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                    if (j > 0) {
                        entityarrow.setDamage(entityarrow.getDamage() + (double) j * 0.5D + 0.5D);
                    }

                    int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                    if (k > 0) {
                        entityarrow.setKnockbackStrength(k);
                    }

                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                        entityarrow.setFire(100);
                    }

                    stack.damageItem(1, player);

                    if (flag1) {
                        entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    }

                    world.spawnEntityInWorld(entityarrow);
                }

                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                if (!flag1) {
                    --itemstack.stackSize;

                    if (itemstack.stackSize == 0) {
                        player.inventory.deleteStack(itemstack);
                    }
                }

                player.addStat(StatList.getObjectUseStats(this));
                player.getCooldownTracker().setCooldown(stack.getItem(), getCooldown(player, stack));
                return true;
            }
        }
        return false;
    }

    /**
     * @return If the given arrow type can be used in an infinite crossbow
     */
    private boolean canArrowBeInfinite(ItemStack arrowStack) {
        if (arrowStack.getItem() instanceof ItemCrossbowArrow) {
            return ((ItemCrossbowArrow) arrowStack.getItem()).isCanBeInfinite(arrowStack);
        }
        return true;
    }

    private ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        } else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack)) {
                    return itemstack;
                }
            }

            return null;
        }
    }
}
