package de.teamlapen.vampirism.common.world.dimensions.velmorra;

import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.world.dimensions.DimensionManager;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaFightData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import org.jetbrains.annotations.Nullable;

import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.Function;

public class VelmorraDimension {

    public static ServerLevel createDimension(MinecraftServer server) {
        return DimensionManager.INSTANCE.getOrCreateLevel(server, ModDimensions.VELMORRA_LEVEL, () -> {
            RegistryAccess.Frozen context = server.registryAccess();
            return new LevelStem(
                    context.lookupOrThrow(Registries.DIMENSION_TYPE).getOrThrow(ModDimensions.VELMORRA_DIMENSION_TYPE),
                    new NoiseBasedChunkGenerator(new VelmorraBiomeSource(context.lookupOrThrow(Registries.BIOME)), context.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(ModDimensions.VELMORRA_NOISE_GENERATOR)),
                    OptionalLong.of(server.getRandomSequence(ModDimensions.VELMORRA).nextLong()));
        });
    }

    /**
     * @return the id of the currently existing Velmorra instance or null if Velmorra does not exist or is about to be removed.
     * A recreated Velmorra gets a new id, so portals can detect if they lead to the Velmorra they were opened for.
     */
    public static @Nullable UUID getInstanceId(MinecraftServer server) {
        if (DimensionManager.INSTANCE.getLevelsPendingUnregistration().contains(ModDimensions.VELMORRA_LEVEL)) {
            return null;
        }
        ServerLevel level = server.getLevel(ModDimensions.VELMORRA_LEVEL);
        return level == null ? null : DraculaFightData.get(level).getInstanceId();
    }

    public static final BlockPos SPAWN_POINT = new BlockPos(9, 197, -148);

    public static class VelmorraPortalPos implements Function<IAttachmentHolder, GlobalPos> {

        @Override
        public GlobalPos apply(IAttachmentHolder iAttachmentHolder) {
            throw new UnsupportedOperationException("Cannot create portal position. This needs to be assigned manually");
        }
    }
}
