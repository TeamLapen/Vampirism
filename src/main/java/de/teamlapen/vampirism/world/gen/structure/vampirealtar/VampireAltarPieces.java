package de.teamlapen.vampirism.world.gen.structure.vampirealtar;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.core.ModStructures;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

public class VampireAltarPieces {

    private static final ResourceLocation LOCATION_SMALL = new ResourceLocation(REFERENCE.MODID, "altar_small");
    private static final ResourceLocation LOCATION_BIG = new ResourceLocation(REFERENCE.MODID, "altar_big");
    private static final ResourceLocation LOCATION_SMALL_OLD = new ResourceLocation(REFERENCE.MODID, "altar_small_old");
    private static final ResourceLocation LOCATION_BIG_OLD = new ResourceLocation(REFERENCE.MODID, "altar_big_old");

    public static void addPieces(StructureTemplateManager structureTemplateManager, StructurePieceAccessor pieceAccessor, RandomSource random, BlockPos pos)  {
        ResourceLocation location;
        if(random.nextFloat() < 0.2) {
            location = random.nextFloat() < 0.3 ? LOCATION_BIG_OLD : LOCATION_BIG;
        } else {
            location = random.nextFloat() < 0.5 ? LOCATION_SMALL_OLD : LOCATION_SMALL;
        }
        pieceAccessor.addPiece(new VampireAltarPiece(structureTemplateManager, location, pos));
    }

    public static class VampireAltarPiece extends TemplateStructurePiece {

        public VampireAltarPiece(StructureTemplateManager pStructureTemplateManager, ResourceLocation pLocation, BlockPos pPos) {
            super(ModStructures.VAMPIRE_ALTAR_PIECE.get(), 0, pStructureTemplateManager, pLocation, pLocation.toString(), makeSettings(), pPos);
        }

        public VampireAltarPiece(StructureTemplateManager pStructureTemplateManager, CompoundTag pTag) {
            super(ModStructures.VAMPIRE_ALTAR_PIECE.get(), pTag, pStructureTemplateManager, (id) -> makeSettings());
        }

        @Override
        protected void handleDataMarker(@NotNull String pName, @NotNull BlockPos pPos, @NotNull ServerLevelAccessor pLevel, @NotNull RandomSource pRandom, @NotNull BoundingBox pBox) {
            switch (pName) {
                case "chest" -> {
                    pLevel.setBlock(pPos, ModBlocks.DARK_STONE_BRICKS.get().defaultBlockState(), 3);
                    BlockEntity blockEntity = pLevel.getBlockEntity(pPos.below());
                    if (blockEntity instanceof ChestBlockEntity chest) {
                        chest.setLootTable(ModLootTables.CHEST_VAMPIRE_ALTAR, pRandom.nextLong());
                    }
                }
                case "entity" -> {
                    pLevel.removeBlock(pPos, false);
                    if (!VampirismAPI.sundamageRegistry().hasSunDamage(pLevel, pPos)) {
                        AdvancedVampireEntity advancedVampireEntity = ModEntities.ADVANCED_VAMPIRE.get().create(pLevel.getLevel());
                        advancedVampireEntity.setPos(pPos.getX() + 0.5, pPos.getY(), pPos.getZ() + 0.5);
                        pLevel.addFreshEntity(advancedVampireEntity);
                    }
                }
            }
        }

        private static @NotNull StructurePlaceSettings makeSettings() {
            return (new StructurePlaceSettings()).setRotation(Rotation.NONE).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }
    }
}
