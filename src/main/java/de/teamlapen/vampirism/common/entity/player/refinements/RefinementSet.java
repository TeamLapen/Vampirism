package de.teamlapen.vampirism.common.entity.player.refinements;

import de.teamlapen.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.Weighted;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public abstract class RefinementSet implements IRefinementSet {

    private final Set<Holder<IRefinement>> refinements;
    private final Rarity rarity;
    private final int color;
    private final @NotNull Weighted<IRefinementSet> weightedRandom;
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
        this(rarity, color, UtilLib.newSortedSet(refinements));
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

    @NotNull
    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    @NotNull
    @Override
    public Set<Holder<IRefinement>> getRefinements() {
        return this.refinements;
    }

    @Override
    public @NotNull Optional<IRefinementItem.AccessorySlotType> getSlotType() {
        return Optional.ofNullable(restrictedType);
    }

    public @NotNull Weighted<IRefinementSet> getWeightedRandom() {
        return weightedRandom;
    }

    /**
     * Specify the one and only accessory type this refinement can be put on
     */
    public @NotNull RefinementSet onlyFor(IRefinementItem.AccessorySlotType restrictedType) {
        this.restrictedType = restrictedType;
        return this;
    }

    public static class VampireRefinementSet extends RefinementSet {
        public VampireRefinementSet(Rarity rarity, int color, Set<Holder<IRefinement>> refinements) {
            super(rarity, color, refinements);
        }

        @SafeVarargs
        public VampireRefinementSet(Rarity rarity, int color, Holder<IRefinement>... refinements) {
            super(rarity, color, refinements);
        }

        @NotNull
        @Override
        public TagKey<IFaction<?>> getFaction() {
            return ModFactionTags.IS_VAMPIRE;
        }
    }
}
