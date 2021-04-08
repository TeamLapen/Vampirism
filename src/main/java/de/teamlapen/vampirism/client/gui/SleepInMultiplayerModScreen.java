package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.util.text.TranslationTextComponent;

public class SleepInMultiplayerModScreen extends SleepInMultiplayerScreen {

    private final String leaveText;

    public SleepInMultiplayerModScreen(String text) {
        this.leaveText = text;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.get(0).setMessage(new TranslationTextComponent(leaveText));
    }
}
