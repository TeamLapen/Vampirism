package de.teamlapen.vampirism.common.world.entity.player.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.factions.skills.IActionSkill;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.factions.skills.ISkillTree;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends VampirismSkill<T> implements IActionSkill<T> {
    private final Holder<? extends IAction<T>> action;

    public ActionSkill(Holder<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree) {
        this(action, skillTree, 2);
    }

    public ActionSkill(Holder<? extends IAction<T>> action, TagKey<ISkillTree> skillTree) {
        this(action, skillTree, 2);
    }

    public ActionSkill(Holder<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree, int skillPointCost) {
        this(action, skillTree, skillPointCost, false);
    }

    public ActionSkill(Holder<? extends IAction<T>> action, TagKey<ISkillTree> skillTree, int skillPointCost) {
        this(action, skillTree, skillPointCost, false);
    }

    public ActionSkill(Holder<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree, boolean customDescription) {
        this(action, skillTree, 2, customDescription);
    }

    public ActionSkill(Holder<? extends IAction<T>> action, TagKey<ISkillTree> skillTree, boolean customDescription) {
        this(action, skillTree, 2, customDescription);
    }

    /**
     * @param action            The corresponding action
     * @param customDescription If false a generic "unlocks action" string is used
     */
    public ActionSkill(Holder<? extends IAction<T>> action, ResourceKey<ISkillTree> skillTree, int skillPointCost, boolean customDescription) {
        this(action, Either.left(skillTree), skillPointCost, customDescription);
    }

    public ActionSkill(Holder<? extends IAction<T>> action, TagKey<ISkillTree> skillTree, int skillPointCost, boolean customDescription) {
        this(action, Either.right(skillTree), skillPointCost, customDescription);
    }

    public ActionSkill(Holder<? extends IAction<T>> action, Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> skillTree, int skillPointCost, boolean customDescription) {
        super(skillTree, skillPointCost, customDescription);
        this.action = action;
        if (!customDescription) {
            this.setDescription(() -> Component.translatable("gui.factions.skill.unlocks_action"));
        }
    }

    public Identifier getActionID() {
        return this.action.unwrapKey().map(ResourceKey::identifier).orElseThrow();
    }

    @Override
    public IAction<T> action() {
        return this.action.value();
    }

    @Override
    public Holder<? extends IAction<T>> actionHolder() {
        return this.action;
    }

    @Override
    public TagKey<? extends IFaction<?>> factions() {
        return this.action.value().factions();
    }

    @Override
    public MutableComponent getName() {
        return this.action.value().getName();
    }

    @Override
    public String getDescriptionId() {
        return this.action.value().getDescriptionId();
    }

    @Override
    protected void getActions(@NotNull Collection<IAction<T>> list) {
        list.add(this.action.value());
    }

    @Override
    protected void collectActions(@NotNull Collection<Holder<? extends IAction<T>>> list) {
        list.add(this.action);
    }
}
