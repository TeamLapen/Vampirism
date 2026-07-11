package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.world.blocks.PortalGatewayBlock;
import de.teamlapen.vampirism.common.world.dimensions.DimensionManager;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraBiomeSource;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaFightData;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import de.teamlapen.vampirism.common.world.portal.VelmorraPortalShape;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
                                .executes(context -> deactivatePortal(context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition()))))
                .then(Commands.literal("fight")
                        .then(Commands.literal("start")
                                .executes(context -> fightStart(context.getSource())))
                        .then(Commands.literal("stage")
                                .then(Commands.literal("passive").executes(context -> fightStage(context.getSource(), DraculaState.PASSIVE)))
                                .then(Commands.literal("ranged").executes(context -> fightStage(context.getSource(), DraculaState.RANGED)))
                                .then(Commands.literal("raged").executes(context -> fightStage(context.getSource(), DraculaState.RAGED))))
                        .then(Commands.literal("kill")
                                .executes(context -> fightKill(context.getSource())))
                        .then(Commands.literal("collapse")
                                .then(Commands.argument("remainingTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> fightCollapse(context.getSource(), IntegerArgumentType.getInteger(context, "remainingTicks"))))));
    }

    private static Optional<DraculaFightData> fightData(CommandSourceStack source) {
        Optional<DraculaFightData> data = DraculaFightData.getOpt(source.getLevel());
        if (data.isEmpty()) {
            source.sendFailure(Component.literal("Not in the Velmorra dimension"));
        }
        return data;
    }

    private static int fightStart(CommandSourceStack source) {
        return fightData(source).map(data -> {
            if (data.debugStartFight()) {
                source.sendSuccess(() -> Component.literal("Started the dracula fight"), true);
                return 1;
            }
            source.sendFailure(Component.literal("Could not start the fight (already running or setup invalid)"));
            return 0;
        }).orElse(0);
    }

    private static int fightStage(CommandSourceStack source, DraculaState state) {
        return fightData(source).flatMap(DraculaFightData::getDracula).map(dracula -> {
            dracula.debugSetState(state);
            source.sendSuccess(() -> Component.literal("Set dracula to " + state), true);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("No dracula found, start the fight first"));
            return 0;
        });
    }

    private static int fightKill(CommandSourceStack source) {
        return fightData(source).flatMap(DraculaFightData::getDracula).map(dracula -> {
            dracula.kill(source.getLevel());
            source.sendSuccess(() -> Component.literal("Killed dracula"), true);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("No dracula found, start the fight first"));
            return 0;
        });
    }

    private static int fightCollapse(CommandSourceStack source, int remainingTicks) {
        return fightData(source).map(data -> {
            if (data.debugSetCollapse(remainingTicks)) {
                source.sendSuccess(() -> Component.literal("Velmorra collapses in " + remainingTicks + " ticks"), true);
                return 1;
            }
            source.sendFailure(Component.literal("The fight has not started yet"));
            return 0;
        }).orElse(0);
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

        VelmorraPortalShape.buildFrame(level, start, nearestViewDirection);

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
