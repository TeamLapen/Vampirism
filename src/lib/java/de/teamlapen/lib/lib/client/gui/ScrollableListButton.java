package de.teamlapen.lib.lib.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * add this buttons first and render them last and call {@link #mouseDragged(double, double, int, double, double)}
 */
public class ScrollableListButton extends ExtendedButton {

    private static final ResourceLocation MISC = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/misc.png");
    protected final int menuSize;
    protected int itemCount;
    protected int scrolled;
    private boolean scrollerPressed;
    protected final Button[] elements;
    private final Consumer<Integer> pressConsumer;
    private final ITextComponent[] desc;
    private final boolean alternate;

    public ScrollableListButton(int xPos, int yPos, int width, int shownItems, int maxItemCount, @Nullable ITextComponent[] strings, ITextComponent displayString, Consumer<Integer> elementPressAction, boolean alternate) {
        super(xPos, yPos + 1, width, Math.min(shownItems, maxItemCount) * 20, displayString, button -> {
        });
        this.itemCount = maxItemCount;
        this.menuSize = shownItems;
        this.visible = true;
        this.elements = new Button[menuSize];
        this.pressConsumer = elementPressAction;
        this.desc = strings;
        this.alternate = alternate;
        this.fillElements();
    }

    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemCount, ITextComponent[] strings, ITextComponent displayString, Consumer<Integer> elementPressAction) {
        this(xPos, yPos, width, height, itemCount, strings, displayString, elementPressAction, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (this.visible) {
            this.scrollerPressed = false;
            if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + height) {
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

    @Override
    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hLine(mStack, this.x, this.x + this.width, this.y - 1, alternate ? 0xff373737 : 0xff000000);
            GuiUtils.drawContinuousTexturedBox(mStack, MISC, this.x + width - 8, this.y - 1, alternate ? 23 : 0, 0, 9, this.height + 2, 9, 200, 2, 2, 2, 2, this.getBlitOffset());
            this.renderScroller(mStack);
            this.renderListButtons(mStack, mouseX, mouseY, partialTicks);
            if (this.elements.length != 0 && this.elements[this.elements.length - 1].visible) {
                this.hLine(mStack, this.x, this.x + this.width, this.y + this.height, alternate ? 0xffffffff : 0xff000000);
            }
        }
    }

    protected void fillElements() {
        for (int i = 0; i < this.elements.length; i++) {
            int finalI = i;
            this.elements[i] = new ExtendedButton(this.x, this.y + i * 20, width - 7, 20, new StringTextComponent(""), (button -> this.pressConsumer.accept(finalI + this.scrolled)));
        }
    }

    protected void renderScroller(MatrixStack mStack) {
        Minecraft.getInstance().textureManager.bindTexture(MISC);
        int i = this.itemCount - this.menuSize;
        if (i >= 1) {
            float k = (float) (this.height + 3 - 30) / i;
            int i1 = Math.min(this.height + 3 - 30, (int) (this.scrolled * k));
            if (this.scrolled >= i) {
                i1 = this.height + 3 - 30;
            }
            blit(mStack, x + this.width - 7, y + i1, this.getBlitOffset(), (alternate ? 23 : 0) + 10 - 1, 0, 7, 27, 256, 256);
        } else {
            //blit(mStack, x + this.width - 7, y, this.getBlitOffset(), (alternate ? 23 : 0) + 10 + 6, 0, 7, this.elements.length == 1 ? 20 : 27, 256, 256); Don't render (disabled) scroller if there are not enough items
        }
    }

    private void renderListButtons(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < this.elements.length; i++) {
            this.elements[i].visible = itemCount > menuSize || i < itemCount;
            if (this.elements[i].visible) {
                this.elements[i].render(mStack, mouseX, mouseY, partialTicks);
                ITextComponent desc = this.desc != null ? this.desc[this.scrolled + i] : new StringTextComponent("Type " + (i + this.scrolled + 1));
                int x = this.x + (this.width - 8) / 2 - Minecraft.getInstance().fontRenderer.getStringPropertyWidth(desc) / 2;
                Minecraft.getInstance().fontRenderer.func_243246_a(mStack, desc, x, this.y + 6 + i * 20, this.elements[i].getFGColor());
            }
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