package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;


public class GuiRevertBack extends GuiYesNo {

    public GuiRevertBack() {
        super(null, I18n.format("gui.vampirism.revertback.head"), I18n.format("gui.vampirism.revertback.desc"), 0);
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
