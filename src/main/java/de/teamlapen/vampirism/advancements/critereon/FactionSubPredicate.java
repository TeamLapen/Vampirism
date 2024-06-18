package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FactionSubPredicate implements EntitySubPredicate {

    public static final MapCodec<FactionSubPredicate> CODEC = RecordCodecBuilder.mapCodec(inst ->
        inst.group(
                IPlayableFaction.CODEC.optionalFieldOf("faction", null).forGetter(p -> p.faction)
        ).apply(inst, FactionSubPredicate::new)
    );

    @Nullable
    private final IPlayableFaction<?> faction;

    private FactionSubPredicate(@Nullable IPlayableFaction<?> faction) {
        this.faction = faction;
    }

    public static FactionSubPredicate faction(@NotNull IPlayableFaction<?> faction) {
        return new FactionSubPredicate(faction);
    }

    @Override
    public boolean matches(@NotNull Entity pEntity, @NotNull ServerLevel pLevel, @Nullable Vec3 p_218830_) {
        return VampirismAPI.factionRegistry().getFaction(pEntity) == this.faction;
    }

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }
}
