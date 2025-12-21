package de.teamlapen.factions.client.gui.overlay;

import de.teamlapen.factions.api.FactionsApi;
import de.teamlapen.factions.api.factions.actions.IActionHandler;
import de.teamlapen.factions.api.factions.actions.ILastingAction;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.common.config.FactionConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;

import java.util.Optional;

public class ActionDurationOverlay<T extends ISkillPlayer<T>> extends BaseOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays() && !FactionConfig.CLIENT.disableHudActionDurationRendering.get()) {
            FactionsApi.factionPlayerHandler(this.player()).<T>getCurrentSkillPlayer().ifPresent(factionPlayer -> {
                IActionHandler<T> actionHandler = factionPlayer.getActionHandler();

                int x = 12;
                int y = this.mc.getWindow().getGuiScaledHeight() - 27;

                if (!FactionConfig.CLIENT.disableHudActionDurationRendering.get()) {
                    for (Holder<? extends ILastingAction<T>> action : factionPlayer.getActionHandler().getActiveActions()) {
                        if (!(action.value().showHudDuration(this.player()))) continue;
                        if (!actionHandler.isActionActive(action)) continue;
                        Optional<Identifier> texture = action.unwrapKey().map(ResourceKey::identifier).map(key -> key.withPath("textures/actions/" + key.getPath() + ".png"));
                        if (texture.isPresent()) {
                            Identifier loc = texture.get();
                            int perc = (int) (((1 - actionHandler.getDurationPercentage(action)) * 16));
                            //render gray transparent background for remaining duration
                            graphics.fillGradient(x, y + perc, x + 16, y + 16, 0x44888888/*Color.GRAY - 0xBB000000 */, 0x44888888/*Color.GRAY - 0xBB000000 */);
                            //render action icon transparent
                            GuiRenderer.blitColored(graphics, texture.get(), x, y,16,16, 16, 16, ARGB.colorFromFloat(0.5f,1,1,1));
                            x += 17;
                        }
                    }
                }
            });
        }
    }
}
