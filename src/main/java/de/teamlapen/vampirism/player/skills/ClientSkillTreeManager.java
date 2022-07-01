package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.network.SSkillTreePacket;
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

    public void loadUpdate(SSkillTreePacket msg) {
        skillTree.loadNodes(msg.getNodes());
        skillTree.updateRenderInfo();
    }
}
