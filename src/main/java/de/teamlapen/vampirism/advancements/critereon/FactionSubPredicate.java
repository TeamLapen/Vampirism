package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FactionSubPredicate implements EntitySubPredicate {

    @SuppressWarnings("unchecked")
    public static final MapCodec<FactionSubPredicate> CODEC = RecordCodecBuilder.mapCodec(inst ->
        inst.group(
                ModRegistries.FACTIONS.holderByNameCodec().optionalFieldOf("faction", null).forGetter(p -> (Holder<IFaction<?>>) p.faction),
                Codec.INT.optionalFieldOf( "level").forGetter(p -> p.level),
                Codec.INT.optionalFieldOf("lord_level").forGetter(p -> p.lordLevel)
        ).apply(inst, FactionSubPredicate::new)
    );

    @Nullable
    private final Holder<? extends IFaction<?>> faction;
    @NotNull
    private final Optional<Integer> level;
    @NotNull
    private final Optional<Integer> lordLevel;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private FactionSubPredicate(@Nullable Holder<? extends IFaction<?>> faction, @NotNull Optional<Integer> level, @NotNull Optional<Integer> lordLevel) {
        this.faction = faction;
        this.level = level;
        this.lordLevel = lordLevel;
    }

    public static FactionSubPredicate faction(@NotNull Holder<? extends IFaction<?>> faction) {
        return new FactionSubPredicate(faction, Optional.empty(), Optional.empty());
    }

    public static FactionSubPredicate level(@NotNull Holder<? extends IFaction<?>> faction, int level) {
        return new FactionSubPredicate(faction, Optional.of(level), Optional.empty());
    }

    public static FactionSubPredicate lord(@NotNull Holder<? extends IFaction<?>> faction, int lordLevel) {
        return new FactionSubPredicate(faction, Optional.empty(), Optional.of(lordLevel));
    }

    public static FactionSubPredicate lord(int lordLevel) {
        return new FactionSubPredicate(null, Optional.empty(), Optional.of(lordLevel));
    }

    public static FactionSubPredicate lord(@NotNull Holder<? extends IFaction<?>> faction) {
        return new FactionSubPredicate(faction, Optional.empty(), Optional.of(1));
    }

    public static FactionSubPredicate level(int level) {
        return new FactionSubPredicate(null, Optional.of(level), Optional.empty());
    }

    @Override
    public boolean matches(@NotNull Entity pEntity, @NotNull ServerLevel pLevel, @Nullable Vec3 p_218830_) {
        if (pEntity instanceof Player player) {
            FactionPlayerHandler fph = FactionPlayerHandler.get(player);
            return (faction == null || IFaction.is(fph.getFaction(), faction))
                    && (level.isEmpty() || fph.getCurrentLevel() >= level.get())
                    && (lordLevel.isEmpty() || fph.getLordLevel() >= lordLevel.get());
        }
        return false;
    }

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }
}
