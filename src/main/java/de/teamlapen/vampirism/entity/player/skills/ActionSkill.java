package de.teamlapen.vampirism.entity.player.skills;

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
import java.util.function.Supplier;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> {
    private final Supplier<? extends IAction<T>> action;
    private final ISkillType type;

    public ActionSkill(Supplier<? extends IAction<T>> action) {
        this(action, false);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, boolean customDescription) {
        this(action, SkillType.LEVEL, false);
    }

    /**
     * @param action            The corresponding action
     * @param customDescription If false a generic "unlocks action" string is used
     */
    public ActionSkill(Supplier<? extends IAction<T>> action, ISkillType type, boolean customDescription) {
        this.action = action;
        this.type = type;
        if (customDescription) {
            this.setHasDefaultDescription();
        } else {
            this.setDescription(() -> Component.translatable("text.vampirism.skill.unlocks_action"));

        }
    }

    public ResourceLocation getActionID() {
        return RegUtil.id(action.get());
    }

    @NotNull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return action.get().getFaction();
    }

    @Override
    public Component getName() {
        return action.get().getName();
    }

    @Deprecated
    @Override
    public String getTranslationKey() {
        return action.get().getTranslationKey();
    }

    @Override
    protected void getActions(@NotNull Collection<IAction<T>> list) {
        list.add(action.get());
    }

    @Override
    public ISkillType getType() {
        return this.type;
    }
}
