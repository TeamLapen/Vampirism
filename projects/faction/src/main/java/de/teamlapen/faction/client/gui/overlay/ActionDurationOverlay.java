package de.teamlapen.faction.client.gui.overlay;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.tags.FactionActionTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;

import java.util.Optional;

public class ActionDurationOverlay<T extends ISkillPlayer<T>> extends BaseOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    protected boolean isEnabledInConfig() {
        return FactionConfig.client().showActionDurationOverlay.get();
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays()) {
            FactionsApi.factionPlayerHandler(this.player()).<T>getCurrentSkillPlayer().ifPresent(factionPlayer -> {
                IActionHandler<T> actionHandler = factionPlayer.getActionHandler();

                int x = 12;
                int y = this.mc.getWindow().getGuiScaledHeight() - 27;

                for (Holder<? extends ILastingAction<T>> action : factionPlayer.getActionHandler().getActiveActions()) {
                    //noinspection rawtypes,unchecked
                    if (!(action.is((TagKey)FactionActionTags.SHOW_DURATION_IN_HUD))) continue;
                    if (!actionHandler.isActionActive(action)) continue;
                    Optional<Identifier> texture = action.unwrapKey().map(ResourceKey::identifier).map(key -> key.withPath("textures/actions/" + key.getPath() + ".png"));
                    if (texture.isPresent()) {
                        Identifier loc = texture.get();
                        int perc = (int) (((1 - actionHandler.getDurationPercentage(action)) * 16));
                        //render gray transparent background for remaining duration
                        graphics.fillGradient(x, y + perc, x + 16, y + 16, 0x44888888/*Color.GRAY - 0xBB000000 */, 0x44888888/*Color.GRAY - 0xBB000000 */);
                        //render action icon transparent
                        GuiRenderer.blitColored(graphics, texture.get(), x, y, 16, 16, 16, 16, ARGB.colorFromFloat(0.5f, 1, 1, 1));
                        x += 17;
                    }
                }
            });
        }
    }
}
