package de.teamlapen.vampirism.common.integration.jei.recipes;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.api.world.items.IBlessableItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record BlessableRecipe(@NonNull IBlessableItem item, @NonNull Item output, @Nullable Holder<? extends ISkill<?>> requiredSkill) {
}
