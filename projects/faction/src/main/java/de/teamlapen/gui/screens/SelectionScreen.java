package de.teamlapen.gui.screens;

import de.teamlapen.gui.components.list.SimpleList;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.gui.components.IComponentWithAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SelectionScreen extends Screen {

    protected final GridLayout layout = new GridLayout();
    private final List<IComponentWithAction> items;
    private final ILastScreenProvider backScreen;

    public SelectionScreen(Component title, List<IComponentWithAction> items) {
        this(title, items, ILastScreenProvider.NONE);
    }
    public SelectionScreen(Component title, List<IComponentWithAction> items, ILastScreenProvider backScreen) {
        super(title);
        this.items = items;
        this.backScreen = backScreen;
    }

    @Override
    protected void init() {
        GridLayout gridLayout = this.layout.addChild(new GridLayout(), 0, 0, layoutSettings -> layoutSettings.alignHorizontallyCenter().alignVerticallyMiddle());
        gridLayout.rowSpacing(5);
        gridLayout.addChild(new StringWidget(100,9,this.title, Minecraft.getInstance().font).setMaxWidth(100),0,0);
        gridLayout.addChild(SimpleList.builder(0,0, 100, 100).components(this.items).anyClicked(this.backScreen::returnToLastScreen).build(), 1, 0);
        gridLayout.addChild(Button.builder(Component.translatable(this.backScreen.hasLastScreen() ? "gui.back": "gui.done"), (_) -> this.backScreen.returnToLastScreen()).width(100).build(),2,0);

        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);

        FrameLayout.centerInRectangle(this.layout,(this.width - this.layout.getWidth()) / 2, (this.height - this.layout.getHeight()) / 2, this.layout.getWidth(), this.layout.getHeight() );
    }

}
