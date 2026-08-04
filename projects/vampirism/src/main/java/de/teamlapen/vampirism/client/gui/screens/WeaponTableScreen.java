package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.inventory.WeaponTableMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
public class WeaponTableScreen extends AbstractContainerScreen<WeaponTableMenu> {

    public static final Identifier BACKGROUND = VIdentifier.mod("textures/gui/container/weapon_table.png");
    private static final Identifier LAVA_SPRITE = VIdentifier.mod("container/weapon_table/lava");
    private static final Identifier EMPTY_BUCKET_SPRITE = VIdentifier.mod("container/weapon_table/empty_bucket");
    private static final Identifier MISSING_LAVA_SPRITE = VIdentifier.mod("container/weapon_table/missing_lava");
    private static final Identifier ERROR_SPRITE = VIdentifier.mc("container/anvil/error");

    public WeaponTableScreen(@NotNull WeaponTableMenu inventorySlotsIn, @NotNull Inventory inventoryPlayer, @NotNull Component name) {
        super(inventorySlotsIn, inventoryPlayer, name, 196, 192);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        GuiRenderer.blit(graphics, BACKGROUND, i, j, this.imageWidth, this.imageHeight);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EMPTY_BUCKET_SPRITE, i + 154, j + 71, 24, 28);
        if (menu.hasLava()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LAVA_SPRITE, i + 154, j + 71, 24, 28);
        }
        if (menu.isMissingLava()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, MISSING_LAVA_SPRITE, i + 152, j + 69, 28, 32);
        }

        List<Holder<? extends ISkill<?>>> missingSkills = this.menu.missingSkills().orElse(List.of());
        if (!missingSkills.isEmpty()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, i + 110, j + 43, 28, 21);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        List<Holder<? extends ISkill<?>>> missingSkills = this.menu.missingSkills().orElse(List.of());
        if (mouseX > i + 110 && mouseX < i + 110 + 28 && mouseY > j + 43 && mouseY < j + 43 + 21 && !missingSkills.isEmpty()) {
            List<Component> components = Stream.concat(Stream.of(Component.translatable("gui.vampirism.weapon_table.missing_skills").withStyle(ChatFormatting.RED)), missingSkills.stream().map(skill -> Component.literal("- ").append(skill.value().getName()).withStyle(ChatFormatting.RED))).collect(Collectors.toUnmodifiableList());
            graphics.setComponentTooltipForNextFrame(this.font, components, i + 110, j + 43);
        } else {
            super.extractTooltip(graphics, mouseX, mouseY);
        }
    }
}
