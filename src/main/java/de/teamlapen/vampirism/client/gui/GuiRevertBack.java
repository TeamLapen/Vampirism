package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiRevertBack extends GuiYesNo {

    public GuiRevertBack() {
        super(null, UtilLib.translate("gui.vampirism.revertback.head"), UtilLib.translate("gui.vampirism.revertback.desc"), 0);
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        boolean result = (p_146284_1_.id == 0);
        if (result) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.REVERTBACK, "0"));
        }
        this.mc.displayGuiScreen(null);
    }

}
