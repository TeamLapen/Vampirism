package de.teamlapen.vampirism.proxy;


import de.teamlapen.vampirism.player.skills.SkillTree;
import de.teamlapen.vampirism.player.skills.SkillTreeManager;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

/**
 * Abstract proxy base for both client and server.
 * Try to keep this quite empty and move larger code parts into dedicated classes.
 *
 * @author Maxanier
 */
public abstract class CommonProxy implements IProxy {

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {
    }

    @Override
    public SkillTree getSkillTree(boolean client) {
        return SkillTreeManager.getInstance().getSkillTree();
    }
}
