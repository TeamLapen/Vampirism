package de.teamlapen.faction.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.util.random.Weighted;

import java.util.List;

/**
 * Data driven implementation of {@link IRefinementSet}.
 * <p>
 * Faction, rarity and slot restrictions are defined through tags, see {@link de.teamlapen.faction.api.tags.FactionRefinementSetTags}
 */
public record RefinementSet(Component name, int color, HolderSet<IRefinement> refinements) implements IRefinementSet {

    public static final Codec<RefinementSet> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(RefinementSet::name),
            Codec.INT.fieldOf("color").forGetter(RefinementSet::color),
            RegistryCodecs.homogeneousList(FactionRegistries.Keys.REFINEMENT).fieldOf("refinements").forGetter(RefinementSet::refinements)
    ).apply(inst, RefinementSet::new));

    public static final Codec<IRefinementSet> DIRECT_CODEC = CODEC.xmap(r -> r, r -> (RefinementSet) r);

    public static Weighted<Holder<IRefinementSet>> weighted(Holder<IRefinementSet> set) {
        return new Weighted<>(set, IRefinementSet.getRarity(set).weight);
    }

    public static String descriptionId(ResourceKey<IRefinementSet> key) {
        return Util.makeDescriptionId("refinement_set", key.identifier());
    }

    /**
     * Creates a refinement set with a translatable name derived from the key
     */
    public static RefinementSet of(ResourceKey<IRefinementSet> key, int color, List<? extends Holder<IRefinement>> refinements) {
        return new RefinementSet(Component.translatable(descriptionId(key)), color, HolderSet.direct(refinements));
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public Component getName() {
        return this.name;
    }

    @Override
    public HolderSet<IRefinement> getRefinements() {
        return this.refinements;
    }
}
