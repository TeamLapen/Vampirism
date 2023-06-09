package de.teamlapen.lib.lib.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link ObjectSelectionList} that can be hidden
 */
public abstract class VisibleObjectSelectionList<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {

    public boolean isVisible = true;

    public VisibleObjectSelectionList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isVisible) {
            super.render(graphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.isVisible) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.isVisible) {
            return super.mouseScrolled(pMouseX, pMouseY, pDelta);
        } else {
            return false;
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (this.isVisible) {
            return super.isMouseOver(pMouseX, pMouseY);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.isVisible) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (this.isVisible) {
            return super.mouseReleased(pMouseX, pMouseY, pButton);
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.isVisible) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        } else {
            return false;
        }
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.isVisible) {
            return super.keyReleased(pKeyCode, pScanCode, pModifiers);
        } else {
            return false;
        }
    }
}
