package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a skill tree such as a skill tree for basic leveling as well as a lord level skill tree. Each faction needs to have their own skill tree.
 */
public interface ISkillTree {

    /**
     * The faction for which the skill tree is
     */
    @NotNull
    Holder<? extends IPlayableFaction<?>> faction();

    /**
     * The unlocking predicate that will be checked against the player to see if the skill tree is unlocked <br>
     * By default this check is only triggered when the faction level / lord level changes (This includes joining the level)
     */
    @NotNull
    EntityPredicate unlockPredicate();

    /**
     * The title of the skill tree shown in the skill screen
     */
    @NotNull
    Component name();

    /**
     * The icon of the skill tree shown in the skill screen
     */
    @NotNull
    ItemStack display();

}
