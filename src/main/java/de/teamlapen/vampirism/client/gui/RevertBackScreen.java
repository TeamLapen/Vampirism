package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.CSimpleInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class RevertBackScreen extends ConfirmScreen {

    private static String getDescription() {
        String s = UtilLib.translate("gui.vampirism.revertback.desc");
        World w = Minecraft.getInstance().level;
        if (w != null && w.getLevelData().isHardcore()) {
            s += " You won't die in hardcore mode.";
        }
        return s;
    }

    public RevertBackScreen() {
        super((context) -> {
            if (context) {
                VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.REVERT_BACK));
            }
            Minecraft.getInstance().setScreen(null);
        }, new TranslationTextComponent("gui.vampirism.revertback.head"), new StringTextComponent(getDescription()));
    }
}
