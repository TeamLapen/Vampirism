package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.HunterBasicMenu;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;


public class HunterBasicScreen extends AbstractContainerScreen<HunterBasicMenu> {
    private static final ResourceLocation guiTexture = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/basic_hunter.png");

    private Button buttonLevelup;
    private int missing = 0;
    private int timer = 0;

    public HunterBasicScreen(@NotNull HunterBasicMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void init() {
        super.init();

        Component name = Component.translatable("text.vampirism.level_up");
        int wid = this.font.width(name) + 5;
        int i = (this.imageWidth - wid) / 2;
        int j = (this.height - this.imageHeight) / 2;
        addRenderableWidget(buttonLevelup = new ExtendedButton(this.leftPos + i, j + 50, wid, 20, name, (context) -> {
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.BASIC_HUNTER_LEVELUP));
            this.onClose();
        }));
        buttonLevelup.active = false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

    }

    @Override
    protected void containerTick() {
        timer = (timer + 1) % 10;
        if (timer == 0) {
            this.missing = menu.getMissingCount();
            this.buttonLevelup.active = missing == 0;
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(guiTexture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        Component text = null;
        if (missing == 0) {
            text = Component.translatable("text.vampirism.basic_hunter.i_will_train_you");
        } else if (missing > 0) {
            text = Component.translatable("text.vampirism.basic_hunter.pay_n_vampire_blood_more", missing);
        }
        if (text != null) {
            graphics.drawWordWrap(this.font, text, 50, 12, 120, 0);
        }
    }
}
