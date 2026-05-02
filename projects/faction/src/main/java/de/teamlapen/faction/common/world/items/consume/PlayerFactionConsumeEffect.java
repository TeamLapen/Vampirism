package de.teamlapen.faction.common.world.items.consume;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.PlayerFactionPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record PlayerFactionConsumeEffect(PlayerFactionPredicate predicate, List<ConsumeEffect> effects) implements ConsumeEffect {

    public static final MapCodec<PlayerFactionConsumeEffect> CODEC = MapCodec.assumeMapUnsafe(RecordCodecBuilder.create(inst ->
            inst.group(
                    PlayerFactionPredicate.CODEC.fieldOf("predicate").forGetter(PlayerFactionConsumeEffect::predicate),
                    ConsumeEffect.CODEC.listOf().fieldOf("effect").forGetter(PlayerFactionConsumeEffect::effects)
            ).apply(inst, PlayerFactionConsumeEffect::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerFactionConsumeEffect> STREAM_CODEC = StreamCodec.composite(
            PlayerFactionPredicate.STREAM_CODEC, PlayerFactionConsumeEffect::predicate,
            ConsumeEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), PlayerFactionConsumeEffect::effects,
            PlayerFactionConsumeEffect::new
    );

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return FactionItems.PLAYER_FACTION_BASED.get();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player && predicate.test(FactionPlayerHandler.get(player))) {
            return this.effects.stream().map(x -> x.apply(level, stack, entity)).reduce(true, Boolean::logicalXor);
        }
        return false;
    }

    public static Builder when(PlayerFactionPredicate predicate) {
        return new Builder(predicate);
    }

    public static Builder when(PlayerFactionPredicate.Builder predicate) {
        return new Builder(predicate);
    }

    public static class Builder {

        private final PlayerFactionPredicate predicate;
        private final List<ConsumeEffect> effects = new ArrayList<>();

        public Builder(PlayerFactionPredicate predicate) {
            this.predicate = predicate;
        }

        public Builder(PlayerFactionPredicate.Builder predicate) {
            this(predicate.build());
        }

        public Builder with(ConsumeEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public PlayerFactionConsumeEffect build() {
            Preconditions.checkNotNull(predicate, "predicate must be provided");
            return new PlayerFactionConsumeEffect(this.predicate, effects.stream().toList());
        }
    }
}
