package de.teamlapen.vampirism.tileentity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.world.FactionPointOfInterestType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TotemHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * stores all BoundingBoxes of vampire controlled villages per dimension, mapped from totem positions
     */
    private static final HashMap<Dimension, Map<BlockPos, MutableBoundingBox>> vampireVillages = Maps.newHashMap();

    /**
     * saves the position
     */
    private static final Map<BlockPos, BlockPos> totemPositions = Maps.newHashMap();

    private static final Map<BlockPos, Set<PointOfInterest>> poiSets = Maps.newHashMap();

    public static void clearCacheForDimension(Dimension dimension) {
        vampireVillages.remove(dimension);
    }

    public static void addVampireVillage(Dimension dimension, BlockPos pos, AxisAlignedBB box) {
        updateVampireBoundingBox(dimension, pos, box);
    }

    public static void removeVampireVillage(Dimension dimension, BlockPos pos) {
        updateVampireBoundingBox(dimension, pos, null);
    }

    public static boolean isInsideVampireAreaCached(Dimension dimension, BlockPos blockPos) {
        if (vampireVillages.containsKey(dimension)) {
            for (Map.Entry<BlockPos, MutableBoundingBox> entry : vampireVillages.get(dimension).entrySet()) {
                if (entry.getValue().isVecInside(blockPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return false if another totem exists
     */
    public static boolean addTotem(World world, Set<PointOfInterest> structures, BlockPos totemPos) {
        BlockPos conflict = null;
        for (PointOfInterest poi : structures) {
            if (totemPositions.containsKey(poi.getPos()) && !totemPositions.get(poi.getPos()).equals(totemPos)) {
                conflict = totemPositions.get(poi.getPos());
                break;
            }
        }
        if (conflict != null) {
            handleTotemConflict(structures, world, totemPos, conflict);
        }
        if (structures.isEmpty()) {
            return false;
        }
        for (PointOfInterest pointOfInterest : structures) {
            totemPositions.put(pointOfInterest.getPos(), totemPos);
        }
        totemPositions.put(totemPos, totemPos);

        if (poiSets.containsKey(totemPos)) {
            poiSets.get(totemPos).forEach(poi -> {
                if (!structures.contains(poi)) {
                    totemPositions.remove(poi.getPos());
                }
            });
        }
        poiSets.put(totemPos, structures);
        return true;
    }

    private static void handleTotemConflict(Set<PointOfInterest> pois, World world, BlockPos totem, BlockPos conflicting) {

        TotemTileEntity totem1 = ((TotemTileEntity) world.getTileEntity(totem));
        TotemTileEntity totem2 = ((TotemTileEntity) world.getTileEntity(conflicting));

        if (totem1.getControllingFaction() != totem2.getControllingFaction()) {
            pois.removeIf(poi -> !totem.equals(totemPositions.get(poi.getPos())));
            return;
            //TODO do nothing
        }

        if (totem1.getCapturingFaction() != null || totem2.getCapturingFaction() != null) {
            pois.removeIf(poi -> !totem.equals(totemPositions.get(poi.getPos())));
            return;
            //TODO do nothing
        }

        StructureStart structure1 = Structures.VILLAGE.getStart(world, totem, false);
        StructureStart structure2 = Structures.VILLAGE.getStart(world, conflicting, false);

        if (structure1 == StructureStart.DUMMY && structure2 != StructureStart.DUMMY) {
            pois.removeIf(poi -> !totem.equals(totemPositions.get(poi.getPos())));
            return;
            //TODO totem 2 winning
        }

        if (totem2.getSize() >= totem1.getSize()) {
            pois.removeIf(poi -> !totem.equals(totemPositions.get(poi.getPos())));
        }
    }

    public static void removeTotem(Collection<PointOfInterest> structure, BlockPos pos) {
        structure.forEach(pointOfInterest -> totemPositions.remove(pointOfInterest.getPos(), pos));
        totemPositions.remove(pos);
    }

    /**
     * @return {@code null} if no totem exists
     */
    @Nullable
    public static BlockPos getTotemPosition(Collection<PointOfInterest> structure) {
        for (PointOfInterest pointOfInterest : structure) {
            if (totemPositions.containsKey(pointOfInterest.getPos())) {
                return totemPositions.get(pointOfInterest.getPos());
            }
        }
        return null;
    }

    public static ITextComponent forceFactionCommand(IFaction<?> faction, ServerPlayerEntity player) {
        List<PointOfInterest> pointOfInterests = ((ServerWorld) player.getEntityWorld()).getPointOfInterestManager().func_219146_b(point -> true, player.getPosition(), 30, PointOfInterestManager.Status.ANY).collect(Collectors.toList());
        if (pointOfInterests.stream().noneMatch(point -> totemPositions.containsKey(point.getPos()))) {
            return new TranslationTextComponent("command.vampirism.test.village.no_village");
        }
        TileEntity te = player.getEntityWorld().getTileEntity(totemPositions.get(pointOfInterests.get(0).getPos()));
        if (!(te instanceof TotemTileEntity)) {
            LOGGER.warn("TileEntity at {} is no TotemTileEntity", totemPositions.get(pointOfInterests.get(0).getPos()));
            return new StringTextComponent("");
        }
        TotemTileEntity tile = (TotemTileEntity) te;
        tile.setForcedFaction(faction);
        return new TranslationTextComponent("command.vampirism.test.village.success", faction.getName());
    }

    public static Set<PointOfInterest> getVillagePointsOfInterest(ServerWorld world, BlockPos pos) {
        PointOfInterestManager manager = world.getPointOfInterestManager();
        Set<PointOfInterest> finished = Sets.newHashSet();
        Set<PointOfInterest> points = manager.func_219146_b(type -> !(type instanceof FactionPointOfInterestType), pos, 35, PointOfInterestManager.Status.ANY).collect(Collectors.toSet());
        while (!points.isEmpty()) {
            List<Stream<PointOfInterest>> list = points.stream().map(pointOfInterest -> manager.func_219146_b(type -> !(type instanceof FactionPointOfInterestType), pointOfInterest.getPos(), 25, PointOfInterestManager.Status.ANY)).collect(Collectors.toList());
            finished.addAll(points);
            points.clear();
            list.forEach(stream -> stream.forEach(point -> {
                if (!finished.contains(point)) {
                    points.add(point);
                }
            }));
        }
        finished.forEach(point -> world.setBlockState(point.getPos().up(10), Blocks.BEDROCK.getDefaultState()));//TODO remove
        return finished;
    }

    public static void updateVampireBoundingBox(@Nonnull Dimension dimension, @Nonnull BlockPos totemPos, @Nullable AxisAlignedBB box) {
        Map<BlockPos, MutableBoundingBox> map = vampireVillages.computeIfAbsent(dimension, dimension1 -> new HashMap<>());
        if (box == null) {
            map.remove(totemPos);
        } else {
            map.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }
}
