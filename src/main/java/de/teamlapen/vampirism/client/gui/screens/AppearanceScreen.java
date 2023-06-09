package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class AppearanceScreen<T extends LivingEntity> extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/appearance.png");

    protected final T entity;
    protected final int xSize = 256;
    protected final int ySize = 177;
    @Nullable
    private final Screen backScreen;
    protected int guiLeft;
    protected int guiTop;

    public AppearanceScreen(@NotNull Component titleIn, T entity, @Nullable Screen backScreen) {
        super(titleIn);
        this.entity = entity;
        this.backScreen = backScreen;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        this.renderGuiBackground(graphics);

        this.drawTitle(graphics);
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.guiLeft + 200, this.guiTop + 145, 60, (float) (this.guiLeft + 200) - mouseX, (float) (this.guiTop + 45) - mouseY, this.entity);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addRenderableWidget(new ExtendedButton(this.guiLeft + this.xSize - 80 - 10, this.guiTop + 152, 80, 20, Component.translatable("gui.done"), (context) -> this.onClose()));
        if (this.backScreen != null) {
            this.addRenderableWidget(new ExtendedButton(this.guiLeft + 10, this.guiTop + 152, 80, 20, Component.translatable("gui.back"), (context) -> {
                if (this.minecraft != null) this.minecraft.setScreen(this.backScreen);
            }));
        }
    }

    protected void renderGuiBackground(@NotNull GuiGraphics graphics) {
        graphics.blit(BACKGROUND, this.guiLeft, this.guiTop, 0, 0, 0, this.xSize, this.ySize, 300, 256);
    }

    private void drawTitle(@NotNull GuiGraphics graphics) {
        graphics.drawString(this.font, title, this.guiLeft + 15, this.guiTop + 5, 0xFFFFFFFF, true);
    }
}