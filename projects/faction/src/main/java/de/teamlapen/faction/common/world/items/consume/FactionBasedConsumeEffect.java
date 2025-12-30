package de.teamlapen.faction.common.world.items.consume;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record FactionBasedConsumeEffect(HolderSet<IFaction<?>> faction, List<ConsumeEffect> effects) implements ConsumeEffect {

    public static final MapCodec<FactionBasedConsumeEffect> CODEC = MapCodec.assumeMapUnsafe(RecordCodecBuilder.create(inst -> 
            inst.group(
                    RegistryCodecs.homogeneousList(FactionRegistries.Keys.FACTION).fieldOf("faction").forGetter(FactionBasedConsumeEffect::faction),
                    ConsumeEffect.CODEC.listOf().fieldOf("effect").forGetter(FactionBasedConsumeEffect::effects)
            ).apply(inst, FactionBasedConsumeEffect::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, FactionBasedConsumeEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(FactionRegistries.Keys.FACTION), FactionBasedConsumeEffect::faction,
            ConsumeEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), FactionBasedConsumeEffect::effects,
            FactionBasedConsumeEffect::new
    );

    public FactionBasedConsumeEffect(HolderSet<IFaction<?>> faction, ConsumeEffect effects) {
        this(faction, List.of(effects));
    }

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return FactionItems.FACTION_BASED.get();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
        Holder<? extends IFaction<?>> faction1 = IFactionHelper.get().getFaction(entity);
        if (IFaction.contains(faction, faction1)) {
           return effects.stream().allMatch(s -> s.apply(level, stack, entity));
        }
        return false;
    }

    public static Builder builder(TagKey<IFaction<?>> faction) {
        return new Builder(faction);
    }

    public static FactionBasedConsumeEffect build(TagKey<IFaction<?>> faction, ConsumeEffect effect) {
        return builder(faction).add(effect).build();
    }

    public static class Builder {

        private final TagKey<IFaction<?>> faction;
        private final List<ConsumeEffect> effects = new ArrayList<>();

        public Builder(TagKey<IFaction<?>> faction) {
            this.faction = faction;
        }

        public Builder add(ConsumeEffect effect) {
            effects.add(effect);
            return this;
        }

        public FactionBasedConsumeEffect build() {
            HolderGetter<IFaction<?>> factions = BuiltInRegistries.acquireBootstrapRegistrationLookup(ModRegistries.FACTIONS);
            Preconditions.checkArgument(!this.effects.isEmpty());
            return new FactionBasedConsumeEffect(factions.getOrThrow(this.faction), effects.stream().toList());
        }
    }
}
