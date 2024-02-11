package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.gui.components.CooldownButton;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
        graphics.pose().pushPose();
        graphics.pose().scale(2.0F, 2.0F, 2.0F);
        graphics.drawCenteredString(this.font, this.title, this.width / 2 / 2, 30, 16777215);
        graphics.pose().popPose();
        if (this.causeOfDeath != null) {
            graphics.drawCenteredString(this.font, this.causeOfDeath, this.width / 2, 85, 16777215);
        }

        if (this.causeOfDeath != null && mouseY > 85 && mouseY < 85 + 9) {
            Style style = this.deriveDeathMessageStyle(mouseX);
            graphics.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
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
        resurrectButton.updateState(Optional.ofNullable(this.minecraft.player).map(VampirePlayer::get).filter(v -> v.getDbnoDuration() > 0).map(v -> v.getDbnoTimer() / (float) v.getDbnoDuration()).orElse(0f));
    }

    protected void init() {
        this.enableButtonsTimer = 0;
        dieButton = this.addRenderableWidget(new ExtendedButton(this.width / 2 - 100, this.height / 4 + 72, 200, 20, Component.translatable("gui.vampirism.dbno.die"), (p_213021_1_) -> {
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.GIVE_UP));
            this.minecraft.setScreen(null);
        }));
        dieButton.active = false;
        resurrectButton = this.addRenderableWidget(new CooldownButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, Component.translatable("gui.vampirism.dbno.resurrect"), (p_213020_1_) -> {
            if (this.minecraft.player != null) {
                VampirePlayer.get(this.minecraft.player).tryResurrect();
            }
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.RESURRECT));
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