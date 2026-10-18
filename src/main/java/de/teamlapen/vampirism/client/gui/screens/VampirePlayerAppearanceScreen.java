package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.network.ServerboundAppearancePacket;
import de.teamlapen.vampirism.util.LordTitles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VampirePlayerAppearanceScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance");
    private static final int TITLE_COUNT = 5;
    private static final int PREVIEW_LEFT = 190;
    private static final int PREVIEW_TOP = 30;
    private static final int PREVIEW_RIGHT = 250;
    private static final int PREVIEW_BOTTOM = 140;
    private static final int PREVIEW_SCALE = 36;
    private static final float PREVIEW_Y_OFFSET = 0.0625F;

    private float[] color;
    private boolean titleGender;
    private boolean openingFaceEditor;

    public VampirePlayerAppearanceScreen(@Nullable Screen backScreen) {
        super(NAME, Minecraft.getInstance().player, backScreen);
    }

    @Override
    public void removed() {
        if (!this.openingFaceEditor) {
            this.saveAppearance();
        }
        super.removed();
    }

    @Override
    protected void init() {
        super.init();
        this.openingFaceEditor = false;
        VampirismPlayerAttributes attributes = VampirismPlayerAttributes.get(this.entity);
        IFaction<?> faction = attributes.faction;
        this.color = faction == null ? Color.GRAY.getRGBColorComponents() : new Color(faction.getColor()).getRGBColorComponents();
        this.titleGender = FactionPlayerHandler.get(this.entity).titleGender() == IPlayableFaction.TitleGender.FEMALE;

        this.addRenderableWidget(new ExtendedButton(this.guiLeft + 20, this.guiTop + 43, 100, 20,
                Component.translatable("gui.vampirism.appearance.edit_eyes"), button -> this.openFaceEditor(true)));
        this.addRenderableWidget(new ExtendedButton(this.guiLeft + 20, this.guiTop + 67, 100, 20,
                Component.translatable("gui.vampirism.appearance.edit_fangs"), button -> this.openFaceEditor(false)));
        Checkbox titleGenderButton = Checkbox.builder(Component.translatable("gui.vampirism.appearance.title_gender"), this.font)
                .pos(this.guiLeft + 20, this.guiTop + 99)
                .selected(this.titleGender)
                .onValueChange((button, selected) -> {
                    this.titleGender = selected;
                    FactionPlayerHandler.get(this.entity).setTitleGender(selected);
                }).build();
        titleGenderButton.setTooltip(this.createTitleGenderTooltip());
        this.addRenderableWidget(titleGenderButton);
    }

    @Override
    protected void renderEntityPreview(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                this.guiLeft + PREVIEW_LEFT, this.guiTop + PREVIEW_TOP,
                this.guiLeft + PREVIEW_RIGHT, this.guiTop + PREVIEW_BOTTOM,
                PREVIEW_SCALE, PREVIEW_Y_OFFSET, mouseX, mouseY, this.entity);
    }

    private Tooltip createTitleGenderTooltip() {
        MutableComponent tooltip = Component.empty();
        for (int level = 1; level <= TITLE_COUNT; level++) {
            if (level > 1) tooltip.append(Component.literal("\n"));
            tooltip.append(LordTitles.getVampireTitle(level, IPlayableFaction.TitleGender.MALE))
                    .append(Component.literal(" --> "))
                    .append(LordTitles.getVampireTitle(level, IPlayableFaction.TitleGender.FEMALE));
        }
        return Tooltip.create(tooltip);
    }

    @Override
    protected void renderGuiBackground(@NotNull GuiGraphics graphics) {
        graphics.setColor(this.color[0], this.color[1], this.color[2], 1f);
        super.renderGuiBackground(graphics);
        graphics.setColor(1, 1, 1, 1);
    }

    private void openFaceEditor(boolean eyes) {
        this.openingFaceEditor = true;
        this.minecraft.setScreen(new VampireFaceEditorScreen(this, eyes));
    }

    void finishEditing() {
        this.saveAppearance();
        this.minecraft.setScreen(null);
    }

    private void saveAppearance() {
        VampirePlayerSpecialAttributes attributes = VampirismPlayerAttributes.get(this.entity).getVampSpecial();
        boolean femaleTitle = FactionPlayerHandler.get(this.entity).titleGender() == IPlayableFaction.TitleGender.FEMALE;
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), "",
                attributes.fangType, attributes.eyeType, attributes.glowingEyes ? 1 : 0, femaleTitle ? 1 : 0,
                attributes.eyeX, attributes.eyeY, attributes.fangX, attributes.fangY));
    }
}
