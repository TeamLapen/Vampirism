package de.teamlapen.vampirism.player.refinements;

import com.google.common.collect.Sets;
import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.item.Rarity;
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
        this.weightedRandom = createWeightedRandom();
        this.color = color;
    }

    public RefinementSet(Rarity rarity, int color, IRefinement... refinements) {
        this.refinements = Sets.newHashSet(refinements);
        this.rarity = rarity;
        this.weightedRandom = createWeightedRandom();
        this.color = color;
    }

    /**
     * Specify the one and only accessory type this refinement can be put on
     */
    public void onlyFor(IRefinementItem.AccessorySlotType restrictedType){
        this.restrictedType=restrictedType;
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
    public ITextComponent getDescription() {
        return this.desc != null? this.desc: (this.desc= new TranslationTextComponent("refinement_set." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()+".desc"));
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

    private WeightedRandomItem<IRefinementSet> createWeightedRandom() {
        int value;
        switch (this.rarity) {
            case UNCOMMON:
                value=4;
                break;
            case COMMON:
                value=2;
                break;
            case RARE:
                value=1;
                break;
            default:
                value=0;
                break;
        }
        return new WeightedRandomItem<>(this, value);
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
