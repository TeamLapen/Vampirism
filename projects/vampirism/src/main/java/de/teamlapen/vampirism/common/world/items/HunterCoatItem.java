package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterSkillProperties;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

public class HunterCoatItem extends HunterArmorItem implements IItemWithTier {

    private final Tier tier;

    public HunterCoatItem(ArmorMaterial material, ArmorType type, Tier tier, Holder<ISkill<?>> skill, Properties properties) {
        super(material, type, skill, properties);
        this.tier = tier;
    }

    /**
     * Consider using cached value instead {@link HunterSkillProperties#fullHunterCoat}
     * Checks if the player has this armor fully equipped
     *
     * @return if fully equipped the tier of the worst item, otherwise null
     */
    @Nullable
    public static IItemWithTier.Tier isFullyEquipped(Player player) {
        int minLevel = 1000;
        for (ItemStack stack : Arrays.stream(EquipmentSlot.values()).filter(x -> x.getType() == EquipmentSlot.Type.HUMANOID_ARMOR).map(player::getItemBySlot).toList()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof HunterCoatItem)) {
                return null;
            } else {
                minLevel = Math.min(minLevel, ((HunterCoatItem) stack.getItem()).getVampirismTier().ordinal());
            }
        }
        return Tier.values()[minLevel];
    }

    @Override
    public Tier getVampirismTier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        addTierInformation(tooltip);
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
    }
}
