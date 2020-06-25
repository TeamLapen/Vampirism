package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

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

    public AppearanceScreen(ITextComponent titleIn, T entity) {
        super(titleIn);
        this.entity = entity;
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
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();

        this.renderGuiBackground();

        this.drawTitle();
        InventoryScreen.drawEntityOnScreen(this.guiLeft + 200, this.guiTop + 145, 60, (float) (this.guiLeft + 200) - mouseX, (float) (this.guiTop + 45) - mouseY, this.entity);

        super.render(mouseX, mouseY, partialTicks);

        for (Button button : this.buttons) {
            button.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addButton(new Button(this.guiLeft + 5, this.guiTop + 152, 80, 20, UtilLib.translate("gui.done"), (context) -> {
            this.onClose();
        }));
    }

    protected void renderGuiBackground() {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(this.guiLeft, this.guiTop, this.getBlitOffset(), 0, 0, this.xSize, this.ySize, 256, 300);
    }

    private void drawTitle() {
        String title = this.title.getFormattedText();
        this.font.drawStringWithShadow(title, this.guiLeft + 15, this.guiTop + 5, 0xFFFFFFFF);
    }
}