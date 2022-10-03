package de.teamlapen.vampirism.proxy;


import de.teamlapen.vampirism.entity.player.skills.SkillTree;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeManager;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

/**
 * Abstract proxy base for both client and server.
 * Try to keep this quite empty and move larger code parts into dedicated classes.
 */
public abstract class CommonProxy implements IProxy {

    @Override
    public SkillTree getSkillTree(boolean client) {
        return SkillTreeManager.getInstance().getSkillTree();
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
    }
}
