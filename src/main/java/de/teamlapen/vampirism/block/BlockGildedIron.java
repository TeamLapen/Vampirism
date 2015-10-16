package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Block that looks golden, but does only contain 4 gold ingots (and 5 iron ingots)
 */
public class BlockGildedIron extends BasicBlock {

    public static String name = "gildedIronBlock";

    public BlockGildedIron() {
        super(Material.iron, name);
        setHardness(4.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockTextureName(REFERENCE.MODID + ":" + name);
    }

    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int meta) {
        super.breakBlock(world, x, y, z, p_149749_5_, meta);
        world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, new ItemStack(Items.gold_ingot, 4)));
        world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, new ItemStack(Items.iron_ingot, 5)));
    }
}
