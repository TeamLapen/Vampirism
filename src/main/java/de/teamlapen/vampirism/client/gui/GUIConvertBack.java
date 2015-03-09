package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;

public class GUIConvertBack extends GuiYesNo {

	public GUIConvertBack() {
		super(null, I18n.format("gui.vampirism:revertback.head", new Object[0]), I18n.format("gui.vampirism:revertback.desc", new Object[0]), 0);
	}

	
    protected void actionPerformed(GuiButton p_146284_1_)
    {
    	boolean result=(p_146284_1_.id == 0);
        if(result){
        	VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.REVERTBACK,"0"));
        }
        this.mc.displayGuiScreen(null);
    }

}
