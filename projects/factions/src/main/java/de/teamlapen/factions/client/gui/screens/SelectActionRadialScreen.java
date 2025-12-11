package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.actions.IActionHandler;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.client.config.ClientConfigHelper;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.client.gui.radialmenu.IRadialMenuSlot;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenu;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenuSlot;
import de.teamlapen.factions.client.gui.screens.radial.DualSwitchingRadialMenu;
import de.teamlapen.factions.common.core.FactionKeys;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.network.packets.server.ServerboundToggleActionPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SelectActionRadialScreen<T extends ISkillPlayer<T>> extends DualSwitchingRadialMenu<Holder<IAction<?>>> {

    private final IActionHandler<T> actionHandler;
    private final T player;

    private SelectActionRadialScreen(T player, List<Holder<IAction<?>>> actions, KeyMapping keyMapping) {
        super(getRadialMenu(actions), keyMapping, SelectMinionTaskRadialScreen::show);
        this.actionHandler = player.getActionHandler();
        this.player = player;
    }

    public static void show() {
        show(FactionKeys.ACTION);
    }

    public static <T extends ISkillPlayer<T>> void show(KeyMapping keyMapping) {
        FactionPlayerHandler.get(Minecraft.getInstance().player).getCurrentSkillPlayer().ifPresent(player -> {
            //noinspection rawtypes
            List<Holder<IAction<?>>> actions = ClientConfigHelper.getActionOrder(player.getFaction()).stream().filter(f -> ((IActionHandler) player.getActionHandler()).isActionUnlocked(f)).collect(Collectors.toList());
            if (!actions.isEmpty()) {
                Minecraft.getInstance().setScreen(new SelectActionRadialScreen<>(player, actions, keyMapping));
            } else {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.factions.no_actions"), true);
                Minecraft.getInstance().setScreen(null);
            }
        });
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    private static RadialMenu<Holder<IAction<?>>> getRadialMenu(List<Holder<IAction<?>>> actions) {
        Player player = Minecraft.getInstance().player;
        List<IRadialMenuSlot<Holder<IAction<?>>>> parts = actions.stream().filter(s -> s.value().showInSelectAction(player)).map(a -> (IRadialMenuSlot<Holder<IAction<?>>>) new RadialMenuSlot<>(a.value().getName(), a, Collections.emptyList())).toList();
        return new RadialMenu<>((i) -> {
            FactionsMod.proxy.sendToServer(ServerboundToggleActionPacket.createFromRaytrace(parts.get(i).primarySlotIcon(), Minecraft.getInstance().hitResult));
        }, parts, SelectActionRadialScreen::drawActionPart, 0);
    }

    private static void drawActionPart(Holder<IAction<?>> action, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        var texture = action.unwrapKey().map(ResourceKey::location).map(s -> s.withPath("textures/actions/" + s.getPath() + ".png")).orElseThrow();
        GuiRenderer.blit(graphics, texture, posX, posY, 16, 16, 16, 16);
    }

    @Override
    public void drawSlice(IRadialMenuSlot<Holder<IAction<?>>> slot, boolean highlighted, GuiGraphics buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        @SuppressWarnings("unchecked")
        Holder<IAction<T>> iActionHolder = (Holder<IAction<T>>) (Object) slot.primarySlotIcon();
        float actionPercentage = actionHandler.getPercentageForAction(iActionHolder);
        if (!iActionHolder.value().canUse(this.player).successful()) {
            actionPercentage = -1;
        }
        if (actionPercentage == 0) {
            super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, r, g, b, 100);
        } else if (actionPercentage > 0) {
            int color = highlighted ? 200 : 160;
            super.drawSlice(slot, true, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, color, color, 60, 100);
            super.drawSlice(slot, true, buffer, x, y, z, radiusIn, radiusIn + ((radiusOut - radiusIn) * actionPercentage), startAngle, endAngle, color, color, 60, 100);
        } else {
            int color = highlighted ? 200 : 160;
            super.drawSlice(slot, true, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, color, 60, 60, 100);
            super.drawSlice(slot, true, buffer, x, y, z, radiusIn, radiusIn + ((radiusOut - radiusIn) * -actionPercentage), startAngle, endAngle, color, 60, 60, 100);
        }
    }
}
