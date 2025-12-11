package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AppearanceScreen<T extends LivingEntity> extends Screen {

    private static final ResourceLocation BACKGROUND = FResourceLocation.mod("background/default");

    protected final T entity;
    protected final int xSize = 256;
    protected final int ySize = 177;
    @Nullable
    private final Screen backScreen;
    protected int guiLeft;
    protected int guiTop;
    protected GridLayout layout = new GridLayout();

    public AppearanceScreen(@NotNull Component titleIn, T entity, @Nullable Screen backScreen) {
        super(titleIn);
        this.entity = entity;
        this.backScreen = backScreen;
    }

    @Override
    protected void rebuildWidgets() {
        this.layout.arrangeElements();
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.layout.addChild(ImageWidget.sprite(xSize, ySize, BACKGROUND), 0, 0);
        GridLayout layout = this.layout.addChild(new GridLayout(), 0, 0);
        layout.rowSpacing(10);

        GridLayout.RowHelper rowHelper = layout.createRowHelper(1);

        GridLayout innerGrid = rowHelper.addChild(new GridLayout());

        var settings = innerGrid.addChild(LinearLayout.vertical().spacing(3), 0,0, innerGrid.newCellSettings().alignHorizontallyCenter().padding(8));
        settings.addChild(new StringWidget(this.title, this.font), settings.newCellSettings().alignHorizontallyCenter());
        settings.addChild(createLayout());
        innerGrid.addChild(new EntityWidget(100, 110),0,1, innerGrid.newCellSettings().alignHorizontallyCenter().padding(8));

        GridLayout buttonsLayout = rowHelper.addChild(new GridLayout(), rowHelper.newCellSettings().alignHorizontallyCenter().padding(4));
        buttonsLayout.columnSpacing(50);
        buttonsLayout.addChild(new ExtendedButton(0,0, 80, 20,  Component.translatable("gui.back"), x -> {
            if (this.minecraft != null && this.backScreen != null) this.minecraft.setScreen(this.backScreen);
        }), 0, 0, buttonsLayout.newCellSettings().alignHorizontallyCenter());
        buttonsLayout.addChild(new ExtendedButton(0,0, 80, 20,  Component.translatable("gui.done"), x -> this.onClose()),0,1, buttonsLayout.newCellSettings().alignHorizontallyCenter());

        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);
        FrameLayout.centerInRectangle(this.layout, this.guiLeft, this.guiTop, this.xSize, this.ySize);
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
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}