package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Optional;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> {
    private final IAction<T> action;
    private final ISkillType type;

    public ActionSkill(IAction<T> action) {
        this(action, false);
    }

    public ActionSkill(IAction<T> action, boolean customDescription) {
        this(action, SkillType.LEVEL, false);
    }

    /**
     * @param action            The corresponding action
     * @param customDescription If false a generic "unlocks action" string is used
     */
    public ActionSkill(IAction<T> action, ISkillType type, boolean customDescription) {
        this.action = action;
        this.type = type;
        if (customDescription) {
            this.setHasDefaultDescription();
        } else {
            this.setDescription(() -> Component.translatable("text.vampirism.skill.unlocks_action"));

        }
    }

    public ResourceLocation getActionID() {
        return RegUtil.id(action);
    }

    @NotNull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
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
    protected void getActions(@NotNull Collection<IAction<T>> list) {
        list.add(action);
    }

    @Override
    public ISkillType getType() {
        return this.type;
    }
}
