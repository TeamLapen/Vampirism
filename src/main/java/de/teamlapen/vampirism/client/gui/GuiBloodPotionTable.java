package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.inventory.BloodPotionTableContainer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiBloodPotionTable extends GuiContainer {

    private final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/blood_potion_table.png");
    private final BloodPotionTableContainer container;
    private GuiButton craftBtn;
    private ISound sound;

    public GuiBloodPotionTable(InventoryPlayer playerInv, BlockPos pos, World world) {
        super(new BloodPotionTableContainer(playerInv, pos, world));
        this.container = (BloodPotionTableContainer) inventorySlots;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttons.add(this.craftBtn = new GuiButton(0, this.width / 2 - 77, this.height / 2 - 78, 80, 20, UtilLib.translate("gui.vampirism.blood_potion_table.create")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.CRAFT_BLOOD_POTION, ""));
            }
        });
        craftBtn.enabled = false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        stopSound();

    }

    @Override
    public void tick() {
        super.tick();
        this.craftBtn.enabled = container.canCurrentlyStartCrafting();
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
        this.mc.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);


        if (container.getCraftingPercentage() > 0) {
            int j1 = (int) (28.0F * container.getCraftingPercentage());

            if (j1 > 0) {
                this.drawTexturedModalRect(i + 145, j + 23, 176, 0, 9, j1);

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
                this.fontRenderer.drawSplitString(hint, i + 5, j + 28, 92, java.awt.Color.WHITE.getRGB());
                j += this.fontRenderer.getWordWrappedHeight(hint, 92);
            }
        }
    }

    private void startSound() {
        if (sound == null) {
            sound = new PositionedSoundRecord(ModSounds.boiling, SoundCategory.BLOCKS, 1, 1, container.getBlockPos());
            this.mc.getSoundHandler().playSound(sound);
        }
    }

    private void stopSound() {
        if (sound != null) {
            this.mc.getSoundHandler().stopSound(sound);
            sound = null;
        }
    }
}
