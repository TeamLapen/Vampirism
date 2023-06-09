package de.teamlapen.vampirism.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Pillar for Altar of Infusion structure
 */
public class AltarPillarBlock extends VampirismBlock {
    public final static EnumProperty<EnumPillarType> TYPE_PROPERTY = EnumProperty.create("type", EnumPillarType.class);
    protected static final VoxelShape pillarShape = makeShape();
    protected static final VoxelShape pillarShapeFilled = makeShapeFull();

    private static @NotNull VoxelShape makeShape() {
        VoxelShape a = Block.box(3, 0, 3, 13, 1, 13);
        VoxelShape b1 = Block.box(3, 0, 3, 4, 16, 4);
        VoxelShape b2 = Block.box(12, 0, 3, 13, 16, 4);
        VoxelShape b3 = Block.box(3, 0, 12, 4, 16, 13);
        VoxelShape b4 = Block.box(12, 0, 12, 13, 16, 13);
        VoxelShape c = Block.box(3, 15, 3, 13, 16, 13);
        return Shapes.or(a, b1, b2, b3, b4, c);
    }

    private static @NotNull VoxelShape makeShapeFull() {
        VoxelShape b = Block.box(4, 1, 2, 12, 15, 14);
        VoxelShape c = Block.box(2, 1, 4, 14, 15, 12);
        return Shapes.or(pillarShape, b, c);
    }

    public AltarPillarBlock() {
        super(Properties.of().mapColor(MapColor.STONE).strength(0.9f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE_PROPERTY, EnumPillarType.NONE));

    }


    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(TYPE_PROPERTY) != EnumPillarType.NONE ? pillarShapeFilled : pillarShape;
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player playerIn, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        EnumPillarType type = state.getValue(TYPE_PROPERTY);
        ItemStack heldItem = playerIn.getItemInHand(hand);
        if (type != EnumPillarType.NONE && heldItem.isEmpty()) {
            if (!playerIn.getAbilities().instabuild) {
                playerIn.setItemSlot(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, new ItemStack(Item.byBlock(type.fillerBlock)));
            }

            worldIn.setBlockAndUpdate(pos, state.setValue(TYPE_PROPERTY, EnumPillarType.NONE));
            return InteractionResult.SUCCESS;
        }
        if (type == EnumPillarType.NONE && !heldItem.isEmpty()) {
            for (EnumPillarType t : EnumPillarType.values()) {
                if (heldItem.getItem().equals(t.fillerBlock.asItem())) {
                    if (!playerIn.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    worldIn.setBlockAndUpdate(pos, state.setValue(TYPE_PROPERTY, t));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(TYPE_PROPERTY);
    }

    public enum EnumPillarType implements StringRepresentable {
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

        public @NotNull String getName() {
            return getSerializedName();
        }

        @NotNull
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
        public @NotNull String toString() {
            return getName();
        }
    }


}
