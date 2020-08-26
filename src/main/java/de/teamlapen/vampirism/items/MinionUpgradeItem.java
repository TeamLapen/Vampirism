package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;


public class MinionUpgradeItem extends VampirismItem {
    private final int minLevel;
    private final int maxLevel;

    public MinionUpgradeItem(String regName, int minLevel, int maxLevel, IFaction<?> faction) {
        super(regName, new Item.Properties().group(VampirismMod.creativeTab));
        this.faction = faction;
        this.maxLevel = maxLevel;
        this.minLevel = minLevel;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("item.vampirism.minion_upgrade_item.desc").applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("text.vampirism.for_to_levels", minLevel + 1, maxLevel + 1).applyTextStyle(TextFormatting.GRAY));
    }

    private final IFaction<?> faction;

    public int getMaxLevel() {
        return maxLevel;
    }

    public IFaction<?> getFaction() {
        return faction;
    }

    public int getMinLevel() {
        return minLevel;
    }
}
