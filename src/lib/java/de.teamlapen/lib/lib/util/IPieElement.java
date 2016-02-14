package de.teamlapen.lib.lib.util;

import de.teamlapen.lib.lib.gui.client.GuiPieMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * Used in {@link GuiPieMenu}
 */
public interface IPieElement {
    /**
     * Should return the location of the icon map where the icon is in
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    ResourceLocation getIconLoc();

    /**
     * @return The assigned Id
     */
    int getId();

    /**
     * Should return the min U texture coordinate within the icon map
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    int getMinU();

    /**
     * Should return the min V texture coordinate within the icon map
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    int getMinV();

    String getUnlocalizedName();
}