package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.inventory.container.BloodPotionTableContainer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
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
    public void func_230430_a_(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(stack);
        super.func_230430_a_(stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);

    }

    @Override
    public void func_231023_e_() { //tick
        super.func_231023_e_();
        this.craftBtn.field_230693_o_ = container.canCurrentlyStartCrafting();
        if (container.getCraftingPercentage() == 0 || container.getCraftingPercentage() == 1) {
            stopSound();
        } else {
            startSound();
        }
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();
        this.func_230480_a_(this.craftBtn = new Button(this.field_230708_k_ / 2 - 77, this.field_230709_l_ / 2 - 78, this.field_230712_o_.getStringWidth(UtilLib.translate("gui.vampirism.blood_potion_table.create")) + 5, 20, new TranslationTextComponent("gui.vampirism.blood_potion_table.create"), (context) -> handleClicked()));
        craftBtn.field_230693_o_ = false;
    }

    @Override
    public void func_231175_as__() {
        super.func_231175_as__();
        stopSound();
    }

    @Override
    protected void func_230450_a_(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.field_230708_k_ - this.xSize) / 2;
        int j = (this.field_230709_l_ - this.ySize) / 2;
        this.field_230706_i_.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
        this.func_238474_b_(stack, i, j, 0, 0, this.xSize, this.ySize);


        if (container.getCraftingPercentage() > 0) {
            int j1 = (int) (28.0F * container.getCraftingPercentage());

            if (j1 > 0) {
                this.func_238474_b_(stack, i + 145, j + 23, 176, 0, 9, j1);

            }

        }

    }

    @Override
    protected void func_230451_b_(MatrixStack stack, int mouseX, int mouseY) {
        //super.func_230451_b_(stack, mouseX, mouseY);
        List<ITextComponent> hints = container.getLocalizedCraftingHint();
        if (hints != null) {
            int i = (this.field_230708_k_ - this.xSize) / 2;
            int j = (this.field_230709_l_ - this.ySize) / 2;
            for (ITextComponent hint : hints) {
                for (ITextProperties t : this.field_230712_o_.func_238425_b_(hint, 92)) {
                    this.field_230712_o_.func_238422_b_(stack, t, i + 5, j + 28, Color.WHITE.getRGB());
                    j += this.field_230712_o_.FONT_HEIGHT;
                }
            }
        }
    }

    private void handleClicked() {
        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.CRAFT_BLOOD_POTION, ""));
    }

    private void startSound() {
        if (sound == null) {
            container.getWorldPosCallable().consume(((world, pos) -> {
                sound = new SimpleSound(ModSounds.boiling, SoundCategory.BLOCKS, 1, 1, pos);
                this.field_230706_i_.getSoundHandler().play(sound);
            }));
        }
    }

    private void stopSound() {
        if (sound != null) {
            this.field_230706_i_.getSoundHandler().stop(sound);
            sound = null;
        }
    }
}
