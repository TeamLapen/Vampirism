package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends ISkillPlayer> extends VampirismSkill<T> {
    private static final ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID, "textures/gui/actions.png");
    private final IAction<T> action;
    private final String id;

    public ActionSkill(IAction<T> action, String id) {
        this.action = action;
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public ResourceLocation getIconLoc() {
        return action.getIconLoc() == null ? defaultIcons : action.getIconLoc();
    }

    @Override
    public String getLocalizedDescription() {
        return UtilLib.translateToLocal("text.vampirism.skill.unlocks_action");
    }

    @Override
    public int getMinU() {
        return action.getMinU();
    }

    @Override
    public int getMinV() {
        return action.getMinV();
    }

    @Override
    public String getUnlocalizedName() {
        return action.getUnlocalizedName();
    }

    @Override
    protected void getActions(Collection<IAction<T>> list) {
        list.add(action);
    }
}
