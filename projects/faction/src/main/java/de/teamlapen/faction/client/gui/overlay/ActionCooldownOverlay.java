package de.teamlapen.faction.client.gui.overlay;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.common.config.FactionConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;

import java.util.Optional;

public class ActionCooldownOverlay<T extends ISkillPlayer<T>> extends BaseOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    protected boolean isEnabledInConfig() {
        return FactionConfig.client().renderActionCooldownOverlay.get();
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays()) {
            FactionsApi.factionPlayerHandler(this.player()).<T>getCurrentSkillPlayer().ifPresent(factionPlayer -> {
                IActionHandler<T> actionHandler = factionPlayer.getActionHandler();

                int y = this.mc.getWindow().getGuiScaledHeight() - 27;
                int x = this.mc.getWindow().getGuiScaledWidth() - 12 - 16;

                for (Holder<? extends IAction<T>> action : actionHandler.getUnlockedActionHolder()) {
                    if (!(action.value().showHudCooldown(this.player()))) continue;
                    if (!actionHandler.isActionOnCooldown(action)) continue;
                    Optional<Identifier> texture = action.unwrapKey().map(ResourceKey::identifier).map(key -> key.withPath("textures/actions/" + key.getPath() + ".png"));
                    if (texture.isPresent()) {
                        int perc = (int) ((1 + actionHandler.getCooldownPercentage(action)) * 16);
                        //render gray transparent background for remaining cooldown
                        graphics.fillGradient(x, y + perc, x + 16, y + 16, 0x44888888/*Color.GRAY - 0xBB000000 */, 0x44888888/*Color.GRAY - 0xBB000000 */);
                        //render action icon transparent
                        GuiRenderer.blitColored(graphics, texture.get(), x, y, 16, 16, 16, 16, ARGB.colorFromFloat(0.5f, 1, 1, 1));
                        x -= 17;
                    }
                }
            });
        }
    }
}
