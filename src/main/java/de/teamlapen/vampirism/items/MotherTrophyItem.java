package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class MotherTrophyItem extends BlockItem {

    public MotherTrophyItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ItemExtensions.MOTHER_TROPHY);
    }
}
