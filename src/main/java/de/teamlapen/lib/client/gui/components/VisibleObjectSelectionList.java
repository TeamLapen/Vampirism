package de.teamlapen.lib.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link ObjectSelectionList} that can be hidden
 */
public abstract class VisibleObjectSelectionList<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {

    public VisibleObjectSelectionList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pItemHeight);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleCLick) {
        if (this.visible) {
            return super.mouseClicked(event, doubleCLick);
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
    public boolean mouseDragged(@NotNull MouseButtonEvent event, double pDragX, double pDragY) {
        if (this.visible) {
            return super.mouseDragged(event, pDragX, pDragY);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        if (this.visible) {
            return super.mouseReleased(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (this.visible) {
            return super.keyPressed(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent event) {
        if (this.visible) {
            return super.keyReleased(event);
        } else {
            return false;
        }
    }
}
