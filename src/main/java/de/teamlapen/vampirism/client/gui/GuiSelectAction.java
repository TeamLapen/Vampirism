package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.gui.client.GuiPieMenu;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.ActionRegistry;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gui which is used to select vampire actions
 */
@SideOnly(Side.CLIENT)
public class GuiSelectAction extends GuiPieMenu<IVampireAction> {
    private final static ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/actions.png");
    private IActionHandler actionHandler;
    /**
     * Fake skill which represents the cancel button
     */
    private IVampireAction fakeAction = new DefaultAction(null) {
        @Override
        public int getCooldown() {
            return 0;
        }

        @Override
        public int getMinLevel() {
            return 0;
        }

        @Override
        public int getMinU() {
            return 16;
        }

        @Override
        public int getMinV() {
            return 0;
        }

        @Override
        public String getUnlocalizedName() {
            return "skill.vampirism.cancel";
        }

        @Override
        public boolean onActivated(IVampirePlayer vampire) {
            return true;
        }

    };

    public GuiSelectAction() {
        super(2298478591L, "selectAction");
    }

    @Override
    protected void afterIconDraw(IVampireAction p, int x, int y) {
        if (p == fakeAction) return;
        // Draw usage indicator

        float active = actionHandler.getPercentageForAction(p);
        if (active > 0) {

            float h = active * IS;
            this.drawGradientRect(x, (int) (y + h), x + IS, y + IS, 0xDDE0E000, 0x88E0E000);
        } else if (active < 0) {

            float h = (1F + (active)) * IS;
            this.drawGradientRect(x, (int) (y + h), x + IS, y + IS, 0x880E0E0E, 0xEE0E0E0E);
        }
    }

    @Override
    protected ResourceLocation getIconLoc(IVampireAction item) {
        return item.getIconLoc() == null ? defaultIcons : item.getIconLoc();
    }

    @Override
    protected int getMenuKeyCode() {
        return ModKeys.getKeyCode(ModKeys.KEY.ACTION);
    }

    @Override
    protected int getMinU(IVampireAction item) {
        return item.getMinU();
    }

    @Override
    protected int getMinV(IVampireAction item) {
        return item.getMinV();
    }

    @Override
    protected String getUnlocalizedName(IVampireAction item) {
        return item.getUnlocalizedName();
    }

    @Override
    protected void onElementSelected(IVampireAction action) {
        if (action != fakeAction) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEACTION, "" + ((ActionRegistry) VampirismAPI.actionRegistry()).getIdFromAction(action)));
        }
    }

    @Override
    protected void onGuiInit() {
        actionHandler = VampirePlayer.get(this.mc.thePlayer).getActionHandler();
        elements.addAll(actionHandler.getAvailableActions());
        elements.add(fakeAction);
    }
}
