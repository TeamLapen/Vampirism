package de.teamlapen.vampirism.world.gen.structure.vampirehut;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

public class VampireHutPieces {

    private static final ResourceLocation LOCATION = new ResourceLocation(REFERENCE.MODID, "vampire_hut");

    public static void addPieces(StructureTemplateManager structureTemplateManager, StructurePieceAccessor pieceAccessor, RandomSource random, BlockPos pos)  {
        Rotation rotation = Rotation.getRandom(random);
        pieceAccessor.addPiece(new VampireHutPiece(structureTemplateManager, LOCATION, pos, rotation));
    }

    public static class VampireHutPiece extends TemplateStructurePiece {

        public VampireHutPiece(StructureTemplateManager pStructureTemplateManager, ResourceLocation pLocation, BlockPos pPos, Rotation pRotation) {
            super(VampirismFeatures.VAMPIRE_HUT_PIECE.get(), 0, pStructureTemplateManager, pLocation, pLocation.toString(), makeSettings(pRotation), pPos);
        }

        public VampireHutPiece(StructureTemplateManager pStructureTemplateManager, CompoundTag pTag) {
            super(VampirismFeatures.VAMPIRE_HUT_PIECE.get(), pTag, pStructureTemplateManager, (id) -> makeSettings(Rotation.valueOf(pTag.getString("Rot"))));
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
                        chest.setLootTable(ModLootTables.CHEST_VAMPIRE_HUT, pRandom.nextLong());
                    }
                }
                case "log" -> {
                    var state = ModBlocks.DARK_SPRUCE_LOG.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
                    pLevel.setBlock(pPos, state, 3);
                    BlockPos down = pPos.below();
                    while (pLevel.getBlockState(down).canBeReplaced()) {
                        pLevel.setBlock(down, state, 3);
                        down = down.below();
                    }
                }
            }
        }

        @Override
        public void postProcess(WorldGenLevel pLevel, @NotNull StructureManager pStructureManager, @NotNull ChunkGenerator pGenerator, @NotNull RandomSource pRandom, @NotNull BoundingBox pBox, @NotNull ChunkPos pChunkPos, BlockPos pPos) {
            int i = pLevel.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pPos.getX(), pPos.getZ());
            var oldPos = this.templatePosition;
            this.templatePosition = this.templatePosition.offset(0,i - 90,0);
            super.postProcess(pLevel, pStructureManager, pGenerator, pRandom, pBox, pChunkPos, pPos);
            this.templatePosition = oldPos;
        }

        private static @NotNull StructurePlaceSettings makeSettings(Rotation pRotation) {
            return (new StructurePlaceSettings()).setRotation(pRotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }
    }
}
