package de.teamlapen.vampirism.world.gen.village;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileTotem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;


public class VillagePieceTotem extends StructureVillagePieces.Village {

    private boolean forceHunter = false;

    public VillagePieceTotem() {
    }

    public VillagePieceTotem(StructureVillagePieces.Start start, int type, MutableBoundingBox boundingBox, EnumFacing facing, boolean forceHunter) {
        super(start, type);
        this.setCoordBaseMode(facing);//Set facing
        this.boundingBox = boundingBox;
        this.forceHunter = forceHunter;
    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
        }

        IBlockState sand_path = getBiomeSpecificBlockState(Blocks.SANDSTONE.getDefaultState());
        IBlockState grass_path = this.getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState());
        IBlockState plank_path = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
        IBlockState cobble = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());


        for (int x = 0; x < 3; ++x) {
            for (int z = 0; z < 3; ++z) {
                this.clearCurrentPositionBlocksUpwards(worldIn, x, 0, z, structureBoundingBoxIn);
                IBlockState old = this.getBlockStateFromPos(worldIn, x, -1, z, structureBoundingBoxIn);
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
    public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) { //TODO Make StructureFeature
        StructureVillagePieces.generateAndAddRoadPiece((StructureVillagePieces.Start) componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.maxY - 4, this.boundingBox.minZ, EnumFacing.WEST, this.getComponentType());
        StructureVillagePieces.generateAndAddRoadPiece((StructureVillagePieces.Start) componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ, EnumFacing.EAST, this.getComponentType());
        StructureVillagePieces.generateAndAddRoadPiece((StructureVillagePieces.Start) componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.maxY - 4, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
        StructureVillagePieces.generateAndAddRoadPiece((StructureVillagePieces.Start) componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.maxY - 4, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
        super.readStructureFromNBT(tagCompound, p_143011_2_);
        if (tagCompound.hasKey("force_hunter")) {
            forceHunter = tagCompound.getBoolean("force_hunter");
        }
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {
        super.writeStructureToNBT(tagCompound);
        tagCompound.setBoolean("force_hunter", forceHunter);
    }

    public static class CreationHandler implements VillagerRegistry.IVillageCreationHandler {

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            MutableBoundingBox structureBoundingBox = MutableBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 2, 4, 2, facing);
            boolean forceHunter = false;
            for (StructureComponent c : pieces) {
                if (c instanceof VillagePieceTrainer) {
                    forceHunter = true;
                }
            }
            return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(pieces, structureBoundingBox) == null ? new VillagePieceTotem(startPiece, p5, structureBoundingBox, facing, forceHunter) : null;

        }

        @Override
        public Class<?> getComponentClass() {
            return VillagePieceTotem.class;
        }

        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            return new StructureVillagePieces.PieceWeight(VillagePieceTotem.class, 20, random.nextInt(2) == 1 ? 1 : 0);
        }
    }
}
