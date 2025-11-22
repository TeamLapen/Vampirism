package de.teamlapen.factions.common;

import de.teamlapen.factions.api.items.IRefinementItem;
import de.teamlapen.factions.api.refinements.IRefinement;
import de.teamlapen.factions.api.refinements.IRefinementSet;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.util.random.Weighted;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class RefinementSet implements IRefinementSet {

    private final Set<Holder<IRefinement>> refinements;
    private final Rarity rarity;
    private final int color;
    private final Weighted<IRefinementSet> weightedRandom;
    @Nullable
    private String descriptionId;
    @Nullable
    private IRefinementItem.AccessorySlotType restrictedType;

    public RefinementSet(Rarity rarity, int color, Set<Holder<IRefinement>> refinements) {
        this.refinements = refinements;
        this.rarity = rarity;
        this.weightedRandom = new Weighted<>(this, this.rarity.weight);
        this.color = color;
    }

    @SafeVarargs
    public RefinementSet(Rarity rarity, int color, Holder<IRefinement>... refinements) {
        this(rarity, color, new LinkedHashSet<>(List.of(refinements)));
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("refinement_set", RegUtil.id(this));
        }
        return this.descriptionId;
    }

    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    @Override
    public Set<Holder<IRefinement>> getRefinements() {
        return this.refinements;
    }

    @Override
    public Optional<IRefinementItem.AccessorySlotType> getSlotType() {
        return Optional.ofNullable(restrictedType);
    }

    public Weighted<IRefinementSet> getWeightedRandom() {
        return weightedRandom;
    }

    /**
     * Specify the one and only accessory type this refinement can be put on
     */
    public RefinementSet onlyFor(IRefinementItem.AccessorySlotType restrictedType) {
        this.restrictedType = restrictedType;
        return this;
    }

}
