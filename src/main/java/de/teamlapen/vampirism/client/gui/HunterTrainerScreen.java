package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.network.CSimpleInputEvent;
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
        this.addButton(this.buttonLevelup = new Button(this.leftPos + 120, this.topPos + 24, this.font.width(name) + 5, 20, name, (context) -> {
            VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.TRAINER_LEVELUP));
            PlayerEntity player = Minecraft.getInstance().player;
            UtilLib.spawnParticles(player.getCommandSenderWorld(), ParticleTypes.ENCHANT, player.getX(), player.getY(), player.getZ(), 1, 1, 1, 100, 1);
            player.playSound(SoundEvents.NOTE_BLOCK_HARP, 4.0F, (1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F) * 0.7F);
            this.onClose();
        }));
        this.buttonLevelup.active = false;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);

    }

    @Override
    public void tick() {
        super.tick();
        if (menu.hasChanged() || this.minecraft.player.getRandom().nextInt(40) == 6) {
            buttonLevelup.active = menu.canLevelup();
        }

    }

    @Override
    protected void renderBg(MatrixStack stack, float var1, int var2, int var3) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(altarGuiTextures);
        this.blit(stack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack stack, int par1, int par2) {
        super.renderLabels(stack, par1, par2);

        ITextComponent text = null;
        if (!menu.getMissingItems().isEmpty()) {
            ItemStack missing = menu.getMissingItems();
            ITextComponent item = missing.getItem() instanceof HunterIntelItem ? ((HunterIntelItem) missing.getItem()).getCustomName() : new TranslationTextComponent(missing.getDescriptionId());
            text = new TranslationTextComponent("text.vampirism.hunter_trainer.ritual_missing_items", missing.getCount(), item);
        }
        if (text != null) this.font.drawWordWrap(text, 8, 50, this.imageWidth - 10, 0x000000);
    }
}
