package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Pillar for Altar of Infusion structure
 */
public class BlockAltarPillar extends VampirismBlock {
    public final static PropertyEnum<EnumPillarType> typeProperty = PropertyEnum.create("type", EnumPillarType.class);
    private final static String name = "altarPillar";

    public BlockAltarPillar() {
        super(name, Material.ROCK);
        this.setHarvestLevel("pickaxe", 0);
        this.setHardness(0.9F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(typeProperty, EnumPillarType.NONE));
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(Item.getItemFromBlock(this), 1));
        EnumPillarType type = state.getValue(typeProperty);
        if (type != EnumPillarType.NONE) {
            list.add(new ItemStack(Item.getItemFromBlock(type.fillerBlock), 1));
        }
        return list;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(typeProperty).meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(typeProperty, EnumPillarType.byMetadata(meta));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumPillarType type = state.getValue(typeProperty);
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (type != EnumPillarType.NONE && heldItem == null) {
            if (!playerIn.capabilities.isCreativeMode) {
                playerIn.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, new ItemStack(Item.getItemFromBlock(type.fillerBlock)));
            }

            worldIn.setBlockState(pos, state.withProperty(typeProperty, EnumPillarType.NONE));
            return true;
        }
        if (type == EnumPillarType.NONE && heldItem != null) {
            ItemStack stack = heldItem;
            for (EnumPillarType t : EnumPillarType.values()) {
                if (stack.getItem().equals(Item.getItemFromBlock(t.fillerBlock))) {
                    if (!playerIn.capabilities.isCreativeMode) {
                        stack.stackSize--;
                    }

                    worldIn.setBlockState(pos, state.withProperty(typeProperty, t));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, typeProperty);
    }

    public enum EnumPillarType implements IStringSerializable {
        NONE(0, "none", 0, Blocks.AIR), STONE(1, "stone", 1, Blocks.STONEBRICK), IRON(2, "iron", 2, Blocks.IRON_BLOCK), GOLD(3, "gold", 3, Blocks.GOLD_BLOCK), BONE(4, "bone", 1.5F, Blocks.BONE_BLOCK);
        private static final EnumPillarType[] METADATA_LOOKUP = new EnumPillarType[values().length];

        static {
            for (EnumPillarType type : values()) {
                METADATA_LOOKUP[type.meta] = type;
            }
        }

        public static EnumPillarType byMetadata(int metadata) {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

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
