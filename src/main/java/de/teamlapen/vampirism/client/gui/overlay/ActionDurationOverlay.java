package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;

public class ActionDurationOverlay implements IGuiOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(ForgeGui gui, @NotNull GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        if (VampirismAPI.factionRegistry().getFaction(this.mc.player) != null) {
            VampirismAPI.getFactionPlayerHandler(this.mc.player).ifPresent(playerHandler -> {
                playerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> {
                    IActionHandler<?> actionHandler = factionPlayer.getActionHandler();

                    int x = 12;
                    int y = this.mc.getWindow().getGuiScaledHeight() - 27;

                    if (!VampirismConfig.CLIENT.disableHudActionDurationRendering.get()) {
                        //noinspection rawtypes
                        for (IAction action : factionPlayer.getActionHandler().getUnlockedActions()) {
                            if (!(action instanceof ILastingAction)) continue;
                            if (!(((ILastingAction<?>) action).showHudDuration(this.mc.player))) continue;
                            //noinspection unchecked,rawtypes
                            if (!actionHandler.isActionActive(((ILastingAction) action))) continue;
                            ResourceLocation id = RegUtil.id(action);
                            ResourceLocation loc = new ResourceLocation(id.getNamespace(), "textures/actions/" + id.getPath() + ".png");
                            //noinspection unchecked
                            int perc = (int) ((1 - actionHandler.getPercentageForAction(action)) * 16);
                            //render gray transparent background for remaining duration
                            graphics.fillGradient(x, y + perc, x + 16, y + 16, 0x44888888/*Color.GRAY - 0xBB000000 */, 0x44888888/*Color.GRAY - 0xBB000000 */);
                            //render action icon transparent
                            RenderSystem.enableBlend();
                            graphics.setColor(1, 1, 1, 0.5f);
                            graphics.blit(loc, x, y, 0, 0, 0, 16, 16, 16, 16);
                            graphics.setColor(1, 1, 1, 1f);
                            x += 17;
                        }
                    }
                });
            });
        }
    }
}
