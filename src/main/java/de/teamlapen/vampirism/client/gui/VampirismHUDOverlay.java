package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.FactionRegistry;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.IVampirePlayer;
import de.teamlapen.vampirism.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Handles general Overlay thingies
 */
public class VampirismHUDOverlay extends Gui {

    private final CachedPlayer cachedPlayer = new CachedPlayer();
    private final Minecraft mc;

    public VampirismHUDOverlay(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        if (cachedPlayer.time % 10 == 1) {
            cachedPlayer.faction = FactionRegistry.getActiveFaction(this.mc.thePlayer);
            if (cachedPlayer.faction == null) {
                cachedPlayer.level = 0;
                cachedPlayer.alt = null;
            } else {
                IFactionPlayer p = cachedPlayer.faction.getProp(this.mc.thePlayer);
                cachedPlayer.level = p.getLevel();
                if (cachedPlayer.faction == VampirismAPI.VAMPIRE_FACTION) {
                    cachedPlayer.alt = ((IVampirePlayer) p).isVampireLord() ? "Lord" : null;
                }
            }
            cachedPlayer.time = 0;
        } else {
            cachedPlayer.time++;
        }
    }

    /**
     * Render Level
     */
    @SubscribeEvent
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        if (mc.playerController.gameIsSurvivalOrAdventure() && cachedPlayer.faction != null) {
            mc.mcProfiler.startSection("factionLevel");
            // boolean flag1 = false;
            int color = Color.MAGENTA.getRGB();
            String text = cachedPlayer.alt == null ? "" + cachedPlayer.level : cachedPlayer.alt;
            int x = (event.resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(text)) / 2 + Configs.gui_level_offset_x;
            int y = event.resolution.getScaledHeight() - Configs.gui_level_offset_y;
            mc.fontRendererObj.drawString(text, x + 1, y, 0);
            mc.fontRendererObj.drawString(text, x - 1, y, 0);
            mc.fontRendererObj.drawString(text, x, y + 1, 0);
            mc.fontRendererObj.drawString(text, x, y - 1, 0);
            mc.fontRendererObj.drawString(text, x, y, color);
            mc.mcProfiler.endSection();
        }
    }

    private class CachedPlayer {
        public int level = 0;
        @Nullable
        public PlayableFaction faction;
        public String alt = null;
        public int time = 0;
    }
}
