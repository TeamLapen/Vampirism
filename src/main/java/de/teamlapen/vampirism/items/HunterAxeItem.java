package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import de.teamlapen.vampirism.items.component.FactionSlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HunterAxeItem extends VampirismSwordItem implements IItemWithTier, ModDisplayItemGenerator.CreativeTabItemProvider {

    public static final ToolMaterial NORMAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 3.5f, 6.0F, 14, Tags.Items.INGOTS_IRON);
    public static final ToolMaterial ENHANCED = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 3.4f, 7.0F, 14, Tags.Items.GEMS_DIAMOND);
    public static final ToolMaterial ULTIMATE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 3.3f, 8.0F, 14, Tags.Items.INGOTS_NETHERITE);

    private final TIER tier;

    public HunterAxeItem(ToolMaterial material, TIER tier, Properties properties) {
        super(material, 3, -2.9f, FactionRestriction.builder(ModFactionTags.IS_HUNTER).minLevel(getMinLevel(tier)).apply(properties).component(ModDataComponents.FACTION_SLAYER, FactionSlayer.create(ModFactionTags.IS_VAMPIRE, getVampireMult(tier))).component(ModDataComponents.DROP_VAMPIRE_SOUL, Unit.INSTANCE));
        this.tier = tier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        tooltip.add(Component.translatable("text.vampirism.deals_more_damage_to", Math.round((getVampireMult(tier) - 1) * 100), ModFactions.VAMPIRE.value().getNamePlural()).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
        output.accept(getEnchantedStack(enchantments));
    }

    /**
     * @return An {@link ItemStack} with the correct knockback enchantment applied
     */
    public @NotNull ItemStack getEnchantedStack(HolderLookup.RegistryLookup<Enchantment> enchantments) {
        ItemStack stack = new ItemStack(this);
        stack.enchant(enchantments.getOrThrow(Enchantments.KNOCKBACK), getKnockback());
        return stack;
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

    private static int getMinLevel(TIER tier) {
        return switch (tier) {
            case ULTIMATE -> 8;
            case ENHANCED -> 6;
            default -> 4;
        };
    }

    private static float getVampireMult(TIER tier) {
        return switch (tier) {
            case ULTIMATE -> 1.4F;
            case ENHANCED -> 1.3F;
            default -> 1.2F;
        };
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }
}
