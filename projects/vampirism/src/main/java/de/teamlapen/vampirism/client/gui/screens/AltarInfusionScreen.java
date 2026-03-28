package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.inventory.AltarInfusionMenu;
import de.teamlapen.vampirism.common.world.items.PureBloodItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AltarInfusionScreen extends AbstractContainerScreen<AltarInfusionMenu> {

    private static final Identifier EMPTY_SLOT_PURE_BLOOD_BOTTLE = VIdentifier.mod("container/slot/pure_blood_bottle");
    private static final Identifier EMPTY_SLOT_HUMAN_HEART = VIdentifier.mod("container/slot/human_heart");
    private static final Identifier EMPTY_SLOT_VAMPIRE_BOOK = VIdentifier.mod("container/slot/vampire_book");
    private static final Identifier BACKGROUND_LOCATION = VIdentifier.mod("textures/gui/container/altar_of_infusion.png");

    private final CyclingSlotBackground pureBloodIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground humanHeartIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground vampireBookIcon = new CyclingSlotBackground(2);

    public AltarInfusionScreen(AltarInfusionMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var requirementsOpt = this.menu.getRequirements();
        this.pureBloodIcon.tick(requirementsOpt.filter(requirements -> requirements.pureBloodQuantity() > 0).map(requirements -> List.of(EMPTY_SLOT_PURE_BLOOD_BOTTLE)).orElse(List.of()));
        this.humanHeartIcon.tick(requirementsOpt.filter(requirements -> requirements.humanHeartQuantity() > 0).map(requirements -> List.of(EMPTY_SLOT_HUMAN_HEART)).orElse(List.of()));
        this.vampireBookIcon.tick(requirementsOpt.filter(requirements -> requirements.vampireBookQuantity() > 0).map(requirements -> List.of(EMPTY_SLOT_VAMPIRE_BOOK)).orElse(List.of()));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        this.pureBloodIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
        this.humanHeartIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
        this.vampireBookIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null && hoveredSlot.index >= 0 && this.hoveredSlot.index < 3) {
            var requirementsOpt = this.menu.getRequirements();
            if (requirementsOpt.isPresent()) {
                var requirements = requirementsOpt.get();
                int slot = this.hoveredSlot.index;
                ItemStack stack = this.hoveredSlot.getItem();

                int requiredCount = 0;
                String requiredId = "";

                switch (slot) {
                    case 0 -> {
                        requiredCount = requirements.pureBloodQuantity();
                        requiredId = "pure_blood";
                    }
                    case 1 -> {
                        requiredCount = requirements.humanHeartQuantity();
                        requiredId = "human_heart";
                    }
                    case 2 -> {
                        requiredCount = requirements.vampireBookQuantity();
                        requiredId = "vampire_book";
                    }
                }

                Component tooltip = null;

                if (slot == 0 && !stack.isEmpty() && stack.getItem() instanceof PureBloodItem pureBloodItem && pureBloodItem.getLevel(stack) != requirements.pureBloodLevel()) {
                    tooltip = Component.translatable("gui.vampirism.altar_infusion.ritual_wrong_purity", requirements.pureBloodLevel() + 1);
                } else if (stack.isEmpty() || stack.getCount() < requiredCount) {
                    tooltip = Component.translatable("gui.vampirism.altar_infusion.ritual_missing_" + requiredId, requiredCount - stack.getCount(), requirements.pureBloodLevel() + 1);
                }

                if (tooltip != null && requiredCount> 0) {
                    ClientTooltipComponent clientTooltip = ClientTooltipComponent.create(tooltip.getVisualOrderText());
                    graphics.renderTooltip(font, List.of(clientTooltip), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, stack.get(DataComponents.TOOLTIP_STYLE));
                    return;
                }
            }
        }

        super.renderTooltip(graphics, mouseX, mouseY);
    }
}