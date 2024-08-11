package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ActionCooldownOverlay implements LayeredDraw.Layer {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics graphics, DeltaTracker partialTicks) {
        if (this.mc.player != null && VampirismConfig.CLIENT.enableHudActionCooldownRendering.get()) {
            VampirismAPI.factionPlayerHandler(this.mc.player).getCurrentFactionPlayer().ifPresent(factionPlayer -> {
                IActionHandler<?> actionHandler = factionPlayer.getActionHandler();

                int y = this.mc.getWindow().getGuiScaledHeight() - 27;
                int x = this.mc.getWindow().getGuiScaledWidth() - 12 - 16;

                //noinspection rawtypes
                for (IAction action : factionPlayer.getActionHandler().getUnlockedActions()) {
                    if (!(action.showHudCooldown(this.mc.player))) continue;
                    // noinspection unchecked
                    if (!actionHandler.isActionOnCooldown(action)) continue;
                    ResourceLocation id = RegUtil.id(action);
                    ResourceLocation loc = id.withPath("textures/actions/" + id.getPath() + ".png");
                    //noinspection unchecked
                    int perc = (int) ((1 + actionHandler.getPercentageForAction(action)) * 16);
                    //render gray transparent background for remaining cooldown
                    graphics.fillGradient(x, y + perc, x + 16, y + 16, 0x44888888/*Color.GRAY - 0xBB000000 */, 0x44888888/*Color.GRAY - 0xBB000000 */);
                    //render action icon transparent
                    graphics.setColor(1, 1, 1, 0.5f);
                    graphics.blit(loc, x, y, 0, 0, 0, 16, 16, 16, 16);
                    graphics.setColor(1, 1, 1, 1);
                    x -= 17;
                }
            });
        }
    }
}
