package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class VampireRefinementItem extends RefinementItem {

    public VampireRefinementItem(Properties properties, AccessorySlotType type) {
        super(properties, type);
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab itemGroup, @NotNull NonNullList<ItemStack> items) {
        if (this.allowedIn(itemGroup)) {
            ItemStack stack = new ItemStack(this);
            IRefinementSet set = getRandomRefinementForItem(this.getExclusiveFaction(stack), this);
            if (set != null) {
                this.applyRefinementSet(stack, set);
            }
            items.add(stack);
        }
    }

    @NotNull
    @Override
    public IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
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
