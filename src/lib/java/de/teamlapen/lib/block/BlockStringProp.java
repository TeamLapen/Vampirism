/*
 *
 * Inspired by @WayofTime's
 * https://github.com/WayofTime/BloodMagic/blob/da6f41039499ea85e77beabf1a685901e7a3323e/src/main/java/WayofTime/bloodmagic/block/base/BlockString.java
 *
 */
package de.teamlapen.lib.block;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;


/**
 * Block that has one String property.
 */
public class BlockStringProp extends Block {
    protected final static String defaultPropName = "type";
    private final int maxMeta;
    private final List<String> values;
    private final PropertyString stringProp;
    /**
     * Own blockstate since the vanilla one cannot be initialized, since the String prop does not exist while {@link Block#createBlockState()}
     */
    private final BlockState realBlockState;


    /**
     * @param materialIn
     * @param values     All possible values. IMPORTANT: they are converted to lowercase for the json state files
     * @param propName
     */
    public BlockStringProp(Material materialIn, String[] values, String propName) {
        super(materialIn);
        this.maxMeta = values.length - 1;
        this.values = Arrays.asList(values);
        this.stringProp = PropertyString.create(propName, values);
        realBlockState = createRealBlockState();
        setupStates();
    }

    public BlockStringProp(Material material, String[] values) {
        this(material, values, defaultPropName);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public BlockState getBlockState() {
        return realBlockState;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return values.indexOf(String.valueOf(state.getValue(stringProp)));
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getBlockState().getBaseState().withProperty(stringProp, values.get(meta));
    }

    public PropertyString getStringProp() {
        return stringProp;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (int i = 0; i < maxMeta + 1; i++)
            list.add(new ItemStack(this, 1, i));
    }

    @Override
    protected BlockState createBlockState() {
        return Blocks.air.getBlockState();
    }

    private BlockState createRealBlockState() {
        return new BlockState(this, stringProp);
    }

    private void setupStates() {
        this.setDefaultState(this.realBlockState.getBaseState().withProperty(stringProp, values.get(0)));
    }
}
