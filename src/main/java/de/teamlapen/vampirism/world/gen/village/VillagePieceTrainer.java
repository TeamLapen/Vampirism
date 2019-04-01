package de.teamlapen.vampirism.world.gen.village;

import de.teamlapen.vampirism.blocks.BlockGarlic;
import de.teamlapen.vampirism.blocks.BlockHunterTable;
import de.teamlapen.vampirism.blocks.BlockMedChair;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainer;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

/**
 * Village Part - House with Vampire Hunter equipment as well as an Hunter Trainer Entity
 */
public class VillagePieceTrainer extends StructureVillagePieces.Village {

    public VillagePieceTrainer() {
    }


    public VillagePieceTrainer(StructureVillagePieces.Start start, int type, StructureBoundingBox boundingBox, EnumFacing facing) {
        super(start, type);
        this.setCoordBaseMode(facing);//Set facing
        this.boundingBox = boundingBox;
    }

    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 7 - 1, 0);
        }
        IBlockState sprucePlanks = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 5, 8, 0, 10, sprucePlanks, sprucePlanks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 7, 0, 4, sprucePlanks, sprucePlanks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 3, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 3, 10, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 2, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 5, 2, 1, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 6, 2, 3, 10, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 10, 7, 3, 10, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 3, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 2, 3, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 8, 4, 1, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 4, 3, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 8, 5, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 0, 4, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 0, 4, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 8, 4, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 8, 4, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 8, 4, 4, structureBoundingBoxIn);
        IBlockState rotatedStairs = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
        IBlockState rotatedStairs1 = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        IBlockState rotatedStairs2 = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        IBlockState rotatedStairs3 = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        for (int k = -1; k <= 2; ++k) {
            for (int l = 0; l <= 8; ++l) {
                this.setBlockState(worldIn, rotatedStairs, l, 4 + k, k, structureBoundingBoxIn);

                if ((k > -1 || l <= 1) && (k > 0 || l <= 3) && (k > 1 || l <= 4 || l >= 6)) {
                    this.setBlockState(worldIn, rotatedStairs1, l, 4 + k, 5 - k, structureBoundingBoxIn);
                }
            }
        }

        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 5, 3, 4, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 2, 7, 4, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 4, 4, 5, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 5, 4, 6, 5, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 6, 3, 5, 6, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);

        for (int l1 = 4; l1 >= 1; --l1) {
            this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), l1, 2 + l1, 7 - l1, structureBoundingBoxIn);

            for (int i1 = 8 - l1; i1 <= 10; ++i1) {
                this.setBlockState(worldIn, rotatedStairs3, l1, 2 + l1, i1, structureBoundingBoxIn);
            }
        }

        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 6, 6, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 7, 5, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, rotatedStairs2, 6, 6, 4, structureBoundingBoxIn);

        for (int j2 = 6; j2 <= 8; ++j2) {
            for (int j1 = 5; j1 <= 10; ++j1) {
                this.setBlockState(worldIn, rotatedStairs2, j2, 12 - j2, j1, structureBoundingBoxIn);
            }
        }
        IBlockState spruceLog = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
        this.setBlockState(worldIn, spruceLog, 0, 2, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 0, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 4, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 6, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 8, 2, 5, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 2, 2, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 2, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 4, 4, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 5, 4, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 6, 4, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 5, 5, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH), 2, 3, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH), 2, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2, 2, 0, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, -1, 3, 2, -1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        //Place decoration etc
        this.setBlockState(worldIn, ModBlocks.hunter_table.getDefaultState().withProperty(BlockHunterTable.FACING, EnumFacing.NORTH), 5, 1, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.BOOKSHELF.getDefaultState(), 7, 1, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.BOOKSHELF.getDefaultState(), 7, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.BOOKSHELF.getDefaultState(), 3, 1, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.BOOKSHELF.getDefaultState(), 3, 2, 9, structureBoundingBoxIn);


        EnumFacing medChairFacing = EnumFacing.WEST;
        this.setBlockState(worldIn, ModBlocks.med_chair.getDefaultState().withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.TOP).withProperty(BlockMedChair.FACING, medChairFacing), 7, 1, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.med_chair.getDefaultState().withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.BOTTOM).withProperty(BlockMedChair.FACING, medChairFacing), 6, 1, 3, structureBoundingBoxIn);

        this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 7, 1, 2, LootHandler.STRUCTURE_VILLAGE_TRAINER);

        this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH), 6, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH), 4, 2, 9, structureBoundingBoxIn);
        //Place itemframe
        BlockPos itemFramePos = new BlockPos(getXWithOffset(1, -1), getYWithOffset(2), getZWithOffset(1, -1));
        if (structureBoundingBoxIn.isVecInside(itemFramePos)) {
            EntityItemFrame itemFrame = new EntityItemFrame(worldIn, itemFramePos, getCoordBaseMode().getOpposite());
            itemFrame.setDisplayedItem(new ItemStack(ModItems.vampire_fang));
            worldIn.spawnEntity(itemFrame);
        }

        //Place garlic plants
        int garlic_age_count = BlockGarlic.AGE.getAllowedValues().size();
        this.setBlockState(worldIn, Blocks.WATER.getDefaultState(), 1, -1, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.FARMLAND.getDefaultState(), 1, -1, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.garlic.getDefaultState().withProperty(BlockGarlic.AGE, randomIn.nextInt(garlic_age_count)), 1, 0, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.FARMLAND.getDefaultState(), 1, -1, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.garlic.getDefaultState().withProperty(BlockGarlic.AGE, randomIn.nextInt(garlic_age_count)), 1, 0, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.FARMLAND.getDefaultState(), 1, -1, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.garlic.getDefaultState().withProperty(BlockGarlic.AGE, randomIn.nextInt(garlic_age_count)), 1, 0, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 0, 0, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 0, 0, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 0, 0, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 0, 0, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 1, 0, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.OAK_FENCE_GATE.getDefaultState().withProperty(BlockFenceGate.FACING, EnumFacing.WEST), 0, 0, 9, structureBoundingBoxIn);

        for (int x = 0; x < 2; x++) {
            for (int z = 6; z < 11; z++) {
                this.clearCurrentPositionBlocksUpwards(worldIn, x, 7, z, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, Blocks.COBBLESTONE.getDefaultState(), x, -2, z, structureBoundingBoxIn);
            }
        }

        if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getMaterial() == Material.AIR && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getMaterial() != Material.AIR) {
            this.setBlockState(worldIn, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), 2, 0, -1, structureBoundingBoxIn);
        }


        for (int k2 = 0; k2 < 5; ++k2) {
            for (int i3 = 0; i3 < 9; ++i3) {
                this.clearCurrentPositionBlocksUpwards(worldIn, i3, 7, k2, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, Blocks.COBBLESTONE.getDefaultState(), i3, -1, k2, structureBoundingBoxIn);
            }
        }

        for (int l2 = 5; l2 < 11; ++l2) {
            for (int j3 = 2; j3 < 9; ++j3) {
                this.clearCurrentPositionBlocksUpwards(worldIn, j3, 7, l2, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, Blocks.COBBLESTONE.getDefaultState(), j3, -1, l2, structureBoundingBoxIn);
            }
        }

        this.spawnHunterTrainer(worldIn, structureBoundingBoxIn, 5, 1, 6);
        return true;
    }


    private void spawnHunterTrainer(World worldIn, StructureBoundingBox structureBoundingBoxIn, int x, int y, int z) {

        int j = this.getXWithOffset(x, z);
        int k = this.getYWithOffset(y);
        int l = this.getZWithOffset(x, z);

        if (!structureBoundingBoxIn.isVecInside(new BlockPos(j, k, l))) {
            return;
        }

        AxisAlignedBB box = new AxisAlignedBB(structureBoundingBoxIn.minX, structureBoundingBoxIn.minY, structureBoundingBoxIn.minZ, structureBoundingBoxIn.maxX, structureBoundingBoxIn.maxY, structureBoundingBoxIn.maxZ);
        EntityHunterTrainer hunterTrainer = new EntityHunterTrainer(worldIn);
        hunterTrainer.setHome(box.grow(-1, 0, -1));
        hunterTrainer.setLocationAndAngles((double) j + 0.5D, (double) k, (double) l + 0.5D, 0.0F, 0.0F);
        worldIn.spawnEntity(hunterTrainer);
    }

    public static class CreationHandler implements VillagerRegistry.IVillageCreationHandler {

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 7, 12, facing);
            return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new VillagePieceTrainer(startPiece, p5, structureboundingbox, facing) : null;
        }

        @Override
        public Class<?> getComponentClass() {
            return VillagePieceTrainer.class;
        }

        /**
         * @param random
         * @param terrainType Apparently rather the village size, than the terrain type
         * @return
         */
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int terrainType) {
            return new StructureVillagePieces.PieceWeight(VillagePieceTrainer.class, 15, MathHelper.getInt(random, 0, 1 + terrainType));
        }
    }


}
