package de.teamlapen.faction.api.factions.skills;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Represents a skill tree such as a skill tree for basic leveling as well as a lord level skill tree. Each faction needs to have their own skill tree.
 */
public interface ISkillTree {

    Codec<Holder<ISkillTree>> CODEC = Codec.lazyInitialized(() -> RegistryFixedCodec.create(FactionRegistries.Keys.SKILL_TREE));
    /**
     * The faction for which the skill tree is
     */
    Holder<? extends IPlayableFaction<?>> faction();

    /**
     * The unlocking predicate that will be checked against the player to see if the skill tree is unlocked <br>
     * By default, this check is only triggered when the faction level / lord level changes (This includes joining the level)
     */
    EntityPredicate unlockPredicate();

    /**
     * The title of the skill tree shown in the skill screen
     */
    Component name();

    /**
     * The icon of the skill tree shown in the skill screen
     */
    ItemStack display();

    Optional<Identifier> background();

    TagKey<ISkillTree> skillPointTag();

}
