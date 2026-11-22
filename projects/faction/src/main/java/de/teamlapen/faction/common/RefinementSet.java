package de.teamlapen.faction.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.util.random.Weighted;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Data driven implementation of {@link IRefinementSet}
 */
public record RefinementSet(Component name, TagKey<IFaction<?>> faction, Rarity rarity, int color, HolderSet<IRefinement> refinements, Optional<IRefinementItem.AccessorySlotType> slotType) implements IRefinementSet {

    public static final Codec<RefinementSet> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(RefinementSet::name),
            TagKey.codec(FactionRegistries.Keys.FACTION).fieldOf("faction").forGetter(RefinementSet::faction),
            Rarity.CODEC.fieldOf("rarity").forGetter(RefinementSet::rarity),
            Codec.INT.fieldOf("color").forGetter(RefinementSet::color),
            RegistryCodecs.homogeneousList(FactionRegistries.Keys.REFINEMENT).fieldOf("refinements").forGetter(RefinementSet::refinements),
            StringRepresentable.fromEnum(IRefinementItem.AccessorySlotType::values).optionalFieldOf("slot_type").forGetter(RefinementSet::slotType)
    ).apply(inst, RefinementSet::new));

    public static final Codec<IRefinementSet> DIRECT_CODEC = CODEC.xmap(r -> r, r -> (RefinementSet) r);

    public static Weighted<Holder<IRefinementSet>> weighted(Holder<IRefinementSet> set) {
        return new Weighted<>(set, set.value().getRarity().weight);
    }

    public static String descriptionId(ResourceKey<IRefinementSet> key) {
        return Util.makeDescriptionId("refinement_set", key.identifier());
    }

    public static Builder builder(TagKey<IFaction<?>> faction, Rarity rarity, int color) {
        return new Builder(faction, rarity, color);
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public TagKey<IFaction<?>> getFaction() {
        return this.faction;
    }

    @Override
    public Component getName() {
        return this.name;
    }

    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    @Override
    public HolderSet<IRefinement> getRefinements() {
        return this.refinements;
    }

    @Override
    public Optional<IRefinementItem.AccessorySlotType> getSlotType() {
        return this.slotType;
    }

    public static class Builder {
        private final TagKey<IFaction<?>> faction;
        private final Rarity rarity;
        private final int color;
        private @Nullable IRefinementItem.AccessorySlotType slotType;

        private Builder(TagKey<IFaction<?>> faction, Rarity rarity, int color) {
            this.faction = faction;
            this.rarity = rarity;
            this.color = color;
        }

        /**
         * Specify the one and only accessory type this refinement can be put on
         */
        public Builder onlyFor(IRefinementItem.AccessorySlotType slotType) {
            this.slotType = slotType;
            return this;
        }

        /**
         * Builds the refinement set with a translatable name derived from the key
         */
        @SafeVarargs
        public final RefinementSet build(ResourceKey<IRefinementSet> key, Holder<IRefinement>... refinements) {
            return build(key, List.of(refinements));
        }

        public RefinementSet build(ResourceKey<IRefinementSet> key, List<? extends Holder<IRefinement>> refinements) {
            return new RefinementSet(Component.translatable(descriptionId(key)), this.faction, this.rarity, this.color, HolderSet.direct(refinements), Optional.ofNullable(this.slotType));
        }
    }
}
