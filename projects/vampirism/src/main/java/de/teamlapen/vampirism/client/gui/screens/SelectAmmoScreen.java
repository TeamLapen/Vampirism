package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.factions.client.gui.radialmenu.GuiRadialMenu;
import de.teamlapen.factions.client.gui.radialmenu.IRadialMenuSlot;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenu;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenuSlot;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSelectAmmoTypePacket;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.items.QuarrelPouch;
import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.world.items.crossbow.CrossbowArrowHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectAmmoScreen extends GuiRadialMenu<SelectAmmoScreen.AmmoType> {

    private static final Identifier NO_RESTRICTION = VResourceLocation.mc("spectator/close");

    public SelectAmmoScreen(Collection<AmmoType> ammoTypes) {
        super(getRadialMenu(ammoTypes), true);
    }

    public static void show() {
        Player player = Minecraft.getInstance().player;
        ItemStack crossbowStack = player.getMainHandItem();
        if (Helper.isHunter(player) && crossbowStack.getItem() instanceof IHunterCrossbow crossbow && crossbow.canSelectAmmunition(crossbowStack)) {
            @NotNull Map<Item, @NotNull Integer> list = player.getInventory().getNonEquipmentItems().stream().filter(s -> s.getItem() instanceof QuarrelPouch).map(x -> x.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY)).flatMap(s -> s.items().stream()).collect(Collectors.groupingBy(ItemStack::getItem, Collectors.summingInt(ItemStack::getCount)));
            var ammoTypes = CrossbowArrowHandler.getCrossbowArrows().stream().map(item -> new AmmoType(item, player.getInventory().countItem(item) + list.getOrDefault(item, 0))).collect(Collectors.toList());
            ammoTypes.add(new AmmoType(null, 0));
            Minecraft.getInstance().setScreen(new SelectAmmoScreen(ammoTypes));
        }
    }

    private static RadialMenu<AmmoType> getRadialMenu(Collection<AmmoType> ammoTypes) {
        List<IRadialMenuSlot<AmmoType>> parts = (List<IRadialMenuSlot<AmmoType>>) (Object) ammoTypes.stream().map(a -> new RadialMenuSlot<>(a.getDisplayName(), a)).toList();
        return new RadialMenu<>((i) -> {
            VampirismMod.proxy.sendToServer(ServerboundSelectAmmoTypePacket.of(parts.get(i).primarySlotIcon()));
        }, parts, SelectAmmoScreen::drawAmmoTypePart, 0);
    }

    private static void drawAmmoTypePart(AmmoType action, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        if (action.renderStack != null) {
            graphics.renderItem(action.renderStack, posX, posY);
            graphics.renderItemDecorations(Minecraft.getInstance().screen.font, action.renderStack, posX, posY, String.valueOf(action.count));
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, NO_RESTRICTION, posX, posY, 16, 16);
        }
    }

    @Override
    public void drawSliceName(GuiGraphics graphics, String sliceName, ItemStack stack, int posX, int posY) {
    }

    public static class AmmoType {
        public final ItemStack renderStack;
        public final int count;

        public AmmoType(@Nullable Item item, int count) {
            this.count = count;
            this.renderStack = item == null ? null : item.getDefaultInstance();
        }

        public Component getDisplayName() {
            return renderStack != null ? renderStack.getHoverName() : Component.translatable("text.vampirism.crossbow.no_restriction");
        }
    }
}
