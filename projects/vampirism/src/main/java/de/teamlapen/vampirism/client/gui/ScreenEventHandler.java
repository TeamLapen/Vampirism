package de.teamlapen.vampirism.client.gui;

import de.teamlapen.faction.client.gui.screens.FactionMenuScreen;
import de.teamlapen.faction.common.util.DescriptionUtil;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.world.blocks.TentBlock;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;

public class ScreenEventHandler {

    private static final Identifier PASSIVE_LEVELING_BACKGROUND = VIdentifier.mod("container/faction_screen/passive_leveling_background");
    private static final Identifier PASSIVE_LEVELING_BAR = VIdentifier.mod("container/faction_screen/passive_leveling_bar");
    private static final int LEVELING_BACKGROUND_WIDTH = 15;
    private static final int LEVELING_BACKGROUND_HEIGHT = 63;
    private static final int BAR_WIDTH = 5;
    private static final int BAR_HEIGHT = 51;

    @SubscribeEvent
    public void onRenderFactionMenu(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof FactionMenuScreen screen)) return;

        Player player = screen.getMinecraft().player;
        if (player == null || !Helper.isVampire(player)) return;

        VampirePlayer vampire = VampirePlayer.get(player);
        if (!VampireLeveling.canLevelPassively(vampire.getLevel())) return;

        int requiredBlood = VampireLeveling.getPassiveLevelingBlood(vampire.getLevel() + 1);
        float progress = requiredBlood <= 0 ? 0f : Math.clamp(vampire.getPassiveLevelBlood() / (float) requiredBlood, 0f, 1f);

        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        int backgroundX = screen.getLeftPos() - LEVELING_BACKGROUND_WIDTH;
        int backgroundY = screen.getTopPos();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PASSIVE_LEVELING_BACKGROUND, backgroundX, backgroundY, LEVELING_BACKGROUND_WIDTH, LEVELING_BACKGROUND_HEIGHT);

        int filledHeight = (int) (BAR_HEIGHT * progress);
        if (filledHeight > 0) {
            int barX = backgroundX + (LEVELING_BACKGROUND_WIDTH - BAR_WIDTH) / 2;
            int barY = backgroundY + (LEVELING_BACKGROUND_HEIGHT - BAR_HEIGHT) / 2;
            int vOffset = BAR_HEIGHT - filledHeight;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PASSIVE_LEVELING_BAR, BAR_WIDTH, BAR_HEIGHT, 0, vOffset, barX, barY + vOffset, BAR_WIDTH, filledHeight);
        }

        int mouseX = event.getMouseX();
        int mouseY = event.getMouseY();
        if (mouseX >= backgroundX && mouseX <= backgroundX + LEVELING_BACKGROUND_WIDTH && mouseY >= backgroundY && mouseY <= backgroundY + LEVELING_BACKGROUND_HEIGHT) {
            String rawText = I18n.get("gui.vampirism.faction_menu.passive_leveling.desc", (int) (progress * 100));
            List<ClientTooltipComponent> lines = DescriptionUtil.normalizeTextWidth(rawText).stream().map(line -> ClientTooltipComponent.create(FormattedCharSequence.forward(line, Style.EMPTY.withColor(ChatFormatting.GRAY)))).toList();
            graphics.tooltip(screen.font, lines, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }

    /**
     * Adds a button to the inventory screen that allows opening the skill menu from there
     */
    @SubscribeEvent
    public void onInitGuiEventPost(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InBedChatScreen) {
            Player p = event.getScreen().getMinecraft().player;
            if (p != null && p.isSleeping()) {
                GuiEventListener l = event.getScreen().children().get(1);
                if (l instanceof AbstractWidget leaveButton) {
                    p.getSleepingPos().map(pos -> p.level().getBlockState(pos).getBlock()).map(block -> block instanceof TentBlock ? "gui.vampirism.tent.stop_sleeping" : (block instanceof CoffinBlock ? "gui.vampirism.coffin.stop_sleeping" : null)).ifPresent(newText ->
                        leaveButton.setMessage(Component.translatable(newText))
                    );
                }
            }
        }
    }

}
