package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface ISkillTree {

    @NotNull
    IPlayableFaction<?> faction();

    @NotNull
    EntityPredicate unlockPredicate();

    @NotNull
    Component name();

    @NotNull
    ItemStack display();

    interface INodeHolder {
        @NotNull
        Holder<ISkillNode> holder();

        @NotNull
        default ISkillNode value() {
            return holder().value();
        }

        @NotNull
        default List<ISkill<?>> skills() {
            return value().elements().stream().map(Holder::value).collect(Collectors.toList());
        }

        int count();

        @NotNull
        List<INodeHolder> children();

        boolean isRoot();
    }
}
