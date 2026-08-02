package de.teamlapen.vampirism.api.world.items;

import de.teamlapen.faction.api.factions.skills.ISkill;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public interface IBlessableItem extends ItemLike {

    @Nullable
    Item getBlessedItem();

    @Nullable
    Holder<ISkill<?>> requiredSkill();

    default boolean canBeBlessed() {
        return getBlessedItem() != null;
    }
}
