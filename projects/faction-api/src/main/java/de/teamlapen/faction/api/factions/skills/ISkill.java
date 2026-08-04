package de.teamlapen.faction.api.factions.skills;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.SafeCast;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.List;

/**
 * Skill that tha unlocks abilities for a player.
 */
public interface ISkill<T extends ISkillPlayer<T>> extends ISkillLike<T> {

    Codec<Holder<? extends ISkill<?>>> CODEC = SafeCast.cast(FactionRegistries.SKILL.get().holderByNameCodec());

    /**
     * The description for this skill or null if there is no description.
     */
    @Nullable
    Component getDescription();

    /**
     * A skill can be either
     *
     * @return The faction this skill belongs to
     */
    TagKey<? extends IFaction<?>> factions();

    Component getName();

    String getDescriptionId();

    /**
     * Called when the skill is disabled (Server: on load from nbt/on disabling all skills e.g. via the gui. Client: on update from server)
     *
     * @param player Must be of the type that {@link ISkill#factions()} belongs to
     */
    void onDisable(T player);

    /**
     * Called when the skill is enabled (Server: on load from nbt/on enabling it via the gui. Client: on update from server)
     *
     * @param player Must be of the type that {@link ISkill#factions()} belongs to
     */
    void onEnable(T player);

    /**
     * Unlocking skills costs a certain amount of skill points.
     *
     * @return The cost of the skill
     */
    int getSkillPointCost();

    /**
     * In case this is an action skill, this will return the one skill
     */
    @Nullable
    Holder<? extends IAction<T>> getAction();

    /**
     * This contains all skills that will be enabled by this skill
     */
    List<Holder<? extends IAction<T>>> getActions();


    /**
     * Skill can only be added to skill trees defined by the return value.
     *
     * @return A key of the allowed skill tree or a tag of skill trees
     */
    SkillTreeRequirement allowedSkillTrees();

    @Override
    default ISkill<T> asSkill() {
        return this;
    }
}
