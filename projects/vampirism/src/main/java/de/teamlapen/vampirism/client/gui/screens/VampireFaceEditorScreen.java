package de.teamlapen.vampirism.client.gui.screens;

import com.google.common.collect.Streams;
import de.teamlapen.faction.client.gui.screens.AppearanceScreen;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.common.world.entities.appearance.AppearanceKey;
import de.teamlapen.faction.common.world.entities.appearance.AppearancePacket;
import de.teamlapen.gui.components.DropdownWidget;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.renderer.TextureLoader;
import de.teamlapen.vampirism.client.renderer.VampireFaceOverlay;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundAppearancePacket;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.properties.Customization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedMap;

public class VampireFaceEditorScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance.face_editor");
    private static final Identifier FALLBACK_EYE = VIdentifier.mod("textures/entity/eyes/eyes0.png");
    private static final Identifier FALLBACK_FANG = VIdentifier.mod("textures/entity/fangs/fangs0.png");
    private static final int CELL = 8;
    private static final int GRID_SIZE = CELL * VampireFaceOverlay.FACE_SIZE;
    private static final int MARKER_COLOR = 0xFFFF3333;
    private static final int FACE_COLOR = 0xFF9C7A5B;
    private static final int CONTROLS_WIDTH = 120;
    private static final int COLUMN_HEIGHT = 110;
    private static final int COLUMN_SPACING = 8;
    private static final int HINT_WIDTH = 260;
    private static final int HINT_MAX_ROWS = 2;

    private final ILastScreenProvider backScreen;
    private final boolean editingEyes;
    private final SequencedMap<Identifier, Identifier> eyeTextures;
    private final SequencedMap<Identifier, Identifier> fangTextures;

    @UnknownNullability
    private Identifier eyeType;
    @UnknownNullability
    private Identifier fangType;
    @Nullable
    private Identifier hoveredType;
    private int eyePosition = Customization.DEFAULT_POSITION;
    private int fangPosition = Customization.DEFAULT_POSITION;
    @UnknownNullability
    private DropdownWidget<Identifier> typeDropdown;

    public VampireFaceEditorScreen(@Nullable ILastScreenProvider backScreen) {
        this(backScreen, true);
    }

    @SuppressWarnings("DataFlowIssue")
    public VampireFaceEditorScreen(@Nullable ILastScreenProvider backScreen, boolean editingEyes) {
        super(NAME, Minecraft.getInstance().player, backScreen);
        this.backScreen = backScreen == null ? ILastScreenProvider.NONE : backScreen;
        this.editingEyes = editingEyes;
        this.eyeTextures = TextureLoader.mapTexturesInById("textures/entity/eyes");
        this.fangTextures = TextureLoader.mapTexturesInById("textures/entity/fangs");
    }

    @Override
    protected void init() {
        var customization = VampirePlayer.get(player()).getCustomization();
        this.eyeType = customization.eyeType();
        this.fangType = customization.fangType();
        this.eyePosition = customization.eyePosition();
        this.fangPosition = customization.fangPosition();
        this.hoveredType = null;
        super.init();
    }

    @Override
    public void removed() {
        var vampire = VampirePlayer.get(player());
        var map = new AppearanceKey.AppearanceMap();
        map.set(VampirePlayer.EyeType, this.eyeType);
        map.set(VampirePlayer.FangType, this.fangType);
        map.set(VampirePlayer.EyePosition, this.eyePosition);
        map.set(VampirePlayer.FangPosition, this.fangPosition);
        map.set(VampirePlayer.GlowingEye, vampire.getGlowingEyes());
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), new AppearancePacket(map)));
        super.removed();
    }

    @NotNull
    @Override
    protected LayoutElement createLayout() {
        LinearLayout columns = LinearLayout.horizontal().spacing(COLUMN_SPACING);
        columns.addChild(createControlsColumn());
        columns.addChild(createFaceColumn());
        return columns;
    }

    private LayoutElement createControlsColumn() {
        FrameLayout frame = new FrameLayout(CONTROLS_WIDTH, COLUMN_HEIGHT);
        frame.defaultChildLayoutSetting().alignHorizontallyLeft().alignVerticallyTop();

        LinearLayout controls = frame.addChild(LinearLayout.vertical().spacing(4));

        LinearLayout modeButtons = controls.addChild(LinearLayout.horizontal());
        modeButtons.addChild(modeButton(true));
        modeButtons.addChild(modeButton(false));

        this.typeDropdown = DropdownWidget.<Identifier>builder(0, 0)
                .width(CONTROLS_WIDTH)
                .itemHeight(20)
                .maxVisibleItems(5)
                .initialSelection(selectedType())
                .onSelect(this::selectType)
                .onHover(this::hoverType)
                .items(typeItems())
                .build();
        controls.addChild(this.typeDropdown);

        if (this.editingEyes) {
            controls.addChild(Checkbox.builder(Component.translatable("gui.vampirism.appearance.glowing_eye"), this.font)
                    .selected(VampirePlayer.get(player()).getGlowingEyes())
                    .onValueChange((_, selected) -> VampirePlayer.get(player()).setGlowingEyes(selected))
                    .build());
        }
        return frame;
    }

    private ExtendedButton modeButton(boolean eyes) {
        Component label = Component.translatable(eyes ? "gui.vampirism.appearance.edit_eyes" : "gui.vampirism.appearance.edit_fangs");
        ExtendedButton button = new ExtendedButton(0, 0, CONTROLS_WIDTH / 2, 20, label, _ -> this.minecraft.setScreen(new VampireFaceEditorScreen(this.backScreen, eyes)));
        button.active = eyes != this.editingEyes;
        return button;
    }

    private LayoutElement createFaceColumn() {
        FrameLayout frame = new FrameLayout(GRID_SIZE, COLUMN_HEIGHT);
        frame.defaultChildLayoutSetting().alignHorizontallyCenter().alignVerticallyTop();

        LinearLayout face = frame.addChild(LinearLayout.vertical().spacing(4));
        face.defaultCellSetting().alignHorizontallyCenter();
        face.addChild(new FaceGridWidget());
        return frame;
    }

    @Override
    protected int entityPreviewWidth() {
        return 60;
    }

    @Override
    protected LayoutElement createFooter() {
        // Fixed size so a hint that wraps into a second line in some languages does not resize the screen.
        FrameLayout frame = new FrameLayout(HINT_WIDTH, HINT_MAX_ROWS * this.font.lineHeight);
        frame.addChild(new MultiLineTextWidget(Component.translatable(this.editingEyes ? "gui.vampirism.appearance.position_eyes" : "gui.vampirism.appearance.position_fangs"), this.font)
                .setMaxWidth(HINT_WIDTH)
                .setMaxRows(HINT_MAX_ROWS)
                .setCentered(true));
        return frame;
    }

    private List<DropdownWidget.Value<Identifier>> typeItems() {
        var textures = this.editingEyes ? this.eyeTextures : this.fangTextures;
        String key = this.editingEyes ? "gui.vampirism.appearance.eye" : "gui.vampirism.appearance.fang";
        var items = new ArrayList<>(Streams.mapWithIndex(textures.sequencedEntrySet().stream(),
                (from, index) -> new DropdownWidget.Value<>(from.getKey(), (Component) Component.translatable(key).append(" " + (index + 1)))).toList());
        items.add(new DropdownWidget.Value<>(Customization.NONE, Component.translatable(this.editingEyes ? "gui.vampirism.appearance.no_eyes" : "gui.vampirism.appearance.no_fangs")));
        return items;
    }

    private Identifier selectedType() {
        return this.editingEyes ? this.eyeType : this.fangType;
    }

    private Identifier shownType() {
        return this.hoveredType != null ? this.hoveredType : selectedType();
    }

    private Identifier textureOf(Identifier type) {
        if (this.editingEyes) {
            return this.eyeTextures.getOrDefault(type, FALLBACK_EYE);
        }
        return this.fangTextures.getOrDefault(type, FALLBACK_FANG);
    }

    private void applyType(Identifier type) {
        VampirePlayer vampire = VampirePlayer.get(player());
        if (this.editingEyes) {
            vampire.setEyeType(type);
        } else {
            vampire.setFangType(type);
        }
    }

    private void selectType(Identifier type) {
        this.hoveredType = null;
        if (this.editingEyes) {
            this.eyeType = type;
        } else {
            this.fangType = type;
        }
        applyType(type);
        int position = this.editingEyes ? this.eyePosition : this.fangPosition;
        if (position >= 0 && !Customization.NONE.equals(type)) {
            placeFeature(position % VampireFaceOverlay.FACE_SIZE, position / VampireFaceOverlay.FACE_SIZE);
        }
    }

    private void hoverType(Identifier type, boolean hovered) {
        if (hovered) {
            this.hoveredType = type;
            applyType(type);
        } else if (type.equals(this.hoveredType)) {
            this.hoveredType = null;
            applyType(selectedType());
        }
    }

    private void placeFeature(int cellX, int cellY) {
        if (Customization.NONE.equals(selectedType())) {
            var textures = this.editingEyes ? this.eyeTextures : this.fangTextures;
            if (textures.isEmpty()) return;
            selectType(textures.firstEntry().getKey());
            this.typeDropdown.setSelectedIndex(0);
        }
        int position = VampireFaceOverlay.positionAt(textureOf(selectedType()), this.editingEyes, cellX, cellY);
        VampirePlayer vampire = VampirePlayer.get(player());
        if (this.editingEyes) {
            this.eyePosition = position;
            vampire.setEyePosition(position);
        } else {
            this.fangPosition = position;
            vampire.setFangPosition(position);
        }
    }

    @Nullable
    private Identifier skinTexture() {
        if (this.entity instanceof AbstractClientPlayer clientPlayer) {
            return clientPlayer.getSkin().body().texturePath();
        }
        return null;
    }

    private class FaceGridWidget extends AbstractWidget {

        private boolean dragging;

        FaceGridWidget() {
            super(0, 0, GRID_SIZE, GRID_SIZE, Component.empty());
        }

        @Override
        protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            Identifier skin = skinTexture();
            Identifier type = shownType();

            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y);
            graphics.pose().scale(CELL, CELL);
            if (skin != null) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, skin, 0, 0, VampireFaceOverlay.FACE_U, VampireFaceOverlay.FACE_V, VampireFaceOverlay.FACE_SIZE, VampireFaceOverlay.FACE_SIZE, VampireFaceOverlay.TEXTURE_SIZE, VampireFaceOverlay.TEXTURE_SIZE);
            } else {
                graphics.fill(0, 0, VampireFaceOverlay.FACE_SIZE, VampireFaceOverlay.FACE_SIZE, FACE_COLOR);
            }
            List<VampireFaceOverlay.Sprite> sprites = null;
            if (!Customization.NONE.equals(type)) {
                Identifier texture = textureOf(type);
                int position = editingEyes ? eyePosition : fangPosition;
                sprites = position >= 0 ? VampireFaceOverlay.sprites(texture, editingEyes, position) : VampireFaceOverlay.defaultSprites(texture, editingEyes);
                if (sprites != null) {
                    for (VampireFaceOverlay.Sprite sprite : sprites) {
                        var source = sprite.source();
                        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, sprite.x(), sprite.y(), VampireFaceOverlay.FACE_U + source.x(), VampireFaceOverlay.FACE_V + source.y(), source.width(), source.height(), VampireFaceOverlay.TEXTURE_SIZE, VampireFaceOverlay.TEXTURE_SIZE);
                    }
                }
            }
            graphics.pose().popMatrix();

            for (int i = 0; i <= VampireFaceOverlay.FACE_SIZE; i++) {
                graphics.fill(x + i * CELL, y, x + i * CELL + 1, y + GRID_SIZE, 0x66404040);
                graphics.fill(x, y + i * CELL, x + GRID_SIZE, y + i * CELL + 1, 0x66404040);
            }
            if (sprites != null && (editingEyes ? eyePosition : fangPosition) >= 0) {
                for (VampireFaceOverlay.Sprite sprite : sprites) {
                    drawOutline(graphics, x + sprite.x() * CELL, y + sprite.y() * CELL, sprite.source().width() * CELL, sprite.source().height() * CELL);
                }
            }
        }

        private void drawOutline(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
            graphics.fill(x, y, x + width, y + 1, MARKER_COLOR);
            graphics.fill(x, y + height - 1, x + width, y + height, MARKER_COLOR);
            graphics.fill(x, y, x + 1, y + height, MARKER_COLOR);
            graphics.fill(x + width - 1, y, x + width, y + height, MARKER_COLOR);
        }

        private boolean isInside(double mouseX, double mouseY) {
            return mouseX >= getX() && mouseX < getX() + GRID_SIZE && mouseY >= getY() && mouseY < getY() + GRID_SIZE;
        }

        private void place(double mouseX, double mouseY) {
            int cellX = Math.max(0, Math.min(VampireFaceOverlay.FACE_SIZE - 1, (int) Math.floor((mouseX - getX()) / CELL)));
            int cellY = Math.max(0, Math.min(VampireFaceOverlay.FACE_SIZE - 1, (int) Math.floor((mouseY - getY()) / CELL)));
            placeFeature(cellX, cellY);
        }

        @Override
        public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
            if (!this.active || !this.visible || !isInside(event.x(), event.y())) {
                return false;
            }
            this.dragging = true;
            place(event.x(), event.y());
            return true;
        }

        @Override
        public boolean mouseDragged(@NotNull MouseButtonEvent event, double dragX, double dragY) {
            if (!this.dragging) {
                return false;
            }
            place(event.x(), event.y());
            return true;
        }

        @Override
        public boolean mouseReleased(@NotNull MouseButtonEvent event) {
            if (!this.dragging) {
                return false;
            }
            this.dragging = false;
            return true;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        }
    }
}
