package de.teamlapen.faction.common.factions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.common.util.IntRange;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public record PlayerFactionPredicate(Optional<HolderSet<IFaction<?>>> factions, Optional<IntRange> levelRange, Optional<IntRange> lordLevelRange) implements Predicate<IFactionPlayerHandler> {

    public static final Codec<PlayerFactionPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(FactionRegistries.Keys.FACTION).optionalFieldOf("faction").forGetter(PlayerFactionPredicate::factions),
            IntRange.CODEC.optionalFieldOf("levelRange").forGetter(PlayerFactionPredicate::levelRange),
            IntRange.CODEC.optionalFieldOf("lordLevelRange").forGetter(PlayerFactionPredicate::lordLevelRange)
    ).apply(instance, PlayerFactionPredicate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerFactionPredicate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(FactionRegistries.Keys.FACTION)), PlayerFactionPredicate::factions,
            ByteBufCodecs.optional(IntRange.STREAM_CODEC), PlayerFactionPredicate::levelRange,
            ByteBufCodecs.optional(IntRange.STREAM_CODEC), PlayerFactionPredicate::lordLevelRange,
            PlayerFactionPredicate::new
    );

    @Override
    public boolean test(IFactionPlayerHandler player) {
        if (this.factions.isPresent() && !IFaction.contains(this.factions.get(), player.getFaction())) {
            return false;
        }

        if (this.levelRange.isPresent() && !this.levelRange.get().contains(player.getCurrentLevel())) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (this.lordLevelRange.isPresent() && player.getPlayerLord().filter(x -> lordLevelRange.get().contains(x.getLordLevel())).isEmpty()) {
            return false;
        }

        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private @Nullable HolderSet<IFaction<?>> factions;
        private @Nullable IntRange levelRange;
        private @Nullable IntRange lordLevelRange;

        public Builder factions(HolderSet<IFaction<?>> factions) {
            this.factions = factions;
            return this;
        }

        public Builder factions(TagKey<IFaction<?>> factions) {
            HolderGetter<IFaction<?>> getter = BuiltInRegistries.acquireBootstrapRegistrationLookup(FactionRegistries.FACTION.get());
            this.factions = getter.getOrThrow(factions);
            return this;
        }

        public Builder levelRange(IntRange levelRange) {
            this.levelRange = levelRange;
            return this;
        }

        public Builder lordLevelRange(IntRange lordLevelRange) {
            this.lordLevelRange = lordLevelRange;
            return this;
        }

        public PlayerFactionPredicate build() {
            return new PlayerFactionPredicate(Optional.ofNullable(factions), Optional.ofNullable(levelRange), Optional.ofNullable(lordLevelRange));
        }
    }
}
