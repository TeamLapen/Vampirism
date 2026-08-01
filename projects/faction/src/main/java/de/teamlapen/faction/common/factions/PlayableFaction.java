package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.factions.FactionProperties;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordTitleProvider;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import de.teamlapen.faction.api.world.items.RefinementItems;
import de.teamlapen.faction.common.core.FactionDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer<T>> extends Faction<T> implements IPlayableFaction<T> {

    public PlayableFaction(FactionProperties properties) {
        super(properties.withValidator(x -> {
            if (!x.has(FactionDataComponents.PLAYER_CAPABILITY)) {
                throw new IllegalStateException("A playable faction needs a player capability");
            }
        }));
    }

    @Override
    public int getHighestLordLevel() {
        return components().getOrDefault(FactionDataComponents.MAX_LORD_LEVEL, 0);
    }

    @Override
    public int getHighestReachableLevel() {
        return components().getOrDefault(FactionDataComponents.MAX_LEVEL, 1);
    }

    @Override
    public T getPlayerCapability(Player player) {
        var attachmentType = components().get(FactionDataComponents.PLAYER_CAPABILITY);
        assert attachmentType != null;
        //noinspection unchecked
        return player.getData((AttachmentType<T>) attachmentType.value());
    }

    @Override
    public boolean hasRefinements() {
        return components().has(FactionDataComponents.REFINEMENTS);
    }

    @Override
    public <Z extends Item & IRefinementItem> Z getRandomRefinementItem(RandomSource random, IRefinementItem.AccessorySlotType type) {
        RefinementItems orDefault = components().getOrDefault(FactionDataComponents.REFINEMENTS, RefinementItems.EMPTY);
        List<IRefinementItem> iRefinementItems = orDefault.stream(type).toList();
        return ((Z) iRefinementItems.get(random.nextInt(iRefinementItems.size())));
    }

    @Override
    public Collection<IRefinementItem> getRefinementItems() {
        RefinementItems orDefault = components().getOrDefault(FactionDataComponents.REFINEMENTS, RefinementItems.EMPTY);
        return orDefault.stream().toList();
    }

    @Override
    public Collection<IRefinementItem> getRefinementItems(IRefinementItem.AccessorySlotType type) {
        RefinementItems orDefault = components().getOrDefault(FactionDataComponents.REFINEMENTS, RefinementItems.EMPTY);
        return orDefault.stream(type).toList();
    }
}
