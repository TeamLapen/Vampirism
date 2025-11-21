package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.client.gui.components.ColoredImageWidget;
import de.teamlapen.lib.client.gui.components.EmptyComponent;
import de.teamlapen.lib.client.renderer.GuiRenderer;
import de.teamlapen.lib.util.IntReference;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.util.ItemOrdering;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.config.ClientConfigHelper;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.screens.radial.edit.ReorderingGuiRadialMenu;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.entity.player.ActionKeys;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundActionBindingPacket;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EditSelectActionScreen<T extends ISkillPlayer<T>> extends ReorderingGuiRadialMenu<Holder<IAction<?>>> {

    public static void show() {
        FactionPlayerHandler.get(Minecraft.getInstance().player).getCurrentSkillPlayer().ifPresent(factionPlayer -> Minecraft.getInstance().setScreen(new EditSelectActionScreen<>(factionPlayer)));
    }

    private static void drawActionPart(@Nullable Holder<IAction<?>> action, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        if (action == null) return;
        GuiRenderer.blit(graphics, getActionIcon(action), posX, posY, 16, 16, 16, 16);
    }

    private static ResourceLocation getActionIcon(Holder<IAction<?>> action) {
        return action.unwrapKey().map(ResourceKey::location).map(s -> s.withPath("textures/actions/" + s.getPath() + ".png")).orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private static <T extends ISkillPlayer<T>> boolean isEnabled(T player, @NotNull Holder<IAction<?>> item) {
        return player.getActionHandler().isActionUnlocked((Holder<IAction<T>>) (Object) item);
    }

    private static <T extends IFactionPlayer<T>> ItemOrdering<Holder<IAction<?>>> getOrdering(T player) {
        return new ItemOrdering<>(ClientConfigHelper.getActionOrder(player.getFaction()).stream().filter(s -> s.value().showInSelectAction(player.asEntity())).toList(), new ArrayList<>(), () -> ModRegistries.ACTIONS.listElements().filter(action -> action.value().matchesFaction(player.getFaction())).filter(s -> s.value().showInSelectAction(player.asEntity())).collect(Collectors.toList()));
    }

    private static <T extends IFactionPlayer<T>> void saveOrdering(T player, ItemOrdering<Holder<IAction<?>>> ordering) {
        ClientConfigHelper.saveActionOrder(player.getFaction().unwrapKey().map(ResourceKey::location).orElseThrow(), ordering.getOrdering());
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
        keyBindingList = rowHelper.addChild(new KeyBindingList(0,0,excludesWidth - 8, this.height - 55));
        this.repositionCallback.add((width1, height1) -> keyBindingList.setHeight(height1 - 55));
        rowHelper.addChild(new ResetButton(0, 0, excludesWidth - 30, 20, (context) -> this.resetKeyBindings()), rowHelper.newCellSettings().paddingHorizontal(1));
        rowHelper.addChild(new ExtendedButton(0, 0, excludesWidth - 30, 20, Component.translatable("text.vampirism.open_settings"), (context) -> Minecraft.getInstance().setScreen(new KeyBindsScreen(this, getMinecraft().options))), rowHelper.newCellSettings().paddingHorizontal(1));

        this.layout.addChild(excludesWrapper,0,2);
    }

    private void resetKeyBindings() {
        ModKeys.ACTION_KEYS.keySet().forEach(key -> {
            FactionPlayerHandler.get(getMinecraft().player).setBoundAction(key, null, false);
            VampirismMod.proxy.sendToServer(new ServerboundActionBindingPacket(key));
        });
        this.keyBindingList.clearActions();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int p_296369_, int p_296477_, float p_294317_) {
        super.renderBackground(graphics, p_296369_, p_296477_, p_294317_);
        graphics.drawCenteredString(this.font, Component.translatable("text.vampirism.key_shortcuts"), this.width - 70, 5, -1);
    }

    public class KeyBindingActionList extends ContainerObjectSelectionList<KeyBindingActionList.ActionSetting> {

        public KeyBindingActionList(Minecraft minecraft, int width) {
            super(minecraft, width, EditSelectActionScreen.this.height - 40, 40, 20);
        }

        public class ActionSetting extends ContainerObjectSelectionList.Entry<ActionSetting> {

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of();
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {

            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of();
            }
        }
    }

    public class KeyBindingList extends ContainerObjectSelectionList<KeyBindingList.KeyBindingSetting> {

        private static final WidgetSprites BUTTON = new WidgetSprites(VResourceLocation.mc("widget/button"), VResourceLocation.mc("widget/button_highlighted"));

        public KeyBindingList(int x, int y, int pWidth, int pHeight) {
            super(Minecraft.getInstance(), pWidth, pHeight, y, 20);
            this.setX(x);
            FactionPlayerHandler handler = FactionPlayerHandler.get(Minecraft.getInstance().player);
            replaceEntries(ModKeys.ACTION_KEYS.entrySet().stream().map(pair -> new KeyBindingSetting(pair.getKey(), pair.getValue(), handler.getBoundAction(pair.getKey()))).sorted(Comparator.comparingInt((KeyBindingSetting o) -> o.actionKey.ordinal())).toList());
        }

        @Override
        protected void renderListBackground(GuiGraphics p_331297_) {
        }

//        @Override
//        protected void renderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, KeyBindingSetting item) {
//            if (movingItem != null) {
//                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON.get(true, pIsMouseOver), pLeft, getRowTop(), getRowWidth(), getHeight() + 5);
//            }
//            super.renderItem(guiGraphics, mouseX, mouseY, partialTick, item);
//        }

        @Override
        public Optional<GuiEventListener> getChildAt(double pMouseX, double pMouseY) {
            return super.getChildAt(pMouseX, pMouseY);
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

            private static final WidgetSprites REMOVE_ICON = new WidgetSprites(VResourceLocation.mod("widget/remove"), VResourceLocation.mod("widget/remove_highlighted"));

            private final ActionKeys actionKey;
            private Holder<IAction<?>> action;
            private final StringWidget stringWidget;
            private ImageWidget imageWidget;
            private final ImageButton imageButton;

            public KeyBindingSetting(ActionKeys actionKey, KeyMapping keyMapping, Holder<IAction<?>> action) {
                this.actionKey = actionKey;
                this.stringWidget = new StringWidget(0, 2, 80, 20, keyMapping.getTranslatedKeyMessage(), Minecraft.getInstance().font);
                this.imageButton = new ImageButton(115, 2, 16, 16, REMOVE_ICON, (a) -> switchAction(null));
                applyAction(action);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (this.imageButton.mouseClicked(new MouseButtonEvent(event.x() - getX(), event.y() - getY() - ((actionKey.ordinal() - 1) * 20), event.buttonInfo()), doubleClick)) {
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
                VampirismMod.proxy.sendToServer(new ServerboundActionBindingPacket(this.actionKey, this.action));
                FactionPlayerHandler.get(Minecraft.getInstance().player).setBoundAction(this.actionKey, this.action, false);
            }

            private void applyAction(@Nullable Holder<IAction<?>> action) {
                this.action = action;
                if (action != null) {
                    this.imageWidget = ImageWidget.texture(16, 16, getActionIcon(action), 16, 16);
                    this.imageWidget.setPosition(90, 2);
                    this.imageButton.visible = true;
                } else {
                    this.imageWidget = ImageWidget.texture(16, 16, null, 16, 16);
                    this.imageWidget.setPosition(90, 2);
                    this.imageWidget.visible = false;
                    this.imageButton.visible = false;
                }
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(stringWidget, imageWidget, imageButton);
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
                Matrix3x2fStack pose = guiGraphics.pose();
                pose.pushMatrix();
                pose.translate(getContentX(), getContentY());
                stringWidget.render(guiGraphics, mouseX - getContentX(), mouseY - getContentY(), partialTick);
                imageWidget.render(guiGraphics, mouseX - getContentX(), mouseY - getContentY(), partialTick);
                imageButton.render(guiGraphics, mouseX - getContentX(), mouseY - getContentY(), partialTick);
                pose.popMatrix();
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(stringWidget, imageWidget, imageButton);
            }
        }
    }
}
