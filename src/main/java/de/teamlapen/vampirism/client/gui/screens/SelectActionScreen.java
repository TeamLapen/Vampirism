package de.teamlapen.vampirism.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.GuiRadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenuSlot;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.ServerboundToggleActionPacket;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SelectActionScreen extends SwitchingRadialMenu<IAction<?>> {

    private final IActionHandler<?> actionHandler;

    public SelectActionScreen(IFactionPlayer<?> player) {
        this(player, ModKeys.ACTION);
    }

    public SelectActionScreen(IFactionPlayer<?> player, KeyMapping keyMapping) {
        super(getRadialMenu(player), keyMapping, (mapping) -> FactionPlayerHandler.getOpt(player.getRepresentingPlayer()).filter(a -> a.getLordLevel() > 0).map(a -> new SelectMinionTaskScreen(a, mapping)).orElse(null));
        this.actionHandler = player.getActionHandler();
    }

    private static RadialMenu<IAction<?>> getRadialMenu(IFactionPlayer<?> player) {
        List<RadialMenuSlot<IAction<?>>> parts = getParts(player);
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

    private static List<RadialMenuSlot<IAction<?>>> getParts(IFactionPlayer<?> player) {
        return player.getActionHandler().getUnlockedActions().stream().filter(a -> a.showInSelectAction(player.getRepresentingPlayer())).map(a -> new RadialMenuSlot<IAction<?>>(a.getName().getString(), a, Collections.emptyList())).toList();
    }

    @Override
    public void drawSlice(RadialMenuSlot<IAction<?>> slot, boolean highlighted, BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        float actionPercentage = actionHandler.getPercentageForAction((IAction) slot.primarySlotIcon());
        if (actionPercentage == 0) {
            super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, r, g, b, a);
        } else if(actionPercentage >= 0) {
            super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, 160, 60,60, a);
        } else {
            super.drawSlice(slot, highlighted, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, 160, 160,60, a);
        }
    }
}
