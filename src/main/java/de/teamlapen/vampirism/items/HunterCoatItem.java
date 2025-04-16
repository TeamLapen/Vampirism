package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HunterCoatItem extends HunterArmorItem implements IItemWithTier {

    /**
     * Consider using cached value instead {@link HunterPlayerSpecialAttribute#fullHunterCoat}
     * Checks if the player has this armor fully equipped
     *
     * @return if fully equipped the tier of the worst item, otherwise null
     */
    @Nullable
    public static TIER isFullyEquipped(@NotNull Player player) {
        int minLevel = 1000;
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.isEmpty() || !(stack.getItem() instanceof HunterCoatItem)) {
                return null;
            } else {
                minLevel = Math.min(minLevel, ((HunterCoatItem) stack.getItem()).getVampirismTier().ordinal());
            }
        }
        return TIER.values()[minLevel];
    }

    private final @NotNull TIER tier;

    public HunterCoatItem(@NotNull ArmorMaterial material, @NotNull ArmorType type, @NotNull TIER tier, Properties properties) {
        super(material, type, properties);
        this.tier = tier;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
