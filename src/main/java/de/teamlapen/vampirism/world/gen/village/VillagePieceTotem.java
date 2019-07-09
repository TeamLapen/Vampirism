package de.teamlapen.vampirism.world.gen.village;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileTotem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;


public class VillagePieceTotem extends VillagePieces.Village {

    private boolean forceHunter = false;

    public VillagePieceTotem() {
    }

    public VillagePieceTotem(VillagePieces.Start start, int type, MutableBoundingBox boundingBox, Direction facing, boolean forceHunter) {
        super(start, type);
        this.setCoordBaseMode(facing);//Set facing
        this.boundingBox = boundingBox;
        this.forceHunter = forceHunter;
    }

    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
        }

        BlockState sand_path = getBiomeSpecificBlockState(Blocks.SANDSTONE.getDefaultState());
        BlockState grass_path = this.getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState());
        BlockState plank_path = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState()); //TODO possible needs more planks reference
        BlockState cobble = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());


        for (int x = 0; x < 3; ++x) {
            for (int z = 0; z < 3; ++z) {
                this.clearCurrentPositionBlocksUpwards(worldIn, x, 0, z, structureBoundingBoxIn);
                BlockState old = this.getBlockStateFromPos(worldIn, x, -1, z, structureBoundingBoxIn);
                if (old.getMaterial().isLiquid()) {
                    this.setBlockState(worldIn, plank_path, x, -1, z, structureBoundingBoxIn);
                } else if (old.getBlock() == Blocks.GRASS || old.getBlock() == Blocks.DIRT) {
                    this.setBlockState(worldIn, grass_path, x, -1, z, structureBoundingBoxIn);
                } else if (old.getBlock() == Blocks.SAND) {
                    this.setBlockState(worldIn, sand_path, x, -1, z, structureBoundingBoxIn);
                }

                this.replaceAirAndLiquidDownwards(worldIn, cobble, x, -2, z, structureBoundingBoxIn);
            }
        }
        this.setBlockState(worldIn, ModBlocks.totem_base.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.totem_top.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);

        BlockPos blockpos = new BlockPos(this.getXWithOffset(1, 1), this.getYWithOffset(1), this.getZWithOffset(1, 1));

        if (structureBoundingBoxIn.isVecInside(blockpos)) {
            TileEntity t = worldIn.getTileEntity(blockpos);
            if (t instanceof TileTotem) {
                IPlayableFaction[] factions = VampirismAPI.factionRegistry().getPlayableFactions();
                IPlayableFaction f = factions[randomIn.nextInt(factions.length)];
                ((TileTotem) t).forceChangeFaction(forceHunter ? VReference.HUNTER_FACTION : f, true);
            }
        }

        return true;
    }

    @Override
    public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) { //TODO possible source of error
        getNextComponentNN((VillagePieces.Start) componentIn, listIn, rand, -4, 0);
        getNextComponentPP((VillagePieces.Start) componentIn, listIn, rand, -4, 0);
        getNextComponentNN((VillagePieces.Start) componentIn, listIn, rand, -4, 0);
        getNextComponentPP((VillagePieces.Start) componentIn, listIn, rand, -4, 0);
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound, TemplateManager p_143011_2_) {
        super.readAdditional(tagCompound, p_143011_2_);
        if (tagCompound.contains("force_hunter")) {
            forceHunter = tagCompound.getBoolean("force_hunter");
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT tagCompound) {
        super.writeAdditional(tagCompound);
        tagCompound.putBoolean("force_hunter", forceHunter);
    }

    public static class CreationHandler implements VillagerRegistry.IVillageCreationHandler {

        @Override
        public VillagePieces.Village buildComponent(VillagePieces.PieceWeight villagePiece, VillagePieces.Start startPiece, List<StructurePiece> pieces, Random random, int p1, int p2, int p3, Direction facing, int p5) {
            MutableBoundingBox structureBoundingBox = MutableBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 2, 4, 2, facing);
            boolean forceHunter = false;
            for (StructurePiece c : pieces) {
                if (c instanceof VillagePieceTrainer) {
                    forceHunter = true;
                }
            }
            return canVillageGoDeeper(structureBoundingBox) && StructurePiece.findIntersecting(pieces, structureBoundingBox) == null ? new VillagePieceTotem(startPiece, p5, structureBoundingBox, facing, forceHunter) : null;

        }

        @Override
        public Class<?> getComponentClass() {
            return VillagePieceTotem.class;
        }

        @Override
        public VillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            return new VillagePieces.PieceWeight(VillagePieceTotem.class, 20, random.nextInt(2) == 1 ? 1 : 0);
        }
    }
}
