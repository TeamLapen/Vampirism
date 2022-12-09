package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.ToolMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HunterAxeItem extends VampirismHunterWeaponItem implements IItemWithTier, CreativeModeTab.DisplayItemsGenerator {

    public static final ToolMaterial.Tiered NORMAL = new ToolMaterial.Tiered(TIER.NORMAL,2, 250, 3.6f, 7.0F, 14, () -> Ingredient.of(Tags.Items.INGOTS_IRON));
    public static final ToolMaterial.Tiered ENHANCED = new ToolMaterial.Tiered(TIER.ENHANCED,2, 1561, 3.6f, 7.0F, 14, () -> Ingredient.of(Tags.Items.GEMS_DIAMOND));
    public static final ToolMaterial.Tiered ULTIMATE = new ToolMaterial.Tiered(TIER.ULTIMATE,2, 2031, 3.6f, 7.0F, 14, () -> Ingredient.of(Tags.Items.INGOTS_NETHERITE));

    private final TIER tier;

    public HunterAxeItem(ToolMaterial.Tiered material) {
        super(material, 3, -2.9f, new Properties());
        this.tier = material.getTier();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        tooltip.add(Component.translatable("text.vampirism.deals_more_damage_to", Math.round((getVampireMult() - 1) * 100), VReference.VAMPIRE_FACTION.getNamePlural()).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void accept(FeatureFlagSet featureFlagSet, CreativeModeTab.Output output, boolean hasPermission) {
        output.accept(getDefaultInstance());
        output.accept(getEnchantedStack());
    }

    @Override
    public float getDamageMultiplierForFaction(@NotNull ItemStack stack) {
        return getVampireMult();
    }

    /**
     * @return An {@link ItemStack} with the correct knockback enchantment applied
     */
    public @NotNull ItemStack getEnchantedStack() {
        ItemStack stack = new ItemStack(this);
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantments.KNOCKBACK, getKnockback());
        EnchantmentHelper.setEnchantments(map, stack);
        return stack;
    }

    @Override
    public int getMinLevel(@NotNull ItemStack stack) {
        return getMinLevel();
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return false;
    }

    private int getKnockback() {
        return switch (tier) {
            case ULTIMATE -> 4;
            case ENHANCED -> 3;
            default -> 2;
        };
    }

    private int getMinLevel() {
        return switch (tier) {
            case ULTIMATE -> 8;
            case ENHANCED -> 6;
            default -> 4;
        };
    }

    private float getVampireMult() {
        return switch (tier) {
            case ULTIMATE -> 1.3F;
            case ENHANCED -> 1.3F;
            default -> 1.2F;
        };
    }


}
