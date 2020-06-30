package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * HolyWaterBottle
 * Exists in different tiers and as splash versions.
 */
public class HolyWaterBottleItem extends VampirismItem implements IItemWithTier, IFactionExclusiveItem {

    public static final String regName = "holy_water_bottle";
    private final TIER tier;

    public HolyWaterBottleItem(TIER tier) {
        this(regName + "_" + tier.getName(), tier, new Properties().group(VampirismMod.creativeTab));
        setTranslation_key(regName);
    }

    protected HolyWaterBottleItem(String regName, TIER tier, Properties props) {
        super(regName, props);
        this.tier = tier;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
    }

    @Override
    public String getBaseRegName() {
        return regName;
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    /**
     * Converts the tier of this bottle into the strength of the applied holy water
     *
     * @param tier
     * @return
     */
    public EnumStrength getStrength(TIER tier) {
        switch (tier) {
            case NORMAL:
                return EnumStrength.WEAK;
            case ENHANCED:
                return EnumStrength.MEDIUM;
            case ULTIMATE:
                return EnumStrength.STRONG;
        }
        return EnumStrength.NONE;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }


}
