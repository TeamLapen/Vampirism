package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.VampirismMod;
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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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
        super(regName, new Properties().maxStackSize(1).defaultMaxDamage(maxDamage).group(VampirismMod.creativeTab));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        this.addFactionLevelToolTip(stack,worldIn,tooltip, flagIn,VampirismMod.proxy.getClientPlayer());
    }

    /**
     * Checks for Frugality enchantment on the crossbow
     *
     * @param crossbowStack crossbow to check
     * @return the enchantment level
     */
    protected static int isCrossbowFrugal(ItemStack crossbowStack) {
        return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.crossbowfrugality, crossbowStack);
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

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        shoot(playerIn, 0, 0, worldIn, stack, handIn);
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    public void setEnchantability(ItemTier material) {
        this.enchantability = material.getEnchantability();
    }

    /**
     * Searches Offhand,Mainhand and the inventory afterwards for arrows
     *
     * @param player   player to search
     * @param bowStack The itemstack of the bow
     * @return The itemstack of the arrows or null
     */
    protected
    @Nonnull
    ItemStack findAmmo(PlayerEntity player, ItemStack bowStack) {
        if (this.isArrow(player.getHeldItem(Hand.OFF_HAND))) {
            return player.getHeldItem(Hand.OFF_HAND);
        } else if (this.isArrow(player.getHeldItem(Hand.MAIN_HAND))) {
            return player.getHeldItem(Hand.MAIN_HAND);
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
    protected int getCooldown(PlayerEntity player, ItemStack stack) {
        return 20;
    }

    /**
     * Can be overridden to use other tileInventory as arrows. Could cause problems though.
     */
    protected boolean isArrow(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IVampirismCrossbowArrow;
    }

    /**
     * @return If the shot arrow should do critical damage
     */
    @SuppressWarnings("unused")
    protected boolean isCritical(Random random) {
        return false;
    }

    /**
     * @return If the crossbow can shoot without an arrow in the players inventory
     */
    protected boolean isCrossbowInfinite(ItemStack stack, PlayerEntity player) {
        int enchant = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
        return enchant > 0 || player.isCreative();
    }

    /**
     * If the hurt timer the hit entity should be ignored.
     * This allows double crossbows to hit twice at once
     */
    protected boolean isIgnoreHurtTime(ItemStack crossbow) {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return Tags.Items.STRING.contains(repair.getItem()) || super.getIsRepairable(toRepair, repair);
    }

    /**
     * Shoots an arrow.
     *
     * @param player The shooting player
     * @param stack  The crossbow item stack
     * @return If successful
     */
    protected boolean shoot(PlayerEntity player, float heightOffset, float centerOffset, World world, ItemStack stack, Hand hand) {
        boolean creative = player.abilities.isCreativeMode;
        boolean bowInfinite = isCrossbowInfinite(stack, player);
        int bowFrugal = isCrossbowFrugal(stack);

        ItemStack itemstack = this.findAmmo(player, stack);

        if (!itemstack.isEmpty() || creative) {
            if (itemstack.isEmpty()) {
                itemstack = new ItemStack(ModItems.crossbow_arrow_normal);
            }

            float f = getArrowVelocity();

            if ((double) f >= 0.1D) {
                boolean consumeArrow = shouldConsumeArrow(player.getRNG(), itemstack, creative, bowInfinite, bowFrugal);

                if (!world.isRemote) {
                    boolean rightHand = player.getPrimaryHand() == HandSide.RIGHT && hand == Hand.MAIN_HAND || player.getPrimaryHand() == HandSide.LEFT && hand == Hand.OFF_HAND;
                    IVampirismCrossbowArrow<?> itemarrow = itemstack.getItem() instanceof IVampirismCrossbowArrow ? (IVampirismCrossbowArrow<?>) itemstack.getItem() : ModItems.crossbow_arrow_normal;
                    AbstractArrowEntity entityarrow = itemarrow.createEntity(itemstack, world, player, heightOffset, 0.3F + centerOffset, rightHand);

                    Vector3d vector3d = player.getLook(1.0F);
                    entityarrow.shoot(vector3d.getX(), vector3d.getY(), vector3d.getZ(), f * 3, 1f);

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

                    stack.damageItem(1, player, (consumer) -> consumer.sendBreakAnimation(hand));

                    if (!consumeArrow) {
                        entityarrow.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    }

                    world.addEntity(entityarrow);
                    world.playSound(null, player.getPosX(), player.getPosY() + 0.5, player.getPosZ(), ModSounds.crossbow, SoundCategory.PLAYERS, 1F, world.rand.nextFloat() * 0.1F + 0.9F);

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

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.INFINITY;
    }

    /**
     * If an arrow should be consumed after being shot
     *
     * @param arrowStack     The stack of the arrow
     * @param playerCreative If the player is creative
     * @param bowInfinite    if the bow is infinite
     */
    protected boolean shouldConsumeArrow(Random rnd, ItemStack arrowStack, boolean playerCreative, boolean bowInfinite, int bowFrugal) {
        return !(playerCreative || bowInfinite && canArrowBeInfinite(arrowStack) || (bowFrugal > 0 && rnd.nextInt(Math.max(2, 4 - bowFrugal)) == 0));
    }

    /**
     * @return If the given arrow type can be used in an infinite crossbow
     */
    private boolean canArrowBeInfinite(ItemStack arrowStack) {
        return !(arrowStack.getItem() instanceof IVampirismCrossbowArrow) || ((IVampirismCrossbowArrow<?>) arrowStack.getItem()).isCanBeInfinite();
    }


}
