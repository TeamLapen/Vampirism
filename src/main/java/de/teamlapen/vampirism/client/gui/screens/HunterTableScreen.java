package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.inventory.HunterTableMenu;
import de.teamlapen.vampirism.items.PureBloodItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Gui for the hunter table
 */
public class HunterTableScreen extends ItemCombinerScreen<HunterTableMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_table.png");
    private static final ResourceLocation EMPTY_BOOK = new ResourceLocation(REFERENCE.MODID, "item/empty_book");
    private static final ResourceLocation EMPTY_FANG = new ResourceLocation(REFERENCE.MODID, "item/empty_vampire_fang");
    private static final ResourceLocation EMPTY_PURE_BLOOD = new ResourceLocation(REFERENCE.MODID, "item/empty_pure_blood");
    private static final ResourceLocation EMPTY_VAMPIRE_BOOK = new ResourceLocation(REFERENCE.MODID, "item/empty_vampire_book");

    private final CyclingSlotBackground bookIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground fangsIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground bloodIcon = new CyclingSlotBackground(2);
    private final CyclingSlotBackground vampireBookIcon = new CyclingSlotBackground(3);

    public HunterTableScreen(@NotNull HunterTableMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name) {
        super(inventorySlotsIn, playerInventory, name, BACKGROUND);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var requirement = this.menu.getTableRequirement();
        this.bookIcon.tick(requirement.filter(s -> s.bookQuantity() > 0).map(s -> List.of(EMPTY_BOOK)).orElse(List.of()));
        this.fangsIcon.tick(requirement.filter(s -> s.vampireFangQuantity() > 0).map(s -> List.of(EMPTY_FANG)).orElse(List.of()));
        this.bloodIcon.tick(requirement.filter(s -> s.pureBloodQuantity() > 0).map(s -> List.of(EMPTY_PURE_BLOOD)).orElse(List.of()));
        this.vampireBookIcon.tick(requirement.filter(s -> s.vampireBookQuantity() > 0).map(s -> List.of(EMPTY_VAMPIRE_BOOK)).orElse(List.of()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderOnBoardingTooltips(graphics, mouseX, mouseY);
    }

    private void renderOnBoardingTooltips(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.index < 4) {
            Optional<Component> optional = Optional.empty();
            var req = this.menu.getTableRequirement();
            ItemStack stack = this.hoveredSlot.getItem();
            var missing = req.map(r -> switch (this.hoveredSlot.index) {
                case 0 -> r.bookQuantity() - stack.getCount();
                case 1 -> r.vampireFangQuantity() - stack.getCount();
                case 2 -> r.pureBloodQuantity() - stack.getCount();
                case 3 -> r.vampireBookQuantity() - stack.getCount();
                default -> 0;
            }).orElse(0);
            if (missing > 0) {
                optional = Optional.of(Component.translatable("text.vampirism.hunter_table.ritual_missing_items", missing, switch (this.hoveredSlot.index) {
                    case 0 -> Items.BOOK.getDefaultInstance().getHoverName();
                    case 1 -> ModItems.VAMPIRE_FANG.get().getDefaultInstance().getHoverName();
                    case 2 -> req.map(HunterLeveling.HunterTableRequirement::pureBloodLevel).map(PureBloodItem::getBloodItemForLevel).map(PureBloodItem::getCustomName).orElseGet(Component::empty);
                    case 3 -> ModItems.VAMPIRE_BOOK.get().getDefaultInstance().getHoverName();
                    default -> throw new IllegalStateException("Unexpected value: " + this.hoveredSlot.index);
                }));
            }
            optional.ifPresent((component) -> {
                graphics.renderTooltip(this.font, this.font.split(component, 115), pMouseX, pMouseY);
            });
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float pPartialTick, int pX, int pY) {
        super.renderBg(graphics, pPartialTick, pX, pY);
        this.bookIcon.render(this.menu, graphics, pPartialTick, this.leftPos, this.topPos);
        this.fangsIcon.render(this.menu, graphics, pPartialTick, this.leftPos, this.topPos);
        this.bloodIcon.render(this.menu, graphics, pPartialTick, this.leftPos, this.topPos);
        this.vampireBookIcon.render(this.menu, graphics, pPartialTick, this.leftPos, this.topPos);
    }

    @Override
    protected void renderErrorIcon(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Optional<Component> component = Optional.empty();
        Optional<HunterLeveling.HunterTableRequirement> requirement = this.menu.getRequirement();
        if (requirement.isEmpty()) {
            component = Optional.of(Component.translatable("container.vampirism.hunter_table.level_wrong"));
        } else if (requirement.filter(this.menu::doesTableFulfillRequirement).isEmpty()) {
            component = Optional.of(Component.translatable("container.vampirism.hunter_table.structure_level_wrong"));
        }
        component.ifPresent((c) -> {
            graphics.renderTooltip(this.font, this.font.split(c, 115), this.leftPos + 10, this.topPos + 60);
        });
    }

    //    @Override
//    protected void renderLabels(@NotNull PoseStack stack, int mouseX, int mouseY) {
//        super.renderLabels(stack, mouseX, mouseY);
//
//        Component text = null;
//        if (!menu.isLevelValid(false)) {
//            text = Component.translatable("container.vampirism.hunter_table.level_wrong");
//        } else if (!menu.isLevelValid(true)) {
//            text = Component.translatable("container.vampirism.hunter_table.structure_level_wrong");
//        } else if (!menu.getMissingItems().isEmpty()) {
//            ItemStack missing = menu.getMissingItems();
//            Component item = missing.getItem() instanceof PureBloodItem ? ((PureBloodItem) missing.getItem()).getCustomName() : Component.translatable(missing.getDescriptionId());
//            text = Component.translatable("text.vampirism.hunter_table.ritual_missing_items", missing.getCount(), item);
//        }
//        if (text != null) this.font.drawWordWrap(stack, text, 8, 50, this.imageWidth - 10, 0x000000);
//    }
}
