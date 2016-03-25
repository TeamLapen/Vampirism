package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

/**
 * Created by max on 19.03.16.
 */
public class BlockMedChair extends VampirismBlock {
    public static final PropertyEnum<EnumPart> PART = PropertyEnum.create("part", EnumPart.class);
    private final static String name = "medChair";

    public BlockMedChair() {
        super(name, Material.iron);
        this.blockState.getBaseState().withProperty(PART, EnumPart.TOP).withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PART, EnumPart.fromMeta(meta >> 2)).withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).getMeta() << 2 | state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING, PART);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getHorizontalFacing().rotateY().rotateY()).withProperty(PART, EnumPart.fromMeta(placer.getRNG().nextInt(2)));//TODO
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    public enum EnumPart implements IStringSerializable {
        TOP("top", 0), BOTTOM("bottom", 1);

        public final String name;
        public final int meta;

        EnumPart(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        public static EnumPart fromMeta(int meta) {
            if (meta == 1) {
                return BOTTOM;
            }
            return TOP;
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }
    }
}
