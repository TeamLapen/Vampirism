package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VampireRefinementItem extends RefinementItem {

    public VampireRefinementItem(@NotNull Properties properties, AccessorySlotType type) {
        super(properties, type);
    }

    @NotNull
    @Override
    public IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.VAMPIRE_FACTION;
    }

    public static @NotNull RefinementItem getItemForType(@NotNull AccessorySlotType type) {
        return switch (type) {
            case AMULET -> ModItems.AMULET.get();
            case RING -> ModItems.RING.get();
            default -> ModItems.OBI_BELT.get();
        };
    }
}
