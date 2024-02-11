package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.client.gui.ProgressBar;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.FogDiffuserBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FogDiffuserScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/fog_diffuser.png");
    protected final int xSize = 220;
    protected final int ySize = 79;
    protected int guiLeft;
    protected int guiTop;

    private final FogDiffuserBlockEntity blockEntity;
    private ProgressBar startupProgress;

    public FogDiffuserScreen(FogDiffuserBlockEntity blockEntity, Component pTitle) {
        super(pTitle);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.startupProgress = this.addRenderableWidget(new ProgressBar(this.guiLeft + 25, this.guiTop + 30, 170, Component.translatable("gui.vampirism.garlic_diffuser.startup")));
        this.startupProgress.setColor(0xaaaaaa);
    }

    @Override
    public void tick() {
        updateStartup();
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTitle(pGuiGraphics);
    }

    private void renderTitle(@NotNull GuiGraphics pGuiGraphics) {
        pGuiGraphics.drawString(this.font, title, this.guiLeft + 15, this.guiTop + 5, -1);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderGuiBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderGuiBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        pGuiGraphics.blit(BACKGROUND, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize, 256, 256);
    }

    protected void updateStartup() {
        this.startupProgress.setProgress(this.blockEntity.getBootProgress());
        this.startupProgress.setMessage(this.getStartupText());
    }

    protected Component getStartupText() {
        return switch (this.blockEntity.getState()) {
            case BOOTING -> Component.translatable("text.vampirism.fog_diffuser.booting");
            case IDLE -> Component.translatable("text.vampirism.fog_diffuser.idle");
            case ACTIVE -> Component.translatable("text.vampirism.fog_diffuser.active");
        };
    }
}
