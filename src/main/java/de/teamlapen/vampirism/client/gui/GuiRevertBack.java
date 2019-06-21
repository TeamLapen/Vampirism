package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class GuiRevertBack extends GuiYesNo {

    public GuiRevertBack() {
        super(null, UtilLib.translate("gui.vampirism.revertback.head"), UtilLib.translate("gui.vampirism.revertback.desc"), 0);
    }

    @Override
    protected void initGui() {
        super.initGui();
        for (GuiButton button : this.buttons) {
            if (button.id == 0) {
                this.buttons.remove(button);
                this.addButton(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText) {
                    /**
                     * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                     */
                    public void onClick(double mouseX, double mouseY) {
                        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.REVERTBACK, "0"));//TODO Dispatcher
                        GuiRevertBack.this.mc.displayGuiScreen(null);
                    }
                });
            } else if (button.id == 1) {
                this.addButton(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText) {
                    /**
                     * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                     */
                    public void onClick(double mouseX, double mouseY) {
                        GuiRevertBack.this.mc.displayGuiScreen(null);
                    }
                });
            }
        }
    }
}
