package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.network.UpgradeMinionStatPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;


public abstract class MinionStatsScreen<T extends MinionData, Q extends MinionEntity<T>> extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/appearance.png");


    protected final Q entity;
    protected final int xSize = 256;
    protected final int ySize = 177;
    protected final int statCount;
    private final TranslationTextComponent textLevel = new TranslationTextComponent("text.vampirism.level");
    private final List<Button> statButtons = new ArrayList<>();
    protected int guiLeft;
    protected int guiTop;

    protected MinionStatsScreen(Q entity, int statCount) {
        super(new TranslationTextComponent("gui.vampirism.minion_stats"));
        assert statCount > 0;
        this.entity = entity;
        this.statCount = statCount;
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.renderGuiBackground();
        this.drawTitle();
        super.render(mouseX, mouseY, partialTicks);
        entity.getMinionData().ifPresent(this::renderStats);

    }

    @Override
    public void tick() {
        for (int i = 0; i < statCount; i++) {
            int finalI = i;
            statButtons.get(i).active = entity.getMinionData().map(d -> isActive(d, finalI)).orElse(false);
            statButtons.get(i).visible = entity.getMinionData().map(d -> !isMaxLevel(d)).orElse(false);
        }

    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addButton(new Button(this.guiLeft + ((this.xSize - 80) / 2), this.guiTop + 152, 80, 20, UtilLib.translate("gui.done"), (context) -> {
            this.onClose();
        }));
        for (int i = 0; i < statCount; i++) {
            int finalI = i;
            statButtons.add(this.addButton(new Button(guiLeft + 225, guiTop + 43 + 26 * i, 20, 20, "+", (button) -> VampirismMod.dispatcher.sendToServer(new UpgradeMinionStatPacket(entity.getEntityId(), finalI)))));
        }
    }

    protected abstract boolean isActive(T data, int i);

    protected abstract boolean isMaxLevel(T d);

    protected void renderGuiBackground() {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(this.guiLeft, this.guiTop, this.blitOffset, 0, 0, this.xSize, this.ySize, 256, 300);
    }

    protected void renderLevelRow(int current, int max) {
        this.font.drawString(textLevel.getFormattedText(), guiLeft + 10, guiTop + 30, 0x0);
        this.font.drawString(current + "/" + max, guiLeft + 150, guiTop + 30, 0x404040);
        this.hLine(guiLeft + 10, guiLeft + xSize - 10, guiTop + 40, 0xF0303030);
    }

    protected void renderStatRow(int i, TranslationTextComponent name, StringTextComponent value, int currentLevel, int maxLevel) {
        this.font.drawString(name.getFormattedText() + ":", guiLeft + 10, guiTop + 50 + 26 * i, 0x404040);
        this.font.drawString(value.getFormattedText(), guiLeft + 145, guiTop + 50 + 26 * i, 0x404040);
        this.font.drawString(UtilLib.translate("text.vampirism.level_short") + ": " + currentLevel + "/" + maxLevel, guiLeft + 175, guiTop + 50 + 26 * i, 0x404040);
    }

    protected void renderStats(T data) {

    }

    private void drawTitle() {
        String title = this.title.getFormattedText();
        this.font.drawStringWithShadow(title, this.guiLeft + 10, this.guiTop + 10, 0xFFFFFF);
    }


}
