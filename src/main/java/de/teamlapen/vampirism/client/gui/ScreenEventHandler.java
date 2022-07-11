package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

/**
 * Add a button to the inventory screen that allows opening the skill menu from there
 */
public class ScreenEventHandler {

    private final static ResourceLocation INVENTORY_SKILLS = new ResourceLocation(REFERENCE.MODID, "textures/gui/inventory_skills.png");
    private ImageButton button;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onButtonClicked(ScreenEvent.MouseButtonPressed.Pre event) {//InventoryScreen changes layout if recipe book button is clicked. Unfortunately it does not propagate this to the screen children, so we need to use this
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getScreen() instanceof InventoryScreen && FactionPlayerHandler.getOpt(event.getScreen().getMinecraft().player).map(FactionPlayerHandler::getCurrentFactionPlayer).map((Optional::isPresent)).orElse(false)) {
            //Do the same thing MouseHelper would do. However, if GUI returns false on mouseclick it will be called again by MouseHelper
            if (event.getScreen().mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
                if (button != null)
                    button.setPosition(((InventoryScreen) event.getScreen()).getGuiLeft() + VampirismConfig.CLIENT.overrideGuiSkillButtonX.get(), event.getScreen().height / 2 + VampirismConfig.CLIENT.overrideGuiSkillButtonY.get());
            }
        }
    }

    // Less intrusive method. However, button is at the wrong position until you release the mouse button
//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public void onButtonClicked(GuiScreenEvent.MouseReleasedEvent.Pre event) {//InventoryScreen changes layout if recipe book button is clicked. Unfortunately it does not propagate this to the screen children, so we need to use this
//        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getGui() instanceof InventoryScreen && FactionPlayerHandler.get(event.getGui().getMinecraft().player).getCurrentFactionPlayer() != null) {
//            if(button!=null)button.setPosition(((InventoryScreen) event.getGui()).getGuiLeft() + 125, event.getGui().height / 2 - 22);
//        }
//    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInitGuiEventPost(ScreenEvent.Init.Post event) {
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getScreen() instanceof InventoryScreen && FactionPlayerHandler.getOpt(event.getScreen().getMinecraft().player).map(FactionPlayerHandler::getCurrentFactionPlayer).map((Optional::isPresent)).orElse(false)) {
            button = new ImageButton(((InventoryScreen) event.getScreen()).getGuiLeft() + VampirismConfig.CLIENT.overrideGuiSkillButtonX.get(), event.getScreen().height / 2 + VampirismConfig.CLIENT.overrideGuiSkillButtonY.get(), 20, 18, 178, 0, 19, INVENTORY_SKILLS, (context) -> {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.OPEN_VAMPIRISM_MENU, ""));
            });
            event.addListener(button);
        }
    }
}
