package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.GuiRadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.IRadialMenuSlot;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenuSlot;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.items.crossbow.CrossbowArrowHandler;
import de.teamlapen.vampirism.network.ServerboundSelectAmmoTypePacket;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SelectAmmoScreen extends GuiRadialMenu<SelectAmmoScreen.AmmoType> {

    private static final ResourceLocation NO_RESTRICTION = new ResourceLocation("textures/gui/spectator_widgets.png");

    public SelectAmmoScreen(Collection<AmmoType> ammoTypes) {
        super(getRadialMenu(ammoTypes));
    }

    public static void show() {
        Player player = Minecraft.getInstance().player;
        ItemStack crossbowStack = player.getMainHandItem();
        if(Helper.isHunter(player) && crossbowStack.getItem() instanceof IVampirismCrossbow crossbow && crossbow.canSelectAmmunition(crossbowStack))  {
            var ammoTypes = CrossbowArrowHandler.getCrossbowArrows().stream().map(item -> new AmmoType(item, player.getInventory().countItem(item))).collect(Collectors.toList());
            ammoTypes.add(new AmmoType(null, 0));
            Minecraft.getInstance().setScreen(new SelectAmmoScreen(ammoTypes));
        }
    }

    private static RadialMenu<AmmoType> getRadialMenu(Collection<AmmoType> ammoTypes) {
        List<IRadialMenuSlot<AmmoType>> parts = (List<IRadialMenuSlot<AmmoType>>) (Object)ammoTypes.stream().map(a -> new RadialMenuSlot<>(a.getDisplayName(), a)).toList();
        return new RadialMenu<>((i) -> {
            VampirismMod.dispatcher.sendToServer(ServerboundSelectAmmoTypePacket.of(parts.get(i).primarySlotIcon()));
        }, parts, SelectAmmoScreen::drawAmmoTypePart, 0);
    }

    private static void drawAmmoTypePart(AmmoType action, PoseStack stack, int posX, int posY, int size, boolean transparent) {
        if (action.renderStack != null) {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, action.renderStack, posX, posY);
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(stack, Minecraft.getInstance().screen.font, action.renderStack, posX, posY, String.valueOf(action.count));
        } else {
            RenderSystem.setShaderTexture(0, NO_RESTRICTION);
            blit(stack, posX, posY, 128, 0, 16, 16, 256, 256);
        }
    }

    @Override
    public void drawSliceName(PoseStack ms, String sliceName, ItemStack stack, int posX, int posY) {
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
