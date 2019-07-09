package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.HunterTrainerContainer;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gui for the Hunter Trainer interaction
 */
@OnlyIn(Dist.CLIENT)
public class HunterTrainerScreen extends ContainerScreen {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_trainer.png");
    private final HunterTrainerContainer container;
    private Button buttonLevelup;

    public HunterTrainerScreen(HunterTrainerContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        String name = I18n.format("text.vampirism.level_up");
        this.buttons.add(this.buttonLevelup = new Button(1, i + 120, j + 24, fontRenderer.getStringWidth(name) + 5, 20, name) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TRAINERLEVELUP, ""));//TODO Dispatcher
                PlayerEntity player = Minecraft.getInstance().player;
                UtilLib.spawnParticles(player.getEntityWorld(), Particles.ENCHANT, player.posX, player.posY, player.posZ, 1, 1, 1, 100, 1);
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 4.0F, (1.0F + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F) * 0.7F);
            }
        });
        this.buttonLevelup.enabled = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (container.hasChanged() || this.mc.player.getRNG().nextInt(40) == 6) {
            buttonLevelup.enabled = container.canLevelup();
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(altarGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String string = container.getHunterTrainerInventory().hasCustomName() ? this.container.getHunterTrainerInventory().getName().getFormattedText() : this.container.getHunterTrainerInventory().getName().getFormattedText();
        this.fontRenderer.drawString(string, 8, 6, 0x404040);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);

        String text = null;
        if (!container.getMissingItems().isEmpty()) {
            ItemStack missing = container.getMissingItems();
            ITextComponent item = missing.getItem() instanceof HunterIntelItem ? missing.getItem().getDisplayName(missing) : new TranslationTextComponent(missing.getTranslationKey() + ".name");
            text = I18n.format("text.vampirism.ritual_missing_items", missing.getCount(), item.getUnformattedComponentText());
        }
        if (text != null) this.fontRenderer.drawSplitString(text, 8, 50, this.xSize - 10, 0x000000);
    }
}
