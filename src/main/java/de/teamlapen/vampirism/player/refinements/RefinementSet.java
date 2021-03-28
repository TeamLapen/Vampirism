package de.teamlapen.vampirism.player.refinements;

import com.google.common.collect.Sets;
import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Set;

public class RefinementSet extends ForgeRegistryEntry<IRefinementSet> implements IRefinementSet {

    private final Set<IRefinement> refinements;
    private final Rarity rarity;
    private TextComponent name;

    public RefinementSet(Rarity rarity, Set<IRefinement> refinements) {
        this.refinements = refinements;
        this.rarity = rarity;
    }

    public RefinementSet(Rarity rarity, IRefinement... refinements) {
        this.refinements = Sets.newHashSet(refinements);
        this.rarity = rarity;
    }

    @Override
    public Set<IRefinement> getRefinements() {
        return this.refinements;
    }

    @Override
    public TextComponent getName() {
        return this.name != null? this.name: (this.name= new TranslationTextComponent(getTranslationKey()));
    }

    @Override
    public String getTranslationKey() {
        return "refinement_set." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();
    }

    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    public WeightedRandomItem<RefinementSet> getRandom() {
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
}
