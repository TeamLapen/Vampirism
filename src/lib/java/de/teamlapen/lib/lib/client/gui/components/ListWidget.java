package de.teamlapen.lib.lib.client.gui.components;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LayoutSettings;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListWidget extends AbstractContainerWidget {

    private final List<ChildContainer> children = new ArrayList<>();
    @SuppressWarnings("StaticPseudoFunctionalStyleMethod")
    private final List<AbstractWidget> containedChildrenView = Collections.unmodifiableList(Lists.transform(this.children, (p_254146_) -> {
        return p_254146_.child;
    }));
    private final LayoutSettings childLayoutSettings = LayoutSettings.defaults();

    public ListWidget() {
        super(0, 0, 0, 0, Component.empty());
    }
    public ListWidget(int xPos, int yPos, int width) {
        super(xPos, yPos, width, 0, Component.empty());
    }

    public void addWidget(AbstractWidget widget) {
        this.children.add(new ChildContainer(widget, this.childLayoutSettings));
    }

    public void addWidget(AbstractWidget widget, LayoutSettings layoutSettings) {
        this.children.add(new ChildContainer(widget, layoutSettings));
    }

    public void addWidget(int index, AbstractWidget widget) {
        this.children.add(index, new ChildContainer(widget, this.childLayoutSettings));
    }

    public void addWidget(int index, AbstractWidget widget, LayoutSettings layoutSettings) {
        this.children.add(index, new ChildContainer(widget, layoutSettings));
    }

    public void addWidgetAfter(AbstractWidget index, AbstractWidget widget) {
        this.addWidget(this.containedChildrenView.indexOf(index) + 1, widget);
    }

    public void addWidgetAfter(AbstractWidget index, AbstractWidget widget, LayoutSettings layoutSettings) {
        this.addWidget(this.containedChildrenView.indexOf(index) + 1, widget, layoutSettings);
    }

    public void removeWidget(AbstractWidget widget) {
        int i = this.containedChildrenView.indexOf(widget);
        if (i != -1) {
            this.children.remove(i);
        }
    }

    public void pack() {
        int currentY = this.getY();
        for (ChildContainer child : this.children) {
            currentY += child.layoutSettings.paddingTop;
            child.child.setPosition(this.getX() + child.layoutSettings.paddingLeft, currentY);
            currentY += child.child.getHeight() + child.layoutSettings.paddingBottom;
        }
        this.setHeight(currentY);
    }

    public LayoutSettings childLayoutSettings() {
        return childLayoutSettings;
    }

    @Override
    protected @NotNull List<? extends AbstractWidget> getContainedChildren() {
        return containedChildrenView;
    }

    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    public void reset() {
        this.children.clear();
    }

    private static class ChildContainer extends AbstractContainerWidget.AbstractChildWrapper {
        public ChildContainer(AbstractWidget widget, LayoutSettings layoutSettings) {
            super(widget, layoutSettings);
        }
    }
}
