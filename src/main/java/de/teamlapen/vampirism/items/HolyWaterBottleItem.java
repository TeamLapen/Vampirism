package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.ItemPropertiesExtension;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * HolyWaterBottle
 * Exists in different tiers and as splash versions.
 */
public class HolyWaterBottleItem extends Item implements IItemWithTier {
    private final Tier tier;

    public HolyWaterBottleItem(Tier tier, Properties props) {
        super(FactionRestriction.apply(ModFactionTags.IS_HUNTER, ItemPropertiesExtension.descriptionWithout(props, "_normal|_enhanced|_ultimate")));
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        addTierInformation(tooltip);
    }

    /**
     * Converts the tier of this bottle into the strength of the applied holy water
     */
    public EnumStrength getStrength(Tier tier) {
        return switch (tier) {
            case NORMAL -> EnumStrength.WEAK;
            case ENHANCED -> EnumStrength.MEDIUM;
            case ULTIMATE -> EnumStrength.STRONG;
        };
    }

    @Override
    public Tier getVampirismTier() {
        return tier;
    }

}
