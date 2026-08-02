package de.teamlapen.vampirism.common.advancements.critereon;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IMarshallPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class MarshallCriterion implements EntitySubPredicate {

    public static final MarshallCriterion INSTANCE = new MarshallCriterion();
    public static final MapCodec<MarshallCriterion> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NonNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(@NonNull Entity entity, @NonNull ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof Player player && IMarshallPlayer.getPresent(player).isPresent();
    }
}
