package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Pillar for Altar of Infusion structure
 */
public class AltarPillarBlock extends VampirismBlock {
    public final static EnumProperty<EnumPillarType> TYPE_PROPERTY = EnumProperty.create("type", EnumPillarType.class);
    protected static final VoxelShape pillarShape = makeShape();
    protected static final VoxelShape pillarShapeFilled = makeShapeFull();
    private final static String name = "altar_pillar";

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(3, 0, 3, 13, 1, 13);
        VoxelShape b1 = Block.makeCuboidShape(3, 0, 3, 4, 16, 4);
        VoxelShape b2 = Block.makeCuboidShape(12, 0, 3, 13, 16, 4);
        VoxelShape b3 = Block.makeCuboidShape(3, 0, 12, 4, 16, 13);
        VoxelShape b4 = Block.makeCuboidShape(12, 0, 12, 13, 16, 13);
        VoxelShape c = Block.makeCuboidShape(3, 15, 3, 13, 16, 13);
        return VoxelShapes.or(a, b1, b2, b3, b4, c);
    }

    private static VoxelShape makeShapeFull() {
        VoxelShape a = pillarShape;
        VoxelShape b = Block.makeCuboidShape(4, 1, 2, 12, 15, 14);
        VoxelShape c = Block.makeCuboidShape(2, 1, 4, 14, 15, 12);
        return VoxelShapes.or(a, b, c);
    }

    public AltarPillarBlock() {
        super(name, Properties.create(Material.ROCK).hardnessAndResistance(0.9f));
        this.setDefaultState(this.stateContainer.getBaseState().with(TYPE_PROPERTY, EnumPillarType.NONE));

    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> loot = super.getDrops(state, builder);
        EnumPillarType type = state.get(TYPE_PROPERTY);
        if (type != EnumPillarType.NONE) {
            loot.add(new ItemStack(type.fillerBlock.asItem(), 1));
        }
        return loot;
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 0;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(TYPE_PROPERTY) != EnumPillarType.NONE ? pillarShapeFilled : pillarShape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        EnumPillarType type = state.get(TYPE_PROPERTY);
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (type != EnumPillarType.NONE && heldItem.isEmpty()) {
            if (!playerIn.abilities.isCreativeMode) {
                playerIn.setItemStackToSlot(hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND, new ItemStack(Item.getItemFromBlock(type.fillerBlock)));
            }

            worldIn.setBlockState(pos, state.with(TYPE_PROPERTY, EnumPillarType.NONE));
            return true;
        }
        if (type == EnumPillarType.NONE && !heldItem.isEmpty()) {
            for (EnumPillarType t : EnumPillarType.values()) {
                if (heldItem.getItem().equals(t.fillerBlock.asItem())) {
                    if (!playerIn.abilities.isCreativeMode) {
                        heldItem.shrink(1);
                    }

                    worldIn.setBlockState(pos, state.with(TYPE_PROPERTY, t));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(TYPE_PROPERTY);
    }

    public enum EnumPillarType implements IStringSerializable {
        NONE(0, "none", 0, Blocks.AIR), STONE(1, "stone", 1, Blocks.STONE_BRICKS), IRON(2, "iron", 2, Blocks.IRON_BLOCK), GOLD(3, "gold", 3, Blocks.GOLD_BLOCK), BONE(4, "bone", 1.5F, Blocks.BONE_BLOCK);


        public final String name;
        public final Block fillerBlock;
        public final int meta;
        private final float value;

        EnumPillarType(int meta, String name, float value, Block fillerBlock) {
            this.meta = meta;
            this.name = name;
            this.fillerBlock = fillerBlock;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        /**
         * @return The "value" or level of this material.
         */
        public float getValue() {
            return value;
        }
    }


}
