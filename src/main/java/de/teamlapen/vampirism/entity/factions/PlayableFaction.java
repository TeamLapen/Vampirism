package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.api.entity.factions.LordTitles;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.Function;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer<?>> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final int highestLordLevel;
    private final NonNullSupplier<Capability<T>> playerCapabilitySupplier;
    private final LordTitles lordTitles;
    private final Function<IRefinementItem.AccessorySlotType, IRefinementItem> refinementItemBySlot;
    private boolean renderLevel = true;
    private boolean hasLordSkills;

    PlayableFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel, int highestLordLevel, @Nonnull LordTitles lordTitles, @Nonnull IVillageFactionData villageFactionData, @Nullable Function<IRefinementItem.AccessorySlotType, IRefinementItem> refinementItemBySlot, TextFormatting chatColor, ITextComponent name, ITextComponent namePlural, boolean hasLordSkills) {
        super(id, entityInterface, color, hostileTowardsNeutral, villageFactionData, chatColor, name, namePlural);
        this.highestLevel = highestLevel;
        this.playerCapabilitySupplier = playerCapabilitySupplier;
        this.highestLordLevel = highestLordLevel;
        this.lordTitles = lordTitles;
        this.refinementItemBySlot = refinementItemBySlot;
        this.hasLordSkills = hasLordSkills;
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

    @Override
    public boolean hasLordSkills() {
        return this.hasLordSkills;
    }

    @Nonnull
    @Override
    public ITextComponent getLordTitle(int level, boolean female) {
        assert level <= highestLordLevel;
        return lordTitles.apply(level, female);
    }

    @Override
    public boolean hasGenderTitles() {
        return this.lordTitles.areGenderNeutral();
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
    public boolean hasRefinements() {
        return this.refinementItemBySlot != null;
    }

    @Override
    public <Z extends Item & IRefinementItem> Z getRefinementItem(IRefinementItem.AccessorySlotType type) {
        assert this.refinementItemBySlot != null;
        //noinspection unchecked
        return ((Z) this.refinementItemBySlot.apply(type));
    }

    @Override
    public String toString() {
        return "PlayableFaction{" +
                "id='" + id + '\'' +
                '}';
    }
}
