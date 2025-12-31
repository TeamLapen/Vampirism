package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.world.dimensions.DimensionManager;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraBiomeSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public class VelmorraCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("velmorra")
                .then(Commands.literal("create")
                        .executes(context -> createVelmorra(context.getSource().getServer())))
                .then(Commands.literal("remove")
                        .executes(context -> removeVelmorra(context.getSource().getServer())));
    }

    private static int createVelmorra(MinecraftServer server) {
        DimensionManager manager = DimensionManager.INSTANCE;

        manager.getOrCreateLevel(server, ModDimensions.VELMORRA_LEVEL, () -> {
            RegistryAccess.Frozen context = server.registryAccess();
            return new LevelStem(context.lookupOrThrow(Registries.DIMENSION_TYPE).getOrThrow(ModDimensions.VELMORRA_DIMENSION_TYPE), new NoiseBasedChunkGenerator(new VelmorraBiomeSource(context.lookupOrThrow(Registries.BIOME)), context.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(ModDimensions.VELMORRA_NOISE_GENERATOR)));
        });


        return 0;
    }

    private static int removeVelmorra(MinecraftServer server) {
        DimensionManager manager = DimensionManager.INSTANCE;
        manager.markDimensionForUnregistration(server, ModDimensions.VELMORRA_LEVEL, true);
        return 0;
    }
}
