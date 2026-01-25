package de.teamlapen.vampirism.common.world.structures.draculacastle;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModLootTables;
import de.teamlapen.vampirism.common.core.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Locale;

public class DraculaCastlePieces {

    private static final int structureWidth = 271;
    private static final int structureDepth = 271;

    public static void addCastlePieces(StructureTemplateManager structureTemplateManager, BlockPos startPos, StructurePieceAccessor pieces) {

        var pos = startPos.offset(-structureWidth / 2 + 5, 0, -structureDepth / 2 + 5);

        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 6; z++) {
                for (int y = 0; y < 2; y++) {
                    pieces.addPiece(new CastlePiece(structureTemplateManager, pos, x, y, z));
                }
            }
        }
    }

    public static void addCavePieces(StructureTemplateManager structureTemplateManager, BlockPos startPos, StructurePieceAccessor pieces) {

        var pos = startPos.offset(-structureWidth / 2 + 5, 0, -structureDepth / 2 + 5);

        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 6; z++) {
                for (int y = -3; y < 0; y++) {
                    pieces.addPiece(new CavePiece(structureTemplateManager, pos, x, y, z));
                }
            }
        }
    }

    public static class CastlePiece extends Piece {

        public CastlePiece(StructureTemplateManager structureTemplateManager, BlockPos position, int xIndex, int yIndex, int zIndex) {
            super(ModStructures.DRACULA_CASTLE_PIECE.get(), structureTemplateManager, VIdentifier.mod("dracula_castle/" + xIndex + "_" + zIndex + "_" + yIndex), position.offset(zIndex * 48, yIndex * 48, xIndex * 48));
        }

        public CastlePiece(StructureTemplateManager structureTemplateManager, CompoundTag tag) {
            super(ModStructures.DRACULA_CASTLE_PIECE.get(), structureTemplateManager, tag);
        }
    }

    public static class CavePiece extends Piece {

        public CavePiece(StructureTemplateManager structureTemplateManager, BlockPos position, int xIndex, int yIndex, int zIndex) {
            super(ModStructures.DRACULA_CAVE_PIECE.get(), structureTemplateManager, VIdentifier.mod("dracula_cave/" + xIndex + "_" + zIndex + "_" + yIndex), position.offset(zIndex * 48, yIndex * 48 + (yIndex == -3 ? 32 : 0), xIndex * 48));
        }

        public CavePiece(StructureTemplateManager structureTemplateManager, CompoundTag tag) {
            super(ModStructures.DRACULA_CAVE_PIECE.get(), structureTemplateManager, tag);
        }
    }

    private static class Piece extends TemplateStructurePiece {

        public Piece(StructurePieceType type, StructureTemplateManager structureTemplateManager, Identifier pieceId, BlockPos position) {
            super(type, 0, structureTemplateManager, pieceId, pieceId.toString(), makeSettings(), position);
        }

        public Piece(StructurePieceType type, StructureTemplateManager structureTemplateManager, CompoundTag tag) {
            super(type, tag, structureTemplateManager, (id) -> new StructurePlaceSettings());
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
