package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class BlockCastleBlock extends VampirismBlock {
    public static final PropertyEnum<BlockCastleBlock.EnumType> VARIANT = PropertyEnum.create("variant", BlockCastleBlock.EnumType.class);
    private static final String name = "castle_block";

    public BlockCastleBlock() {
        super(name, Material.ROCK);
        this.setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.DARK_BRICK));
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        int meta = stack.getMetadata();
        if (meta < EnumType.META_LOOKUP.length) {
            tooltip.add(UtilLib.translate(getTranslationKey() + "." + EnumType.META_LOOKUP[meta].getUnlocalizedName()));
        }
        tooltip.add("§o" + UtilLib.translate(getTranslationKey() + (meta == EnumType.DARK_STONE.getMetadata() ? ".no_spawn" : ".vampire_spawn")) + "§r");
    }


    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }


    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.values()) {
            items.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (!BlockCastleStairs.isBlockStairs(state) && state.getValue(VARIANT) == EnumType.DARK_BRICK_BLOODY) {
            if (rand.nextInt(180) == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.ambient_castle, SoundCategory.AMBIENT, 0.8F, 1.0F, false);
            }

        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    public enum EnumType implements IStringSerializable {
        DARK_BRICK(0, "dark_brick"),
        PURPLE_BRICK(1, "purple_brick"),
        DARK_BRICK_BLOODY(2, "dark_brick_bloody"),
        NORMAL_BRICK(3, "normal_brick"),
        DARK_STONE(4, "dark_stone");

        /**
         * Array of the Block's BlockStates
         */
        private static final EnumType[] META_LOOKUP = new EnumType[values().length];

        static {
            for (EnumType blockstone$enumtype : values()) {
                META_LOOKUP[blockstone$enumtype.getMetadata()] = blockstone$enumtype;
            }
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        /**
         * The BlockState's metadata.
         */
        private final int meta;

        /**
         * The EnumType's name.
         */
        private final String name;
        private final String unlocalizedName;

        EnumType(int metaIn, String nameIn) {
            this.meta = metaIn;
            this.name = nameIn;
            this.unlocalizedName = nameIn;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }

    }


}
