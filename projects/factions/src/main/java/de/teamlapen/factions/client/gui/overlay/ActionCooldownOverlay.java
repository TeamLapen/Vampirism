package de.teamlapen.factions.client.gui.overlay;

import de.teamlapen.factions.api.FactionApi;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.actions.IActionHandler;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.common.config.ModConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionCooldownOverlay<T extends ISkillPlayer<T>> extends BaseOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays() && !ModConfig.CLIENT.disableHudActionCooldownRendering.get()) {
            FactionApi.factionPlayerHandler(this.player()).<T>getCurrentSkillPlayer().ifPresent(factionPlayer -> {
                IActionHandler<T> actionHandler = factionPlayer.getActionHandler();

                int y = this.mc.getWindow().getGuiScaledHeight() - 27;
                int x = this.mc.getWindow().getGuiScaledWidth() - 12 - 16;

                if (!ModConfig.CLIENT.disableHudActionCooldownRendering.get()) {
                    for (Holder<? extends IAction<T>> action : actionHandler.getUnlockedActionHolder()) {
                        if (!(action.value().showHudCooldown(this.player()))) continue;
                        if (!actionHandler.isActionOnCooldown(action)) continue;
                        Optional<ResourceLocation> texture = action.unwrapKey().map(ResourceKey::location).map(key -> key.withPath("textures/actions/" + key.getPath() + ".png"));
                        if (texture.isPresent()) {
                            int perc = (int) ((1 + actionHandler.getCooldownPercentage(action)) * 16);
                            //render gray transparent background for remaining cooldown
                            graphics.fillGradient(x, y + perc, x + 16, y + 16, 0x44888888/*Color.GRAY - 0xBB000000 */, 0x44888888/*Color.GRAY - 0xBB000000 */);
                            //render action icon transparent
                            GuiRenderer.blitColored(graphics, texture.get(), x, y,16,16, 16, 16, ARGB.colorFromFloat(0.5f,1,1,1));
                            x -= 17;
                        }
                    }
                }
            });
        }
    }
}
