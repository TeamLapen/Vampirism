package de.teamlapen.vampirism.entity.player.refinements;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.random.WeightedEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public abstract class RefinementSet implements IRefinementSet {

    private final Set<Supplier<? extends IRefinement>> refinements;
    private final Rarity rarity;
    private final int color;
    private final @NotNull WeightedEntry.Wrapper<IRefinementSet> weightedRandom;
    private Component name;
    private Component desc;
    @Nullable
    private IRefinementItem.AccessorySlotType restrictedType;

    public RefinementSet(Rarity rarity, int color, Set<Supplier<? extends IRefinement>> refinements) {
        this.refinements = refinements;
        this.rarity = rarity;
        this.weightedRandom = WeightedEntry.wrap(this, this.rarity.weight);
        this.color = color;
    }

    @SafeVarargs
    public RefinementSet(Rarity rarity, int color, Supplier<? extends IRefinement>... refinements) {
        this(rarity, color, UtilLib.newSortedSet(refinements));
    }

    @Override
    public int getColor() {
        return color;
    }

    @NotNull
    @Override
    public Component getName() {
        return this.name != null ? this.name : (this.name = Component.translatable("refinement_set." + RegUtil.id(this).getNamespace() + "." + RegUtil.id(this).getPath()));
    }

    @NotNull
    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    @NotNull
    @Override
    public Set<Supplier<? extends IRefinement>> getRefinements() {
        return this.refinements;
    }

    @Override
    public @NotNull Optional<IRefinementItem.AccessorySlotType> getSlotType() {
        return Optional.ofNullable(restrictedType);
    }

    public WeightedEntry.Wrapper<IRefinementSet> getWeightedRandom() {
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
        public VampireRefinementSet(Rarity rarity, int color, Set<Supplier<? extends IRefinement>> refinements) {
            super(rarity, color, refinements);
        }

        @SafeVarargs
        public VampireRefinementSet(Rarity rarity, int color, Supplier<? extends IRefinement>... refinements) {
            super(rarity, color, refinements);
        }

        @NotNull
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    }
}
