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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);

    }

    @Override
    public void tick() {
        super.tick();
        timer = (timer + 1) % 10;
        if (timer == 0) {
            this.missing = container.getMissingCount();
            this.buttonLevelup.active = missing == 0;
        }
    }

    @Override
    public void init() {
        super.init();

        ITextComponent name = new TranslationTextComponent("text.vampirism.level_up");
        int wid = this.font.func_238414_a_(name) + 5;
        int i = (this.xSize - wid) / 2;
        int j = (this.height - this.ySize) / 2;
        addButton(buttonLevelup = new Button(this.guiLeft + i, j + 50, wid, 20, name, (context) -> {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.BASICHUNTERLEVELUP, ""));
            this.closeScreen();
        }));
        buttonLevelup.active = false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(stack, i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(stack, mouseX, mouseY);

        ITextComponent text = null;
        if (missing == 0) {
            text = new TranslationTextComponent("text.vampirism.basic_hunter.i_will_train_you");
        } else if (missing > 0) {
            text = new TranslationTextComponent("text.vampirism.basic_hunter.pay_n_vampire_blood_more", missing);
        }
        if (text != null) {
            this.font.func_238418_a_(text, 50, 12, 120, 0);
        }
    }
}
