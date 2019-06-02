package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * Pillar for Altar of Infusion structure
 */
public class BlockAltarPillar extends VampirismBlock {
    private final static String name = "altar_pillar";
    private final static EnumProperty<EnumPillarType> TYPE_PROPERTY = EnumProperty.create("type", EnumPillarType.class);

    public BlockAltarPillar() {
        super(name, Properties.create(Material.ROCK).hardnessAndResistance(0.9f));
        this.setDefaultState(this.stateContainer.getBaseState().with(TYPE_PROPERTY, EnumPillarType.NONE));

    }

    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
        drops.add(new ItemStack(this.asItem(), 1));
        EnumPillarType type = state.get(TYPE_PROPERTY);
        if (type != EnumPillarType.NONE) {
            drops.add(new ItemStack(type.fillerBlock.asItem(), 1));
        }
    }

    @Override
    public int getHarvestLevel(IBlockState p_getHarvestLevel_1_) {
        return 0;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(IBlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing p_196250_6_, float hitX, float hitY, float hitZ) {
        EnumPillarType type = state.get(TYPE_PROPERTY);
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (type != EnumPillarType.NONE && heldItem.isEmpty()) {
            if (!playerIn.isCreative()) {
                playerIn.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, new ItemStack(Item.getItemFromBlock(type.fillerBlock)));
            }

            worldIn.setBlockState(pos, state.with(TYPE_PROPERTY, EnumPillarType.NONE));
            return true;
        }
        if (type == EnumPillarType.NONE && !heldItem.isEmpty()) {
            for (EnumPillarType t : EnumPillarType.values()) {
                if (heldItem.getItem().equals(t.fillerBlock.asItem())) {
                    if (!playerIn.isCreative()) {
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
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
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
