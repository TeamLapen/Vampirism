package de.teamlapen.lib.lib.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

/**
 * A simple {@link ObjectSelectionList} that can be hidden
 */
public abstract class VisibleObjectSelectionList<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {

    public VisibleObjectSelectionList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pItemHeight);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.visible) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(double p_93416_, double p_93417_, double p_93418_, double p_294369_) {
        if (this.visible) {
            return super.mouseScrolled(p_93416_, p_93417_, p_93418_, p_294369_);
        } else {
            return false;
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (this.visible) {
            return super.isMouseOver(pMouseX, pMouseY);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.visible) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (this.visible) {
            return super.mouseReleased(pMouseX, pMouseY, pButton);
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.visible) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        } else {
            return false;
        }
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.visible) {
            return super.keyReleased(pKeyCode, pScanCode, pModifiers);
        } else {
            return false;
        }
    }
}
