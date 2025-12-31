package de.teamlapen.vampirism.common.world.dimensions.velmorra;

import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.world.dimensions.DimensionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Function;

public class VelmorraDimension {

    public static void createDimension(MinecraftServer server) {
        DimensionManager.INSTANCE.getOrCreateLevel(server, ModDimensions.VELMORRA_LEVEL, () -> {
            RegistryAccess.Frozen context = server.registryAccess();
            return new LevelStem(context.lookupOrThrow(Registries.DIMENSION_TYPE).getOrThrow(ModDimensions.VELMORRA_DIMENSION_TYPE), new NoiseBasedChunkGenerator(new VelmorraBiomeSource(context.lookupOrThrow(Registries.BIOME)), context.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(ModDimensions.VELMORRA_NOISE_GENERATOR)));
        });
    }

    public static final BlockPos SPAWN_POINT = new BlockPos(100, 60, 0);

    public static class VelmorraPortalPos implements Function<IAttachmentHolder, GlobalPos> {

        @Override
        public GlobalPos apply(IAttachmentHolder iAttachmentHolder) {
            throw new UnsupportedOperationException("Cannot create portal position. This needs to be assigned manually");
        }
    }
}
