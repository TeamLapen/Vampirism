package de.teamlapen.gui.components;

import com.google.common.collect.Streams;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullUnmarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DropdownWidget<TData> extends AbstractWidget implements IRenderLast {

    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            FIdentifier.mc("widget/button"),
            FIdentifier.mc("widget/button_disabled"),
            FIdentifier.mc("widget/button_highlighted")
    );

    private final List<DropdownEntry<TData>> entries = new ArrayList<>();
    private final int itemHeight;
    private final int maxVisibleItems;
    @Nullable
    private final Consumer<TData> onSelect;
    @Nullable
    private final BiConsumer<TData, Boolean> onHover;

    private boolean expanded = false;
    private int selectedIndex = 0;
    private int hoveredIndex = -1;
    private int scrollOffset = 0;

    public DropdownWidget(int x, int y, int width, int itemHeight, int maxVisibleItems,
                          List<Value<TData>> items, @Nullable TData initialSelection,
                          @Nullable Consumer<TData> onSelect, @Nullable BiConsumer<TData, Boolean> onHover) {
        int initialIndex = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).data().equals(initialSelection)) {
                initialIndex = i;
                break;
            }
        }
        super(x, y, width, itemHeight, items.isEmpty() ? Component.empty() : items.get(initialIndex).text());
        this.itemHeight = itemHeight;
        this.maxVisibleItems = maxVisibleItems;
        this.onSelect = onSelect;
        this.onHover = onHover;
        this.selectedIndex = initialIndex;

        for (int i = 0; i < items.size(); i++) {
            this.entries.add(new DropdownEntry<>(items.get(i), i));
        }

        updateMessage();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Check if mouse left the entire dropdown area and close if needed
        if (expanded && !isMouseOverDropdownArea(mouseX, mouseY)) {
            expanded = false;
            clearHover();
        }

        // Render the main button
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.itemHeight);

        // Render the selected text on the button
        int textColor = this.active ? 0xFFFFFFFF : 0xFFA0A0A0;
        graphics.centeredText(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.itemHeight - 8) / 2, textColor);

        // Render dropdown arrow
        String arrow = expanded ? "▲" : "▼";
        graphics.text(Minecraft.getInstance().font, arrow, this.getX() + this.getWidth() - 12, this.getY() + (this.itemHeight - 8) / 2, textColor);

        // Render expanded dropdown list
        if (expanded) {
            renderDropdownList(graphics, mouseX, mouseY);
        }
    }

    public void renderLast(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.expanded) {
            renderDropdownList(graphics, mouseX, mouseY);
        }
    }

    private boolean isMouseOverDropdownArea(double mouseX, double mouseY) {
        // Check button area
        if (isMouseOverButton(mouseX, mouseY)) {
            return true;
        }

        // Check dropdown list area if expanded
        if (expanded) {
            int listY = this.getY() + this.itemHeight;
            int visibleItems = Math.min(maxVisibleItems, entries.size());
            int listHeight = visibleItems * itemHeight;

            return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                    && mouseY >= listY && mouseY < listY + listHeight;
        }

        return false;
    }

    private void renderDropdownList(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int visibleItems = Math.min(maxVisibleItems, entries.size());
        int listHeight = visibleItems * itemHeight;
        int listY = this.getY() + this.itemHeight;

        // Render background
        graphics.fill(this.getX() - 1, listY - 1, this.getX() + this.getWidth() + 1, listY + listHeight + 1, 0xFF000000);
        graphics.fill(this.getX(), listY, this.getX() + this.getWidth(), listY + listHeight, 0xF0101010);


        // Render items
        int newHoveredIndex = -1;
        for (int i = 0; i < visibleItems; i++) {
            int entryIndex = i + scrollOffset;
            if (entryIndex >= entries.size()) break;

            DropdownEntry<TData> entry = entries.get(entryIndex);
            int entryY = listY + i * itemHeight;

            boolean isHovered = mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                    && mouseY >= entryY && mouseY < entryY + itemHeight;

            if (isHovered) {
                newHoveredIndex = entryIndex;
            }

            // Render entry background
            int bgColor = isHovered ? 0x80808080 : (entryIndex == selectedIndex ? 0x40808080 : 0x00000000);
            graphics.fill(this.getX(), entryY, this.getX() + this.getWidth(), entryY + itemHeight, bgColor);

            // Render entry text
            int textColor = isHovered ? 0xFFFFFFFF : 0xFFE0E0E0;
            graphics.centeredText(Minecraft.getInstance().font, entry.value.text(), this.getX() + this.getWidth() / 2, entryY + (itemHeight - 8) / 2, textColor);
        }

        // Handle hover callbacks
        if (newHoveredIndex != hoveredIndex) {
            if (hoveredIndex >= 0 && onHover != null) {
                onHover.accept(this.entries.get(hoveredIndex).value().data(), false);
            }
            hoveredIndex = newHoveredIndex;
            if (hoveredIndex >= 0 && onHover != null) {
                onHover.accept(this.entries.get(hoveredIndex).value().data(), true);
            }
        }

        // Render scrollbar if needed
        if (entries.size() > maxVisibleItems) {
            renderScrollbar(graphics, listY, listHeight);
        }
    }

    private void renderScrollbar(GuiGraphicsExtractor graphics, int listY, int listHeight) {
        int scrollbarWidth = 4;
        int scrollbarX = this.getX() + this.getWidth() - scrollbarWidth - 2;

        // Scrollbar track
        graphics.fill(scrollbarX, listY, scrollbarX + scrollbarWidth, listY + listHeight, 0x40FFFFFF);

        // Scrollbar thumb
        float scrollRatio = (float) scrollOffset / (entries.size() - maxVisibleItems);
        int thumbHeight = Math.max(10, listHeight * maxVisibleItems / entries.size());
        int thumbY = listY + (int) ((listHeight - thumbHeight) * scrollRatio);
        graphics.fill(scrollbarX, thumbY, scrollbarX + scrollbarWidth, thumbY + thumbHeight, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.active || !this.visible) {
            return false;
        }

        double mouseX = event.x();
        double mouseY = event.y();

        if (expanded) {
            int listY = this.getY() + this.itemHeight;
            int visibleItems = Math.min(maxVisibleItems, entries.size());

            // Check if clicked on dropdown list
            if (mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                    && mouseY >= listY && mouseY < listY + visibleItems * itemHeight) {
                int clickedIndex = (int) ((mouseY - listY) / itemHeight) + scrollOffset;
                if (clickedIndex >= 0 && clickedIndex < entries.size()) {
                    selectItem(clickedIndex);
                    expanded = false;
                    return true;
                }
            }

            // Check if clicked on main button (to collapse)
            if (isMouseOverButton(mouseX, mouseY)) {
                expanded = false;
                clearHover();
                return true;
            }

            // Clicked outside, collapse
            expanded = false;
            clearHover();
            return false;
        } else {
            // Check if clicked on main button (to expand)
            if (isMouseOverButton(mouseX, mouseY)) {
                expanded = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (expanded && entries.size() > maxVisibleItems) {
            int listY = this.getY() + this.itemHeight;
            int listHeight = maxVisibleItems * itemHeight;

            if (mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                    && mouseY >= listY && mouseY < listY + listHeight) {
                scrollOffset = Math.max(0, Math.min(entries.size() - maxVisibleItems, scrollOffset - (int) scrollY));
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isMouseOverButton(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() && mouseY < this.getY() + this.itemHeight;
    }

    private void selectItem(int index) {
        if (index >= 0 && index < entries.size()) {
            selectedIndex = index;
            updateMessage();
            if (onSelect != null) {
                onSelect.accept(this.entries.get(index).value().data());
            }
        }
    }

    private void clearHover() {
        if (hoveredIndex >= 0 && onHover != null) {
            onHover.accept(this.entries.get(hoveredIndex).value().data(), false);
        }
        hoveredIndex = -1;
    }

    private void updateMessage() {
        if (selectedIndex >= 0 && selectedIndex < entries.size()) {
            this.setMessage(entries.get(selectedIndex).value().text());
        }
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (!expanded) {
            clearHover();
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public DropdownWidget<TData> setSelectedIndex(int index) {
        this.selectedIndex = index;
        updateMessage();

        return this;
    }

    @Override
    public int getHeight() {
        if (expanded) {
            return itemHeight + Math.min(maxVisibleItems, entries.size()) * itemHeight;
        }
        return itemHeight;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    /**
     * Builder for creating DropdownWidget instances.
     */
    public static class Builder<TData> {
        private final int x;
        private final int y;
        private int width = 100;
        private int itemHeight = 20;
        private int maxVisibleItems = 5;
        protected List<Value<TData>> items = new ArrayList<>();
        @Nullable
        private TData initialSelection;
        @Nullable
        private Consumer<TData> onSelect;
        @Nullable
        private BiConsumer<TData, Boolean> onHover;

        public Builder(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Builder<TData> width(int width) {
            this.width = width;
            return this;
        }

        public Builder<TData> itemHeight(int itemHeight) {
            this.itemHeight = itemHeight;
            return this;
        }

        public Builder<TData> maxVisibleItems(int maxVisibleItems) {
            this.maxVisibleItems = maxVisibleItems;
            return this;
        }

        public Builder<TData> items(List<Value<TData>> items) {
            this.items = items;
            return this;
        }

        public Builder<TData> initialSelection(TData index) {
            this.initialSelection = index;
            return this;
        }

        public Builder<TData> onSelect(Consumer<TData> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder<TData> onHover(BiConsumer<TData, Boolean> onHover) {
            this.onHover = onHover;
            return this;
        }

        public DropdownWidget<TData> build() {
            return new DropdownWidget<>(x, y, width, itemHeight, maxVisibleItems, items, initialSelection, onSelect, onHover);
        }
    }

    public static class SimpleBuilder extends Builder<Integer> {

        private int initialIndex;

        private SimpleBuilder(int x, int y) {
            super(x, y);
        }

        public SimpleBuilder simpleItems(List<Component> items) {
            super.items(Streams.mapWithIndex(items.stream(), (from, index) -> new Value<>((int) index, from)).toList());
            return this;
        }

        public SimpleBuilder initialSelection(int index) {
            this.initialIndex = index;
            return this;
        }

        public SimpleBuilder width(int width) {
            return (SimpleBuilder) super.width(width);
        }

        public SimpleBuilder itemHeight(int itemHeight) {
            return (SimpleBuilder) super.itemHeight(itemHeight);
        }

        public SimpleBuilder maxVisibleItems(int maxVisibleItems) {
            return (SimpleBuilder) super.maxVisibleItems(maxVisibleItems);
        }

        public SimpleBuilder initialSelection(Integer index) {
            return (SimpleBuilder) super.initialSelection(index);
        }

        public SimpleBuilder onSelect(Consumer<Integer> onSelect) {
            return (SimpleBuilder) super.onSelect(onSelect);
        }

        @Override
        public SimpleBuilder onHover(BiConsumer<Integer, Boolean> onHover) {
            return (SimpleBuilder) super.onHover(onHover);
        }

        @Override
        public DropdownWidget<Integer> build() {
            Value<Integer> integerValue = this.items.get(this.initialIndex);
            initialSelection(integerValue.data);
            return super.build();
        }
    }

    public static <TData> Builder<TData> builder(int x, int y) {
        return new Builder<>(x, y);
    }

    public static SimpleBuilder simple(int x, int y) {
        return new SimpleBuilder(x, y);
    }

    private record DropdownEntry<TData>(Value<TData> value, int index) {
    }

    public record Value<TData>(TData data, Component text) {}
}