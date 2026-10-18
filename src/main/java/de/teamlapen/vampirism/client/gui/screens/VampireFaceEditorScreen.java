package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.client.gui.components.HoverList;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.renderer.entity.layers.VampireFaceOverlay;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class VampireFaceEditorScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance.face_editor");
    private static final int FACE_GRID_SIZE = 8;
    private static final int FACE_PIXEL_SIZE = 8;

    private final VampirePlayerAppearanceScreen parent;
    private boolean editingEyes;
    private float[] color;
    private int eyeType;
    private int fangType;
    private int eyeX;
    private int eyeY;
    private int fangX;
    private int fangY;
    private int faceGridX;
    private int faceGridY;
    private boolean showFacePositionHint = true;
    private ExtendedButton modeButton;
    private ExtendedButton typeButton;
    private Checkbox glowingEyesButton;
    private HoverList<?> eyeList;
    private HoverList<?> fangList;
    private int hoveredTypeIndex = -1;

    public VampireFaceEditorScreen(VampirePlayerAppearanceScreen parent, boolean editingEyes) {
        super(NAME, Minecraft.getInstance().player, parent);
        this.parent = parent;
        this.editingEyes = editingEyes;
    }

    @Override
    protected void init() {
        this.restoreHoveredPreview();
        super.init();
        VampirismPlayerAttributes attributes = VampirismPlayerAttributes.get(this.entity);
        IFaction<?> faction = attributes.faction;
        this.color = faction == null ? Color.GRAY.getRGBColorComponents() : new Color(faction.getColor()).getRGBColorComponents();
        VampirePlayerSpecialAttributes vampireAttributes = attributes.getVampSpecial();
        this.eyeType = vampireAttributes.eyeType;
        this.fangType = vampireAttributes.fangType;
        this.eyeX = vampireAttributes.eyeX;
        this.eyeY = vampireAttributes.eyeY;
        this.fangX = vampireAttributes.fangX;
        this.fangY = vampireAttributes.fangY;
        this.clampPositionForType(true);
        this.clampPositionForType(false);
        this.faceGridX = this.guiLeft + 125;
        this.faceGridY = this.guiTop + 47;

        this.modeButton = this.addRenderableWidget(new ExtendedButton(this.guiLeft + 20, this.guiTop + 30, 100, 20,
                Component.empty(), button -> this.switchMode()));
        this.typeButton = this.addRenderableWidget(new ExtendedButton(this.guiLeft + 20, this.guiTop + 55, 100, 20,
                Component.empty(), button -> this.setTypeListVisibility(!this.isTypeListVisible())));
        this.glowingEyesButton = this.addRenderableWidget(Checkbox.builder(
                        Component.translatable("gui.vampirism.appearance.glowing_eye"), this.font)
                .pos(this.guiLeft + 20, this.guiTop + 124)
                .selected(vampireAttributes.glowingEyes)
                .onValueChange((button, selected) -> VampirePlayer.get(this.entity).setGlowingEyes(selected))
                .build());

        this.eyeList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 76, 100, 70)
                .componentsWithClickAndHover(IntStream.rangeClosed(0, REFERENCE.EYE_TYPE_COUNT)
                        .mapToObj(index -> index == REFERENCE.EYE_TYPE_COUNT
                                ? Component.translatable("gui.vampirism.appearance.no_eyes")
                                : Component.translatable("gui.vampirism.appearance.eye").append(" " + (index + 1)))
                        .toList(), this::selectEyeType, (index, hovered) -> {})
                .build());
        this.fangList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 76, 100, 70)
                .componentsWithClickAndHover(IntStream.rangeClosed(0, REFERENCE.FANG_TYPE_COUNT)
                        .mapToObj(index -> index == REFERENCE.FANG_TYPE_COUNT
                                ? Component.translatable("gui.vampirism.appearance.no_fangs")
                                : Component.translatable("gui.vampirism.appearance.fang").append(" " + (index + 1)))
                        .toList(), this::selectFangType, (index, hovered) -> {})
                .build());
        this.setTypeListVisibility(false);
    }

    @Override
    public void onClose() {
        this.restoreHoveredPreview();
        this.parent.finishEditing();
    }

    @Override
    public void removed() {
        this.restoreHoveredPreview();
        super.removed();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isTypeListVisible()) {
            HoverList<?> list = this.editingEyes ? this.eyeList : this.fangList;
            if (list.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= this.faceGridX && mouseX < this.faceGridX + FACE_GRID_SIZE * FACE_PIXEL_SIZE
                && mouseY >= this.faceGridY && mouseY < this.faceGridY + FACE_GRID_SIZE * FACE_PIXEL_SIZE) {
            int x = (int) (mouseX - this.faceGridX) / FACE_PIXEL_SIZE;
            int y = (int) (mouseY - this.faceGridY) / FACE_PIXEL_SIZE;
            this.showFacePositionHint = false;
            this.positionFeature(x, y);
            this.setTypeListVisibility(false);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderFaceEditor(graphics);
    }

    @Override
    protected void renderEntityPreview(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        this.updateHoveredPreview();
        net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                this.guiLeft + 190, this.guiTop + 30, this.guiLeft + 250, this.guiTop + 140,
                36, 0.0625F, mouseX, mouseY, this.entity);
    }

    @Override
    protected void renderGuiBackground(@NotNull GuiGraphics graphics) {
        graphics.setColor(this.color[0], this.color[1], this.color[2], 1f);
        super.renderGuiBackground(graphics);
        graphics.setColor(1, 1, 1, 1);
    }

    private void switchMode() {
        this.restoreHoveredPreview();
        this.editingEyes = !this.editingEyes;
        this.showFacePositionHint = true;
        this.setTypeListVisibility(false);
    }

    private void setTypeListVisibility(boolean visible) {
        if (!visible) this.restoreHoveredPreview();
        this.eyeList.visible = visible && this.editingEyes;
        this.fangList.visible = visible && !this.editingEyes;
        this.glowingEyesButton.visible = this.editingEyes && !visible;
        this.modeButton.setMessage(Component.translatable(this.editingEyes
                ? "gui.vampirism.appearance.edit_eyes" : "gui.vampirism.appearance.edit_fangs"));
        this.typeButton.setMessage(this.editingEyes ? this.eyeTypeLabel() : this.fangTypeLabel());
    }

    private boolean isTypeListVisible() {
        return this.eyeList.visible || this.fangList.visible;
    }

    private void updateHoveredPreview() {
        int hoveredIndex = this.editingEyes ? this.eyeList.getHoveredIndex() : this.fangList.getHoveredIndex();
        if (hoveredIndex == this.hoveredTypeIndex) return;

        this.hoveredTypeIndex = hoveredIndex;
        VampirePlayer vampire = VampirePlayer.get(this.entity);
        if (this.editingEyes) {
            vampire.setEyeType(hoveredIndex < 0 ? this.eyeType
                    : hoveredIndex == REFERENCE.EYE_TYPE_COUNT ? -1 : hoveredIndex);
        } else {
            vampire.setFangType(hoveredIndex < 0 ? this.fangType
                    : hoveredIndex == REFERENCE.FANG_TYPE_COUNT ? -1 : hoveredIndex);
        }
    }

    private void restoreHoveredPreview() {
        if (this.hoveredTypeIndex < 0) return;
        this.hoveredTypeIndex = -1;
        VampirePlayer vampire = VampirePlayer.get(this.entity);
        if (this.editingEyes) vampire.setEyeType(this.eyeType);
        else vampire.setFangType(this.fangType);
    }

    private int displayedEyeType() {
        return this.editingEyes && this.hoveredTypeIndex >= 0
                ? (this.hoveredTypeIndex == REFERENCE.EYE_TYPE_COUNT ? -1 : this.hoveredTypeIndex)
                : this.eyeType;
    }

    private int displayedFangType() {
        return !this.editingEyes && this.hoveredTypeIndex >= 0
                ? (this.hoveredTypeIndex == REFERENCE.FANG_TYPE_COUNT ? -1 : this.hoveredTypeIndex)
                : this.fangType;
    }

    private Component eyeTypeLabel() {
        return this.eyeType < 0 ? Component.translatable("gui.vampirism.appearance.no_eyes")
                : Component.translatable("gui.vampirism.appearance.eye").append(" " + (this.eyeType + 1));
    }

    private Component fangTypeLabel() {
        return this.fangType < 0 ? Component.translatable("gui.vampirism.appearance.no_fangs")
                : Component.translatable("gui.vampirism.appearance.fang").append(" " + (this.fangType + 1));
    }

    private void selectEyeType(int index) {
        this.eyeType = index == REFERENCE.EYE_TYPE_COUNT ? -1 : index;
        VampirePlayer vampire = VampirePlayer.get(this.entity);
        vampire.setEyeType(this.eyeType);
        this.clampPositionForType(true);
        this.setTypeListVisibility(false);
    }

    private void selectFangType(int index) {
        this.fangType = index == REFERENCE.FANG_TYPE_COUNT ? -1 : index;
        VampirePlayer vampire = VampirePlayer.get(this.entity);
        vampire.setFangType(this.fangType);
        this.clampPositionForType(false);
        this.setTypeListVisibility(false);
    }

    private void clampPositionForType(boolean eyes) {
        int x = eyes ? this.eyeX : this.fangX;
        int y = eyes ? this.eyeY : this.fangY;
        if (x < 0 || y < 0) return;
        VampireFaceOverlay.SpriteBounds motif = eyes ? this.eyeMotif() : this.fangMotif();
        if (motif == null) return;
        int maxX = (eyes ? FACE_GRID_SIZE / 2 : FACE_GRID_SIZE) - motif.width();
        x = Math.max(0, Math.min(maxX, x));
        y = Math.max(0, Math.min(FACE_GRID_SIZE - motif.height(), y));
        if (eyes) {
            this.eyeX = x;
            this.eyeY = y;
            VampirePlayer.get(this.entity).setEyePosition(x, y);
        } else {
            this.fangX = x;
            this.fangY = y;
            VampirePlayer.get(this.entity).setFangPosition(x, y);
        }
    }

    private void positionFeature(int x, int y) {
        VampirePlayer vampire = VampirePlayer.get(this.entity);
        if (this.editingEyes) {
            if (this.eyeType < 0) {
                this.eyeType = 0;
                vampire.setEyeType(this.eyeType);
            }
            VampireFaceOverlay.SpriteBounds motif = this.eyeMotif();
            int width = motif == null ? 1 : motif.width();
            int height = motif == null ? 1 : motif.height();
            if (x >= FACE_GRID_SIZE / 2) x = FACE_GRID_SIZE - x - width;
            this.eyeX = Math.max(0, Math.min(FACE_GRID_SIZE / 2 - width, x));
            this.eyeY = Math.max(0, Math.min(FACE_GRID_SIZE - height, y));
            vampire.setEyePosition(this.eyeX, this.eyeY);
        } else {
            if (this.fangType < 0) {
                this.fangType = 0;
                vampire.setFangType(this.fangType);
            }
            VampireFaceOverlay.SpriteBounds motif = this.fangMotif();
            int width = motif == null ? 1 : motif.width();
            int height = motif == null ? 1 : motif.height();
            this.fangX = Math.max(0, Math.min(FACE_GRID_SIZE - width, x));
            this.fangY = Math.max(0, Math.min(FACE_GRID_SIZE - height, y));
            vampire.setFangPosition(this.fangX, this.fangY);
        }
        this.typeButton.setMessage(this.editingEyes ? this.eyeTypeLabel() : this.fangTypeLabel());
    }

    @Nullable
    private VampireFaceOverlay.SpriteBounds eyeMotif() {
        return this.eyeMotif(this.eyeType);
    }

    @Nullable
    private VampireFaceOverlay.SpriteBounds eyeMotif(int type) {
        VampireFaceOverlay.SpritePair pair = VampireFaceOverlay.eye(type);
        return pair == null ? null : pair.left();
    }

    @Nullable
    private VampireFaceOverlay.SpriteBounds fangMotif() {
        return this.fangMotif(this.fangType);
    }

    @Nullable
    private VampireFaceOverlay.SpriteBounds fangMotif(int type) {
        return VampireFaceOverlay.fangMotif(type);
    }

    private void renderFaceEditor(@NotNull GuiGraphics graphics) {
        if (this.showFacePositionHint) {
            graphics.drawWordWrap(this.font, Component.translatable(this.editingEyes
                            ? "gui.vampirism.appearance.position_eyes" : "gui.vampirism.appearance.position_fangs"),
                    this.faceGridX, this.guiTop + 7, 125, 0xFFFFFFFF);
        }

        ResourceLocation skin = Minecraft.getInstance().getSkinManager()
                .getInsecureSkin(this.entity.getGameProfile()).texture();
        PlayerFaceRenderer.draw(graphics, skin, this.faceGridX, this.faceGridY,
                FACE_GRID_SIZE * FACE_PIXEL_SIZE, false, false);
        int displayedType = this.editingEyes ? this.displayedEyeType() : this.displayedFangType();
        RenderSystem.enableBlend();
        if (this.editingEyes && displayedType >= 0) {
            this.renderEyes(graphics, displayedType);
        } else if (!this.editingEyes && displayedType >= 0) {
            this.renderFang(graphics, displayedType);
        }
        if (this.entity.isModelPartShown(PlayerModelPart.HAT)) {
            graphics.blit(skin, this.faceGridX, this.faceGridY,
                    FACE_GRID_SIZE * FACE_PIXEL_SIZE, FACE_GRID_SIZE * FACE_PIXEL_SIZE,
                    40, 8, 8, 8, 64, 64);
        }
        RenderSystem.disableBlend();

        for (int x = 0; x < FACE_GRID_SIZE; x++) {
            for (int y = 0; y < FACE_GRID_SIZE; y++) {
                int left = this.faceGridX + x * FACE_PIXEL_SIZE;
                int top = this.faceGridY + y * FACE_PIXEL_SIZE;
                graphics.fill(left, top, left + FACE_PIXEL_SIZE, top + FACE_PIXEL_SIZE, 0x33202020);
                graphics.renderOutline(left, top, FACE_PIXEL_SIZE, FACE_PIXEL_SIZE, 0x66404040);
            }
        }
        if (this.editingEyes && displayedType >= 0 && this.eyeX >= 0 && this.eyeY >= 0) {
            VampireFaceOverlay.SpritePair pair = VampireFaceOverlay.eye(displayedType);
            if (pair != null) {
                int x = Math.max(0, Math.min(FACE_GRID_SIZE / 2 - pair.width(), this.eyeX));
                int y = Math.max(0, Math.min(FACE_GRID_SIZE - pair.height(), this.eyeY));
                this.renderPositionMarker(graphics, x, y, pair.left().width(), pair.left().height(), 0xFFFF3333);
                this.renderPositionMarker(graphics, FACE_GRID_SIZE - x - pair.right().width(), y,
                        pair.right().width(), pair.right().height(), 0xFFFF3333);
            }
        } else if (!this.editingEyes && displayedType >= 0 && this.fangX >= 0 && this.fangY >= 0) {
            VampireFaceOverlay.SpriteBounds motif = this.fangMotif(displayedType);
            if (motif != null) {
                int x = Math.max(0, Math.min(FACE_GRID_SIZE - motif.width(), this.fangX));
                int y = Math.max(0, Math.min(FACE_GRID_SIZE - motif.height(), this.fangY));
                this.renderPositionMarker(graphics, x, y,
                        motif.width(), motif.height(), 0xFF55AAFF);
            }
        }
    }

    private void renderEyes(@NotNull GuiGraphics graphics, int type) {
        VampireFaceOverlay.SpritePair pair = VampireFaceOverlay.eye(type);
        if (pair == null) return;
        int leftX;
        int rightX;
        int y;
        if (this.eyeX < 0 || this.eyeY < 0) {
            if (!this.isOnBaseFace(pair.left()) || !this.isOnBaseFace(pair.right())) return;
            leftX = pair.left().x() - 8;
            rightX = pair.right().x() - 8;
            y = pair.left().y() - 8;
        } else {
            leftX = Math.max(0, Math.min(FACE_GRID_SIZE / 2 - pair.width(), this.eyeX));
            rightX = FACE_GRID_SIZE - leftX - pair.right().width();
            y = Math.max(0, Math.min(FACE_GRID_SIZE - pair.height(), this.eyeY));
        }
        ResourceLocation texture = VResourceLocation.mod("textures/entity/vanilla/eyes" + REFERENCE.eyeTextureId(type) + ".png");
        this.blitOverlaySprite(graphics, texture, pair.left(), leftX, y);
        this.blitOverlaySprite(graphics, texture, pair.right(), rightX, y);
    }

    private void renderFang(@NotNull GuiGraphics graphics, int type) {
        VampireFaceOverlay.SpriteBounds motif = this.fangMotif(type);
        if (motif == null) return;
        int x = this.fangX < 0 || this.fangY < 0 ? motif.x() - 8
                : Math.max(0, Math.min(FACE_GRID_SIZE - motif.width(), this.fangX));
        int y = this.fangX < 0 || this.fangY < 0 ? motif.y() - 8
                : Math.max(0, Math.min(FACE_GRID_SIZE - motif.height(), this.fangY));
        ResourceLocation texture = VResourceLocation.mod("textures/entity/vanilla/fangs" + type + ".png");
        this.blitOverlaySprite(graphics, texture, motif, x, y);
    }

    private boolean isOnBaseFace(VampireFaceOverlay.SpriteBounds sprite) {
        return sprite.x() >= 8 && sprite.x() + sprite.width() <= 16
                && sprite.y() >= 8 && sprite.y() + sprite.height() <= 16;
    }

    private void blitOverlaySprite(@NotNull GuiGraphics graphics, ResourceLocation texture,
                                   VampireFaceOverlay.SpriteBounds motif, int x, int y) {
        graphics.blit(texture, this.faceGridX + x * FACE_PIXEL_SIZE, this.faceGridY + y * FACE_PIXEL_SIZE,
                motif.width() * FACE_PIXEL_SIZE, motif.height() * FACE_PIXEL_SIZE,
                motif.x(), motif.y(), motif.width(), motif.height(), 64, 64);
    }

    private void renderPositionMarker(@NotNull GuiGraphics graphics, int x, int y,
                                      int width, int height, int color) {
        graphics.renderOutline(this.faceGridX + x * FACE_PIXEL_SIZE, this.faceGridY + y * FACE_PIXEL_SIZE,
                width * FACE_PIXEL_SIZE, height * FACE_PIXEL_SIZE, color);
    }
}
