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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * Pillar for Altar of Infusion structure
 */
public class AltarPillarBlock extends VampirismBlock {
    public final static EnumProperty<EnumPillarType> TYPE_PROPERTY = EnumProperty.create("type", EnumPillarType.class);
    protected static final VoxelShape pillarShape = makeShape();
    protected static final VoxelShape pillarShapeFilled = makeShapeFull();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(3, 0, 3, 13, 1, 13);
        VoxelShape b1 = Block.box(3, 0, 3, 4, 16, 4);
        VoxelShape b2 = Block.box(12, 0, 3, 13, 16, 4);
        VoxelShape b3 = Block.box(3, 0, 12, 4, 16, 13);
        VoxelShape b4 = Block.box(12, 0, 12, 13, 16, 13);
        VoxelShape c = Block.box(3, 15, 3, 13, 16, 13);
        return VoxelShapes.or(a, b1, b2, b3, b4, c);
    }

    private static VoxelShape makeShapeFull() {
        VoxelShape b = Block.box(4, 1, 2, 12, 15, 14);
        VoxelShape c = Block.box(2, 1, 4, 14, 15, 12);
        return VoxelShapes.or(pillarShape, b, c);
    }

    public AltarPillarBlock() {
        super(Properties.of(Material.STONE).strength(0.9f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE_PROPERTY, EnumPillarType.NONE));

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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.getValue(TYPE_PROPERTY) != EnumPillarType.NONE ? pillarShapeFilled : pillarShape;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        EnumPillarType type = state.getValue(TYPE_PROPERTY);
        ItemStack heldItem = playerIn.getItemInHand(hand);
        if (type != EnumPillarType.NONE && heldItem.isEmpty()) {
            if (!playerIn.abilities.instabuild) {
                playerIn.setItemSlot(hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND, new ItemStack(Item.byBlock(type.fillerBlock)));
            }

            worldIn.setBlockAndUpdate(pos, state.setValue(TYPE_PROPERTY, EnumPillarType.NONE));
            return ActionResultType.SUCCESS;
        }
        if (type == EnumPillarType.NONE && !heldItem.isEmpty()) {
            for (EnumPillarType t : EnumPillarType.values()) {
                if (heldItem.getItem().equals(t.fillerBlock.asItem())) {
                    if (!playerIn.abilities.instabuild) {
                        heldItem.shrink(1);
                    }

                    worldIn.setBlockAndUpdate(pos, state.setValue(TYPE_PROPERTY, t));
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE_PROPERTY);
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

        public String getName() {
            return getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        /**
         * @return The "value" or level of this material.
         */
        public float getValue() {
            return value;
        }

        @Override
        public String toString() {
            return getName();
        }
    }


}
