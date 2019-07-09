package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class RevertBackScreen extends ConfirmScreen {

    public RevertBackScreen() {
        super(null, UtilLib.translate("gui.vampirism.revertback.head"), UtilLib.translate("gui.vampirism.revertback.desc"), 0);
    }

    @Override
    protected void initGui() {
        super.initGui();
        for (Button button : this.buttons) {
            if (button.id == 0) {
                this.buttons.remove(button);
                this.addButton(new OptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText) {
                    /**
                     * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                     */
                    public void onClick(double mouseX, double mouseY) {
                        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.REVERTBACK, "0"));//TODO Dispatcher
                        RevertBackScreen.this.mc.displayGuiScreen(null);
                    }
                });
            } else if (button.id == 1) {
                this.addButton(new OptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText) {
                    /**
                     * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                     */
                    public void onClick(double mouseX, double mouseY) {
                        RevertBackScreen.this.mc.displayGuiScreen(null);
                    }
                });
            }
        }
    }
}
