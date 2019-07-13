package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
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
        }, new TranslationTextComponent("revertbackscreen_title"), new TranslationTextComponent("doyouwanttorevertback"));//TODO 1.14 name
        this.confirmButtonText = UtilLib.translate("gui.vampirism.revertback.head");
        this.cancelButtonText = UtilLib.translate("gui.vampirism.revertback.desc");
    }
}
