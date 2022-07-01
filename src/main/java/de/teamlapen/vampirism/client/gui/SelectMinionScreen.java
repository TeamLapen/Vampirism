package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableArrayTextComponentList;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.CSelectMinionTaskPacket;
import de.teamlapen.vampirism.network.SRequestMinionSelectPacket;
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
    private final ITextComponent[] minionNames;
    private final SRequestMinionSelectPacket.Action action;
    private ScrollableArrayTextComponentList list;

    public SelectMinionScreen(SRequestMinionSelectPacket.Action a, List<Pair<Integer, ITextComponent>> minions) {
        super(new StringTextComponent(""));
        this.action = a;
        this.minionIds = minions.stream().map(Pair::getLeft).toArray(Integer[]::new);
        this.minionNames = minions.stream().map(Pair::getRight).toArray(ITextComponent[]::new);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return true;
    }

    @Override
    public void render(MatrixStack mStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground(mStack);
        super.render(mStack, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    protected void init() {
        super.init();

        int w = 100;
        int maxH = 5;
        this.list = this.addButton(new ScrollableArrayTextComponentList((this.width - w) / 2, (this.height - maxH * 20) / 2, w, Math.min(maxH * 20, 20 * minionNames.length), 20, () -> this.minionNames, SelectMinionScreen.this::onMinionSelected));
    }

    private void onMinionSelected(int id) {
        int selectedMinion = minionIds[id];
        if (action == SRequestMinionSelectPacket.Action.CALL) {
            VampirismMod.dispatcher.sendToServer(new CSelectMinionTaskPacket(selectedMinion, CSelectMinionTaskPacket.RECALL));
        }
        this.onClose();
    }
}