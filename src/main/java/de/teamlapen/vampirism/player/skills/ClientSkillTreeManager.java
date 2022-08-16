package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.network.ClientboundSkillTreePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSkillTreeManager {
    private final SkillTree skillTree = new SkillTree();

    public SkillTree getSkillTree() {
        return skillTree;
    }

    public void init() {
        skillTree.initRootSkills();
    }

    public void loadUpdate(ClientboundSkillTreePacket msg) {
        skillTree.loadNodes(msg.nodes());
        skillTree.updateRenderInfo();
    }
}
