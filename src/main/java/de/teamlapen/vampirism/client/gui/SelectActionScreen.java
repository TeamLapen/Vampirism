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
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gui which is used to select vampire actions
 */
@OnlyIn(Dist.CLIENT)
public class SelectActionScreen extends GuiPieMenu<IAction> {
    private final static int ICON_TEXTURE_WIDTH = 256;
    private final static int ICON_TEXTURE_HEIGHT = 80;
    private IActionHandler actionHandler;
    /**
     * Fake skill which represents the cancel button
     */
    private IAction fakeAction = new DefaultVampireAction() {
        @Override
        public boolean activate(IVampirePlayer vampire) {
            return true;
        }

        @Override
        public int getCooldown() {
            return 0;
        }

        @Override
        public String getTranslationKey() {
            return "action.vampirism.cancel";
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    };

    public SelectActionScreen() {
        super(ICON_TEXTURE_WIDTH, ICON_TEXTURE_HEIGHT, 2298478591L, new TranslationTextComponent("selectAction"));
    }

    @Override
    protected void afterIconDraw(IAction p, int x, int y) {
        if (p == fakeAction) return;
        // Draw usage indicator

        float active = actionHandler.getPercentageForAction(p);
        if (active > 0) {

            float h = active * IS;
            this.fillGradient(x, (int) (y + h), x + 16, y + 16, 0xDDE0E000, 0x88E0E000);
        } else if (active < 0) {

            float h = (1F + (active)) * IS;
            this.fillGradient(x, (int) (y + h), x + 16, y + 16, 0x880E0E0E, 0xEE0E0E0E);
        }
    }

    @Override
    protected ResourceLocation getIconLoc(IAction item) {
        if (item == fakeAction) return new ResourceLocation(REFERENCE.MODID, "textures/actions/cancel.png");
        return new ResourceLocation(item.getRegistryName().getNamespace(), "textures/actions/" + item.getRegistryName().getPath() + ".png");
    }

    @Override
    protected float[] getColor(IAction s) {
        if (s != fakeAction && !(s.canUse(FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer()) == IAction.PERM.ALLOWED)) {
            return new float[]{0.8125f, 0.0859375f, 0.20703125f, 1f};
        } else {
            return super.getColor(s);
        }
    }

    @Override
    protected KeyBinding getMenuKeyBinding() {
        return ModKeys.getKeyBinding(ModKeys.KEY.ACTION);
    }

    @Override
    protected String getUnlocalizedName(IAction item) {
        return item.getTranslationKey();
    }

    @Override
    protected void onElementSelected(IAction action) {
        if (action != fakeAction && action.canUse(FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer()) == IAction.PERM.ALLOWED) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEACTION, "" + action.getRegistryName().toString()));
        }
    }

    @Override
    protected void onGuiInit() {
        IFactionPlayer player = FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer();
        if (player != null) {
            actionHandler = player.getActionHandler();
            elements.addAll(actionHandler.getUnlockedActions());
            elements.add(fakeAction);
        }

    }
}
