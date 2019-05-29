package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiRevertBack extends GuiYesNo {

    private static String getDescription() {
        String s = UtilLib.translate("gui.vampirism.revertback.desc");
        World w = Minecraft.getMinecraft().world;
        if (w != null && w.getWorldInfo().isHardcoreModeEnabled()) {
            s += " You won't die in hardcore mode.";
        }
        return s;
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        boolean result = (p_146284_1_.id == 0);
        if (result) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.REVERTBACK, "0"));
        }
        this.mc.displayGuiScreen(null);
    }

    public GuiRevertBack() {
        super(null, UtilLib.translate("gui.vampirism.revertback.head"), getDescription(), 0);
    }
}
