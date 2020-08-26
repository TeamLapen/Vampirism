package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.GuiPieMenu;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.network.SelectMinionTaskPacket;
import de.teamlapen.vampirism.network.SelectMinionTaskPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class SelectMinionTaskScreen extends GuiPieMenu<SelectMinionTaskScreen.Entry> {


    public SelectMinionTaskScreen() {
        super(Color.gray, new TranslationTextComponent("text.vampirism.minion.give_order"));
    }


    @Override
    protected ResourceLocation getIconLoc(Entry item) {
        return item.getIconLoc();
    }

    @Override
    protected KeyBinding getMenuKeyBinding() {
        return ModKeys.getKeyBinding(ModKeys.KEY.MINION);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == GLFW.GLFW_KEY_SPACE) {
            if (Minecraft.getInstance().player.isAlive()) {
                IPlayableFaction faction = FactionPlayerHandler.get(Minecraft.getInstance().player).getCurrentFaction();
                if (faction != null) {
                    Minecraft.getInstance().displayGuiScreen(new SelectActionScreen(faction.getColor(), false));
                }
            }
        }
        return super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (ModKeys.getKeyBinding(ModKeys.KEY.MINION).matchesKey(key, scancode) || ModKeys.getKeyBinding(ModKeys.KEY.ACTION).matchesKey(key, scancode)) {
            onClose();
            if (getSelectedElement() >= 0) {
                this.onElementSelected(elements.get(getSelectedElement()));
            }
        }
        return false;
    }

    @Override
    protected ITextComponent getName(Entry item) {
        return item.getText();
    }


    @Override
    protected void onElementSelected(Entry id) {
        id.onSelected(this);
    }

    @Override
    protected void onGuiInit() {
        this.elements.clear();
        FactionPlayerHandler.getOpt(minecraft.player).ifPresent(fp -> elements.addAll(PlayerMinionController.getAvailableTasks(fp).stream().map(Entry::new).collect(Collectors.toList())));
        this.elements.add(new Entry(new TranslationTextComponent("action.vampirism.cancel"), new ResourceLocation(REFERENCE.MODID, "textures/actions/cancel.png"), (GuiPieMenu::onClose)));
        this.elements.add(new Entry(new TranslationTextComponent("text.vampirism.minion.call_single"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/recall_single.png"), (SelectMinionTaskScreen::callSingle)));
        this.elements.add(new Entry(new TranslationTextComponent("text.vampirism.minion.call_all"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/recall.png"), (SelectMinionTaskScreen::callAll)));
        this.elements.add(new Entry(new TranslationTextComponent("text.vampirism.minion.respawn"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/respawn.png"), (SelectMinionTaskScreen::callRespawn)));
    }

    private void callAll() {
        VampirismMod.dispatcher.sendToServer(new SelectMinionTaskPacket(-1, SelectMinionTaskPacket.RECALL));

    }

    private void callRespawn() {
        VampirismMod.dispatcher.sendToServer(new SelectMinionTaskPacket(-1, SelectMinionTaskPacket.RESPAWN));

    }

    private void callSingle() {

    }

    private void sendTask(IMinionTask<?,?> task) {
        VampirismMod.dispatcher.sendToServer(new SelectMinionTaskPacket(-1, task.getRegistryName()));
    }

    public static class Entry {

        private final ITextComponent text;
        private final ResourceLocation loc;
        private final Consumer<SelectMinionTaskScreen> onSelected;

        public Entry(IMinionTask<?,?> task) {
            this(task.getName(), new ResourceLocation(task.getRegistryName().getNamespace(), "textures/tasks/" + task.getRegistryName().getPath() + ".png"), (screen -> screen.sendTask(task)));
        }

        public Entry(ITextComponent text, ResourceLocation icon, Consumer<SelectMinionTaskScreen> onSelected) {
            this.text = text;
            this.loc = icon;
            this.onSelected = onSelected;
        }

        public ResourceLocation getIconLoc() {
            return loc;
        }

        public ITextComponent getText() {
            return text;
        }

        public void onSelected(SelectMinionTaskScreen screen) {
            this.onSelected.accept(screen);
        }

    }

}