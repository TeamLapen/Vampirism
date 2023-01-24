package de.teamlapen.vampirism.client.gui.screens.radial;

import de.teamlapen.lib.lib.client.gui.components.ScrollWidget;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.IRadialMenuSlot;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class ReorderingItemWidget<T> extends AbstractButton implements ScrollWidget.ItemWidget<IRadialMenuSlot<ItemWrapper<T>>> {

    private final IRadialMenuSlot<ItemWrapper<T>> item;
    private final Consumer<ReorderingItemWidget<T>> onClick;

    public ReorderingItemWidget(@NotNull IRadialMenuSlot<ItemWrapper<T>> item, int pX, int pY, int pWidth, int pHeight, Consumer<ReorderingItemWidget<T>> onClick) {
        super(pX, pY, pWidth, pHeight, item.slotName());
        this.item = item;
        this.onClick = onClick;
    }

    @Override
    public void onPress() {
        this.onClick.accept(this);
    }

    @Override
    public IRadialMenuSlot<ItemWrapper<T>> getItem() {
        return this.item;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
    }
}
