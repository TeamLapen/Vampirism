package de.teamlapen.vampirism.player.refinements;

import com.google.common.collect.Sets;
import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public abstract class RefinementSet extends ForgeRegistryEntry<IRefinementSet> implements IRefinementSet {

    private final Set<IRefinement> refinements;
    private final Rarity rarity;
    private final int color;
    private ITextComponent name;
    private ITextComponent desc;
    private final WeightedRandomItem<IRefinementSet> weightedRandom;
    @Nullable
    private IRefinementItem.AccessorySlotType restrictedType;

    public RefinementSet(Rarity rarity, int color, Set<IRefinement> refinements) {
        this.refinements = refinements;
        this.rarity = rarity;
        this.weightedRandom = new WeightedRandomItem<>(this, this.rarity.weight);
        this.color = color;
    }

    public RefinementSet(Rarity rarity, int color, IRefinement... refinements) {
        this(rarity, color, Sets.newHashSet(refinements));
    }

    /**
     * Specify the one and only accessory type this refinement can be put on
     */
    public RefinementSet onlyFor(IRefinementItem.AccessorySlotType restrictedType){
        this.restrictedType=restrictedType;
        return this;
    }

    @Nonnull
    @Override
    public Set<IRefinement> getRefinements() {
        return this.refinements;
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        return this.name != null? this.name: (this.name= new TranslationTextComponent("refinement_set." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()));
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    public WeightedRandomItem<IRefinementSet> getWeightedRandom() {
        return weightedRandom;
    }

    @Override
    public Optional<IRefinementItem.AccessorySlotType> getSlotType() {
        return Optional.ofNullable(restrictedType);
    }

    @Override
    public int getColor() {
        return color;
    }

    public static class VampireRefinementSet extends RefinementSet {
        public VampireRefinementSet(Rarity rarity, int color, Set<IRefinement> refinements) {
            super(rarity,color, refinements);
        }

        public VampireRefinementSet(Rarity rarity, int color, IRefinement... refinements) {
            super(rarity,color, refinements);
        }

        @Nonnull
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    }
}
