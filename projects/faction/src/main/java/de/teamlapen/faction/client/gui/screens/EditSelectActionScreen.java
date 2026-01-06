package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.client.config.values.ActionOrderValue;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.components.ColoredImageWidget;
import de.teamlapen.faction.client.gui.components.EmptyComponent;
import de.teamlapen.faction.client.gui.screens.radial.edit.ReorderingGuiRadialMenu;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionKeys;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.actions.ActionKeys;
import de.teamlapen.faction.common.network.packets.server.ServerboundActionBindingPacket;
import de.teamlapen.faction.common.util.IntReference;
import de.teamlapen.faction.common.util.ItemOrdering;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EditSelectActionScreen<T extends ISkillPlayer<T>> extends ReorderingGuiRadialMenu<Holder<IAction<?>>> {

    public static void show() {
        FactionPlayerHandler.get(Minecraft.getInstance().player).getCurrentSkillPlayer().ifPresent(factionPlayer -> Minecraft.getInstance().setScreen(new EditSelectActionScreen<>(factionPlayer)));
    }

    private static void drawActionPart(@Nullable Holder<IAction<?>> action, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        if (action == null) return;
        GuiRenderer.blit(graphics, getActionIcon(action), posX, posY, 16, 16, 16, 16);
    }

    private static Identifier getActionIcon(Holder<IAction<?>> action) {
        return action.unwrapKey().map(ResourceKey::identifier).map(s -> s.withPath("textures/actions/" + s.getPath() + ".png")).orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private static <T extends ISkillPlayer<T>> boolean isEnabled(T player, @NotNull Holder<IAction<?>> item) {
        return player.getActionHandler().isActionUnlocked((Holder<IAction<T>>) (Object) item);
    }

    private static <T extends IFactionPlayer<T>> ItemOrdering<Holder<IAction<?>>> getOrdering(T player) {
        ActionOrderValue actionOrder = FactionConfig.client().actionOrder;
        return new ItemOrdering<>(actionOrder.get(player.getFaction()), new ArrayList<>(), () -> actionOrder.allowedValues(player.getFaction()));
    }

    private static <T extends IFactionPlayer<T>> void saveOrdering(T player, ItemOrdering<Holder<IAction<?>>> ordering) {
        FactionConfig.client().actionOrder.set(player.getFaction(), ordering.getOrdering());
    }

    private KeyBindingList keyBindingList;

    public EditSelectActionScreen(T player) {
        super(getOrdering(player), action -> action.value().getName().plainCopy(), EditSelectActionScreen::drawActionPart, (ordering) -> saveOrdering(player, ordering), (item) -> EditSelectActionScreen.isEnabled(player, item));
    }


    @Override
    protected void setupGrid() {
        super.setupGrid();

        addKeyBindingList();
        this.layout.arrangeElements();
        IntReference width = new IntReference();
        this.layout.visitChildren(x -> width.add(x.getWidth()));
        var spacer = this.layout.addChild(new EmptyComponent(this.width - width.get(), this.height), 0,1);
        this.repositionCallback.add((width1, height1) -> {
            this.layout.arrangeElements();
            IntReference width2 = new IntReference();
            this.layout.visitChildren(x -> {
                if (x == spacer) return;
                width2.add(x.getWidth());
            });
            spacer.setWidth(width1 - width2.get());
        });
    }

    protected void addKeyBindingList() {
        int excludesWidth = 140;
        var excludesWrapper = new GridLayout();
        var background = excludesWrapper.addChild(ColoredImageWidget.sprite(excludesWidth, this.height, BACKGROUND, ARGB.colorFromFloat(1, 0.5f, 0.5f, 0.5f)), 0,0);
        this.repositionCallback.add(((width1, height1) -> background.setHeight(height1)));
        var excludes = new GridLayout()
                .rowSpacing(2);
        excludesWrapper.addChild(excludes,0,0, excludesWrapper.newCellSettings().padding(4).paddingTop(5));

        GridLayout.RowHelper rowHelper = excludes.createRowHelper(1);
        rowHelper.defaultCellSetting().alignHorizontallyCenter();
        rowHelper.addChild(new StringWidget(Component.translatable("controls.keybinds.title"), Minecraft.getInstance().font), rowHelper.newCellSettings().alignHorizontallyCenter().paddingVertical(1));
        this.keyBindingList = rowHelper.addChild(new KeyBindingList(excludesWidth - 8, this.height - 55 - 11));
        this.repositionCallback.add((width1, height1) -> keyBindingList.setHeight(height1 - 55 - 11));
        rowHelper.addChild(new ResetButton(0, 0, excludesWidth - 30, 20, (context) -> this.resetKeyBindings()), rowHelper.newCellSettings().paddingHorizontal(1));
        rowHelper.addChild(new ExtendedButton(0, 0, excludesWidth - 30, 20, Component.translatable("gui.factionapi.edit_action.open_keybinding_settings"), (context) -> Minecraft.getInstance().setScreen(new KeyBindsScreen(this, getMinecraft().options))), rowHelper.newCellSettings().paddingHorizontal(1));

        this.layout.addChild(excludesWrapper,0,2);
    }

    private void resetKeyBindings() {
        FactionKeys.ACTION_KEYS.keySet().forEach(key -> {
            FactionPlayerHandler.get(getMinecraft().player).setBoundAction(key, null, false);
            FactionsMod.proxy.sendToServer(new ServerboundActionBindingPacket(key));
        });
        this.keyBindingList.clearActions();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int p_296369_, int p_296477_, float p_294317_) {
        super.renderBackground(graphics, p_296369_, p_296477_, p_294317_);
        graphics.drawCenteredString(this.font, Component.translatable("gui.factionapi.edit_action.key_shortcuts"), this.width - 70, 5, -1);
    }

    public class KeyBindingList extends ContainerObjectSelectionList<KeyBindingList.KeyBindingSetting> {

        public KeyBindingList(int pWidth, int pHeight) {
            super(Minecraft.getInstance(), pWidth, pHeight, 0, 20);
            FactionPlayerHandler handler = FactionPlayerHandler.get(Minecraft.getInstance().player);
            replaceEntries(FactionKeys.ACTION_KEYS.entrySet().stream().map(pair -> new KeyBindingSetting(pair.getKey(), pair.getValue(), handler.getBoundAction(pair.getKey()))).sorted(Comparator.comparingInt((KeyBindingSetting o) -> o.actionKey.ordinal())).toList());
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            this.children().forEach(entry -> entry.setX(x));
        }

        @Override
        protected void renderListBackground(@NotNull GuiGraphics guiGraphics) {
        }

        @Override
        protected int scrollBarX() {
            return this.getRight() - 6;
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        public int getRowLeft() {
            return super.getRowLeft() - 2;
        }

        @Override
        public int getRowTop(int pIndex) {
            return super.getRowTop(pIndex);
        }

        public void clearActions() {
            this.children().forEach(entry -> entry.switchAction(null));
        }

        private class KeyBindingSetting extends ContainerObjectSelectionList.Entry<KeyBindingSetting> {

            private static final WidgetSprites REMOVE_ICON = new WidgetSprites(FIdentifier.mod("widget/remove"), FIdentifier.mod("widget/remove_highlighted"));

            private final ActionKeys actionKey;
            private Holder<IAction<?>> action;
            private final StringWidget stringWidget;
            private ImageWidget imageWidget;
            private final ImageButton imageButton;

            public KeyBindingSetting(ActionKeys actionKey, KeyMapping keyMapping, Holder<IAction<?>> action) {
                this.actionKey = actionKey;
                this.stringWidget = new StringWidget(0, 0, 80, 20, keyMapping.getTranslatedKeyMessage(), Minecraft.getInstance().font);
                this.imageButton = new ImageButton(0, 0, 16, 16, REMOVE_ICON, (a) -> switchAction(null));
                applyAction(action);
            }

            @Override
            public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
                if (this.imageButton.mouseClicked(event, doubleClick)) {
                    return true;
                } else if (movingItem != null) {
                    switchAction(movingItem.get());
                    movingItem = null;
                    removeDummyItems();
                    return true;
                }
                return false;
            }

            private void switchAction(@Nullable Holder<IAction<?>> action) {
                applyAction(action);
                FactionsMod.proxy.sendToServer(new ServerboundActionBindingPacket(this.actionKey, this.action));
                FactionPlayerHandler.get(Minecraft.getInstance().player).setBoundAction(this.actionKey, this.action, false);
            }

            private void applyAction(@Nullable Holder<IAction<?>> action) {
                this.action = action;
                if (action != null) {
                    this.imageWidget = ImageWidget.texture(16, 16, getActionIcon(action), 16, 16);
                    this.imageButton.visible = true;
                } else {
                    this.imageWidget = ImageWidget.texture(16, 16, null, 16, 16);
                    this.imageWidget.visible = false;
                    this.imageButton.visible = false;
                }
            }

            @Override
            public @NotNull List<? extends NarratableEntry> narratables() {
                return List.of(stringWidget, imageWidget, imageButton);
            }

            @Override
            public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
                stringWidget.setPosition(KeyBindingList.this.getX() + 2, getContentY() + 2);
                imageWidget.setPosition(KeyBindingList.this.getX() + 90,getContentY() + 2);
                imageButton.setPosition(KeyBindingList.this.getX() + 115,getContentY() + 2);
                stringWidget.render(guiGraphics, mouseX, mouseY, partialTick);
                imageWidget.render(guiGraphics, mouseX, mouseY, partialTick);
                imageButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public @NotNull List<? extends GuiEventListener> children() {
                return List.of(stringWidget, imageWidget, imageButton);
            }
        }
    }
}
