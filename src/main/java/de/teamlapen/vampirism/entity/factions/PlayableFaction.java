package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer<?>> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final int highestLordLevel;
    private final NonNullSupplier<Capability<T>> playerCapabilitySupplier;
    private final BiFunction<Integer, Boolean, ITextComponent> lordTitleFunction;
    private final Function<IRefinementItem.AccessorySlotType, IRefinementItem> accessoryBySlotFunction;
    private boolean renderLevel = true;

    PlayableFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel, int highestLordLevel, @Nonnull BiFunction<Integer, Boolean, ITextComponent> lordTitleFunction, @Nonnull IVillageFactionData villageFactionData, @Nullable Function<IRefinementItem.AccessorySlotType, IRefinementItem> accessoryBySlotFunction) {
        super(id, entityInterface, color, hostileTowardsNeutral, villageFactionData);
        this.highestLevel = highestLevel;
        this.playerCapabilitySupplier = playerCapabilitySupplier;
        this.highestLordLevel = highestLordLevel;
        this.lordTitleFunction = lordTitleFunction;
        this.accessoryBySlotFunction = accessoryBySlotFunction;
    }

    @Override
    public Class<T> getFactionPlayerInterface() {
        return super.getFactionEntityInterface();
    }

    @Override
    public int getHighestLordLevel() {
        return highestLordLevel;
    }

    @Override
    public int getHighestReachableLevel() {
        return highestLevel;
    }

    @Nonnull
    @Override
    public ITextComponent getLordTitle(int level, boolean female) {
        assert level <= highestLordLevel;
        return lordTitleFunction.apply(level, female);
    }

    @Override
    public LazyOptional<T> getPlayerCapability(PlayerEntity player) {
        return player.getCapability(playerCapabilitySupplier.get(), null);
    }

    @Override
    public boolean renderLevel() {
        return renderLevel;
    }

    @Override
    public PlayableFaction<T> setRenderLevel(boolean render) {
        renderLevel = render;
        return this;
    }

    @Override
    public boolean hasAccessories() {
        return this.accessoryBySlotFunction != null;
    }

    @Override
    public <Z extends Item & IRefinementItem> Z  getAccessoryItem(IRefinementItem.AccessorySlotType type) {
        assert this.accessoryBySlotFunction != null;
        //noinspection unchecked
        return ((Z) this.accessoryBySlotFunction.apply(type));
    }

    @Override
    public String toString() {
        return "PlayableFaction{" +
                "id='" + id + '\'' +
                '}';
    }
}
