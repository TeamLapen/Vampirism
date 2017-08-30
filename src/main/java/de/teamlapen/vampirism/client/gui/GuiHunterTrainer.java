package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.inventory.HunterTrainerContainer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

/**
 * Gui for the Hunter Trainer interaction
 */
@SideOnly(Side.CLIENT)
public class GuiHunterTrainer extends GuiContainer {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_trainer.png");
    private final HunterTrainerContainer container;
    private GuiButton buttonLevelup;

    public GuiHunterTrainer(HunterTrainerContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        String name = I18n.format("text.vampirism.level_up");
        this.buttonList.add(this.buttonLevelup = new GuiButton(1, i + 120, j + 24, fontRenderer.getStringWidth(name) + 5, 20, name));
        this.buttonLevelup.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (container.hasChanged() || this.mc.player.getRNG().nextInt(40) == 6) {
            buttonLevelup.enabled = container.canLevelup();
        }

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TRAINERLEVELUP, ""));
            EntityPlayer player = Minecraft.getMinecraft().player;
            UtilLib.spawnParticles(player.getEntityWorld(), EnumParticleTypes.ENCHANTMENT_TABLE, player.posX, player.posY, player.posZ, 1, 1, 1, 100, 1);
            player.playSound(SoundEvents.BLOCK_NOTE_HARP, 4.0F, (1.0F + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F) * 0.7F);
        } else {
            super.actionPerformed(button);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(altarGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String string = container.getHunterTrainerInventory().hasCustomName() ? this.container.getHunterTrainerInventory().getName() : I18n.format(this.container.getHunterTrainerInventory().getName());
        this.fontRenderer.drawString(string, 8, 6, 0x404040);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);

        String text = null;
        if (!container.getMissingItems().isEmpty()) {
            ItemStack missing = container.getMissingItems();
            ITextComponent item = missing.getItem().equals(ModItems.hunter_intel) ? ModItems.hunter_intel.getDisplayName(missing) : new TextComponentTranslation(missing.getUnlocalizedName() + ".name");
            text = I18n.format("text.vampirism.ritual_missing_items", ItemStackUtil.getCount(missing), item.getUnformattedText());
        }
        if (text != null) this.fontRenderer.drawSplitString(text, 8, 50, this.xSize - 10, 0x000000);
    }
}
