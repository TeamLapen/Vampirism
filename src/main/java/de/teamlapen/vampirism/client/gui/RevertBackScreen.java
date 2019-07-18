package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class RevertBackScreen extends ConfirmScreen {

    public RevertBackScreen() {
        super((context) -> {
            if (context) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.REVERTBACK, "0"));
                Minecraft.getInstance().displayGuiScreen(null);
            } else {
                Minecraft.getInstance().displayGuiScreen(null);
            }
        }, new TranslationTextComponent("gui.vampirism.revertback.head"), new TranslationTextComponent("gui.vampirism.revertback.desc"));
    }
}
