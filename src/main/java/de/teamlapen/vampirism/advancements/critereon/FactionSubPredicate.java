package de.teamlapen.vampirism.advancements.critereon;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Optional;

public class FactionSubPredicate implements EntitySubPredicate {

    private static final Codec<FactionSubPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IPlayableFaction.CODEC.optionalFieldOf("faction").forGetter(p -> Optional.ofNullable(p.faction)),
            Codec.INT.fieldOf("level").forGetter(p -> p.level)
    ).apply(instance, (Optional<IFaction<?>> faction1, Integer level1) -> new FactionSubPredicate(((IPlayableFaction<?>) faction1.orElse(null)), level1)));

    @Nullable
    private final IPlayableFaction<?> faction;
    @Range(from = 1, to = Integer.MAX_VALUE)
    private final int level;

    private FactionSubPredicate(@Nullable IPlayableFaction<?> faction, int level) {
        Preconditions.checkArgument(level > 0, "Level must be greater than 0");
        this.faction = faction;
        this.level = level;
    }

    public static FactionSubPredicate of(@NotNull IPlayableFaction<?> faction) {
        return new FactionSubPredicate(faction, 1);
    }

    public static FactionSubPredicate of(@NotNull IPlayableFaction<?> faction, @Range(from = 1, to = Integer.MAX_VALUE) int level) {
        return new FactionSubPredicate(faction, level);
    }

    public static FactionSubPredicate of(@Range(from = 1, to = Integer.MAX_VALUE) int level) {
        return new FactionSubPredicate(null, level);
    }

    @Override
    public boolean matches(@NotNull Entity pEntity, @NotNull ServerLevel pLevel, @Nullable Vec3 p_218830_) {
        if (pEntity instanceof Player player) {
            return FactionPlayerHandler.getOpt(player).map(handler -> {
                IPlayableFaction<?> currentFaction = handler.getCurrentFaction();
                if (currentFaction != null) {
                    if (this.faction == null) {
                        return handler.getCurrentLevel() >= this.level;
                    } else if (currentFaction == this.faction) {
                        return handler.getCurrentLevel() >= this.level;
                    }
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    @Override
    public @NotNull JsonObject serializeCustomData() {
        DataResult<JsonElement> jsonElementDataResult = CODEC.encodeStart(JsonOps.INSTANCE, this);
        return jsonElementDataResult.result().orElseThrow().getAsJsonObject();
    }

    @Override
    public @NotNull Type type() {
        return FactionSubPredicate::fromJson;
    }

    public static FactionSubPredicate fromJson(JsonObject json) {
        DataResult<Pair<FactionSubPredicate, JsonElement>> decode = CODEC.decode(JsonOps.INSTANCE, json);
        return decode.result().orElseThrow().getFirst();
    }
}
