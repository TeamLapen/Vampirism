package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.gui.components.CooldownButton;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Optional;

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

    @Override
    public boolean mouseClicked(MouseButtonEvent p_446287_, boolean p_433128_) {
        ActiveTextCollector.ClickableStyleFinder activetextcollector$clickablestylefinder = new ActiveTextCollector.ClickableStyleFinder(
                this.getFont(), (int)p_446287_.x(), (int)p_446287_.y()
        );
        this.visitText(activetextcollector$clickablestylefinder);
        Style style = activetextcollector$clickablestylefinder.result();
        return style != null && style.getClickEvent() instanceof ClickEvent.OpenUrl(URI uri)
                ? clickUrlAction(this.minecraft, this, uri)
                : super.mouseClicked(p_446287_, p_433128_);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR));

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
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.GIVE_UP));
            this.minecraft.setScreen(null);
        }));
        dieButton.active = false;
        resurrectButton = this.addRenderableWidget(new CooldownButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, Component.translatable("gui.vampirism.dbno.resurrect"), (p_213020_1_) -> {
            if (this.minecraft.player != null) {
                VampirePlayer.get(this.minecraft.player).tryResurrect();
            }
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.RESURRECT));
            this.minecraft.setScreen(null);
        }));
        resurrectButton.updateState(1f);


    }

    private void visitText(ActiveTextCollector collector) {
        ActiveTextCollector.Parameters activetextcollector$parameters = collector.defaultParameters();
        int i = this.width / 2;
        collector.defaultParameters(activetextcollector$parameters.withScale(2.0F));
        collector.accept(TextAlignment.CENTER, i / 2, 30, this.title);
        collector.defaultParameters(activetextcollector$parameters);
        if (this.causeOfDeath != null) {
            collector.accept(TextAlignment.CENTER, i, 85, this.causeOfDeath);
        }
    }
}