package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
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

    /**
     * @param regName   Registration name
     * @param maxDamage Max damage or 0 if unbreakable
     */
    public VampirismItemCrossbow(String regName, int maxDamage) {
        super(regName);
        this.maxStackSize = 1;
        if (maxDamage > 0) {
            this.setMaxDamage(maxDamage);
        }
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
     * Searches Offhand,Mainhand and the inventory afterwards for arrows
     * @param player
     * @param bowStack The itemstack of the bow
     * @return The itemstack of the arrows or null
     */
    protected
    @Nullable
    ItemStack findAmmo(EntityPlayer player, ItemStack bowStack) {
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
        int enchant = EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, stack);
        return enchant > 0 || player.isCreative();
    }

    /**
     * If the hurt timer the hit entity should be ignored.
     * This allows double crossbows to hit twice at once
     */
    protected boolean isIgnoreHurtTime(ItemStack crossbow) {
        return false;
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
        boolean creative = player.capabilities.isCreativeMode;
        boolean bowInfinite = isCrossbowInfinite(stack, player);

        ItemStack itemstack = this.findAmmo(player, stack);


        if (itemstack != null || bowInfinite) {
            if (itemstack == null) {
                itemstack = new ItemStack(ModItems.crossbowArrow);
            }

            float f = getArrowVelocity();

            if ((double) f >= 0.1D) {
                boolean consumeArrow = shouldConsumeArrow(itemstack, creative, bowInfinite);

                if (!world.isRemote) {
                    ItemCrossbowArrow itemarrow = itemstack.getItem() instanceof ItemCrossbowArrow ? (ItemCrossbowArrow) itemstack.getItem() : ModItems.crossbowArrow;
                    EntityCrossbowArrow entityarrow = itemarrow.createEntity(itemstack, world, player, heightOffset);
                    entityarrow.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

                    if (isCritical(player.getRNG())) {
                        entityarrow.setIsCritical(true);
                    }

                    if (isIgnoreHurtTime(stack)) {
                        entityarrow.setIgnoreHurtTimer();
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

                    if (!consumeArrow) {
                        entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    }

                    world.spawnEntityInWorld(entityarrow);
                }

                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                if (consumeArrow) {
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
     * If an arrow should be consumed after being shot
     * @param arrowStack The stack of the arrow
     * @param playerCreative If the player is creative
     * @param bowInfinite if the bow is infinite
     */
    protected boolean shouldConsumeArrow(ItemStack arrowStack, boolean playerCreative, boolean bowInfinite) {
        return !(playerCreative || bowInfinite && canArrowBeInfinite(arrowStack));
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
}
