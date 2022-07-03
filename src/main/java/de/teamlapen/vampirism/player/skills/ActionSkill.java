package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> {
    private final IAction<T> action;

    public ActionSkill(IAction<T> action) {
        this(action, false);
    }

    /**
     * @param action            The corresponding action
     * @param customDescription If false a generic "unlocks action" string is used
     */
    public ActionSkill(IAction<T> action, boolean customDescription) {
        this.action = action;
        if (customDescription) {
            this.setHasDefaultDescription();
        } else {
            this.setDescription(() -> Component.translatable("text.vampirism.skill.unlocks_action"));

        }
    }

    public ResourceLocation getActionID() {
        return RegUtil.id(action);
    }

    @Nonnull
    @Override
    public IPlayableFaction<?> getFaction() {
        return action.getFaction();
    }

    @Override
    public Component getName() {
        return action.getName();
    }

    @Deprecated
    @Override
    public String getTranslationKey() {
        return action.getTranslationKey();
    }

    @Override
    protected void getActions(Collection<IAction<T>> list) {
        list.add(action);
    }
}
