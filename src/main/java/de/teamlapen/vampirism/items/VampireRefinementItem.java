package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

import de.teamlapen.vampirism.api.items.IRefinementItem.AccessorySlotType;
import net.minecraft.world.item.Item.Properties;

public class VampireRefinementItem extends RefinementItem {

    public VampireRefinementItem(Properties properties, AccessorySlotType type) {
        super(properties, type);
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction(@Nonnull ItemStack stack) {
        return VReference.VAMPIRE_FACTION;
    }

    public static RefinementItem getItemForType(AccessorySlotType type) {
        return switch (type) {
            case AMULET -> ModItems.AMULET.get();
            case RING -> ModItems.RING.get();
            default -> ModItems.OBI_BELT.get();
        };
    }
}
