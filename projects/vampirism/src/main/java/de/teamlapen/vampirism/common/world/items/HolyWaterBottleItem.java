package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * HolyWaterBottle
 * Exists in different tiers and as splash versions.
 */
public class HolyWaterBottleItem extends Item implements IItemWithTier {
    private final Tier tier;

    public HolyWaterBottleItem(Tier tier, Properties props) {
        super(props.factions$restrictFaction(ModFactionTags.IS_HUNTER).factions$descriptionWithout("_normal|_enhanced|_ultimate"));
        this.tier = tier;
    }

    /**
     * Handles the logic for using a Holy Water bottle on a block. Should be put into the block's useItemOn method.
     *
     * @param onUse What happens after the holy water is applied. Usually, the transformation of the block, for example, cursed earth becoming normal dirt.
     * @return InteractionResult.TRY_WITH_EMPTY_HAND if the interaction is unsuccessful, or it wasn't holy water that player was using. InteractionResult.SUCCESS if it applied holy water successfully.
     */
    public static InteractionResult onHolyWaterUsedOnBlock(ItemStack stack, Player player, Runnable onUse) {
        Item heldItem = stack.getItem();
        if (heldItem instanceof HolyWaterBottleItem && !(heldItem instanceof HolyWaterSplashBottleItem)) {
            int uses = heldItem == ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get() ? 100 : (heldItem == ModItems.HOLY_WATER_BOTTLE_ENHANCED.get() ? 50 : 25);
            if (!player.getAbilities().instabuild && player.getRandom().nextInt(uses) == 0) {
                stack.shrink(1);
            }
            onUse.run();

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
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
