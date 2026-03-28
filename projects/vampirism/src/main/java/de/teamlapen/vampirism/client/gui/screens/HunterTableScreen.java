package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.common.world.inventory.HunterTableMenu;
import de.teamlapen.vampirism.common.world.items.PureBloodItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HunterTableScreen extends ItemCombinerScreen<HunterTableMenu> {

    private static final Identifier EMPTY_SLOT_BOOK = VIdentifier.mod("container/slot/book");
    private static final Identifier EMPTY_SLOT_FANG = VIdentifier.mod("container/slot/fang");
    private static final Identifier EMPTY_SLOT_PURE_BLOOD_BOTTLE = VIdentifier.mod("container/slot/pure_blood_bottle");
    private static final Identifier EMPTY_SLOT_VAMPIRE_BOOK = VIdentifier.mod("container/slot/vampire_book");
    private static final Identifier BACKGROUND_LOCATION = VIdentifier.mod("textures/gui/container/hunter_table.png");

    private final CyclingSlotBackground bookIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground fangsIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground bloodIcon = new CyclingSlotBackground(2);
    private final CyclingSlotBackground vampireBookIcon = new CyclingSlotBackground(3);

    public HunterTableScreen(HunterTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND_LOCATION);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var requirement = this.menu.getTableRequirement();
        this.bookIcon.tick(requirement.filter(r -> r.bookQuantity() > 0).map(r -> List.of(EMPTY_SLOT_BOOK)).orElse(List.of()));
        this.fangsIcon.tick(requirement.filter(r -> r.vampireFangQuantity() > 0).map(r -> List.of(EMPTY_SLOT_FANG)).orElse(List.of()));
        this.bloodIcon.tick(requirement.filter(r -> r.pureBloodQuantity() > 0).map(r -> List.of(EMPTY_SLOT_PURE_BLOOD_BOTTLE)).orElse(List.of()));
        this.vampireBookIcon.tick(requirement.filter(r -> r.vampireBookQuantity() > 0).map(r -> List.of(EMPTY_SLOT_VAMPIRE_BOOK)).orElse(List.of()));
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.index >= 0 && this.hoveredSlot.index < 4) {
            var requirementOpt = this.menu.getTableRequirement();
            if (requirementOpt.isPresent()) {
                var requirement = requirementOpt.get();
                int slot = this.hoveredSlot.index;
                ItemStack stack = this.hoveredSlot.getItem();
                Component tooltip = null;

                switch (slot) {
                    case 0 -> {
                        int missing = requirement.bookQuantity() - stack.getCount();
                        if (missing > 0) {
                            tooltip = Component.translatable("gui.vampirism.hunter_table.ritual_missing_book", missing);
                        }
                    }
                    case 1 -> {
                        int missing = requirement.vampireFangQuantity() - stack.getCount();
                        if (missing > 0) {
                            tooltip = Component.translatable("gui.vampirism.hunter_table.ritual_missing_vampire_fang", missing);
                        }
                    }
                    case 2 -> {
                        int requiredLevel = requirement.pureBloodLevel();
                        if (!stack.isEmpty() && stack.getItem() instanceof PureBloodItem pureBloodItem && pureBloodItem.getLevel(stack) != requiredLevel) {
                            tooltip = Component.translatable("gui.vampirism.hunter_table.ritual_wrong_purity", requiredLevel + 1);
                        } else {
                            int missing = requirement.pureBloodQuantity() - stack.getCount();
                            if (missing > 0) {
                                tooltip = Component.translatable("gui.vampirism.hunter_table.ritual_missing_pure_blood", missing, requiredLevel + 1);
                            }
                        }
                    }
                    case 3 -> {
                        int missing = requirement.vampireBookQuantity() - stack.getCount();
                        if (missing > 0) {
                            tooltip = Component.translatable("gui.vampirism.hunter_table.ritual_missing_vampire_book", missing);
                        }
                    }
                }

                if (tooltip != null) {
                    ClientTooltipComponent clientTooltip = ClientTooltipComponent.create(tooltip.getVisualOrderText());
                    graphics.renderTooltip(this.font, List.of(clientTooltip), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, stack.get(DataComponents.TOOLTIP_STYLE));
                    return;
                }
            }
        }

        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        this.bookIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
        this.fangsIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
        this.bloodIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
        this.vampireBookIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
    }

    @Override
    protected void renderErrorIcon(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Optional<Component> component = Optional.empty();
        Optional<HunterLeveling.HunterTableRequirement> requirement = this.menu.getRequirement();
        if (requirement.isEmpty()) {
            component = Optional.of(Component.translatable("gui.vampirism.hunter_table.level_wrong"));
        } else if (requirement.filter(this.menu::doesTableFulfillRequirement).isEmpty()) {
            component = Optional.of(Component.translatable("gui.vampirism.hunter_table.structure_level_wrong"));
        }
        component.ifPresent(c -> graphics.setTooltipForNextFrame(this.font, this.font.split(c, 115), this.leftPos + 10, this.topPos + 60));
    }
}
