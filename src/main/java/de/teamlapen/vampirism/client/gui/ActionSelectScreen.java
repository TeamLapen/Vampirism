package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.GuiPieMenu;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
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
import de.teamlapen.vampirism.network.ServerboundActionBindingPacket;
import de.teamlapen.vampirism.network.ServerboundToggleActionPacket;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Gui which is used to select vampire actions
 */
@OnlyIn(Dist.CLIENT)
public class ActionSelectScreen<T extends IFactionPlayer<T>> extends GuiPieMenu<IAction<T>> {
    public final static List<IAction<?>> ACTIONORDER = NonNullList.create();
    /**
     * Fake skill which represents the cancel button
     */
    private static final IAction<?> fakeAction = new DefaultVampireAction() {

        @Override
        protected boolean activate(IVampirePlayer player, ActivationContext context) {
            return true;
        }

        @Override
        public int getCooldown(IVampirePlayer player) {
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
    public static IAction<?> SELECTEDACTION;

    /**
     * safes the action order to client config
     */
    private static void saveActionOrder() {
        VampirismConfig.CLIENT.actionOrder.set(ACTIONORDER.stream().filter(action -> action != fakeAction).map(action -> RegUtil.id(action).toString()).collect(Collectors.toList()));
    }

    /**
     * loades action order from client config
     */
    public static void loadActionOrder() {
        List<IAction<?>> actions = Lists.newArrayList(RegUtil.values(ModRegistries.ACTIONS));
        //Keep in mind some previously saved actions may have been removed
        VampirismConfig.CLIENT.actionOrder.get().stream().map(action -> RegUtil.getAction(new ResourceLocation(action))).filter(Objects::nonNull).forEachOrdered(action -> {
            actions.remove(action);
            ACTIONORDER.add(action);
        });
        if (!actions.isEmpty()) {
            ACTIONORDER.addAll(actions);
            saveActionOrder();
        }
    }

    private final boolean editActions;
    private IActionHandler<T> actionHandler;

    public ActionSelectScreen(Color backgroundColor, boolean edit) {
        super(backgroundColor, Component.translatable("selectAction"));
        editActions = edit;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (editActions && key == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        } else if (key == GLFW.GLFW_KEY_SPACE && !this.editActions) { //TODO as long as minion tasks are not editable prevent switching to them
            if (FactionPlayerHandler.getOpt(minecraft.player).map(FactionPlayerHandler::getLordLevel).orElse(0) > 0) {
                this.minecraft.setScreen(new SelectMinionTaskScreen());
            }
        }
        if (getSelectedElement() >= 0) {
            if (checkBinding(binding -> binding.matches(key, scancode))) {
                return true;
            }
        }
        return super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (!editActions) {
            if (ModKeys.MINION.matches(key, scancode) || ModKeys.ACTION.matches(key, scancode)) {
                this.onClose();
                if (getSelectedElement() >= 0) {
                    this.onElementSelected(elements.get(getSelectedElement()));
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (getSelectedElement() >= 0) {
            if (editActions) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    if (elements.get(getSelectedElement()) != fakeAction) {
                        SELECTEDACTION = elements.get(getSelectedElement());
                    } else {
                        onClose();
                    }
                }

            }
            if (checkBinding(binding -> binding.matchesMouse(mouseButton))) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onClose() {
        if (editActions) {
            saveActionOrder();
            SELECTEDACTION = null;
        }
        super.onClose();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (editActions) {
            if (SELECTEDACTION != null) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && getSelectedElement() >= 0 && elements.get(getSelectedElement()) != fakeAction) {
                    //noinspection unchecked
                    switchActions((IAction<T>) SELECTEDACTION, elements.get(getSelectedElement()));
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
    public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (editActions) {
            List<Component> tooltips = Lists.newArrayList(Component.translatable("gui.vampirism.action_select.action_binding")
                    , ModKeys.ACTION1.getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.AQUA)
                    , ModKeys.ACTION2.getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.AQUA));
            this.renderTooltip(stack, tooltips.stream().flatMap(t -> this.font.split(t,this.width / 4).stream()).collect(Collectors.toList()),0, ((int) (this.height * 0.82)), this.font);
        }
    }

    @Override
    protected void afterIconDraw(PoseStack stack, IAction<T> p, int x, int y) {
        if (p == fakeAction || editActions)
            return;
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
    protected Color getColor(IAction<T> s) {
        if (s == fakeAction) return super.getColor(s);
        if (editActions) {
            if (SELECTEDACTION != null && (s == SELECTEDACTION || (getSelectedElement() >= 0 && elements.get(getSelectedElement()) == s)))
                return Color.GREEN;
            else return Color.WHITE;
        }
        if (!minecraft.player.isAlive()) return Color.RED;
        return FactionPlayerHandler.getCurrentFactionPlayer(minecraft.player).map(factionPlayer -> {
            //noinspection unchecked
            if (!(s.canUse((T) factionPlayer) == IAction.PERM.ALLOWED) || actionHandler.getPercentageForAction(s) < 0) {
                return Color.RED;
            } else if (actionHandler.getPercentageForAction(s) > 0) {
                return Color.YELLOW;
            } else {
                return null;
            }
        }).orElse(super.getColor(s));

    }

    @Override
    protected ResourceLocation getIconLoc(IAction<T> item) {
        if (item == fakeAction) return new ResourceLocation(REFERENCE.MODID, "textures/actions/cancel.png");
        return new ResourceLocation(RegUtil.id(item).getNamespace(), "textures/actions/" + RegUtil.id(item).getPath() + ".png");
    }

    @Override
    protected KeyMapping getMenuKeyBinding() {
        return ModKeys.ACTION;
    }

    @Override
    protected Component getName(IAction<T> item) {
        return item.getName();
    }

    @Override
    protected void onElementSelected(IAction<T> action) {
        //noinspection unchecked
        if (action != fakeAction && action.canUse((T)FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer().orElse(null)) == IAction.PERM.ALLOWED) {
            VampirismMod.dispatcher.sendToServer(ServerboundToggleActionPacket.createFromRaytrace(RegUtil.id(action), Minecraft.getInstance().hitResult));
        }
    }

    @Override
    protected void onGuiInit() {
        //noinspection unchecked
        T player = (T)FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer().orElseThrow(() -> new NullPointerException("Player can not be null"));
        actionHandler = player.getActionHandler();
        updateElements();

    }

    private boolean checkBinding(Function<KeyMapping, Boolean> func) {
        if (elements.get(getSelectedElement()) == fakeAction) {
            return true;
        }
        if (func.apply(ModKeys.ACTION1) && ModKeys.ACTION1.getKeyModifier().isActive(KeyConflictContext.GUI)) {
            setBinding(1);
            return true;
        } else if (func.apply(ModKeys.ACTION2) && ModKeys.ACTION2.getKeyModifier().isActive(KeyConflictContext.GUI)) {
            setBinding(2);
            return true;
        } else if (func.apply(ModKeys.ACTION3) && ModKeys.ACTION3.getKeyModifier().isActive(KeyConflictContext.GUI)) {
            setBinding(3);
            return true;
        }
        return false;
    }

    /**
     * orders the given list after client preference
     */
    private ImmutableList<IAction<T>> getActionOrdered(List<IAction<T>> toSort) {
        if (ACTIONORDER.isEmpty()) ACTIONORDER.addAll(RegUtil.values(ModRegistries.ACTIONS));
        @SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
        List<IAction<T>> list = (List<IAction<T>>) (Object)ACTIONORDER.stream().filter(toSort::contains).collect(Collectors.toList());
        toSort.removeAll(list);
        list.addAll(toSort);
        return ImmutableList.copyOf(list);
    }

    private void setBinding(int id) {
        IAction<T> action = elements.get(getSelectedElement());
        FactionPlayerHandler.get(minecraft.player).setBoundAction(id, action, false, true);
        VampirismMod.dispatcher.sendToServer(new ServerboundActionBindingPacket(id, action));
        if (!editActions) {
            GLFW.glfwSetCursorPos(this.minecraft.getWindow().getWindow(), this.minecraft.getWindow().getScreenWidth() / 2f, this.minecraft.getWindow().getScreenHeight() / 2f);
            onClose();
        }
    }

    /**
     * switches the position of the given actions
     */
    private void switchActions(IAction<T> first, IAction<T> second) {
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
        elements.addAll(getActionOrdered(actionHandler.getUnlockedActions().stream().filter(a -> a.showInSelectAction(minecraft.player)).collect(Collectors.toList())));
        //noinspection unchecked
        elements.add((IAction<T>) fakeAction);
    }
}
