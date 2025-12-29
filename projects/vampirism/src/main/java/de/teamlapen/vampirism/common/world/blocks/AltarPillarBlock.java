package de.teamlapen.vampirism.common.world.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Pillar for Altar of Infusion structure
 */
public class AltarPillarBlock extends Block {
    
    public final static EnumProperty<EnumPillarType> PILLAR_TYPE = EnumProperty.create("type", EnumPillarType.class);
    
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 16, 13);

    public AltarPillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PILLAR_TYPE, EnumPillarType.NONE));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        EnumPillarType type = state.getValue(PILLAR_TYPE);
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (type != EnumPillarType.NONE && heldItem.isEmpty()) {
            if (!player.getAbilities().instabuild) {
                if (!player.getInventory().add(new ItemStack(type.fillerBlock))) {
                    player.drop(new ItemStack(type.fillerBlock), false);
                }
            }

            level.setBlockAndUpdate(pos, state.setValue(PILLAR_TYPE, EnumPillarType.NONE));
            return InteractionResult.SUCCESS;
        }
        if (type == EnumPillarType.NONE && !heldItem.isEmpty()) {
            for (EnumPillarType t : EnumPillarType.values()) {
                if (heldItem.getItem().equals(t.fillerBlock.asItem())) {
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.setBlockAndUpdate(pos, state.setValue(PILLAR_TYPE, t));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PILLAR_TYPE);
    }

    public enum EnumPillarType implements StringRepresentable {
        NONE("none", 0, Blocks.AIR),
        STONE("stone", 1, Blocks.STONE_BRICKS),
        IRON("iron", 2, Blocks.IRON_BLOCK),
        GOLD("gold", 3, Blocks.GOLD_BLOCK),
        BONE("bone", 1.5F, Blocks.BONE_BLOCK);

        public final String name;
        public final Block fillerBlock;
        private final float value;

        EnumPillarType(String name, float value, Block fillerBlock) {
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
