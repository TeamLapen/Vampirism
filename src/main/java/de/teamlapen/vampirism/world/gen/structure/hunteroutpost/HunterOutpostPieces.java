package de.teamlapen.vampirism.world.gen.structure.hunteroutpost;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.GarlicBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

public class HunterOutpostPieces {

    private static final ResourceLocation LOCATION_TOWER = new ResourceLocation(REFERENCE.MODID, "hunter_tower");

    public static void addPieces(StructureTemplateManager structureTemplateManager, StructurePieceAccessor pieceAccessor, RandomSource random, BlockPos pos)  {
        Rotation rotation = Rotation.getRandom(random);
        pieceAccessor.addPiece(new OutpostPiece(structureTemplateManager, LOCATION_TOWER, pos, rotation));
    }

    public static class OutpostPiece extends TemplateStructurePiece {

        public OutpostPiece(StructureTemplateManager pStructureTemplateManager, ResourceLocation pLocation, BlockPos pPos, Rotation pRotation) {
            super(VampirismFeatures.HUNTER_OUTPOST_PIECE.get(), 0, pStructureTemplateManager, pLocation, pLocation.toString(), makeSettings(pRotation), pPos);
        }

        public OutpostPiece(StructureTemplateManager pStructureTemplateManager, CompoundTag pTag) {
            super(VampirismFeatures.HUNTER_OUTPOST_PIECE.get(), pTag, pStructureTemplateManager, (id) -> makeSettings(Rotation.valueOf(pTag.getString("Rot"))));
        }

        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext pContext, @NotNull CompoundTag pTag) {
            super.addAdditionalSaveData(pContext, pTag);
            pTag.putString("Rot", this.placeSettings.getRotation().name());
        }

        @Override
        protected void handleDataMarker(@NotNull String pName, @NotNull BlockPos pPos, @NotNull ServerLevelAccessor pLevel, @NotNull RandomSource pRandom, @NotNull BoundingBox pBox) {
            switch (pName) {
                case "chest" -> {
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                    BlockEntity blockEntity = pLevel.getBlockEntity(pPos.below());
                    if (blockEntity instanceof ChestBlockEntity chest) {
                        chest.setLootTable(ModLootTables.CHEST_HUNTER_OUTPOST, pRandom.nextLong());
                    }
                }
                case "chest_alchemy" -> {
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                    BlockEntity blockEntity = pLevel.getBlockEntity(pPos.below());
                    if (blockEntity instanceof ChestBlockEntity chest) {
                        chest.setLootTable(ModLootTables.CHEST_HUNTER_OUTPOST_ALCHEMY, pRandom.nextLong());
                    }
                }
                case "horse" -> {
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                    Horse horse = EntityType.HORSE.create(pLevel.getLevel());
                    horse.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
                    pLevel.addFreshEntity(horse);
                }
                case "garlic" -> {
                    pLevel.setBlock(pPos, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE), 3);
                    pLevel.setBlock(pPos.above(), ModBlocks.GARLIC.get().defaultBlockState().setValue(GarlicBlock.AGE, pRandom.nextInt(GarlicBlock.MAX_AGE)), 3);
                }
            }
        }

        private static @NotNull StructurePlaceSettings makeSettings(Rotation pRotation) {
            return (new StructurePlaceSettings()).setRotation(pRotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }

        @Override
        public void postProcess(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, RandomSource pRandom, BoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos) {
            var pos = this.templatePosition;
            this.templatePosition = pos.below(1);
            super.postProcess(pLevel, pStructureManager, pGenerator, pRandom, pBox, pChunkPos, pPos);
            this.templatePosition = pos;
        }
    }
}
