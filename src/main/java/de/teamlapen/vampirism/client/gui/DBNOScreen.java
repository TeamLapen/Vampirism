package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.gui.widget.CooldownButton;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DBNOScreen extends Screen {

    private final @Nullable Component causeOfDeath;
    private int enableButtonsTimer;
    private CooldownButton resurrectButton;
    private Button dieButton;


    public DBNOScreen(@Nullable Component textComponent) {
        super(Component.translatable("gui.vampirism.dbno.title"));
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

    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, 1615855616, -1602211792);
        matrixStack.pushPose();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2 / 2, 30, 16777215);
        matrixStack.popPose();
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
        dieButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, Component.translatable("gui.vampirism.dbno.die"), (p_213021_1_) -> {
            VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.GIVE_UP));
            this.minecraft.setScreen(null);
        }));
        dieButton.active = false;
        resurrectButton = this.addRenderableWidget(new CooldownButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, Component.translatable("gui.vampirism.dbno.resurrect"), (p_213020_1_) -> {
            if (this.minecraft.player != null) {
                VampirePlayer.getOpt(this.minecraft.player).ifPresent(VampirePlayer::tryResurrect);
            }
            VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.RESURRECT));
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