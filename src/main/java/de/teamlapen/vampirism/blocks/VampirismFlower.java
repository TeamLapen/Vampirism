package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.item.ItemMetaBlock;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockBush;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

/**
 * Vampirism's flowers. To add one add it to {@link EnumFlowerType}
 */
public class VampirismFlower extends BlockBush implements ItemMetaBlock.IMetaItemName {
    public final static PropertyEnum<EnumFlowerType> TYPE = PropertyEnum.create("type", EnumFlowerType.class);
    private final static String regName = "vampirism_flower";

    public VampirismFlower() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumFlowerType.ORCHID));
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    public String getItemstackName(ItemStack stack) {
        return EnumFlowerType.getType(stack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    public String getRegisteredName() {
        return regName;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, EnumFlowerType.getType(meta));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumFlowerType type : EnumFlowerType.values()) {
            list.add(new ItemStack(this, 1, type.getMeta()));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    public enum EnumFlowerType implements IStringSerializable {

        ORCHID(0, "vampire_orchid", "vampire_orchid");
        private static final EnumFlowerType[] TYPE_FOR_META = new EnumFlowerType[values().length];

        static {
            for (final EnumFlowerType type : values()) {
                TYPE_FOR_META[type.getMeta()] = type;
            }
        }

        public static EnumFlowerType getType(int meta) {
            if (meta >= TYPE_FOR_META.length) {
                return TYPE_FOR_META[0];
            }
            return TYPE_FOR_META[meta];
        }

        private final int meta;
        private final String name;
        private final String unlocalizedName;

        EnumFlowerType(int meta, String name, String unlocalizedName) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }

    }
}
