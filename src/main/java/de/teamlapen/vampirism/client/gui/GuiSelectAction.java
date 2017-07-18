package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.GuiPieMenu;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gui which is used to select vampire actions
 */
@SideOnly(Side.CLIENT)
public class GuiSelectAction extends GuiPieMenu<IAction> {
    private final static int ICON_TEXTURE_WIDTH = 256;
    private final static int ICON_TEXTURE_HEIGHT = 80;
    private final static ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/actions.png");
    private IActionHandler actionHandler;
    /**
     * Fake skill which represents the cancel button
     */
    private IAction fakeAction = new DefaultVampireAction(null) {
        @Override
        public boolean activate(IVampirePlayer vampire) {
            return true;
        }

        @Override
        public int getCooldown() {
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
            return "action.vampirism.cancel";
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    };

    public GuiSelectAction() {
        super(ICON_TEXTURE_WIDTH, ICON_TEXTURE_HEIGHT, 2298478591L, "selectAction");
    }

    @Override
    protected void afterIconDraw(IAction p, int x, int y) {
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
    protected ResourceLocation getIconLoc(IAction item) {
        return item.getIconLoc() == null ? defaultIcons : item.getIconLoc();
    }

    @Override
    protected int getMenuKeyCode() {
        return ModKeys.getKeyCode(ModKeys.KEY.ACTION);
    }

    @Override
    protected int getMinU(IAction item) {
        return item.getMinU();
    }

    @Override
    protected int getMinV(IAction item) {
        return item.getMinV();
    }

    @Override
    protected String getUnlocalizedName(IAction item) {
        return item.getUnlocalizedName();
    }

    @Override
    protected void onElementSelected(IAction action) {
        if (action != fakeAction) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEACTION, "" + ((ActionHandler) actionHandler).getIdFromAction(action)));
        }
    }

    @Override
    protected void onGuiInit() {
        IFactionPlayer player = FactionPlayerHandler.get(mc.player).getCurrentFactionPlayer();
        if (player != null) {
            actionHandler = player.getActionHandler();
            elements.addAll(actionHandler.getAvailableActions());
            elements.add(fakeAction);
        }

    }
}
