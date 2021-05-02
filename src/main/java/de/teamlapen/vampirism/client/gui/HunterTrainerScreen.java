package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
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
public class HunterTrainerScreen extends ContainerScreen<HunterTrainerContainer> {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_trainer.png");
    private Button buttonLevelup;

    public HunterTrainerScreen(HunterTrainerContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void init() {
        super.init();
        ITextComponent name = new TranslationTextComponent("text.vampirism.level_up");
        this.addButton(this.buttonLevelup = new Button(this.guiLeft + 120, this.guiTop + 24, this.font.getStringPropertyWidth(name) + 5, 20, name, (context) -> {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TRAINERLEVELUP, ""));
            PlayerEntity player = Minecraft.getInstance().player;
            UtilLib.spawnParticles(player.getEntityWorld(), ParticleTypes.ENCHANT, player.getPosX(), player.getPosY(), player.getPosZ(), 1, 1, 1, 100, 1);
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 4.0F, (1.0F + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F) * 0.7F);
            this.closeScreen();
        }));
        this.buttonLevelup.active = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (container.hasChanged() || this.minecraft.player.getRNG().nextInt(40) == 6) {
            buttonLevelup.active = container.canLevelup();
        }

    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(stack, mouseX, mouseY);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float var1, int var2, int var3) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(altarGuiTextures);
        this.blit(stack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int par1, int par2) {
        super.drawGuiContainerForegroundLayer(stack, par1, par2);

        ITextComponent text = null;
        if (!container.getMissingItems().isEmpty()) {
            ItemStack missing = container.getMissingItems();
            ITextComponent item = missing.getItem() instanceof HunterIntelItem ? ((HunterIntelItem) missing.getItem()).getCustomName() : new TranslationTextComponent(missing.getTranslationKey());
            text = new TranslationTextComponent("text.vampirism.hunter_trainer.ritual_missing_items", missing.getCount(), item);
        }
        if (text != null) this.font.func_238418_a_(text, 8, 50, this.xSize - 10, 0x000000);
    }
}
