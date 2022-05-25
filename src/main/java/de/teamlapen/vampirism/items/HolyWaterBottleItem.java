package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * HolyWaterBottle
 * Exists in different tiers and as splash versions.
 */
public class HolyWaterBottleItem extends VampirismItem implements IItemWithTier, IFactionExclusiveItem {
    private final TIER tier;

    public HolyWaterBottleItem(TIER tier) {
        this(tier, new Properties().tab(VampirismMod.creativeTab));
    }

    protected HolyWaterBottleItem(TIER tier, Properties props) {
        super(props);
        this.tier = tier;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    /**
     * Converts the tier of this bottle into the strength of the applied holy water
     */
    public EnumStrength getStrength(TIER tier) {
        return switch (tier) {
            case NORMAL -> EnumStrength.WEAK;
            case ENHANCED -> EnumStrength.MEDIUM;
            case ULTIMATE -> EnumStrength.STRONG;
        };
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }


}
