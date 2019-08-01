package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Add a button to the inventory screen that allows opening the skill menu from there
 */
public class ModifyInventoryScreen {

    private final static ResourceLocation INVENTORY_SKILLS = new ResourceLocation("vampirism", "textures/gui/inventory_skills.png");
    private ImageButton button;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
    public void onButtonClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {//InventoryScreen changes layout if recipe book button is clicked. Unfortunately it does not propagate this to the screen children so we need to use this
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getGui() instanceof InventoryScreen && FactionPlayerHandler.get(event.getGui().getMinecraft().player).getCurrentFactionPlayer() != null) {
            //Do the same thing MouseHelper would do. However, if GUI returns false on mouseclick it will be called again by MouseHelper
            if (event.getGui().mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
                if (button != null)
                    button.setPosition(((InventoryScreen) event.getGui()).getGuiLeft() + 125, event.getGui().height / 2 - 22);

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
        if (VampirismConfig.CLIENT.guiSkillButton.get() && event.getGui() instanceof InventoryScreen && FactionPlayerHandler.get(event.getGui().getMinecraft().player).getCurrentFactionPlayer() != null) {
            button = new ImageButton(((InventoryScreen) event.getGui()).getGuiLeft() + 125, event.getGui().height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_SKILLS, (context) -> {
                Minecraft.getInstance().displayGuiScreen(new SkillsScreen());
            });
            event.addWidget(button);
        }
    }

}
