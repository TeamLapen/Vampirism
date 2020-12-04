package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.lib.lib.client.gui.GuiPieMenu;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gui which is used to select vampire actions
 */
@OnlyIn(Dist.CLIENT)
public class SelectActionScreen extends GuiPieMenu<IAction> {
    public final static List<IAction> ACTIONORDER = Lists.newArrayList();
    public static IAction SELECTEDACTION;
    /**
     * Fake skill which represents the cancel button
     */
    private static final IAction fakeAction = new DefaultVampireAction() {
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

    /**
     * safes the action order to client config
     */
    private static void saveActionOrder() {
        StringBuilder s = new StringBuilder();
        Iterator<IAction> iterator = ACTIONORDER.iterator();
        while (iterator.hasNext()) {
            IAction action = iterator.next();
            if (action == fakeAction) continue;
            s.append(action.getRegistryName());
            if (iterator.hasNext())
                s.append(",");
        }
        VampirismConfig.CLIENT.actionOrder.set(s.toString());
    }

    /**
     * loades action order from client config
     */
    public static void loadActionOrder() {
        List<IAction> actions = Lists.newArrayList(ModRegistries.ACTIONS.getValues());
        String[] actionOrder = VampirismConfig.CLIENT.actionOrder.get().split(",");
        for (String s : actionOrder) {
            ResourceLocation name = ResourceLocation.tryCreate(s);
            if (name != null) {
                IAction a = ModRegistries.ACTIONS.getValue(name);
                if (a == null) continue;
                ACTIONORDER.add(a);
                actions.remove(a);
            }
        }
        if (!actions.isEmpty()) {
            ACTIONORDER.addAll(actions);
            saveActionOrder();
        }
    }

    private IActionHandler actionHandler;
    private final boolean editActions;

    public SelectActionScreen(Color backgroundColor, boolean edit) {
        super(backgroundColor, new TranslationTextComponent("selectAction"));
        editActions = edit;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) { //mouseClicked
        if (editActions && mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && getSelectedElement() >= 0) {
            if (elements.get(getSelectedElement()) != fakeAction) {
                SELECTEDACTION = elements.get(getSelectedElement());
            } else {
                closeScreen();
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (editActions && key == GLFW.GLFW_KEY_ESCAPE) {
            closeScreen();
            return true;
        } else if (key == GLFW.GLFW_KEY_SPACE && !this.editActions) { //TODO as long as minion tasks are not editable prevent switching to them
            if (FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getLordLevel).orElse(0) > 0) {
                this.minecraft.displayGuiScreen(new SelectMinionTaskScreen());
            }
        }
        if (getSelectedElement() >= 0) {
            if (elements.get(getSelectedElement()) == fakeAction) {
                return true;
            }
            if (ModKeys.getKeyBinding(ModKeys.KEY.ACTION1).matchesKey(key, scancode)) {
                FactionPlayerHandler.get(minecraft.player).setBoundAction1(elements.get(getSelectedElement()), true);
                if (!editActions) {
                    GLFW.glfwSetCursorPos(this.minecraft.getMainWindow().getHandle(), this.minecraft.getMainWindow().getWidth() / 2f, this.minecraft.getMainWindow().getHeight() / 2f);
                    closeScreen();
                }
                return true;
            } else if (ModKeys.getKeyBinding(ModKeys.KEY.ACTION2).matchesKey(key, scancode)) {
                FactionPlayerHandler.get(Minecraft.getInstance().player).setBoundAction2(elements.get(getSelectedElement()), true);
                if (!editActions) {
                    GLFW.glfwSetCursorPos(this.minecraft.getMainWindow().getHandle(), this.minecraft.getMainWindow().getWidth() / 2f, this.minecraft.getMainWindow().getHeight() / 2f);
                    closeScreen();
                }
                return true;
            }
        }
        return super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (!editActions) {
            if (ModKeys.getKeyBinding(ModKeys.KEY.MINION).matchesKey(key, scancode) || ModKeys.getKeyBinding(ModKeys.KEY.ACTION).matchesKey(key, scancode)) {
                this.closeScreen();
                if (getSelectedElement() >= 0) {
                    this.onElementSelected(elements.get(getSelectedElement()));
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (editActions) {
            if (SELECTEDACTION != null) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && getSelectedElement() >= 0 && elements.get(getSelectedElement()) != fakeAction) {
                    switchActions(SELECTEDACTION, elements.get(getSelectedElement()));
                    updateElements();
                }
                SELECTEDACTION = null;
            }
        } else {
            super.mouseReleased(mouseX, mouseY, mouseButton);
        }
        return true;
    }

    @Override
    public void closeScreen() {
        if (editActions) {
            saveActionOrder();
            SELECTEDACTION = null;
        }
        super.closeScreen();
    }

    @Override
    protected void afterIconDraw(MatrixStack stack, IAction p, int x, int y) {
        if (p == fakeAction || editActions) return;
        // Draw usage indicator

        float active = actionHandler.getPercentageForAction(p);
        if (active > 0) {

            float h = active * 16;
            this.fillGradient(stack, x, (int) (y + h), x + 16, y + 16, Color.YELLOW.getRGB() - 0x88000000, Color.YELLOW.getRGB());
        } else if (active < 0) {

            float h = (1F + (active)) * 16;
            this.fillGradient(stack, x, (int) (y + h), x + 16, y + 16, Color.BLACK.getRGB() - 0x55000000, Color.BLACK.getRGB());
        }
    }

    @Override
    @Nonnull
    protected Color getColor(IAction s) {
        if (s == fakeAction) return super.getColor(s);
        if (editActions) {
            if (SELECTEDACTION != null && (s == SELECTEDACTION || (getSelectedElement() >= 0 && elements.get(getSelectedElement()) == s)))
                return Color.GREEN;
            else return Color.WHITE;
        }
        if (!minecraft.player.isAlive()) return Color.RED;
        IFactionPlayer factionPlayer = FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer().orElse(null);
        if (!(s.canUse(factionPlayer) == IAction.PERM.ALLOWED) || actionHandler.getPercentageForAction(s) < 0) {
            return Color.RED;
        } else if (actionHandler.getPercentageForAction(s) > 0) {
            return Color.YELLOW;
        } else {
            return super.getColor(s);
        }
    }

    @Override
    protected ResourceLocation getIconLoc(IAction item) {
        if (item == fakeAction) return new ResourceLocation(REFERENCE.MODID, "textures/actions/cancel.png");
        return new ResourceLocation(item.getRegistryName().getNamespace(), "textures/actions/" + item.getRegistryName().getPath() + ".png");
    }

    @Override
    protected KeyBinding getMenuKeyBinding() {
        return ModKeys.getKeyBinding(ModKeys.KEY.ACTION);
    }

    @Override
    protected ITextComponent getName(IAction item) {
        return item.getName();
    }

    @Override
    protected void onElementSelected(IAction action) {
        if (action != fakeAction && action.canUse(FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer().orElse(null)) == IAction.PERM.ALLOWED) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEACTION, "" + action.getRegistryName().toString()));
        }
    }

    @Override
    protected void onGuiInit() {
        IFactionPlayer player = FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer().orElse(null);
        actionHandler = player.getActionHandler();
        updateElements();

    }

    /**
     * orders the given list after client preference
     */
    private ImmutableList<IAction> getActionOrdered(List<IAction> toSort) {
        List<IAction> ordered = Lists.newArrayList();
        if (ACTIONORDER.isEmpty()) ACTIONORDER.addAll(ModRegistries.ACTIONS.getValues());
        for (IAction a : ACTIONORDER) {
            if (toSort.contains(a)) {
                ordered.add(a);
                toSort.remove(a);
            }
        }
        ordered.addAll(toSort);
        return ImmutableList.copyOf(ordered);
    }

    /**
     * switches the position of the given actions
     */
    private void switchActions(IAction first, IAction second) {
        if (first == second) return;
        int a = ACTIONORDER.indexOf(first);
        int b = ACTIONORDER.indexOf(second);
        ACTIONORDER.set(a, second);
        ACTIONORDER.set(b, first);
    }

    /**
     * adds ordered actions to the gui
     */
    private void updateElements() {
        elements.clear();
        //noinspection unchecked
        elements.addAll(getActionOrdered(((List<IAction>) actionHandler.getUnlockedActions()).stream().filter(a -> a.showInSelectAction(minecraft.player)).collect(Collectors.toList())));
        elements.add(fakeAction);
    }
}
