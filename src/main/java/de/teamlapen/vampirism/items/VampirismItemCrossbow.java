package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Base class for crossbows
 */
public abstract class VampirismItemCrossbow extends VampirismItem implements IFactionLevelItem<IHunterPlayer>, IVampirismCrossbow {

    private int enchantability = 0;

    /**
     * @param regName   Registration name
     * @param maxDamage Max damage or 0 if unbreakable
     */
    public VampirismItemCrossbow(String regName, int maxDamage) {
        super(regName, new Properties().maxStackSize(1).defaultMaxDamage(maxDamage));
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return enchantability;
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return 0;
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return null;
    }

    @Nullable
    @Override
    public IPlayableFaction<IHunterPlayer> getUsingFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        shoot(playerIn, 0, 0, worldIn, stack, handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }


    /**
     * Searches Offhand,Mainhand and the inventory afterwards for arrows
     *
     * @param player
     * @param bowStack The itemstack of the bow
     * @return The itemstack of the arrows or null
     */
    protected
    @Nonnull
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

            return ItemStack.EMPTY;
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
    protected boolean isArrow(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IVampirismCrossbowArrow;
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
        int enchant = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.crossbowinfinite, stack);
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
     * @param player The shooting player
     * @param stack  The crossbow item stack
     * @return If successful
     */
    protected boolean shoot(EntityPlayer player, float heightOffset, float centerOffset, World world, ItemStack stack, EnumHand hand) {
        boolean creative = player.isCreative();
        boolean bowInfinite = isCrossbowInfinite(stack, player);

        ItemStack itemstack = this.findAmmo(player, stack);


        if (!itemstack.isEmpty() || bowInfinite) {
            if (itemstack.isEmpty()) {
                itemstack = new ItemStack(ModItems.crossbow_arrow);
            }

            float f = getArrowVelocity();

            if ((double) f >= 0.1D) {
                boolean consumeArrow = shouldConsumeArrow(itemstack, creative, bowInfinite);

                if (!world.isRemote) {
                    boolean rightHand = player.getPrimaryHand() == EnumHandSide.RIGHT && hand == EnumHand.MAIN_HAND || player.getPrimaryHand() == EnumHandSide.LEFT && hand == EnumHand.OFF_HAND;
                    IVampirismCrossbowArrow itemarrow = itemstack.getItem() instanceof IVampirismCrossbowArrow ? (IVampirismCrossbowArrow) itemstack.getItem() : ModItems.crossbow_arrow;
                    EntityArrow entityarrow = itemarrow.createEntity(itemstack, world, player, heightOffset, 0.3F + centerOffset, rightHand);
                    entityarrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

                    if (isCritical(player.getRNG())) {
                        entityarrow.setIsCritical(true);
                    }

                    if (isIgnoreHurtTime(stack)) {
                        ((IEntityCrossbowArrow) entityarrow).setIgnoreHurtTimer();
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

                    world.spawnEntity(entityarrow);
                    world.playSound(null, player.posX, player.posY + 0.5, player.posZ, ModSounds.crossbow, SoundCategory.PLAYERS, 1F, world.rand.nextFloat() * 0.1F + 0.9F);

                }


                if (consumeArrow) {
                    itemstack.shrink(1);

                    if (itemstack.isEmpty()) {
                        player.inventory.deleteStack(itemstack);
                    }
                }

                player.getCooldownTracker().setCooldown(stack.getItem(), getCooldown(player, stack));
                return true;
            }
        }
        return false;
    }

    /**
     * If an arrow should be consumed after being shot
     *
     * @param arrowStack     The stack of the arrow
     * @param playerCreative If the player is creative
     * @param bowInfinite    if the bow is infinite
     */
    protected boolean shouldConsumeArrow(ItemStack arrowStack, boolean playerCreative, boolean bowInfinite) {
        return !(playerCreative || bowInfinite && canArrowBeInfinite(arrowStack));
    }

    /**
     * @return If the given arrow type can be used in an infinite crossbow
     */
    private boolean canArrowBeInfinite(ItemStack arrowStack) {
        return !(arrowStack.getItem() instanceof IVampirismCrossbowArrow) || ((IVampirismCrossbowArrow) arrowStack.getItem()).isCanBeInfinite();
    }


}
