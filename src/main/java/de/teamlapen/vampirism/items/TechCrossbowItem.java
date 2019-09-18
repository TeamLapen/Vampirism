package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * A set of arrows can be loaded into this crossbow.
 * It stores the current loaded arrow count in it's nbt->"arrows". -1 stands for infinite
 */
public class TechCrossbowItem extends SimpleCrossbowItem {

    public static final int MAX_ARROW_COUNT = 12;

    /**
     * @return The loaded arrows or -1 if infinite
     */
    private static int getArrowsLeft(@Nonnull ItemStack bowStack) {
        CompoundNBT nbt = bowStack.getTag();
        if (nbt == null || !nbt.contains("arrows")) return 0;
        return nbt.getInt("arrows");

    }

    /**
     * @param arrows The loaded arrows
     * @return The same bow stack
     */
    private static ItemStack setArrowsLeft(@Nonnull ItemStack bowStack, int arrows) {
        int i = Math.max(-1, Math.min(MAX_ARROW_COUNT, arrows));
        CompoundNBT nbt = bowStack.hasTag() ? bowStack.getTag() : new CompoundNBT();
        nbt.putInt("arrows", i);
        bowStack.setTag(nbt);
        return bowStack;
    }

    /**
     * Reduce the arrows by one
     *
     * @param bowStack
     * @return If there was an arrow
     */
    private static boolean reduceArrowCount(@Nonnull ItemStack bowStack, Random rnd) {
        CompoundNBT nbt = bowStack.getTag();
        if (nbt == null || !nbt.contains("arrows")) return false;
        int count = nbt.getInt("arrows");
        if (count == -1) return true;
        if (count == 0) return false;
        int frugal = isCrossbowFrugal(bowStack);
        if (frugal > 0 && rnd.nextInt(frugal + 2) == 0) return true;
        nbt.putInt("arrows", count - 1);
        bowStack.setTag(nbt);
        return true;
    }

    /**
     * Returns an itemstack of a fully loaded crossbow of the given type
     */
    public static ItemStack getLoadedItemStack(TechCrossbowItem crossbow) {
        return setArrowsLeft(new ItemStack(crossbow, 1), MAX_ARROW_COUNT);
    }

    /**
     * Returns an itemstack of a fully unloaded crossbow of the given type
     */
    public static ItemStack getUnLoadedItemStack(TechCrossbowItem crossbow) {
        return setArrowsLeft(new ItemStack(crossbow, 1), 0);
    }

    /**
     * Checks for Frugality enchanment on the crossbow
     *
     * @param crossbowStack
     * @return the enchantmen level
     */
    protected static int isCrossbowFrugal(ItemStack crossbowStack) {
        return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.crossbowfrugality, crossbowStack);
    }

    public TechCrossbowItem(String regName, float speed, int cooldown, int maxDamage) {
        super(regName, speed, cooldown, maxDamage);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int arrows = getArrowsLeft(stack);
        if (arrows == -1) {
            tooltip.add(new TranslationTextComponent(Enchantments.INFINITY.getName()).applyTextStyle(TextFormatting.DARK_GRAY));
        } else if (arrows == 0) {
            tooltip.add(new TranslationTextComponent("text.vampirism.crossbow.not_loaded").applyTextStyle(TextFormatting.DARK_GRAY));

        } else {
            tooltip.add(new TranslationTextComponent("text.vampirism.crossbow.loaded_arrow_count", arrows).applyTextStyle(TextFormatting.DARK_GRAY));
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(setArrowsLeft(new ItemStack(this), 0));
            items.add(setArrowsLeft(new ItemStack(this), MAX_ARROW_COUNT));
        }
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.tech_weapons;
    }

    @Nonnull
    @Override
    protected ItemStack findAmmo(PlayerEntity player, ItemStack bowStack) {
        boolean arrow = reduceArrowCount(bowStack, player.getRNG());
        if (!arrow) {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (!itemstack.isEmpty() && this.isArrowPackage(itemstack)) {
                    setArrowsLeft(bowStack, MAX_ARROW_COUNT);
                    if (!player.abilities.isCreativeMode) {
                        player.inventory.decrStackSize(i, 1);
                    }
                    player.getCooldownTracker().setCooldown(bowStack.getItem(), getReloadCooldown(player, bowStack));
                }
            }
            return ItemStack.EMPTY;
        }
        return new ItemStack(ModItems.crossbow_arrow_normal);
    }

    @Override
    protected float getArrowVelocity() {
        return 1.7F;
    }

    @Override
    protected int getCooldown(PlayerEntity player, ItemStack stack) {
        return 2;
    }

    @Override
    protected boolean isCrossbowInfinite(ItemStack stack, PlayerEntity player) {
        return false;
    }

    @Override
    protected boolean shouldConsumeArrow(ItemStack arrowStack, boolean playerCreative, boolean bowInfinite) {
        return false;
    }

    private int getReloadCooldown(PlayerEntity player, ItemStack bowStack) {
        return 100;
    }

    private boolean isArrowPackage(@Nonnull ItemStack stack) {
        return ModItems.tech_crossbow_ammo_package.equals(stack.getItem());
    }


}
