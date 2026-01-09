package de.teamlapen.faction.common.world.items.consume;

import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.ConsumableListener;

public interface FactionConsumableListener extends ConsumableListener {

    boolean isCorrectFaction(Holder<? extends IFaction<?>> entityFaction);

    TagKey<IFaction<?>> getTargetFaction();
}
