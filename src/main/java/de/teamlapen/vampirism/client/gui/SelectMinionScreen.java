package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.ScrollableListButton;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.RequestMinionSelectPacket;
import de.teamlapen.vampirism.network.SelectMinionTaskPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;


@OnlyIn(Dist.CLIENT)
public class SelectMinionScreen extends Screen {
    private final Integer[] minionIds;
    private final String[] minionNames;
    private final RequestMinionSelectPacket.Action action;

    public SelectMinionScreen(RequestMinionSelectPacket.Action a, List<Pair<Integer, ITextComponent>> minions) {
        super(new StringTextComponent(""));
        this.action = a;
        this.minionIds = minions.stream().map(Pair::getLeft).toArray(Integer[]::new);
        this.minionNames = minions.stream().map(Pair::getRight).map(ITextComponent::getFormattedText).toArray(String[]::new);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    protected void init() {
        super.init();

        int w = 100;
        int maxH = 5;
        this.addButton(new ScrollableListButton((this.width - w) / 2, (this.height - maxH *20) / 2, w, Math.min(5,minionNames.length), minionNames.length, minionNames, "", this::onMinionSelected, false));
    }

    private void onMinionSelected(int id) {
        int selectedMinion = minionIds[id];
        if (action == RequestMinionSelectPacket.Action.CALL) {
            VampirismMod.dispatcher.sendToServer(new SelectMinionTaskPacket(selectedMinion, SelectMinionTaskPacket.RECALL));
        }
        this.onClose();
    }
}