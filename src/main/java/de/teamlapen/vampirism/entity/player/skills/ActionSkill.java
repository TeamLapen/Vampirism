package de.teamlapen.vampirism.entity.player.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.IActionSkill;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> implements IActionSkill<T> {
    private final Supplier<? extends IAction<T>> action;

    public ActionSkill(Supplier<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree) {
        this(action, skillTree, 2);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, TagKey<ISkillTree> skillTree) {
        this(action, skillTree, 2);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree, int skillPointCost) {
        this(action, skillTree, skillPointCost, false);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, TagKey<ISkillTree> skillTree, int skillPointCost) {
        this(action, skillTree, skillPointCost, false);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree, boolean customDescription) {
        this(action, skillTree,2, customDescription);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, TagKey<ISkillTree> skillTree, boolean customDescription) {
        this(action, skillTree,2, customDescription);
    }

    /**
     * @param action            The corresponding action
     * @param customDescription If false a generic "unlocks action" string is used
     */
    public ActionSkill(Supplier<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree, int skillPointCost, boolean customDescription) {
        this(action, Either.left(skillTree), skillPointCost, customDescription);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, TagKey<ISkillTree> skillTree, int skillPointCost, boolean customDescription) {
        this(action, Either.right(skillTree), skillPointCost, customDescription);
    }

    public ActionSkill(Supplier<? extends IAction<T>> action, Either<ResourceKey<ISkillTree>,TagKey<ISkillTree>> skillTree, int skillPointCost, boolean customDescription) {
        super(skillTree, skillPointCost, customDescription);
        this.action = action;
        if (!customDescription) {
            this.setDescription(() -> Component.translatable("text.vampirism.skill.unlocks_action"));
        }
    }

    public ResourceLocation getActionID() {
        return RegUtil.id(action.get());
    }

    public IAction<T> action() {
        return action.get();
    }

    @NotNull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return action.get().getFaction();
    }

    @Override
    public MutableComponent getName() {
        return action.get().getName();
    }

    @Override
    public String getTranslationKey() {
        return action.get().getTranslationKey();
    }

    @Override
    protected void getActions(@NotNull Collection<IAction<T>> list) {
        list.add(action.get());
    }

}
