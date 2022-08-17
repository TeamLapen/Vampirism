package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * A set of arrows can be loaded into this crossbow.
 * It stores the current loaded arrow count in it's nbt->"arrows". -1 stands for infinite
 */
public class TechCrossbowItem extends SimpleCrossbowItem {

    public static final int MAX_ARROW_COUNT = 12;

    /**
     * @return The loaded arrows or -1 if infinite
     */
    private static int getArrowsLeft(@NotNull ItemStack bowStack) {
        CompoundTag nbt = bowStack.getTag();
        if (nbt == null || !nbt.contains("arrows")) return 0;
        return nbt.getInt("arrows");

    }

    /**
     * @param arrows The loaded arrows
     * @return The same bow stack
     */
    private static @NotNull ItemStack setArrowsLeft(@NotNull ItemStack bowStack, int arrows) {
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
    private static boolean reduceArrowCount(@NotNull ItemStack bowStack, @NotNull RandomSource rnd) {
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
    public static @NotNull ItemStack getLoadedItemStack(TechCrossbowItem crossbow) {
        return setArrowsLeft(new ItemStack(crossbow, 1), MAX_ARROW_COUNT);
    }

    /**
     * Returns an itemstack of a fully unloaded crossbow of the given type
     */
    public static @NotNull ItemStack getUnLoadedItemStack(TechCrossbowItem crossbow) {
        return setArrowsLeft(new ItemStack(crossbow, 1), 0);
    }


    public TechCrossbowItem(float speed, int cooldown, int maxDamage, Tiers material) {
        super(speed, cooldown, maxDamage, material);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int arrows = getArrowsLeft(stack);
        if (arrows == -1) {
            tooltip.add(Component.translatable(Enchantments.INFINITY_ARROWS.getDescriptionId()).withStyle(ChatFormatting.DARK_GRAY));
        } else if (arrows == 0) {
            tooltip.add(Component.translatable("text.vampirism.crossbow.not_loaded").withStyle(ChatFormatting.DARK_GRAY));

        } else {
            tooltip.add(Component.translatable("text.vampirism.crossbow.loaded_arrow_count", arrows).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        if (this.allowedIn(group)) {
            items.add(setArrowsLeft(new ItemStack(this), 0));
            items.add(setArrowsLeft(new ItemStack(this), MAX_ARROW_COUNT));
        }
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        return repair.is(Tags.Items.INGOTS_IRON);
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@NotNull ItemStack stack) {
        return HunterSkills.TECH_WEAPONS.get();
    }

    @NotNull
    @Override
    protected ItemStack findAmmo(@NotNull Player player, @NotNull ItemStack bowStack) {
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
        return new ItemStack(ModItems.CROSSBOW_ARROW_NORMAL.get());
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
    protected boolean shouldConsumeArrow(RandomSource rnd, ItemStack arrowStack, boolean playerCreative, boolean bowInfinite, int frugal) {
        return false;
    }

    private int getReloadCooldown(Player player, ItemStack bowStack) {
        return 100;
    }

    private boolean isArrowPackage(@NotNull ItemStack stack) {
        return ModItems.TECH_CROSSBOW_AMMO_PACKAGE.get() == stack.getItem();
    }


}
