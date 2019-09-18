package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends IFactionPlayer> extends VampirismSkill<T> {
    private final IAction action;
    private final boolean customDescription;

    @Deprecated
    public ActionSkill(String id, IAction action) {
        this(new ResourceLocation("vampirism", id), action, false);

    }

    @Deprecated
    public ActionSkill(String id, IAction action, boolean customDescription) {
        this(new ResourceLocation("vampirism", id), action, customDescription);
    }

    public ActionSkill(ResourceLocation id, IAction action) {
        this(id, action, false);
    }

    /**
     * @param id                Registry id
     * @param action            The corresponding action
     * @param customDescription If false a generic "unlocks action" string is used
     */
    public ActionSkill(ResourceLocation id, IAction action, boolean customDescription) {
        super(action.getFaction());
        this.action = action;
        this.setRegistryName(id);
        this.customDescription = customDescription;
    }

    public ResourceLocation getActionID() {
        return action.getRegistryName();
    }

    @Override
    public String getTranslationKey() {
        return action.getTranslationKey();
    }

    @Override
    protected void getActions(Collection<IAction> list) {
        list.add(action);
    }
}
