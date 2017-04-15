package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

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
        if (rnd.nextInt(frugal + 1) != 0) return true;
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
        int enchant = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.crossbowFrugality, crossbowStack);
        return enchant;
    }

    public ItemTechCrossbow(String regName, float speed, int cooldown, int maxDamage) {
        super(regName, speed, cooldown, maxDamage);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
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
    public ISkill<IHunterPlayer> getRequiredSkill(ItemStack stack) {
        return HunterSkills.techWeapons;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        subItems.add(setArrowsLeft(new ItemStack(itemIn), 0));
        subItems.add(setArrowsLeft(new ItemStack(itemIn), MAX_ARROW_COUNT));
        //subItems.add(setArrowsLeft(new ItemStack(itemIn), -1));

    }

    @Nullable
    @Override
    protected ItemStack findAmmo(EntityPlayer player, ItemStack bowStack) {
        boolean arrow = reduceArrowCount(bowStack, player.getRNG());
        return arrow ? new ItemStack(ModItems.crossbowArrow) : null;
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

    /**
     * Recipe which accepts an unloaded bow and an additional item and outputs the loaded bow
     */
    public static class ShapelessFillRecipe implements IRecipe {
        private final ItemTechCrossbow crossbowItem;
        private final ItemStack arrowPackage;
        private final ItemStack loadedCrossbow;

        public ShapelessFillRecipe(@Nonnull ItemTechCrossbow crossbowItem, @Nonnull ItemStack arrowPackage) {
            this.crossbowItem = crossbowItem;
            this.arrowPackage = arrowPackage;
            loadedCrossbow = getLoadedItemStack(crossbowItem);
        }

        @Nullable
        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            for (int i = 0; i < inv.getHeight(); ++i) {
                for (int j = 0; j < inv.getWidth(); ++j) {
                    ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                    if (itemstack != null) {
                        if (this.crossbowItem.equals(itemstack.getItem())) {
                            ItemStack result = loadedCrossbow.copy();
                            result.setItemDamage(itemstack.getItemDamage());
                            return result;
                        }
                    }
                }
            }
            return loadedCrossbow.copy();
        }

        @Nullable
        @Override
        public ItemStack getRecipeOutput() {
            return loadedCrossbow;
        }

        @Override
        public int getRecipeSize() {
            return 2;
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            return NonNullList.create();
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            boolean crossbow = false;
            boolean arrows = false;
            for (int i = 0; i < inv.getHeight(); ++i) {
                for (int j = 0; j < inv.getWidth(); ++j) {
                    ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                    if (itemstack != null) {
                        boolean flag = false;

                        if (!crossbow && this.crossbowItem.equals(itemstack.getItem())) {
                            if (getArrowsLeft(itemstack) == 0) {
                                crossbow = true;
                                flag = true;
                            }
                        } else if (!arrows && itemstack.getItem() == arrowPackage.getItem() && (arrowPackage.getMetadata() == 32767 || itemstack.getMetadata() == arrowPackage.getMetadata())) {
                            flag = true;
                            arrows = true;

                        }

                        if (!flag) {
                            return false;
                        }
                    }
                }
            }

            return arrows && crossbow;
        }
    }


}
