package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Temporary warning screen until Optifine fixes stuff
 */
@OnlyIn(Dist.CLIENT)
public class OptifineWarningScreen extends Screen {
    private static final ITextComponent HEADER = (new StringTextComponent("Optifine + Shaders messes up world generation")).mergeStyle(TextFormatting.BOLD);
    private static final ITextComponent TEXT = new StringTextComponent("Unfortunately, Optifine is breaking mods that introduce new biomes when you have shaders enabled.\n You can proceed, but be aware there might be crashes or broken worlds.\nPlease disable shaders or uninstall Optifine and restart the game.\nMessage by Vampirism");
    private static final ITextComponent CONFIRM = new StringTextComponent("Continue on my own risk");
    private static final ITextComponent ABORT = new StringTextComponent("Back to menu");
    private final Screen originalScreen;
    private IBidiRenderer field_243364_s = IBidiRenderer.field_243257_a;

    public OptifineWarningScreen(Screen p) {
        super(NarratorChatListener.EMPTY);
        this.originalScreen = p;
    }

    public String getNarrationMessage() {
        return HEADER.getString();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(0);
        drawCenteredString(matrixStack, this.font, HEADER, this.width / 2, 30, 16777215);
        this.field_243364_s.func_241863_a(matrixStack, this.width / 2, 70);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void init() {
        super.init();
        this.field_243364_s = IBidiRenderer.func_243258_a(this.font, TEXT, this.width - 50);
        int i = (this.field_243364_s.func_241862_a() + 1) * 9;
        this.addButton(new Button(this.width / 2 - 155, 100 + i, 150, 20, CONFIRM, (p_230165_1_) -> {
            ClientEventHandler.skipOptifineWarningOnce = true;
            this.minecraft.displayGuiScreen(originalScreen);
        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, ABORT, (p_230164_1_) -> {
            this.minecraft.displayGuiScreen(new MainMenuScreen());
        }));
    }
}