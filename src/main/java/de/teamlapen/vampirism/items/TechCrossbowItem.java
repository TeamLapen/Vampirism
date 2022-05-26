package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

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
        CompoundTag nbt = bowStack.getTag();
        if (nbt == null || !nbt.contains("arrows")) return 0;
        return nbt.getInt("arrows");

    }

    /**
     * @param arrows The loaded arrows
     * @return The same bow stack
     */
    private static ItemStack setArrowsLeft(@Nonnull ItemStack bowStack, int arrows) {
        int i = Math.max(-1, Math.min(MAX_ARROW_COUNT, arrows));
        CompoundTag nbt = bowStack.hasTag() ? bowStack.getTag() : new CompoundTag();
        nbt.putInt("arrows", i);
        bowStack.setTag(nbt);
        return bowStack;
    }

    /**
     * Reduce the arrows by one
     *
     * @return If there was an arrow
     */
    private static boolean reduceArrowCount(@Nonnull ItemStack bowStack, Random rnd) {
        CompoundTag nbt = bowStack.getTag();
        if (nbt == null || !nbt.contains("arrows")) return false;
        int count = nbt.getInt("arrows");
        if (count == -1) return true;
        if (count == 0) return false;
        int frugal = isCrossbowFrugal(bowStack);
        if (frugal > 0 && rnd.nextInt(Math.max(2, 4 - frugal)) == 0) return true;
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


    public TechCrossbowItem(float speed, int cooldown, int maxDamage, Tiers material) {
        super(speed, cooldown, maxDamage, material);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int arrows = getArrowsLeft(stack);
        if (arrows == -1) {
            tooltip.add(new TranslatableComponent(Enchantments.INFINITY_ARROWS.getDescriptionId()).withStyle(ChatFormatting.DARK_GRAY));
        } else if (arrows == 0) {
            tooltip.add(new TranslatableComponent("text.vampirism.crossbow.not_loaded").withStyle(ChatFormatting.DARK_GRAY));

        } else {
            tooltip.add(new TranslatableComponent("text.vampirism.crossbow.loaded_arrow_count", arrows).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(setArrowsLeft(new ItemStack(this), 0));
            items.add(setArrowsLeft(new ItemStack(this), MAX_ARROW_COUNT));
        }
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, ItemStack repair) {
        return repair.is(Tags.Items.INGOTS_IRON);
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.tech_weapons.get();
    }

    @Nonnull
    @Override
    protected ItemStack findAmmo(Player player, ItemStack bowStack) {
        boolean arrow = reduceArrowCount(bowStack, player.getRandom());
        if (!arrow) {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = player.getInventory().getItem(i);

                if (!itemstack.isEmpty() && this.isArrowPackage(itemstack)) {
                    setArrowsLeft(bowStack, MAX_ARROW_COUNT);
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().removeItem(i, 1);
                    }
                    player.getCooldowns().addCooldown(bowStack.getItem(), getReloadCooldown(player, bowStack));
                }
            }
            return ItemStack.EMPTY;
        }
        return new ItemStack(ModItems.crossbow_arrow_normal.get());
    }

    @Override
    protected float getArrowVelocity() {
        return 1.7F;
    }

    @Override
    protected int getCooldown(Player player, ItemStack stack) {
        return 8;
    }

    @Override
    protected boolean isCrossbowInfinite(ItemStack stack, Player player) {
        return false;
    }

    @Override
    protected boolean shouldConsumeArrow(Random rnd, ItemStack arrowStack, boolean playerCreative, boolean bowInfinite, int frugal) {
        return false;
    }

    private int getReloadCooldown(Player player, ItemStack bowStack) {
        return 100;
    }

    private boolean isArrowPackage(@Nonnull ItemStack stack) {
        return ModItems.tech_crossbow_ammo_package.get().equals(stack.getItem());
    }


}
