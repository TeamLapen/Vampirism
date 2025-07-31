package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.client.gui.GuiRenderer;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionCooldownOverlay<T extends ISkillPlayer<T>> extends BaseOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays() && !VampirismConfig.CLIENT.disableHudActionCooldownRendering.get()) {
            VampirismAPI.factionPlayerHandler(this.player()).<T>getCurrentSkillPlayer().ifPresent(factionPlayer -> {
                IActionHandler<T> actionHandler = factionPlayer.getActionHandler();

                int y = this.mc.getWindow().getGuiScaledHeight() - 27;
                int x = this.mc.getWindow().getGuiScaledWidth() - 12 - 16;

                if (!VampirismConfig.CLIENT.disableHudActionCooldownRendering.get()) {
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
