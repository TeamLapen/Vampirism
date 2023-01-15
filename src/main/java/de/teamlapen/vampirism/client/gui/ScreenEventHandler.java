package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.client.gui.screens.SleepInMultiplayerModScreen;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.teamlapen.vampirism.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.blocks.TentBlock.POSITION;

/**
 * Add a button to the inventory screen that allows opening the skill menu from there
 */
public class ScreenEventHandler {

    private final static ResourceLocation INVENTORY_SKILLS = new ResourceLocation(REFERENCE.MODID, "textures/gui/inventory_skills.png");
    private ImageButton button;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onButtonClicked(ScreenEvent.MouseButtonPressed.@NotNull Pre event) {//InventoryScreen changes layout if recipe book button is clicked. Unfortunately it does not propagate this to the screen children, so we need to use this
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getScreen() instanceof InventoryScreen && FactionPlayerHandler.getOpt(event.getScreen().getMinecraft().player).map(FactionPlayerHandler::getCurrentFactionPlayer).map((Optional::isPresent)).orElse(false)) {
            //Do the same thing MouseHelper would do. However, if GUI returns false on mouseclick it will be called again by MouseHelper
            if (event.getScreen().mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
                if (button != null) {
                    button.setPosition(((InventoryScreen) event.getScreen()).getGuiLeft() + VampirismConfig.CLIENT.overrideGuiSkillButtonX.get(), event.getScreen().height / 2 + VampirismConfig.CLIENT.overrideGuiSkillButtonY.get());
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInitGuiEventPost(ScreenEvent.Init.@NotNull Post event) {
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getScreen() instanceof InventoryScreen && FactionPlayerHandler.getOpt(event.getScreen().getMinecraft().player).map(FactionPlayerHandler::getCurrentFactionPlayer).map((Optional::isPresent)).orElse(false)) {
            button = new ImageButton(((InventoryScreen) event.getScreen()).getGuiLeft() + VampirismConfig.CLIENT.overrideGuiSkillButtonX.get(), event.getScreen().height / 2 + VampirismConfig.CLIENT.overrideGuiSkillButtonY.get(), 20, 18, 178, 0, 19, INVENTORY_SKILLS, (context) -> {
                VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.VAMPIRISM_MENU));
            });
            event.addListener(button);
        }
        else if(event.getScreen() instanceof InBedChatScreen){
            Player p = event.getScreen().getMinecraft().player;
            if (p!= null && p.isSleeping()) {
                GuiEventListener l = event.getScreen().children().get(1);
                if (l instanceof AbstractWidget leaveButton) {
                    p.getSleepingPos().map(pos -> p.level.getBlockState(pos).getBlock()).map(block -> block instanceof TentBlock ? "text.vampirism.tent.stop_sleeping" : (block instanceof CoffinBlock ? "text.vampirism.coffin.stop_sleeping" : null)).ifPresent(newText -> {
                        leaveButton.setMessage(Component.translatable(newText));
                    });
                }
            }
        }
    }

}
