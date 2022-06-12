package de.teamlapen.vampirism.player.refinements;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RefinementSet extends ForgeRegistryEntry<IRefinementSet> implements IRefinementSet {

    private final Set<RegistryObject<? extends IRefinement>> refinements;
    private final Rarity rarity;
    private final int color;
    private final WeightedRandomItem<IRefinementSet> weightedRandom;
    private ITextComponent name;
    private ITextComponent desc;
    @Nullable
    private IRefinementItem.AccessorySlotType restrictedType;

    public RefinementSet(Rarity rarity, int color, Set<RegistryObject<? extends IRefinement>> refinements) {
        this.refinements = refinements;
        this.rarity = rarity;
        this.weightedRandom = new WeightedRandomItem<>(this, this.rarity.weight);
        this.color = color;
    }

    @SafeVarargs
    public RefinementSet(Rarity rarity, int color, RegistryObject<? extends IRefinement>... refinements) {
        this(rarity, color, UtilLib.newSortedSet(refinements));
    }

    @Override
    public int getColor() {
        return color;
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        return this.name != null ? this.name : (this.name = new TranslationTextComponent("refinement_set." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()));
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    @Nonnull
    @Override
    public Set<IRefinement> getRefinements() {
        return this.refinements.stream().map(RegistryObject::get).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<RegistryObject<? extends IRefinement>> getRefinementRegistryObjects() {
        return this.refinements;
    }

    @Override
    public Optional<IRefinementItem.AccessorySlotType> getSlotType() {
        return Optional.ofNullable(restrictedType);
    }

    public WeightedRandomItem<IRefinementSet> getWeightedRandom() {
        return weightedRandom;
    }

    /**
     * Specify the one and only accessory type this refinement can be put on
     */
    public RefinementSet onlyFor(IRefinementItem.AccessorySlotType restrictedType) {
        this.restrictedType = restrictedType;
        return this;
    }

    public static class VampireRefinementSet extends RefinementSet {
        public VampireRefinementSet(Rarity rarity, int color, Set<RegistryObject<? extends IRefinement>> refinements) {
            super(rarity, color, refinements);
        }

        @SafeVarargs
        public VampireRefinementSet(Rarity rarity, int color, RegistryObject<? extends IRefinement>... refinements) {
            super(rarity, color, refinements);
        }

        @Nonnull
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    }
}
