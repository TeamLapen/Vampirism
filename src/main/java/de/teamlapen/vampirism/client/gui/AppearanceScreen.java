package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AppearanceScreen<T extends LivingEntity> extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/appearance.png");

    protected final T entity;
    protected final int xSize = 256;
    protected final int ySize = 177;
    private final List<Button> buttons = Lists.newArrayList();
    protected int guiLeft;
    protected int guiTop;
    @Nullable
    private final Screen backScreen;

    public AppearanceScreen(ITextComponent titleIn, T entity, @Nullable Screen backScreen) {
        super(titleIn);
        this.entity = entity;
        this.backScreen = backScreen;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        for (Button button : this.buttons) {
            if (button.visible && button.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);

        this.renderGuiBackground(mStack);

        this.drawTitle(mStack);
        InventoryScreen.drawEntityOnScreen(this.guiLeft + 200, this.guiTop + 145, 60, (float) (this.guiLeft + 200) - mouseX, (float) (this.guiTop + 45) - mouseY, this.entity);

        super.render(mStack, mouseX, mouseY, partialTicks);

        for (Button button : this.buttons) {
            button.render(mStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void init() {
        this.buttons.clear();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addButton(new Button(this.guiLeft + this.xSize - 80 - 10, this.guiTop + 152, 80, 20, new TranslationTextComponent("gui.done"), (context) -> {
            this.closeScreen();
        }));
        if (this.backScreen != null) {
            this.addButton(new Button(this.guiLeft + 10, this.guiTop + 152, 80, 20, new TranslationTextComponent("gui.back"), (context) -> {

                if (this.minecraft != null) this.minecraft.displayGuiScreen(this.backScreen);
            }));
        }
    }

    protected void renderGuiBackground(MatrixStack mStack) {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(mStack, this.guiLeft, this.guiTop, this.getBlitOffset(), 0, 0, this.xSize, this.ySize, 256, 300);
    }

    private void drawTitle(MatrixStack mStack) {
        this.font.func_243246_a(mStack, title, this.guiLeft + 15, this.guiTop + 5, 0xFFFFFFFF);
    }
}