package de.teamlapen.vampirism.api;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumColor implements IStringSerializable {
    REDBLACK(0, "red_black"), BLACKRED(1, "black_red"), BLACKWHITE(2, "black_white"), WHITEBLACK(3,
            "white_black"), BLACKBLUE(4, "black_blue");

    private static final EnumColor[] META_LOOKUP = new EnumColor[values().length];
    private final int meta;
    private final String name;

    private EnumColor(int metaIn, String nameIn) {
        this.meta = metaIn;
        this.name = nameIn;
    }

    /**
     * @return color index
     */
    public int getMetadata() {
        return this.meta;
    }

    /**
     * @return color name
     */
    @SideOnly(Side.CLIENT)
    public String getDyeColorName() {
        return this.name;
    }

    /**
     * search for color by the given index
     * 
     * @param index
     * @return color enumtype
     */
    public static EnumColor byMetadata(int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
        }

        return META_LOOKUP[meta];
    }

    /**
     * @return color name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return color unlocalized name
     */
    public String getUnlocalizedName() {
        return this.name;
    }

    static {
        for (EnumColor enumdyecolor : values()) {
            META_LOOKUP[enumdyecolor.getMetadata()] = enumdyecolor;
        }
    }

}