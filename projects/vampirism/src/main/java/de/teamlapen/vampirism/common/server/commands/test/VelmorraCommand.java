package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.world.blocks.PortalGatewayBlock;
import de.teamlapen.vampirism.common.world.dimensions.DimensionManager;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraBiomeSource;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaFightData;
import de.teamlapen.vampirism.common.world.portal.VelmorraPortalShape;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class VelmorraCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("velmorra")
                .then(Commands.literal("create")
                        .executes(context -> createVelmorra(context.getSource().getServer())))
                .then(Commands.literal("remove")
                        .executes(context -> removeVelmorra(context.getSource().getServer())))
                .then(Commands.literal("marker")
                        .then(Commands.literal("dracula")
                                .executes(c -> markerDracula(c.getSource().getLevel(), c.getSource().getPlayerOrException().position()))))
                .then(Commands.literal("portal")
                        .then(Commands.literal("create")
                                .executes(context -> createPortal(context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition(), context.getSource().getPlayerOrException().getNearestViewDirection())))
                        .then(Commands.literal("activate")
                                .executes(context -> activatePortal(context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition())))
                        .then(Commands.literal("deactivate")
                                .executes(context -> deactivatePortal(context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition()))));
    }

    private static int markerDracula(ServerLevel server, Vec3 blockPos) {
        Marker marker = EntityType.MARKER.create(server, EntitySpawnReason.COMMAND);
        if (marker == null) {
            throw new IllegalStateException("Could not create marker");
        }
        marker.setData(ModAttachments.MARKER, DraculaFightData.DRACULA_SPAWN_MARKER);
        marker.setPos(blockPos);
        server.addFreshEntity(marker);
        return 0;
    }

    private static int createPortal(ServerLevel level, BlockPos blockPos, Direction nearestViewDirection) {

        if (nearestViewDirection.getAxis() == Direction.Axis.Y) {
            throw new IllegalArgumentException("Can not create portal. Look into a direction");
        }
        BlockPos start = blockPos.relative(nearestViewDirection, 3).relative(nearestViewDirection.getCounterClockWise(), 2).below();

        level.setBlockAndUpdate(start, ModBlocks.DARK_STONE_BRICKS.get().defaultBlockState());
        level.setBlockAndUpdate(start.relative(nearestViewDirection.getClockWise(), 1), ModBlocks.DARK_STONE_BRICKS.get().defaultBlockState());
        level.setBlockAndUpdate(start.relative(nearestViewDirection.getClockWise(), 2), ModBlocks.DARK_STONE_BRICKS.get().defaultBlockState());
        level.setBlockAndUpdate(start.relative(nearestViewDirection.getClockWise(), 3), ModBlocks.DARK_STONE_BRICKS.get().defaultBlockState());
        level.setBlockAndUpdate(start.relative(nearestViewDirection.getClockWise(), 4), ModBlocks.DARK_STONE_BRICKS.get().defaultBlockState());
        level.setBlockAndUpdate(start.above(), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.FIRST).setValue(PortalGatewayBlock.FACING, nearestViewDirection));
        level.setBlockAndUpdate(start.above().relative(nearestViewDirection.getClockWise(), 1), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above().relative(nearestViewDirection.getClockWise(), 2), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above().relative(nearestViewDirection.getClockWise(), 3), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above().relative(nearestViewDirection.getClockWise(), 4), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.FIRST).setValue(PortalGatewayBlock.FACING, nearestViewDirection.getOpposite()));
        level.setBlockAndUpdate(start.above(2), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.SECOND).setValue(PortalGatewayBlock.FACING, nearestViewDirection));
        level.setBlockAndUpdate(start.above(2).relative(nearestViewDirection.getClockWise(), 1), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(2).relative(nearestViewDirection.getClockWise(), 2), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(2).relative(nearestViewDirection.getClockWise(), 3), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(2).relative(nearestViewDirection.getClockWise(), 4), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.SECOND).setValue(PortalGatewayBlock.FACING, nearestViewDirection.getOpposite()));
        level.setBlockAndUpdate(start.above(3), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.THIRD).setValue(PortalGatewayBlock.FACING, nearestViewDirection));
        level.setBlockAndUpdate(start.above(3).relative(nearestViewDirection.getClockWise(), 1), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(3).relative(nearestViewDirection.getClockWise(), 2), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(3).relative(nearestViewDirection.getClockWise(), 3), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(3).relative(nearestViewDirection.getClockWise(), 4), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.THIRD).setValue(PortalGatewayBlock.FACING, nearestViewDirection.getOpposite()));
        level.setBlockAndUpdate(start.above(4), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.FOURTH).setValue(PortalGatewayBlock.FACING, nearestViewDirection));
        level.setBlockAndUpdate(start.above(4).relative(nearestViewDirection.getClockWise(), 1), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(4).relative(nearestViewDirection.getClockWise(), 2), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(4).relative(nearestViewDirection.getClockWise(), 3), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(4).relative(nearestViewDirection.getClockWise(), 4), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.FOURTH).setValue(PortalGatewayBlock.FACING, nearestViewDirection.getOpposite()));
        level.setBlockAndUpdate(start.above(5).relative(nearestViewDirection.getClockWise()), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.FIFTH).setValue(PortalGatewayBlock.FACING, nearestViewDirection));
        level.setBlockAndUpdate(start.above(5).relative(nearestViewDirection.getClockWise(), 2), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(start.above(5).relative(nearestViewDirection.getClockWise(), 3), ModBlocks.VELMORRA_PORTAL_ARCH.get().defaultBlockState().setValue(PortalGatewayBlock.TYPE, PortalGatewayBlock.Type.FIFTH).setValue(PortalGatewayBlock.FACING, nearestViewDirection.getOpposite()));

        return 0;
    }

    private static int activatePortal(ServerLevel level, BlockPos blockPos) {
        VelmorraPortalShape.findEmptyPortalShape(level, blockPos)
                .ifPresent(x -> x.activate(level));
        return 0;
    }

    private static int deactivatePortal(ServerLevel level, BlockPos blockPos) {
        VelmorraPortalShape.findActivePortalShape(level, blockPos)
                .ifPresent(x -> x.deactivate(level));
        return 0;
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
