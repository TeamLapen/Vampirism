package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

/**
 * Very similar to GuiSleepMP, but for coffin sleep
 */
@OnlyIn(Dist.CLIENT)
public class SleepCoffinScreen extends ChatScreen {

    public SleepCoffinScreen() {
        super("");
    }

    @Override
    public void init() {
        super.init();
        this.buttons.add(new Button(this.width / 2 - 100, this.height - 40, 150, 20, I18n.format("multiplayer.stopSleeping"), (context) -> VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.WAKEUP, ""))));
    }

    @Override
    public boolean keyPressed(int key1, int key2, int key3) {
        if (key1 == GLFW.GLFW_KEY_ESCAPE) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.WAKEUP, ""));
            return true;
        } else if (key1 != GLFW.GLFW_KEY_ENTER && key1 != GLFW.GLFW_KEY_KP_ENTER) {
            super.keyPressed(key1, key2, key3);
            return false;
        } else {
            String s = this.inputField.getText().trim();

            if (!s.isEmpty()) {
                this.sendMessage(s);
            }

            this.inputField.setText("");
            this.minecraft.ingameGUI.getChatGUI().resetScroll();
            return true;
        }
    }
}
