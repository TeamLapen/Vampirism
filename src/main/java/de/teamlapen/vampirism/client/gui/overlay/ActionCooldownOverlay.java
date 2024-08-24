package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionCooldownOverlay<T extends ISkillPlayer<T>> implements LayeredDraw.Layer {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics graphics, DeltaTracker partialTicks) {
        if (this.mc.player != null && !VampirismConfig.CLIENT.disableHudActionCooldownRendering.get()) {
            VampirismAPI.factionPlayerHandler(this.mc.player).<T>getCurrentSkillPlayer().ifPresent(factionPlayer -> {
                IActionHandler<T> actionHandler = factionPlayer.getActionHandler();

                int y = this.mc.getWindow().getGuiScaledHeight() - 27;
                int x = this.mc.getWindow().getGuiScaledWidth() - 12 - 16;

                if (!VampirismConfig.CLIENT.disableHudActionCooldownRendering.get()) {
                    for (Holder<? extends IAction<T>> action : actionHandler.getUnlockedActionHolder()) {
                        if (!(action.value().showHudCooldown(this.mc.player))) continue;
                        if (!actionHandler.isActionOnCooldown(action)) continue;
                        Optional<ResourceLocation> texture = action.unwrapKey().map(ResourceKey::location).map(key -> key.withPath("textures/actions/" + key.getPath() + ".png"));
                        if (texture.isPresent()) {
                            int perc = (int) ((1 + actionHandler.getCooldownPercentage(action)) * 16);
                            //render gray transparent background for remaining cooldown
                            graphics.fillGradient(x, y + perc, x + 16, y + 16, 0x44888888/*Color.GRAY - 0xBB000000 */, 0x44888888/*Color.GRAY - 0xBB000000 */);
                            //render action icon transparent
                            graphics.setColor(1, 1, 1, 0.5f);
                            graphics.blit(texture.get(), x, y, 0, 0, 0, 16, 16, 16, 16);
                            graphics.setColor(1, 1, 1, 1);
                            x -= 17;
                        }
                    }
                }
            });
        }
    }
}
