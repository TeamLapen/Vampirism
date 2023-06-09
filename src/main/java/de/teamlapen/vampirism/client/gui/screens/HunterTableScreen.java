package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.HunterTableMenu;
import de.teamlapen.vampirism.items.PureBloodItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Gui for the hunter table
 */
@OnlyIn(Dist.CLIENT)
public class HunterTableScreen extends AbstractContainerScreen<HunterTableMenu> {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_table.png");
    @SuppressWarnings("FieldCanBeLocal")
    private final ContainerLevelAccess worldPos;

    public HunterTableScreen(@NotNull HunterTableMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name) {
        this(inventorySlotsIn, playerInventory, name, ContainerLevelAccess.NULL);
    }

    public HunterTableScreen(@NotNull HunterTableMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name, ContainerLevelAccess worldPosIn) {
        super(inventorySlotsIn, playerInventory, name);
        this.worldPos = worldPosIn;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float var1, int var2, int var3) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(altarGuiTextures, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        Component text = null;
        if (!menu.isLevelValid(false)) {
            text = Component.translatable("container.vampirism.hunter_table.level_wrong");
        } else if (!menu.isLevelValid(true)) {
            text = Component.translatable("container.vampirism.hunter_table.structure_level_wrong");
        } else if (!menu.getMissingItems().isEmpty()) {
            ItemStack missing = menu.getMissingItems();
            Component item = missing.getItem() instanceof PureBloodItem ? ((PureBloodItem) missing.getItem()).getCustomName() : Component.translatable(missing.getDescriptionId());
            text = Component.translatable("text.vampirism.hunter_table.ritual_missing_items", missing.getCount(), item);
        }
        if (text != null){
            graphics.drawWordWrap(this.font, text, 8, 50, this.imageWidth - 10, 0x000000);
        }
    }
}
