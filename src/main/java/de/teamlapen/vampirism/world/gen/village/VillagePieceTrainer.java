package de.teamlapen.vampirism.world.gen.village;

import de.teamlapen.vampirism.blocks.BlockGarlic;
import de.teamlapen.vampirism.blocks.BlockHunterTable;
import de.teamlapen.vampirism.blocks.BlockMedChair;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainer;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
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


    public VillagePieceTrainer(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox boundingBox, EnumFacing facing) {
        super(start, type);
        this.coordBaseMode = facing;
        this.boundingBox = boundingBox;
    }

    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        if (this.field_143015_k < 0) {
            this.field_143015_k = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.field_143015_k < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 7 - 1, 0);
        }
        IBlockState sprucePlanks = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 5, 8, 0, 10, sprucePlanks, sprucePlanks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 7, 0, 4, sprucePlanks, sprucePlanks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 2, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 5, 2, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 6, 2, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 10, 7, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 2, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 8, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 4, 3, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 8, 5, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 4, structureBoundingBoxIn);
        int i = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
        int j = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

        for (int k = -1; k <= 2; ++k) {
            for (int l = 0; l <= 8; ++l) {
                this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(i), l, 4 + k, k, structureBoundingBoxIn);

                if ((k > -1 || l <= 1) && (k > 0 || l <= 3) && (k > 1 || l <= 4 || l >= 6)) {
                    this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(j), l, 4 + k, 5 - k, structureBoundingBoxIn);
                }
            }
        }

        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 5, 3, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 2, 7, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 4, 4, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 5, 4, 6, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 6, 3, 5, 6, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
        int k1 = this.getMetadataWithOffset(Blocks.oak_stairs, 0);

        for (int l1 = 4; l1 >= 1; --l1) {
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), l1, 2 + l1, 7 - l1, structureBoundingBoxIn);

            for (int i1 = 8 - l1; i1 <= 10; ++i1) {
                this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(k1), l1, 2 + l1, i1, structureBoundingBoxIn);
            }
        }

        int i2 = this.getMetadataWithOffset(Blocks.oak_stairs, 1);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 6, 6, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 7, 5, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(i2), 6, 6, 4, structureBoundingBoxIn);

        for (int j2 = 6; j2 <= 8; ++j2) {
            for (int j1 = 5; j1 <= 10; ++j1) {
                this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(i2), j2, 12 - j2, j1, structureBoundingBoxIn);
            }
        }
        IBlockState spruceLog = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
        this.setBlockState(worldIn, spruceLog, 0, 2, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 0, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 4, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 6, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 2, 5, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 8, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 2, 2, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 2, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 4, 4, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 4, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, spruceLog, 6, 4, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 5, 5, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 3, 1, structureBoundingBoxIn);
        this.placeDoorCurrentPosition(worldIn, structureBoundingBoxIn, randomIn, 2, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1)));
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, -1, 3, 2, -1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        //Place decoration etc
        this.setBlockState(worldIn, ModBlocks.hunterTable.getDefaultState().withProperty(BlockHunterTable.FACING, this.coordBaseMode), 5, 1, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.bookshelf.getDefaultState(), 7, 1, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.bookshelf.getDefaultState(), 7, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.bookshelf.getDefaultState(), 3, 1, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.bookshelf.getDefaultState(), 3, 2, 9, structureBoundingBoxIn);


        boolean mirror = coordBaseMode.equals(EnumFacing.SOUTH) || coordBaseMode.equals(EnumFacing.WEST);
        EnumFacing medChairFacing = mirror ? this.coordBaseMode.rotateY() : this.coordBaseMode.rotateYCCW();
        this.setBlockState(worldIn, ModBlocks.medChair.getDefaultState().withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.TOP).withProperty(BlockMedChair.FACING, medChairFacing), 7, 1, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.medChair.getDefaultState().withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.BOTTOM).withProperty(BlockMedChair.FACING, medChairFacing), 6, 1, 3, structureBoundingBoxIn);

        this.setBlockState(worldIn, Blocks.chest.getDefaultState().withProperty(BlockChest.FACING, this.coordBaseMode.rotateY()), 7, 1, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 6, 2, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 4, 2, 9, structureBoundingBoxIn);
        //Place itemframe
        BlockPos itemFramePos = new BlockPos(getXWithOffset(1, -1), getYWithOffset(2), getZWithOffset(1, -1));
        if (structureBoundingBoxIn.isVecInside(itemFramePos)) {
            EntityItemFrame itemFrame = new EntityItemFrame(worldIn, itemFramePos, this.coordBaseMode.getOpposite());
            itemFrame.setDisplayedItem(new ItemStack(ModItems.vampireFang));
            worldIn.spawnEntityInWorld(itemFrame);
        }

        //Place garlic plants
        int garlic_age_count = BlockGarlic.AGE.getAllowedValues().size();
        this.setBlockState(worldIn, Blocks.water.getDefaultState(), 1, -1, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.farmland.getDefaultState(), 1, -1, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.garlic.getDefaultState().withProperty(BlockGarlic.AGE, randomIn.nextInt(garlic_age_count)), 1, 0, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.farmland.getDefaultState(), 1, -1, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.garlic.getDefaultState().withProperty(BlockGarlic.AGE, randomIn.nextInt(garlic_age_count)), 1, 0, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.farmland.getDefaultState(), 1, -1, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.garlic.getDefaultState().withProperty(BlockGarlic.AGE, randomIn.nextInt(garlic_age_count)), 1, 0, 9, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 0, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 0, 7, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 0, 8, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 0, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 0, 10, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.oak_fence_gate.getDefaultState().withProperty(BlockFenceGate.FACING, coordBaseMode.rotateY()), 0, 0, 9, structureBoundingBoxIn);

        for (int x = 0; x < 2; x++) {
            for (int z = 6; z < 11; z++) {
                this.clearCurrentPositionBlocksUpwards(worldIn, x, 7, z, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), x, -2, z, structureBoundingBoxIn);
            }
        }

        if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBoxIn);
        }


        for (int k2 = 0; k2 < 5; ++k2) {
            for (int i3 = 0; i3 < 9; ++i3) {
                this.clearCurrentPositionBlocksUpwards(worldIn, i3, 7, k2, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), i3, -1, k2, structureBoundingBoxIn);
            }
        }

        for (int l2 = 5; l2 < 11; ++l2) {
            for (int j3 = 2; j3 < 9; ++j3) {
                this.clearCurrentPositionBlocksUpwards(worldIn, j3, 7, l2, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j3, -1, l2, structureBoundingBoxIn);
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

        AxisAlignedBB box = AxisAlignedBB.fromBounds(structureBoundingBoxIn.minX, structureBoundingBoxIn.minY, structureBoundingBoxIn.minZ, structureBoundingBoxIn.maxX, structureBoundingBoxIn.maxY, structureBoundingBoxIn.maxZ);
        EntityHunterTrainer hunterTrainer = new EntityHunterTrainer(worldIn);
        hunterTrainer.setHome(box.contract(1, 0, 1));
        hunterTrainer.setLocationAndAngles((double) j + 0.5D, (double) k, (double) l + 0.5D, 0.0F, 0.0F);
        worldIn.spawnEntityInWorld(hunterTrainer);
    }

    public static class CreationHandler implements VillagerRegistry.IVillageCreationHandler {

        /**
         * @param random
         * @param terrainType Apparently rather the village size, than the terrain type
         * @return
         */
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int terrainType) {
            return new StructureVillagePieces.PieceWeight(VillagePieceTrainer.class, 20, MathHelper.getRandomIntegerInRange(random, 0, 1 + terrainType));
        }

        @Override
        public Class<?> getComponentClass() {
            return VillagePieceTrainer.class;
        }

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 7, 12, facing);
            return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new VillagePieceTrainer(startPiece, p5, random, structureboundingbox, facing) : null;
        }
    }


}
