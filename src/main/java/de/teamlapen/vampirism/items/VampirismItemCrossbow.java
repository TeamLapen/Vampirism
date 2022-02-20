package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for crossbows
 */
public abstract class VampirismItemCrossbow extends Item implements IFactionLevelItem<IHunterPlayer>, IVampirismCrossbow {

    /**
     * Checks for Frugality enchantment on the crossbow
     *
     * @param crossbowStack crossbow to check
     * @return the enchantment level
     */
    protected static int isCrossbowFrugal(ItemStack crossbowStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CROSSBOWFRUGALITY.get(), crossbowStack);
    }

    private int enchantability = 0;

    /**
     * @param maxDamage Max damage or 0 if unbreakable
     */
    public VampirismItemCrossbow(int maxDamage, Tiers material) {
        super(new Properties().stacksTo(1).defaultDurability(maxDamage).tab(VampirismMod.creativeTab));
        setEnchantability(material);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        this.addFactionLevelToolTip(stack, worldIn, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.INFINITY_ARROWS;
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, ItemStack repair) {
        return repair.is(Tags.Items.STRING) || super.isValidRepairItem(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return enchantability;
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return 0;
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@Nonnull ItemStack stack) {
        return null;
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    private void setEnchantability(Tiers material) {
        this.enchantability = material.getEnchantmentValue();
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        shoot(playerIn, 0, 0, worldIn, stack, handIn);
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
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
    ItemStack findAmmo(Player player, ItemStack bowStack) {
        if (this.isArrow(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        } else if (this.isArrow(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = player.getInventory().getItem(i);

                if (this.isArrow(itemstack)) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    public static boolean hasAmmo(Player player, ItemStack crossbowStack) {
        if (player.isCreative()) return true;
        Item i = crossbowStack.getItem();
        if (i instanceof VampirismItemCrossbow) {
            return !((VampirismItemCrossbow) i).findAmmo(player, crossbowStack).isEmpty();
        }
        return false;
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
    protected int getCooldown(Player player, ItemStack stack) {
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
    protected boolean isCritical(RandomSource random) {
        return false;
    }

    /**
     * @return If the crossbow can shoot without an arrow in the players inventory
     */
    protected boolean isCrossbowInfinite(ItemStack stack, Player player) {
        int enchant = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack);
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
    protected boolean shoot(Player player, float heightOffset, float centerOffset, Level world, ItemStack stack, InteractionHand hand) {
        boolean creative = player.getAbilities().instabuild;
        boolean bowInfinite = isCrossbowInfinite(stack, player);
        int bowFrugal = isCrossbowFrugal(stack);

        ItemStack itemstack = this.findAmmo(player, stack);

        if (!itemstack.isEmpty() || creative) {
            if (itemstack.isEmpty()) {
                itemstack = new ItemStack(ModItems.CROSSBOW_ARROW_NORMAL.get());
            }

            float f = getArrowVelocity();

            if ((double) f >= 0.1D) {
                boolean consumeArrow = shouldConsumeArrow(player.getRandom(), itemstack, creative, bowInfinite, bowFrugal);

                if (!world.isClientSide) {
                    boolean rightHand = player.getMainArm() == HumanoidArm.RIGHT && hand == InteractionHand.MAIN_HAND || player.getMainArm() == HumanoidArm.LEFT && hand == InteractionHand.OFF_HAND;
                    IVampirismCrossbowArrow<?> itemarrow = itemstack.getItem() instanceof IVampirismCrossbowArrow ? (IVampirismCrossbowArrow<?>) itemstack.getItem() : ModItems.CROSSBOW_ARROW_NORMAL.get();
                    AbstractArrow entityarrow = itemarrow.createEntity(itemstack, world, player, heightOffset, 0.3F + centerOffset, rightHand);

                    Vec3 vector3d = player.getViewVector(1.0F);
                    entityarrow.shoot(vector3d.x(), vector3d.y(), vector3d.z(), f * 3, 1f);

                    if (isCritical(player.getRandom())) {
                        entityarrow.setCritArrow(true);
                    }

                    if (isIgnoreHurtTime(stack)) {
                        ((IEntityCrossbowArrow) entityarrow).setIgnoreHurtTimer();
                    }

                    int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);

                    if (j > 0) {
                        entityarrow.setBaseDamage(entityarrow.getBaseDamage() + (double) j * 0.5D + 0.5D);
                    }

                    int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);

                    if (k > 0) {
                        entityarrow.setKnockback(k);
                    }

                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                        entityarrow.setSecondsOnFire(100);
                    }

                    stack.hurtAndBreak(1, player, (consumer) -> consumer.broadcastBreakEvent(hand));

                    if (!consumeArrow) {
                        entityarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    world.addFreshEntity(entityarrow);
                    world.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), ModSounds.CROSSBOW.get(), SoundSource.PLAYERS, 1F, world.random.nextFloat() * 0.1F + 0.9F);

                }


                if (consumeArrow) {
                    itemstack.shrink(1);

                    if (itemstack.isEmpty()) {
                        player.getInventory().removeItem(itemstack);
                    }
                }

                player.getCooldowns().addCooldown(stack.getItem(), getCooldown(player, stack));
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
    protected boolean shouldConsumeArrow(RandomSource rnd, ItemStack arrowStack, boolean playerCreative, boolean bowInfinite, int bowFrugal) {
        return !(playerCreative || bowInfinite && canArrowBeInfinite(arrowStack) || (bowFrugal > 0 && rnd.nextInt(Math.max(2, 4 - bowFrugal)) == 0));
    }

    /**
     * @return If the given arrow type can be used in an infinite crossbow
     */
    private boolean canArrowBeInfinite(ItemStack arrowStack) {
        return !(arrowStack.getItem() instanceof IVampirismCrossbowArrow) || ((IVampirismCrossbowArrow<?>) arrowStack.getItem()).isCanBeInfinite();
    }


}
