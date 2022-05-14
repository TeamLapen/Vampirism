package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.CSimpleInputEvent;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
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
    public void onButtonClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {//InventoryScreen changes layout if recipe book button is clicked. Unfortunately it does not propagate this to the screen children so we need to use this
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getGui() instanceof InventoryScreen && FactionPlayerHandler.getOpt(event.getGui().getMinecraft().player).map(FactionPlayerHandler::getCurrentFactionPlayer).map((Optional::isPresent)).orElse(false)) {
            //Do the same thing MouseHelper would do. However, if GUI returns false on mouseclick it will be called again by MouseHelper
            if (event.getGui().mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
                if (button != null)
                    button.setPosition(((InventoryScreen) event.getGui()).getGuiLeft() + VampirismConfig.CLIENT.overrideGuiSkillButtonX.get(), event.getGui().height / 2 + VampirismConfig.CLIENT.overrideGuiSkillButtonY.get());
            }
        }
    }

    // Less intrusive method. However, button is at the wrong position until you release the mouse button
//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public void onButtonClicked(GuiScreenEvent.MouseReleasedEvent.Pre event) {//InventoryScreen changes layout if recipe book button is clicked. Unfortunately it does not propagate this to the screen children so we need to use this
//        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getGui() instanceof InventoryScreen && FactionPlayerHandler.get(event.getGui().getMinecraft().player).getCurrentFactionPlayer() != null) {
//            if(button!=null)button.setPosition(((InventoryScreen) event.getGui()).getGuiLeft() + 125, event.getGui().height / 2 - 22);
//        }
//    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInitGuiEventPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getGui() instanceof InventoryScreen && FactionPlayerHandler.getOpt(event.getGui().getMinecraft().player).map(FactionPlayerHandler::getCurrentFactionPlayer).map((Optional::isPresent)).orElse(false)) {
            button = new ImageButton(((InventoryScreen) event.getGui()).getGuiLeft() + VampirismConfig.CLIENT.overrideGuiSkillButtonX.get(), event.getGui().height / 2 + VampirismConfig.CLIENT.overrideGuiSkillButtonY.get(), 20, 18, 178, 0, 19, INVENTORY_SKILLS, (context) -> {
                VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.VAMPIRISM_MENU));
            });
            event.addWidget(button);
        }
    }
}
