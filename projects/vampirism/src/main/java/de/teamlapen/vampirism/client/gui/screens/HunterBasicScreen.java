package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.common.world.inventory.HunterBasicMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HunterBasicScreen extends ItemCombinerScreen<HunterBasicMenu> {
    private static final Identifier BACKGROUND = VIdentifier.mod("textures/gui/container/basic_hunter.png");
    private static final Identifier PURE_BLOOD_BOTTLE_SLOT_SPRITE = VIdentifier.mod("container/slot/pure_blood_bottle");

    private Button buttonLevelUp;

    private final CyclingSlotBackground bloodIcon = new CyclingSlotBackground(0);

    public HunterBasicScreen(@NotNull HunterBasicMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name) {
        super(inventorySlotsIn, playerInventory, name, BACKGROUND);
    }

    @Override
    protected void renderErrorIcon(@NotNull GuiGraphics guiGraphics, int x, int y) {

        Component component = switch (this.menu.canLevelUp()) {
            case WRONG_LEVEL -> Component.translatable("text.vampirism.basic_hunter.cannot_train_you_any_further");
            case NEED_BLOOD ->  Component.translatable("text.vampirism.basic_hunter.pay_n_vampire_blood_more", this.menu.requiredBloodBottles());
            case CAN_LEVEL_UP -> Component.translatable("text.vampirism.basic_hunter.i_will_train_you");
        };

        guiGraphics.setTooltipForNextFrame(this.font, this.font.split(component, 120), x + 45, y + 23);

//        guiGraphics.drawWordWrap(this.font, component, x + 50, y + 12, 120, -1);
    }



    @Override
    public void init() {
        super.init();

        Component name = Component.translatable("text.vampirism.level_up");
        int wid = this.font.width(name) + 10;
        int i = (this.imageWidth - wid) / 2;
        int j = (this.height - this.imageHeight) / 2;
        addRenderableWidget(buttonLevelUp = new ExtendedButton(this.leftPos + i, j + 50, wid, 20, name, (context) -> {
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.BASIC_HUNTER_LEVELUP));
            this.onClose();
        }));
        buttonLevelUp.active = false;
    }



    @Override
    protected void containerTick() {
        buttonLevelUp.active = this.menu.canLevelUp() == HunterBasicMenu.LevelingState.CAN_LEVEL_UP;
        super.containerTick();
        this.bloodIcon.tick(List.of(PURE_BLOOD_BOTTLE_SLOT_SPRITE));
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        super.renderBg(guiGraphics, partialTicks, x, y);
        this.bloodIcon.render(this.menu, guiGraphics, partialTicks, this.leftPos, this.topPos);
    }
}
