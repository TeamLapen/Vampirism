package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.components.IRenderLast;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public abstract class AppearanceScreen<T extends LivingEntity> extends Screen {

    private static final Identifier BACKGROUND = FResourceLocation.mod("background/default");

    protected final T entity;
    @Nullable
    private final Screen backScreen;
    protected final GridLayout layout = new GridLayout();
    @UnknownNullability
    private ImageWidget background;

    public AppearanceScreen(@NotNull Component titleIn, T entity, @Nullable Screen backScreen) {
        super(titleIn);
        this.entity = entity;
        this.backScreen = backScreen;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof IRenderLast last) {
                last.renderLast(guiGraphics, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    protected void rebuildWidgets() {
        this.layout.arrangeElements();
        fitLayout();
    }

    @Override
    protected void init() {
        this.background = this.layout.addChild(ImageWidget.sprite(0, 0, BACKGROUND), 0, 0);
        GridLayout layout = this.layout.addChild(new GridLayout(), 0, 0);
        layout.rowSpacing(10);

        GridLayout.RowHelper rowHelper = layout.createRowHelper(1);

        GridLayout innerGrid = rowHelper.addChild(new GridLayout());
        innerGrid.addChild(new StringWidget(this.title, this.font),0,0,1,2, innerGrid.newCellSettings().paddingLeft(8).paddingTop(8));

        innerGrid.addChild(createLayout(), 1,0, innerGrid.newCellSettings().alignHorizontallyCenter().padding(8));
        innerGrid.addChild(new EntityWidget(100, 110),1,1, innerGrid.newCellSettings().alignHorizontallyCenter().padding(8));

        GridLayout buttonsLayout = innerGrid.addChild(new GridLayout(),2,0,1,2, rowHelper.newCellSettings().alignHorizontallyCenter().padding(4));
        buttonsLayout.columnSpacing(50);
        buttonsLayout.addChild(new ExtendedButton(0,0, 80, 20,  Component.translatable("gui.back"), x -> {
            if (this.minecraft != null && this.backScreen != null) this.minecraft.setScreen(this.backScreen);
        }), 0, 0, buttonsLayout.newCellSettings().alignHorizontallyCenter());
        buttonsLayout.addChild(new ExtendedButton(0,0, 80, 20,  Component.translatable("gui.done"), x -> this.onClose()),0,1, buttonsLayout.newCellSettings().alignHorizontallyCenter());

        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);
        fitLayout();
    }

    private void fitLayout() {
        int layoutWidth = this.layout.getWidth();
        int layoutHeight = this.layout.getHeight();
        this.background.setWidth(layoutWidth);
        this.background.setHeight(layoutHeight);
        FrameLayout.centerInRectangle(this.layout, (this.width - layoutWidth) / 2, (this.height - layoutHeight) / 2, layoutWidth, layoutHeight);
    }

    @NotNull
    protected abstract LayoutElement createLayout();

    private class EntityWidget extends AbstractWidget {

        public EntityWidget(int width, int height) {
            super(0, 0, width, height, Component.empty());
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, this.getX(), this.getY(), this.getX() + getWidth(), this.getY() + getHeight(), 50, 0.0625F, mouseX, mouseY, entity);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        }
    }
}