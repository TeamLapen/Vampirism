package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.CSimpleInputEvent;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class DBNOScreen extends Screen {

    private final ITextComponent causeOfDeath;
    private int enableButtonsTimer;
    private CooldownButton resurrectButton;
    private Button dieButton;


    public DBNOScreen(@Nullable ITextComponent textComponent) {
        super(new TranslationTextComponent("gui.vampirism.dbno.title"));
        this.causeOfDeath = textComponent;
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.causeOfDeath != null && mouseY > 85.0D && mouseY < (double) (85 + 9)) {
            Style style = this.deriveDeathMessageStyle((int) mouseX);
            if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                this.handleComponentClicked(style);
                return false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, 1615855616, -1602211792);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2 / 2, 30, 16777215);
        RenderSystem.popMatrix();
        if (this.causeOfDeath != null) {
            drawCenteredString(matrixStack, this.font, this.causeOfDeath, this.width / 2, 85, 16777215);
        }

        if (this.causeOfDeath != null && mouseY > 85 && mouseY < 85 + 9) {
            Style style = this.deriveDeathMessageStyle(mouseX);
            this.renderComponentHoverEffect(matrixStack, style, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void tick() {
        super.tick();
        ++this.enableButtonsTimer;
        if (this.enableButtonsTimer == 20) {
            dieButton.active = true;
        }
        float prog = this.minecraft.player != null ? VampirePlayer.getOpt(this.minecraft.player).map(v -> v.getDbnoTimer() / (float) v.getDbnoDuration()).orElse(1f) : 1f;
        resurrectButton.updateState(prog);

    }

    protected void init() {
        this.enableButtonsTimer = 0;
        dieButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, new TranslationTextComponent("gui.vampirism.dbno.die"), (p_213021_1_) -> {
            VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.GIVE_UP));
            this.minecraft.setScreen(null);
        }));
        dieButton.active = false;
        resurrectButton = this.addButton(new CooldownButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, new TranslationTextComponent("gui.vampirism.dbno.resurrect"), (p_213020_1_) -> {
            if (this.minecraft.player != null)
                VampirePlayer.getOpt(this.minecraft.player).ifPresent(VampirePlayer::tryResurrect);
            VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.RESURRECT));
            this.minecraft.setScreen(null);
        }));
        resurrectButton.updateState(1f);


    }

    @Nullable
    private Style deriveDeathMessageStyle(int mouseX) {
        if (this.causeOfDeath == null) {
            return null;
        } else {
            int i = this.minecraft.font.width(this.causeOfDeath);
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            return mouseX >= j && mouseX <= k ? this.minecraft.font.getSplitter().componentStyleAtWidth(this.causeOfDeath, mouseX - j) : null;
        }
    }
}