package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.inventory.container.BloodPotionTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BloodPotionTableScreen extends ContainerScreen<BloodPotionTableContainer> {

    private final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/blood_potion_table.png");
    private Button craftBtn;
    private ISound sound;

    public BloodPotionTableScreen(BloodPotionTableContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    public void init() {
        super.init();
        this.addButton(this.craftBtn = new Button(this.width / 2 - 77, this.height / 2 - 78, font.getStringWidth(UtilLib.translate("gui.vampirism.blood_potion_table.create")) + 5, 20, UtilLib.translate("gui.vampirism.blood_potion_table.create"), (context) -> container.onCraftingClicked()));
        craftBtn.active = false;
    }

    @Override
    public void onClose() {
        super.onClose();
        stopSound();
    }

    @Override
    public void tick() {
        super.tick();
        container.tick();
        this.craftBtn.active = container.canCurrentlyStartCrafting();
        if (container.getCraftingPercentage() == 0 || container.getCraftingPercentage() == 1) {
            stopSound();
        } else {
            startSound();
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
        this.blit(i, j, 0, 0, this.xSize, this.ySize);


        if (container.getCraftingPercentage() > 0) {
            int j1 = (int) (28.0F * container.getCraftingPercentage());

            if (j1 > 0) {
                this.blit(i + 145, j + 23, 176, 0, 9, j1);

            }

        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        List<String> hints = container.getLocalizedCraftingHint();
        if (hints != null) {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            for (String hint : hints) {
                this.font.drawSplitString(hint, i + 5, j + 28, 92, java.awt.Color.WHITE.getRGB());
                j += this.font.getWordWrappedHeight(hint, 92);
            }
        }
    }

    private void startSound() {
        if (sound == null) {
            container.getWorldPosCallable().consume(((world, pos) -> {
                sound = new SimpleSound(ModSounds.boiling, SoundCategory.BLOCKS, 1, 1, pos);
                this.minecraft.getSoundHandler().play(sound);
            }));
        }
    }

    private void stopSound() {
        if (sound != null) {
            this.minecraft.getSoundHandler().stop(sound);
            sound = null;
        }
    }
}
