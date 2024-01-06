package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.client.gui.components.SimpleList;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ClientboundRequestMinionSelectPacket;
import de.teamlapen.vampirism.network.ServerboundSelectMinionTaskPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SelectMinionScreen extends Screen {
    private final Integer @NotNull [] minionIds;
    private final @NotNull List<Component> minionNames;
    private final ClientboundRequestMinionSelectPacket.Action action;

    public SelectMinionScreen(ClientboundRequestMinionSelectPacket.Action a, @NotNull List<com.mojang.datafixers.util.Pair<Integer, Component>> minions) {
        super(Component.literal(""));
        this.action = a;
        this.minionIds = minions.stream().map(com.mojang.datafixers.util.Pair::getFirst).toArray(Integer[]::new);
        this.minionNames = minions.stream().map(Pair::getSecond).toList();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int p_render_1_, int p_render_2_, float p_render_3_) {
        if (this.minecraft != null) {
            renderBackground(graphics, p_render_1_, p_render_2_, p_render_3_);
        }
        super.render(graphics, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    protected void init() {
        int w = 100;
        int maxH = 5;
        this.addRenderableWidget(SimpleList.builder((this.width - w) / 2, (this.height - maxH * 18 + 2) / 2, w, Math.min(maxH * 18, 18 * minionNames.size()) + 2).componentsWithClick(this.minionNames, SelectMinionScreen.this::onMinionSelected).build());
    }

    private void onMinionSelected(int id) {
        int selectedMinion = minionIds[id];
        if (action == ClientboundRequestMinionSelectPacket.Action.CALL) {
            VampirismMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(selectedMinion, ServerboundSelectMinionTaskPacket.RECALL));
        }
        this.onClose();
    }
}