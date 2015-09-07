package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class GUISleepCoffin extends GuiSleepMP {
	@Override
	protected void actionPerformed(GuiButton p_146284_1_) throws IOException{
		if (p_146284_1_.id == 1) {
			this.func_146418_g();
		} else {
			super.actionPerformed(p_146284_1_);
		}
	}

	private void func_146418_g() {
		VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.LEAVE_COFFIN, ""));
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		super.initGui();
		if (this.buttonList.size() > 0) {
			((GuiButton) this.buttonList.get(0)).displayString = I18n.format("text.vampirism.coffin.stopsleeping", new Object[0]);
		}

		// this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, ));
	}

	/**
	 * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char p_73869_1_, int p_73869_2_) throws IOException{
		if (p_73869_2_ == 1) {
			this.func_146418_g();
		} else if (p_73869_2_ != 28 && p_73869_2_ != 156) {
			super.keyTyped(p_73869_1_, p_73869_2_);
		} else {
			String s = this.inputField.getText().trim();

			this.inputField.setText("");
			this.mc.ingameGUI.getChatGUI().resetScroll();
		}
	}

}
