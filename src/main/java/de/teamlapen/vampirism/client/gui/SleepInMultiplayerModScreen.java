package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class SleepInMultiplayerModScreen extends SleepInMultiplayerScreen {

    private String leaveText;

    public SleepInMultiplayerModScreen(String text) {
        this.leaveText = text;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20, I18n.format(leaveText), (p_212998_1_) -> this.wakeFromSleep()));
    }
}
