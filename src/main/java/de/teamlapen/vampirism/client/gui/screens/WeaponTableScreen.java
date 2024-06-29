package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.util.MultilineTooltip;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.inventory.WeaponTableMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
public class WeaponTableScreen extends AbstractContainerScreen<WeaponTableMenu> {

    public static final ResourceLocation BACKGROUND = VResourceLocation.mod("textures/gui/container/weapon_table.png");
    private static final ResourceLocation LAVA_SPRITE = VResourceLocation.mod("container/weapon_table/lava");
    private static final ResourceLocation EMPTY_BUCKET_SPRITE = VResourceLocation.mod("container/weapon_table/empty_bucket");
    private static final ResourceLocation MISSING_LAVA_SPRITE = VResourceLocation.mod("container/weapon_table/missing_lava");
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/error");

    public WeaponTableScreen(@NotNull WeaponTableMenu inventorySlotsIn, @NotNull Inventory inventoryPlayer, @NotNull Component name) {
        super(inventorySlotsIn, inventoryPlayer, name);
        this.imageWidth = 196;
        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        graphics.blitSprite(EMPTY_BUCKET_SPRITE, i + 154, j + 71, 24, 28);
        if (menu.hasLava()) {
            graphics.blitSprite(LAVA_SPRITE, i + 154, j + 71, 24, 28);
        }
        if (menu.isMissingLava()) {
            graphics.blitSprite(MISSING_LAVA_SPRITE, i + 152, j + 69, 28, 32);
        }

        List<Holder<ISkill<?>>> missingSkills = this.menu.missingSkills().orElse(List.of());
        if (!missingSkills.isEmpty()) {
            graphics.blitSprite(ERROR_SPRITE, i + 110, j + 43, 28, 21);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        List<Holder<ISkill<?>>> missingSkills = this.menu.missingSkills().orElse(List.of());
        if (pX > i + 110 && pX < i + 110 + 28 && pY > j + 43 && pY < j + 43 + 21 && !missingSkills.isEmpty()) {
            List<Component> components = Stream.concat(Stream.of(Component.translatable("gui.vampirism.weapon_table.missing_skills").withStyle(ChatFormatting.RED)), missingSkills.stream().map(skill -> Component.literal("- ").append(skill.value().getName()).withStyle(ChatFormatting.RED))).collect(Collectors.toUnmodifiableList());
            setTooltipForNextRenderPass(new MultilineTooltip(components), new BelowOrAboveWidgetTooltipPositioner(new ScreenRectangle(i + 110, j + 43, 28, 21)), false);
        } else {
            super.renderTooltip(pGuiGraphics, pX, pY);
        }
    }
}
