package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class MinionUpgradeItem extends Item {
    private final int minLevel;
    private final int maxLevel;
    private final IFaction<?> faction;

    public MinionUpgradeItem(int minLevel, int maxLevel, IFaction<?> faction) {
        super(new Item.Properties());
        this.faction = faction;
        this.maxLevel = maxLevel;
        this.minLevel = minLevel;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Component.translatable("item.vampirism.minion_upgrade_item.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("text.vampirism.for_to_levels", minLevel + 1, maxLevel + 1).withStyle(ChatFormatting.GRAY));
    }

    public IFaction<?> getFaction() {
        return faction;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMinLevel() {
        return minLevel;
    }
}
