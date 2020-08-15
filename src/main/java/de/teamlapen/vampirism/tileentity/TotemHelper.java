package de.teamlapen.vampirism.tileentity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.world.FactionPointOfInterestType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
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
import java.util.function.Function;
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
    public static boolean addTotem(World world, Set<PointOfInterest> pois, BlockPos totemPos) {
        BlockPos conflict = null;
        for (PointOfInterest poi : pois) {
            if (totemPositions.containsKey(poi.getPos()) && !totemPositions.get(poi.getPos()).equals(totemPos)) {
                conflict = totemPositions.get(poi.getPos());
                break;
            }
        }
        if (conflict != null) {
            handleTotemConflict(pois, world, totemPos, conflict);
        }
        if (pois.isEmpty()) {
            return false;
        }
        for (PointOfInterest pointOfInterest : pois) {
            totemPositions.put(pointOfInterest.getPos(), totemPos);
        }
        totemPositions.put(totemPos, totemPos);

        if (poiSets.containsKey(totemPos)) {
            poiSets.get(totemPos).forEach(poi -> {
                if (!pois.contains(poi)) {
                    totemPositions.remove(poi.getPos());
                }
            });
        }
        poiSets.put(totemPos, pois);
        return !pois.isEmpty();
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

    @Nullable
    public static BlockPos getTotemPosition(BlockPos pos) {
        return totemPositions.get(pos);
    }

    public static ITextComponent forceFactionCommand(IFaction<?> faction, ServerPlayerEntity player) {
        List<PointOfInterest> pointOfInterests = ((ServerWorld) player.getEntityWorld()).getPointOfInterestManager().func_219146_b(point -> true, player.getPosition(), 15, PointOfInterestManager.Status.ANY).sorted((point1, point2) -> (int) (new Vec3d(point1.getPos()).distanceTo(new Vec3d(player.getPosition())) - new Vec3d(point2.getPos()).distanceTo(new Vec3d(player.getPosition())))).collect(Collectors.toList());
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
        Set<PointOfInterest> points = manager.func_219146_b(type -> !(type instanceof FactionPointOfInterestType), pos, 50, PointOfInterestManager.Status.ANY).collect(Collectors.toSet());
        while (!points.isEmpty()) {
            List<Stream<PointOfInterest>> list = points.stream().map(pointOfInterest -> manager.func_219146_b(type -> !(type instanceof FactionPointOfInterestType), pointOfInterest.getPos(), 40, PointOfInterestManager.Status.ANY)).collect(Collectors.toList());
            points.clear();
            list.forEach(stream -> stream.forEach(point -> {
                if (!finished.contains(point)) {
                    if (point.getPos().withinDistance(pos, VampirismConfig.BALANCE.viMaxTotemRadius.get())) {
                        points.add(point);
                    }
                }
                finished.add(point);
            }));
        }
        return finished;
    }

    public static boolean isVillage(Set<PointOfInterest> pointOfInterests, World world, boolean hasInteraction) {
        Map<PointOfInterestType, Long> poiTCounts = pointOfInterests.stream().map(PointOfInterest::getType).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        if (poiTCounts.getOrDefault(PointOfInterestType.HOME, 0L) >= 4) {
            if (poiTCounts.entrySet().stream().filter(entry -> entry.getKey() != PointOfInterestType.HOME).mapToLong(Map.Entry::getValue).sum() >= 2) {
                if (hasInteraction || world.getEntitiesWithinAABB(VillagerEntity.class, getAABBAroundPOIs(pointOfInterests)).size() >= 4) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void updateVampireBoundingBox(@Nonnull Dimension dimension, @Nonnull BlockPos totemPos, @Nullable AxisAlignedBB box) {
        Map<BlockPos, MutableBoundingBox> map = vampireVillages.computeIfAbsent(dimension, dimension1 -> new HashMap<>());
        if (box == null) {
            map.remove(totemPos);
        } else {
            map.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }

    /**
     * @throws NoSuchElementException if poi is empty
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static AxisAlignedBB getAABBAroundPOIs(@Nonnull Set<PointOfInterest> pois) {
        return pois.stream().map(poi -> new AxisAlignedBB(poi.getPos()).grow(25)).reduce(AxisAlignedBB::union).get();
    }
}
