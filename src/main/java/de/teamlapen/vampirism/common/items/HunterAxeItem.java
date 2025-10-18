package de.teamlapen.vampirism.common.items;

import de.teamlapen.lib.common.items.BaseDisplayItemGenerator;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.items.component.FactionRestriction;
import de.teamlapen.vampirism.common.items.component.FactionSlayer;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class HunterAxeItem extends VampirismSwordItem implements IItemWithTier, BaseDisplayItemGenerator.CreativeTabItemProvider {

    public static final ToolMaterial NORMAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 3.5f, 6.0F, 14, Tags.Items.INGOTS_IRON);
    public static final ToolMaterial ENHANCED = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 3.4f, 7.0F, 14, Tags.Items.GEMS_DIAMOND);
    public static final ToolMaterial ULTIMATE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 3.3f, 8.0F, 14, Tags.Items.INGOTS_NETHERITE);

    private final Tier tier;

    public HunterAxeItem(ToolMaterial material, Tier tier, Properties properties) {
        super(material, 3, -2.9f, FactionRestriction.builder(ModFactionTags.IS_HUNTER).minLevel(getMinLevel(tier)).apply(properties).component(ModDataComponents.FACTION_SLAYER, FactionSlayer.create(ModFactionTags.IS_VAMPIRE, getVampireMult(tier))).component(ModDataComponents.DROP_VAMPIRE_SOUL, Unit.INSTANCE));
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        addTierInformation(tooltip);
        tooltip.add(Component.translatable("text.vampirism.deals_more_damage_to", Math.round((getVampireMult(tier) - 1) * 100), ModFactions.VAMPIRE.value().getNamePlural()).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
        output.accept(getEnchantedStack(enchantments));
    }

    /**
     * @return An {@link ItemStack} with the correct knockback enchantment applied
     */
    public ItemStack getEnchantedStack(HolderLookup.RegistryLookup<Enchantment> enchantments) {
        ItemStack stack = new ItemStack(this);
        stack.enchant(enchantments.getOrThrow(Enchantments.KNOCKBACK), getKnockback());
        return stack;
    }

    @Override
    public Tier getVampirismTier() {
        return tier;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    private int getKnockback() {
        return switch (tier) {
            case ULTIMATE -> 4;
            case ENHANCED -> 3;
            default -> 2;
        };
    }

    private static int getMinLevel(Tier tier) {
        return switch (tier) {
            case ULTIMATE -> 8;
            case ENHANCED -> 6;
            default -> 4;
        };
    }

    private static float getVampireMult(Tier tier) {
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
