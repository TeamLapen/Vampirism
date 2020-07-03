package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class SleepInMultiplayerModScreen extends SleepInMultiplayerScreen {

    private final String leaveText;

    public SleepInMultiplayerModScreen(String text) {
        this.leaveText = text;
    }

    @Override
    protected void func_231160_c_() {
        super.func_231160_c_();
        this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 100, this.field_230709_l_ - 40, 200, 20, new TranslationTextComponent(leaveText), (p_212998_1_) -> this.wakeFromSleep()));
    }
}
