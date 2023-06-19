package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.client.gui.screens.radialmenu.IRadialMenuSlot;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenuSlot;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.client.ClientConfigHelper;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.screens.radial.DualSwitchingRadialMenu;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.network.ServerboundToggleActionPacket;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SelectActionRadialScreen<T extends IFactionPlayer<T>> extends DualSwitchingRadialMenu<IAction<?>> {

    private final IActionHandler<?> actionHandler;
    private final T player;

    private SelectActionRadialScreen(T player, List<IAction<?>> actions, KeyMapping keyMapping) {
        super(getRadialMenu(actions), keyMapping, SelectMinionTaskRadialScreen::show);
        this.actionHandler = player.getActionHandler();
        this.player = player;
    }

    public static void show() {
        show(ModKeys.ACTION);
    }

    public static void show(KeyMapping keyMapping) {
        IPlayableFaction<?> faction = VampirismPlayerAttributes.get(Minecraft.getInstance().player).faction;
        if (faction != null) {
            faction.getPlayerCapability(Minecraft.getInstance().player).ifPresent(player -> {
                List<IAction<?>> actions = ClientConfigHelper.getActionOrder(player.getFaction()).stream().filter(f -> ((IActionHandler)player.getActionHandler()).isActionUnlocked(f)).collect(Collectors.toList());
                if (!actions.isEmpty()) {
                    //noinspection rawtypes
                    Minecraft.getInstance().setScreen(new SelectActionRadialScreen(player, actions, keyMapping));
                } else {
                    Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.vampirism.no_actions"), true);
                    Minecraft.getInstance().setScreen(null);
                }
            });
        }
    }

    private static RadialMenu<IAction<?>> getRadialMenu(List<IAction<?>> actions) {
        List<IRadialMenuSlot<IAction<?>>> parts = actions.stream().map(a -> (IRadialMenuSlot<IAction<?>>)new RadialMenuSlot<IAction<?>>(a.getName(), a, Collections.emptyList())).toList();
        return new RadialMenu<>((i) -> {
            VampirismMod.dispatcher.sendToServer(ServerboundToggleActionPacket.createFromRaytrace(RegUtil.id(parts.get(i).primarySlotIcon()), Minecraft.getInstance().hitResult));
        }, parts , SelectActionRadialScreen::drawActionPart,0);
    }

    private static void drawActionPart(IAction<?> action, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        ResourceLocation id = RegUtil.id(action);
        ResourceLocation texture = new ResourceLocation(id.getNamespace(), "textures/actions/" + id.getPath() + ".png");
        graphics.setColor(1,1,1,1);
        graphics.blit(texture, posX, posY, 0, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void drawSlice(IRadialMenuSlot<IAction<?>> slot, boolean highlighted, GuiGraphics buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        float actionPercentage = actionHandler.getPercentageForAction((IAction) slot.primarySlotIcon());
        if (((IAction<T>)slot.primarySlotIcon()).canUse(this.player) != IAction.PERM.ALLOWED) {
            actionPercentage = -1;
        }
        if (actionPercentage == 0) {
            super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, r, g, b, 100);
        } else if(actionPercentage > 0) {
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
