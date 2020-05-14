package de.teamlapen.lib.client.gui;

import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.function.Consumer;

/*
add this buttons first and render them last
 */
public class ScrollableListButton extends GuiButtonExt {

    private static final ResourceLocation MISC = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/misc.png");
    private final int menuSize;
    private int itemCount;
    private int scrolled;
    private boolean scrollerPressed;
    private Button[] elements;
    private Consumer<Integer> pressConsumer;
    private String[] desc;

    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemCount, int menuSize, String[] strings, String displayString, Consumer<Integer> elementPressAction) {
        super(xPos, yPos, width, height, displayString, button -> {
        });
        if (strings.length < itemCount) throw new IllegalStateException("String array size must be >= itemCount");
        this.itemCount = itemCount;
        this.menuSize = menuSize;
        this.visible = true;
        this.elements = new Button[menuSize];
        this.pressConsumer = elementPressAction;
        this.desc = strings;

        for (int i = 0; i < this.elements.length; i++) {
            int finalI = i;
            this.elements[i] = new GuiButtonExt(xPos, yPos + i * 20, width - 8, 20, "", (button -> this.pressConsumer.accept(finalI + this.scrolled)));
        }
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Minecraft.getInstance().textureManager.bindTexture(MISC);
            blit(this.x + this.width - 8, this.y, this.blitOffset, 1, 0, 8, this.height, 256, 256);
            blit(this.x + this.width - 8, this.y + this.height - 2, this.blitOffset, 1, 198, 8, 2, 256, 256);
            this.renderScroller();
            this.renderListButtons(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (this.visible) {
            this.scrollerPressed = false;
            if (this.itemCount - this.menuSize >= 0 && mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + height) {
                if (this.itemCount - this.menuSize > 0 && mouseX > this.x + this.width - 8) {
                    this.scrollerPressed = true;
                }
                for (Button button : this.elements) {
                    if (button.mouseClicked(mouseX, mouseY, buttonId)) {
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, buttonId);
    }

    private void renderListButtons(int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < this.elements.length; i++) {
            this.elements[i].render(mouseX, mouseY, partialTicks);
            String desc = this.desc[this.scrolled + i];
            int x = this.x + (this.width - 8) / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(desc) / 2;
            Minecraft.getInstance().fontRenderer.drawStringWithShadow(desc, x, this.y + 6 + i * 20, this.elements[i].getFGColor());
        }
    }

    private void renderScroller() {
        Minecraft.getInstance().textureManager.bindTexture(MISC);
        int i = this.itemCount - this.menuSize;
        if (i >= 1) {
            i += 2;
            int k = this.height - (2 + (i - 1) * this.height / i);
            int i1 = Math.min(this.height - 30, this.scrolled * k);
            if (this.scrolled + 2 >= i) {
                i1 = this.height - 30;
            }
            blit(x + 93, y + 2 + i1, this.blitOffset, 9, 0, 6, 27, 256, 256);
        } else {
            blit(x + 93, y + 2, this.blitOffset, 9 + 6, 0, 6, 27, 256, 256);
        }
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        int scrollItems = this.itemCount - this.menuSize;
        if (scrollItems > 0) {
            this.scrolled = (int) ((double) this.scrolled - p_mouseScrolled_5_);
            this.scrolled = MathHelper.clamp(this.scrolled, 0, scrollItems);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (scrollerPressed) {
            float amount = ((float) p_mouseDragged_3_ - (float) this.y - 13.5F) / ((float) (this.height) - 27.0F);
            amount = amount * (float) (this.itemCount - this.menuSize) + 0.5f;
            this.scrolled = MathHelper.clamp((int) amount, 0, this.itemCount - this.menuSize);
            return true;
        } else {
            return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
        }
    }
}
