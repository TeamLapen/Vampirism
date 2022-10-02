package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenuSlot;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.network.ServerboundToggleActionPacket;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class SelectActionScreen<T extends IFactionPlayer<T>> extends SwitchingRadialMenu<IAction<?>> {

    private final IActionHandler<?> actionHandler;
    private final T player;

    private SelectActionScreen(T player, Collection<IAction<?>> actions, KeyMapping keyMapping) {
        super(getRadialMenu(actions), keyMapping, SelectMinionTaskScreen::show);
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
                Collection<IAction<?>> actions = getActions(player);
                if (actions.size() > 0) {
                    //noinspection rawtypes
                    Minecraft.getInstance().setScreen(new SelectActionScreen(player, actions, keyMapping));
                }
            });
        }
    }

    public static Collection<IAction<?>> getActions(IFactionPlayer<?> player) {
        return player.getActionHandler().getUnlockedActions().stream().filter(a -> a.showInSelectAction(player.getRepresentingPlayer())).collect(Collectors.toList());
    }

    private static RadialMenu<IAction<?>> getRadialMenu(Collection<IAction<?>> actions) {
        List<RadialMenuSlot<IAction<?>>> parts = actions.stream().map(a -> new RadialMenuSlot<IAction<?>>(a.getName().getString(), a, Collections.emptyList())).toList();
        return new RadialMenu<>((i) -> {
            VampirismMod.dispatcher.sendToServer(ServerboundToggleActionPacket.createFromRaytrace(RegUtil.id(parts.get(i).primarySlotIcon()), Minecraft.getInstance().hitResult));
        }, parts , SelectActionScreen::drawActionPart,0);
    }

    private static void drawActionPart(IAction<?> action, PoseStack stack, int posX, int posY, int size, boolean transparent) {
        ResourceLocation id = RegUtil.id(action);
        ResourceLocation texture = new ResourceLocation(id.getNamespace(), "textures/actions/" + id.getPath() + ".png");
        RenderSystem.setShaderTexture(0, texture);
        blit(stack, posX, posY, 0, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void drawSlice(RadialMenuSlot<IAction<?>> slot, boolean highlighted, BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
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
