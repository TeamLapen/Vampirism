package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import de.teamlapen.vampirism.client.renderer.tooltips.LordTitlesTooltip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VampirePlayerAppearanceScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance");
    private static final Component TITLES_HEADER = Component.translatable("gui.vampirism.appearance.title_table");
    private static final int PREVIEW_LEFT = 190;
    private static final int PREVIEW_TOP = 30;
    private static final int PREVIEW_RIGHT = 250;
    private static final int PREVIEW_BOTTOM = 140;
    private static final int PREVIEW_SCALE = 36;
    private static final float PREVIEW_Y_OFFSET = 0.0625F;

    private float[] color;
    private boolean titleGender;
    private boolean openingFaceEditor;
    @Nullable
    private Checkbox titleGenderBox;

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
        this.titleGenderBox = Checkbox.builder(Component.translatable("gui.vampirism.appearance.title_gender"), this.font)
                .pos(this.guiLeft + 20, this.guiTop + 99)
                .selected(this.titleGender)
                .onValueChange((button, selected) -> {
                    this.titleGender = selected;
                    FactionPlayerHandler.get(this.entity).setTitleGender(selected);
                }).build();
        this.addRenderableWidget(this.titleGenderBox);
    }

    @Override
    protected void renderEntityPreview(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                this.guiLeft + PREVIEW_LEFT, this.guiTop + PREVIEW_TOP,
                this.guiLeft + PREVIEW_RIGHT, this.guiTop + PREVIEW_BOTTOM,
                PREVIEW_SCALE, PREVIEW_Y_OFFSET, mouseX, mouseY, this.entity);
    }

    private LordTitlesTooltip lordTitlesTooltip() {
        List<Component> defaultTitles = new ArrayList<>();
        List<Component> femaleTitles = new ArrayList<>();
        for (int level = 1; level <= REFERENCE.HIGHEST_VAMPIRE_LORD; level++) {
            defaultTitles.add(LordTitles.getVampireTitle(level, IPlayableFaction.TitleGender.MALE));
            femaleTitles.add(LordTitles.getVampireTitle(level, IPlayableFaction.TitleGender.FEMALE));
        }
        return new LordTitlesTooltip(defaultTitles, femaleTitles, FactionPlayerHandler.get(this.entity).getLordLevel(), this.titleGender);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.titleGenderBox != null && this.titleGenderBox.isHovered()) {
            graphics.renderTooltip(this.font, List.of(TITLES_HEADER), Optional.of(this.lordTitlesTooltip()), mouseX, mouseY);
        }
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
