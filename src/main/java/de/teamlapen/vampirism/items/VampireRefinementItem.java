package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VampireRefinementItem extends RefinementItem {

    public VampireRefinementItem(@NotNull Properties properties, AccessorySlotType type) {
        super(properties, type);
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> getExclusiveFaction(@NotNull ItemStack stack) {
        return ModFactions.VAMPIRE;
    }

    public static @NotNull RefinementItem getItemForType(@NotNull AccessorySlotType type) {
        return switch (type) {
            case AMULET -> ModItems.AMULET.get();
            case RING -> ModItems.RING.get();
            default -> ModItems.OBI_BELT.get();
        };
    }
}
