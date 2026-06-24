package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.client.gui.radialmenu.GuiRadialMenu;
import de.teamlapen.faction.client.gui.radialmenu.IRadialMenuSlot;
import de.teamlapen.faction.client.gui.radialmenu.RadialMenu;
import de.teamlapen.faction.client.gui.radialmenu.RadialMenuSlot;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.items.crossbow.HunterCrossbowItem;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSelectAmmoTypePacket;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.items.crossbow.QuarrelPouchItem;
import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectAmmoScreen extends GuiRadialMenu<SelectAmmoScreen.AmmoType> {

    private static final Identifier NO_RESTRICTION = VIdentifier.mc("spectator/close");

    public SelectAmmoScreen(Collection<AmmoType> ammoTypes) {
        super(getRadialMenu(ammoTypes), true);
    }

    public static void show() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack crossbowStack = player.getMainHandItem();
        if (Helper.isHunter(player) && crossbowStack.getItem() instanceof HunterCrossbowItem crossbow && crossbow.canSelectAmmunition(crossbowStack)) {
            Collection<Item> selectable = crossbow.getSelectableAmmo();
            Map<Item, Integer> available = new HashMap<>();

            for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                if (stack.getItem() instanceof QuarrelPouchItem) {
                    for (ItemStack quarrel : stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY).items()) {
                        if (selectable.contains(quarrel.getItem())) {
                            available.merge(quarrel.getItem(), quarrel.getCount(), Integer::sum);
                        }
                    }
                } else if (selectable.contains(stack.getItem())) {
                    available.merge(stack.getItem(), stack.getCount(), Integer::sum);
                }
            }

            List<AmmoType> ammoTypes = new ArrayList<>();

            for (Item ammo : selectable) {
                int count = available.getOrDefault(ammo, 0);
                if (count > 0) {
                    ammoTypes.add(new AmmoType(ammo, count));
                }
            }

            ammoTypes.add(new AmmoType((ItemStack) null, 0));
            Minecraft.getInstance().setScreen(new SelectAmmoScreen(ammoTypes));
        }
    }

    private static RadialMenu<AmmoType> getRadialMenu(Collection<AmmoType> ammoTypes) {
        List<IRadialMenuSlot<AmmoType>> parts = ammoTypes.stream().<IRadialMenuSlot<AmmoType>>map(a -> new RadialMenuSlot<>(a.getDisplayName(), a)).toList();
        return new RadialMenu<>(i -> VampirismMod.proxy.sendToServer(ServerboundSelectAmmoTypePacket.of(parts.get(i).primarySlotIcon())), parts, SelectAmmoScreen::drawAmmoTypePart, 0);
    }

    private static void drawAmmoTypePart(AmmoType action, GuiGraphicsExtractor graphics, int posX, int posY, int size, boolean transparent) {
        if (action.renderStack != null) {
            var font = Minecraft.getInstance().font;
            graphics.item(action.renderStack, posX, posY);
            if (action.count > 0) {
                graphics.itemDecorations(font, action.renderStack, posX, posY, String.valueOf(action.count));
            }
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, NO_RESTRICTION, posX, posY, 16, 16);
        }
    }

    @Override
    public void drawSliceName(GuiGraphicsExtractor graphics, String sliceName, ItemStack stack, int posX, int posY) {
    }

    public record AmmoType(@Nullable ItemStack renderStack, int count) {

        public AmmoType(@Nullable Item renderStack, int count) {
            this(renderStack == null ? null : renderStack.getDefaultInstance(), count);
        }

        public Component getDisplayName() {
            return renderStack != null ? renderStack.getHoverName() : Component.translatable("gui.vampirism.select_ammo.no_restriction");
        }
    }
}
