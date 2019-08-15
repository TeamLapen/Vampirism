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
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Gui which is used to select vampire actions
 */
@OnlyIn(Dist.CLIENT)
public class SelectActionScreen extends GuiPieMenu<IAction> {
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

    public SelectActionScreen(Color backgroundColor) {
        super(backgroundColor, new TranslationTextComponent("selectAction"));
    }

    @Override
    protected void afterIconDraw(IAction p, int x, int y) {
        if (p == fakeAction) return;
        // Draw usage indicator

        float active = actionHandler.getPercentageForAction(p);
        if (active > 0) {

            float h = active * 16;
            this.fillGradient(x, (int) (y + h), x + 16, y + 16, Color.YELLOW.getRGB() - 0x88000000, Color.YELLOW.getRGB());
        } else if (active < 0) {

            float h = (1F + (active)) * 16;
            this.fillGradient(x, (int) (y + h), x + 16, y + 16, Color.BLACK.getRGB() - 0x55000000, Color.BLACK.getRGB());
        }
    }

    @Override
    protected ResourceLocation getIconLoc(IAction item) {
        if (item == fakeAction) return new ResourceLocation(REFERENCE.MODID, "textures/actions/cancel.png");
        return new ResourceLocation(item.getRegistryName().getNamespace(), "textures/actions/" + item.getRegistryName().getPath() + ".png");
    }

    @Override
    @Nonnull
    protected Color getColor(IAction s) {
        if (s == fakeAction) return super.getColor(s);
        IFactionPlayer factionPlayer = FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer();
        if (!(s.canUse(factionPlayer) == IAction.PERM.ALLOWED) || actionHandler.getPercentageForAction(s) < 0) {
            return Color.RED;
        } else if (actionHandler.getPercentageForAction(s) > 0) {
            return Color.YELLOW;
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
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (getSelectedElement() >= 0) {
            if (elements.get(getSelectedElement()) == fakeAction) {
                return true;
            }
            if (ModKeys.getKeyBinding(ModKeys.KEY.ACTION1).matchesKey(key, scancode)) {
                FactionPlayerHandler.get(Minecraft.getInstance().player).setBoundAction1(elements.get(getSelectedElement()), true);
                GLFW.glfwSetCursorPos(this.minecraft.mainWindow.getHandle(), this.minecraft.mainWindow.getWidth() / 2, this.minecraft.mainWindow.getHeight() / 2);
                onClose();
                return true;
            } else if (ModKeys.getKeyBinding(ModKeys.KEY.ACTION2).matchesKey(key, scancode)) {
                FactionPlayerHandler.get(Minecraft.getInstance().player).setBoundAction2(elements.get(getSelectedElement()), true);
                GLFW.glfwSetCursorPos(this.minecraft.mainWindow.getHandle(), this.minecraft.mainWindow.getWidth() / 2, this.minecraft.mainWindow.getHeight() / 2);
                onClose();
                return true;
            }
        }
        return super.keyPressed(key, scancode, modifiers);
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
