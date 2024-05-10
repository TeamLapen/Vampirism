package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;


public class RevertBackScreen extends ConfirmScreen {

    protected static Component getDescription() {
        var text = Component.translatable("gui.vampirism.revertback.desc");
        Level w = Minecraft.getInstance().level;
        if (w != null && w.getLevelData().isHardcore()) {
            text = text.append(" You won't die in hardcore mode.");
        }
        return text;
    }

    public RevertBackScreen() {
        super((context) -> {
            if (context) {
                VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.REVERT_BACK));
            }
            Minecraft.getInstance().setScreen(null);
        }, Component.translatable("gui.vampirism.revertback.head"), getDescription());
    }
}
