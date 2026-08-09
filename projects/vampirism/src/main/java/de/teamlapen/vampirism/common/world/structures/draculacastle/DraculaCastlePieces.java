package de.teamlapen.vampirism.common.world.structures.draculacastle;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModLootTables;
import de.teamlapen.vampirism.common.core.ModStructures;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaFightData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class DraculaCastlePieces {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DraculaCastlePieces.class);
    private static final int structureWidth = 271;
    private static final int structureDepth = 271;

    public static void addCastlePieces(StructureTemplateManager structureTemplateManager, BlockPos startPos, StructurePieceAccessor pieces) {

        var pos = startPos.offset(-structureWidth / 2 + 5, 0, -structureDepth / 2 + 5);

        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 6; z++) {
                for (int y = -1; y < 2; y++) {
                    pieces.addPiece(new DraculaCastlePiece(structureTemplateManager, pos, x, y, z, y == -1 ? TerrainAdjustment.BURY : TerrainAdjustment.BEARD_BOX));
                }
            }
        }
    }

    public static class DraculaCastlePiece extends TemplateStructurePiece implements PieceBeardifierModifier {

        private final TerrainAdjustment terrainAdjustment;

        public DraculaCastlePiece(StructureTemplateManager structureTemplateManager, BlockPos position, int xIndex, int yIndex, int zIndex, TerrainAdjustment terrainAdjustment) {
            var id = VIdentifier.mod("dracula_castle/" + xIndex + "_" + zIndex + "_" + yIndex);
            super(ModStructures.DRACULA_CASTLE_PIECE.get(), 0, structureTemplateManager, id, id.toString(), makeSettings(), position.offset(zIndex * 48, yIndex * 48, xIndex * 48));
            this.terrainAdjustment = terrainAdjustment;
        }

        public DraculaCastlePiece(StructureTemplateManager structureTemplateManager, CompoundTag tag) {
            super(ModStructures.DRACULA_CASTLE_PIECE.get(), tag, structureTemplateManager, _ -> makeSettings());
            this.terrainAdjustment = TerrainAdjustment.valueOf(tag.getStringOr("terrain_adjustment", TerrainAdjustment.BEARD_BOX.name()));
        }

        @Override
        public BoundingBox getBeardifierBox() {
            return getBoundingBox();
        }

        @Override
        public TerrainAdjustment getTerrainAdjustment() {
            return this.terrainAdjustment;
        }

        @Override
        public int getGroundLevelDelta() {
            return 0;
        }

        private static StructurePlaceSettings makeSettings() {
            return new StructurePlaceSettings()
                    .setRotation(Rotation.NONE)
                    .setMirror(Mirror.NONE)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING)
                    ;
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);
            tag.putString("terrain_adjustment", this.terrainAdjustment.name());
        }

        @Override
        protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
            name = name.toLowerCase(Locale.ROOT);
            if (name.contains("chest") || name.contains("barrel")) {
                boolean chest = name.contains("chest");
                BlockState state = chest ? Blocks.CHEST.defaultBlockState() : Blocks.BARREL.defaultBlockState();
                EnumProperty<Direction> facing = chest ? ChestBlock.FACING : BarrelBlock.FACING;
                if (name.contains("north")) {
                    state = state.setValue(facing, Direction.NORTH);
                } else if (name.contains("south")) {
                    state = state.setValue(facing, Direction.SOUTH);
                } else if (name.contains("east")) {
                    state = state.setValue(facing, Direction.EAST);
                } else if (name.contains("west")) {
                    state = state.setValue(facing, Direction.WEST);
                }
                var lootTable = ModLootTables.DRACULA_CASTLE;
                if (name.contains("pantry")) {
                    lootTable = ModLootTables.DRACULA_CASTLE_PANTRY;
                } else if (name.contains("library")) {
                    lootTable = ModLootTables.DRACULA_CASTLE_LIBRARY;
                } else if (name.contains("kitchen")) {
                    lootTable = chest ? ModLootTables.DRACULA_CASTLE_KITCHEN : ModLootTables.DRACULA_CASTLE_KITCHEN_BARREL;
                } else if (name.contains("dungeon")) {
                    lootTable = ModLootTables.DRACULA_CASTLE_DUNGEON;
                } else if (name.contains("cave")) {
                    lootTable = chest ? ModLootTables.DRACULA_CAVE_CHEST : ModLootTables.DRACULA_CAVE_BARREL;
                } else if (name.contains("bedroom")) {
                    lootTable = ModLootTables.DRACULA_CASTLE_BEDROOM;
                } else if (name.contains("guardtower")) {
                    lootTable = ModLootTables.DRACULA_CASTLE_GUARDTOWER;
                } else if (name.contains("huntercamp")) {
                    lootTable = ModLootTables.DRACULA_CASTLE_HUNTERCAMP;
                }
                create(level, box, random, pos, lootTable, state);
            } else if (name.equals("village") || name.equals("librarian")) {
                Villager villager = EntityType.VILLAGER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
                if (villager != null) {
                    if (name.equals("librarian")) {
                        villager.getVillagerData().withProfession(level.getLevel().registryAccess(), VillagerProfession.LIBRARIAN);
                    }
                    villager.snapTo(pos, 0,0);
                    villager.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.STRUCTURE, null);
                    level.addFreshEntity(villager);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            } else if (name.equals("vampirism:dracula_marker")) {
                Marker marker = EntityType.MARKER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
                if (marker == null) {
                    LOGGER.error("Failed to spawn dracula marker");
                } else {
                    marker.setData(ModAttachments.MARKER, DraculaFightData.DRACULA_SPAWN_MARKER);
                    marker.setPos(pos.getCenter());
                    level.addFreshEntity(marker);
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }

        protected boolean create(
                ServerLevelAccessor level,
                BoundingBox box,
                RandomSource random,
                BlockPos pos,
                ResourceKey<LootTable> lootTable,
                BlockState state
        ) {
            if (box.isInside(pos) && !level.getBlockState(pos).is(Blocks.CHEST)) {

                level.setBlock(pos, state, Block.UPDATE_CLIENTS);
                BlockEntity blockentity = level.getBlockEntity(pos);
                if (blockentity instanceof RandomizableContainer container) {
                    container.setLootTable(lootTable, random.nextLong());
                }

                return true;
            } else {
                return false;
            }
        }
    }
}
