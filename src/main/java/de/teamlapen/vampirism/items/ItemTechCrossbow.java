package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
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
public class ItemTechCrossbow extends ItemSimpleCrossbow {

    public static final int MAX_ARROW_COUNT = 12;

    /**
     * @return The loaded arrows or -1 if infinite
     */
    private static int getArrowsLeft(@Nonnull ItemStack bowStack) {
        NBTTagCompound nbt = bowStack.getTagCompound();
        if (nbt == null || !nbt.hasKey("arrows")) return 0;
        return nbt.getInteger("arrows");

    }

    /**
     * @param arrows The loaded arrows
     * @return The same bow stack
     */
    private static ItemStack setArrowsLeft(@Nonnull ItemStack bowStack, int arrows) {
        int i = Math.max(-1, Math.min(MAX_ARROW_COUNT, arrows));
        NBTTagCompound nbt = bowStack.hasTagCompound() ? bowStack.getTagCompound() : new NBTTagCompound();
        nbt.setInteger("arrows", i);
        bowStack.setTagCompound(nbt);
        return bowStack;
    }

    /**
     * Reduce the arrows by one
     *
     * @param bowStack
     * @return If there was an arrow
     */
    private static boolean reduceArrowCount(@Nonnull ItemStack bowStack, Random rnd) {
        NBTTagCompound nbt = bowStack.getTagCompound();
        if (nbt == null || !nbt.hasKey("arrows")) return false;
        int count = nbt.getInteger("arrows");
        if (count == -1) return true;
        if (count == 0) return false;
        int frugal = isCrossbowFrugal(bowStack);
        if (frugal > 0 && rnd.nextInt(frugal + 2) == 0) return true;
        nbt.setInteger("arrows", count - 1);
        bowStack.setTagCompound(nbt);
        return true;
    }

    /**
     * Returns an itemstack of a fully loaded crossbow of the given type
     */
    public static ItemStack getLoadedItemStack(ItemTechCrossbow crossbow) {
        return setArrowsLeft(new ItemStack(crossbow, 1), MAX_ARROW_COUNT);
    }

    /**
     * Returns an itemstack of a fully unloaded crossbow of the given type
     */
    public static ItemStack getUnLoadedItemStack(ItemTechCrossbow crossbow) {
        return setArrowsLeft(new ItemStack(crossbow, 1), 0);
    }

    /**
     * Checks for Frugality enchanment on the crossbow
     *
     * @param crossbowStack
     * @return the enchantmen level
     */
    protected static int isCrossbowFrugal(ItemStack crossbowStack) {
        int enchant = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.crossbowfrugality, crossbowStack);
        return enchant;
    }

    public ItemTechCrossbow(String regName, float speed, int cooldown, int maxDamage) {
        super(regName, speed, cooldown, maxDamage);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int arrows = getArrowsLeft(stack);
        if (arrows == -1) {
            tooltip.add(TextFormatting.DARK_GRAY + UtilLib.translate(Enchantments.INFINITY.getName()));
        } else if (arrows == 0) {
            tooltip.add(TextFormatting.DARK_GRAY + UtilLib.translate("text.vampirism.crossbow.not_loaded"));
        } else {
            tooltip.add(TextFormatting.DARK_GRAY + UtilLib.translateFormatted("text.vampirism.crossbow.loaded_arrow_count", arrows));
        }
    }


    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.tech_weapons;
    }


    @Override
    public void getSubItems(ItemGroup tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(setArrowsLeft(new ItemStack(this), 0));
            items.add(setArrowsLeft(new ItemStack(this), MAX_ARROW_COUNT));
            //subItems.add(setArrowsLeft(new ItemStack(itemIn), -1));
        }

    }


    @Nonnull
    @Override
    protected ItemStack findAmmo(EntityPlayer player, ItemStack bowStack) {
        boolean arrow = reduceArrowCount(bowStack, player.getRNG());
        if (!arrow) {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (!itemstack.isEmpty() && this.isArrowPackage(itemstack)) {
                    setArrowsLeft(bowStack, MAX_ARROW_COUNT);
                    if (!player.capabilities.isCreativeMode) {
                        player.inventory.decrStackSize(i, 1);
                    }
                    player.getCooldownTracker().setCooldown(bowStack.getItem(), getReloadCooldown(player, bowStack));
                }
            }
            return ItemStack.EMPTY;
        }
        return new ItemStack(ModItems.crossbow_arrow);
    }

    @Override
    protected float getArrowVelocity() {
        return 1.7F;
    }

    @Override
    protected int getCooldown(EntityPlayer player, ItemStack stack) {
        return 2;
    }

    @Override
    protected boolean isCrossbowInfinite(ItemStack stack, EntityPlayer player) {
        return false;
    }

    @Override
    protected boolean shouldConsumeArrow(ItemStack arrowStack, boolean playerCreative, boolean bowInfinite) {
        return false;
    }

    private int getReloadCooldown(EntityPlayer player, ItemStack bowStack) {
        return 100;
    }

    private boolean isArrowPackage(@Nonnull ItemStack stack) {
        return ModItems.tech_crossbow_ammo_package.equals(stack.getItem());
    }


}
