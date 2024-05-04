package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.inventory.AltarInfusionMenu;
import de.teamlapen.vampirism.items.PureBloodItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AltarInfusionScreen extends ItemCombinerScreen<AltarInfusionMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/altar_of_infusion.png");
    private static final ResourceLocation EMPTY_PURE_BLOOD = new ResourceLocation(REFERENCE.MODID, "item/empty_pure_blood");
    private static final ResourceLocation EMPTY_HUMAN_HEART = new ResourceLocation(REFERENCE.MODID, "item/empty_human_heart");
    private static final ResourceLocation EMPTY_VAMPIRE_BOOK = new ResourceLocation(REFERENCE.MODID, "item/empty_vampire_book");

    private final CyclingSlotBackground pureBloodIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground humanHeartIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground vampireBookIcon = new CyclingSlotBackground(2);

    public AltarInfusionScreen(@NotNull AltarInfusionMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name) {
        super(inventorySlotsIn, playerInventory, name, BACKGROUND);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var requirement = this.menu.getRequirement();
        this.pureBloodIcon.tick(requirement.filter(s -> s.pureBloodQuantity() > 0).map(s -> List.of(EMPTY_PURE_BLOOD)).orElse(List.of()));
        this.humanHeartIcon.tick(requirement.filter(s -> s.humanHeartQuantity() > 0).map(s -> List.of(EMPTY_HUMAN_HEART)).orElse(List.of()));
        this.vampireBookIcon.tick(requirement.filter(s -> s.vampireBookQuantity() > 0).map(s -> List.of(EMPTY_VAMPIRE_BOOK)).orElse(List.of()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        this.renderOnBoardingTooltips(graphics, pMouseX, pMouseY);
    }

    private void renderOnBoardingTooltips(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.index < 3) {
            Optional<Component> optional = Optional.empty();
            var req = this.menu.getRequirement();
            ItemStack stack = this.hoveredSlot.getItem();
            var missing = req.map(s -> switch (this.hoveredSlot.index) {
                case 0 -> s.pureBloodQuantity() - stack.getCount();
                case 1 -> s.humanHeartQuantity() - stack.getCount();
                case 2 -> s.vampireBookQuantity() - stack.getCount();
                default -> 0;
            }).orElse(0);
            if (missing > 0) {
                optional = Optional.of(Component.translatable("text.vampirism.altar_infusion.ritual_missing_items", missing, (switch (this.hoveredSlot.index) {
                    case 0 -> req.map(VampireLeveling.AltarInfusionRequirements::pureBloodLevel).map(PureBloodItem::getBloodItemForLevel).map(PureBloodItem::getCustomName).orElseGet(Component::empty);
                    case 1 -> ModItems.HUMAN_HEART.get().getDefaultInstance().getHoverName();
                    case 2 -> ModItems.VAMPIRE_BOOK.get().getDefaultInstance().getHoverName();
                    default -> null;
                })));
            }
            optional.ifPresent(component -> graphics.renderTooltip(this.font, component, pMouseX, pMouseY));
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float pPartialTick, int pX, int pY) {
        super.renderBg(graphics, pPartialTick, pX, pY);
        this.pureBloodIcon.render(this.menu, graphics, pPartialTick, this.leftPos, this.topPos);
        this.humanHeartIcon.render(this.menu, graphics, pPartialTick, this.leftPos, this.topPos);
        this.vampireBookIcon.render(this.menu, graphics, pPartialTick, this.leftPos, this.topPos);
    }

    @Override
    protected void renderErrorIcon(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Optional<Component> component = Optional.empty();
        var requirement = this.menu.getRequirement();
        if (requirement.isEmpty()) {
            component = Optional.of(Component.translatable("text.vampirism.altar_infusion.ritual_level_wrong"));
        }
        component.ifPresent(c -> graphics.renderTooltip(this.font, this.font.split(c, 115), this.leftPos + 10, this.topPos + 60));
    }
}