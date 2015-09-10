package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.util.IBlockRegistrable;
import de.teamlapen.vampirism.util.IIgnorePropsForRender;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Basic tent block. Mainly placeholder for the tent rendered for {@link BlockMainTent}
 */
public class BlockTent extends BasicBlock  implements IIgnorePropsForRender,IBlockRegistrable{

    public static final PropertyDirection FACING = PropertyDirection.create("facing",EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum POSITION = PropertyEnum.create("position",EnumPos.class);

    @Override
    public IProperty[] getRenderIgnoredProperties() {
        return new IProperty[]{FACING,POSITION};
    }

    @Override
    public String[] getVariantsToRegister() {
        return new String[]{name};
    }

    @Override
    public boolean shouldRegisterSimpleItem() {
        return false;
    }

    public enum EnumPos implements IStringSerializable{
        TOP_LEFT(0,"tl"),TOP_RIGHT(1,"tr"),BOTTOM_LEFT(2,"bl"),BOTTOM_RIGHT(3,"br");
        private String name;
        private int id;
        private static EnumPos[] ID_LOOKUP;
        static{
            ID_LOOKUP=values();
        }
        EnumPos(int id,String name){
            this.id=id;
            this.name=name;
        }

        public static EnumPos getFromId(int id){
            if(id>=ID_LOOKUP.length)return BOTTOM_RIGHT;
            return ID_LOOKUP[id];
        }
        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public String getName() {
            return name;
        }
    }
    public static final String name = "tent";
    public BlockTent() {
        super(Material.cloth, name);
        this.setCreativeTab(null);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING,EnumFacing.NORTH).withProperty(POSITION,EnumPos.BOTTOM_RIGHT));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta=((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        meta+=((EnumPos)state.getValue(POSITION)).getId()<<2;
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int dir=meta&3;
        int id=(meta&12)>>2;
        return getDefaultState().withProperty(FACING,EnumFacing.getHorizontal(dir)).withProperty(POSITION,EnumPos.getFromId(id));
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this,new IProperty[]{FACING,POSITION});
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        EnumPos rpos= (EnumPos) state.getValue(POSITION);
        EnumFacing facing= (EnumFacing) state.getValue(FACING);
        switch (rpos){
            case BOTTOM_RIGHT:
                worldIn.setBlockToAir(pos.offset(facing.getOpposite()));
                worldIn.setBlockToAir(pos.offset(facing.rotateY()));
                worldIn.setBlockToAir(pos.offset(facing.rotateY()).offset(facing.getOpposite()));
                break;
            case BOTTOM_LEFT:
                worldIn.setBlockToAir(pos.offset(facing.getOpposite()));
                worldIn.setBlockToAir(pos.offset(facing.rotateYCCW()));
                worldIn.setBlockToAir(pos.offset(facing.rotateYCCW()).offset(facing.getOpposite()));
                break;
            case TOP_LEFT:
                worldIn.setBlockToAir(pos.offset(facing));
                worldIn.setBlockToAir(pos.offset(facing.rotateYCCW()));
                worldIn.setBlockToAir(pos.offset(facing.rotateYCCW()).offset(facing));
                break;
            case TOP_RIGHT:
                worldIn.setBlockToAir(pos.offset(facing));
                worldIn.setBlockToAir(pos.offset(facing.rotateY()));
                worldIn.setBlockToAir(pos.offset(facing.rotateY()).offset(facing));
                break;
        }
        super.breakBlock(worldIn, pos, state);
    }


    @Override
    public boolean isOpaqueCube() {
        return false;
    }

}
