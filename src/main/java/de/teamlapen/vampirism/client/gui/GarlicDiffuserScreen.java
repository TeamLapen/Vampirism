package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.ProgressBar;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.GarlicDiffuserBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GarlicDiffuserScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/garlic_diffuser.png");
    protected final int xSize = 220;
    protected final int ySize = 114;
    private final GarlicDiffuserBlockEntity tile;
    protected int guiLeft;
    protected int guiTop;
    protected ProgressBar startupBar;
    protected ProgressBar fueledTimer;

    public GarlicDiffuserScreen(GarlicDiffuserBlockEntity tile, @NotNull Component title) {
        super(title);
        this.tile = tile;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);

        this.renderGuiBackground(mStack);

        this.drawTitle(mStack);

        super.render(mStack, mouseX, mouseY, partialTicks);

    }

    @Override
    public void tick() {
        startupBar.setProgress(tile.getBootProgress());
        float f = tile.getFueledState();
        if (f == 0) {
            fueledTimer.active = false;
        } else {
            fueledTimer.active = true;
            fueledTimer.setProgress(f);
        }

    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        startupBar = this.addRenderableWidget(new ProgressBar(this, this.guiLeft + (xSize - 170) / 2, this.guiTop + 30, 170, Component.translatable("gui.vampirism.garlic_diffuser.startup")));
        startupBar.setColor(0xD0D0FF);
        startupBar.setFGColor(0xFFFFFF);

        fueledTimer = this.addRenderableWidget(new ProgressBar(this, this.guiLeft + (xSize - 170) / 2, this.guiTop + 60, 170, Component.translatable("gui.vampirism.garlic_diffuser.fueled")));
        fueledTimer.setColor(0xD0FFD0);
        fueledTimer.setFGColor(0xFFFFFF);
    }

    protected void renderGuiBackground(@NotNull PoseStack mStack) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(mStack, this.guiLeft, this.guiTop, this.getBlitOffset(), 0, 0, this.xSize, this.ySize, 256, 256);
    }

    private void drawTitle(@NotNull PoseStack mStack) {
        this.font.drawShadow(mStack, title, this.guiLeft + 15, this.guiTop + 5, 0xFFFFFFFF);
    }
}
