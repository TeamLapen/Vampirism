package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.container.HunterBasicContainer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class HunterBasicScreen extends ContainerScreen<HunterBasicContainer> {
    private static final ResourceLocation guiTexture = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_basic.png");

    private Button buttonLevelup;
    private int missing = 0;
    private int timer = 0;

    public HunterBasicScreen(HunterBasicContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void func_230430_a_(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(stack);
        super.func_230430_a_(stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);

    }

    @Override
    public void func_231023_e_() {
        super.func_231023_e_();
        timer = (timer + 1) % 10;
        if (timer == 0) {
            this.missing = container.getMissingCount();
            this.buttonLevelup.field_230693_o_ = missing == 0;
        }
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();

        ITextComponent name = new TranslationTextComponent("text.vampirism.level_up");
        int wid = this.field_230712_o_.func_238414_a_(name) + 5;
        int i = (this.xSize - wid) / 2;
        int j = (this.field_230709_l_ - this.ySize) / 2;
        func_230480_a_(buttonLevelup = new Button(this.guiLeft + i, j + 50, wid, 20, name, (context) -> {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.BASICHUNTERLEVELUP, ""));
            this.func_231175_as__();
        }));
        buttonLevelup.field_230693_o_ = false;
    }

    @Override
    protected void func_230450_a_(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(guiTexture);
        int i = (this.field_230708_k_ - this.xSize) / 2;
        int j = (this.field_230709_l_ - this.ySize) / 2;
        this.func_238474_b_(stack, i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void func_230451_b_(MatrixStack stack, int mouseX, int mouseY) {
        super.func_230451_b_(stack, mouseX, mouseY);

        ITextComponent text = null;
        if (missing == 0) {
            text = new TranslationTextComponent("text.vampirism.basic_hunter.i_will_train_you");
        } else if (missing > 0) {
            text = new TranslationTextComponent("text.vampirism.basic_hunter.pay_n_vampire_blood_more", missing);
        }
        if (text != null) {
            this.field_230712_o_.func_238418_a_(text, 50, 12, 120, 0);
        }
    }
}
