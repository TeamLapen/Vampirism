package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCastleStairs extends BlockStairs {
    private final static String REGNAME_BASE = "castle_stairs_";

    public BlockCastleStairs(BlockCastleBlock block, BlockCastleBlock.EnumType type, String name) {
        super(block.getDefaultState().withProperty(BlockCastleBlock.VARIANT, type));
        this.setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, REGNAME_BASE + name);
        this.setTranslationKey(REFERENCE.MODID + "." + REGNAME_BASE + name);
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add("§o" + UtilLib.translate(ModBlocks.castle_block.getTranslationKey() + (this.equals(ModBlocks.castle_stairs_dark_stone) ? ".no_spawn" : ".vampire_spawn")) + "§r");

    }

}
