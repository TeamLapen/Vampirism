package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;


public class MinionUpgradeItem extends VampirismItem {
    private final int level;
    private final IFaction<?> faction;

    public MinionUpgradeItem(int level, String regNameBase, IFaction<?> faction) {
        super(regNameBase + level, new Item.Properties().group(VampirismMod.creativeTab));
        this.level = level;
        this.setTranslation_key(regNameBase);
        this.faction = faction;
    }

    public IFaction<?> getFaction() {
        return faction;
    }


    public int getLevel() {
        return this.level;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("text.vampirism.for_up_to_level").appendSibling(new StringTextComponent(": " + (this.level + 1))));
    }
}
