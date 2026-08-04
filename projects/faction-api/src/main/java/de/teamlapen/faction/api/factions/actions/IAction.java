package de.teamlapen.faction.api.factions.actions;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillLike;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Interface for player actions
 */
public interface IAction<T extends ISkillPlayer<T>> extends ISkillLike<T> {

    Codec<Holder<IAction<?>>> CODEC = FactionRegistries.ACTION.get().holderByNameCodec();

    /**
     * Checks if the player can use this action
     *
     * @param player Must be an instance of class that belongs to {@link IAction#factions()}
     */
    IActionResult canUse(T player);

    /**
     * @return Cooldown time in ticks until the action can be used again
     */
    int getCooldown(T player);

    /**
     * allowed factions to use this action
     */
    TagKey<? extends IFaction<?>> factions();

    default MutableComponent getName() {
        return Component.translatable(getDescriptionId());
    }

    String getDescriptionId();

    /**
     * Called when the action is activated. Only called server side
     *
     * @param player  Must be an instance of class that belongs to {@link IAction#factions()}
     * @param context Holds Block/Entity the player was looking at when activating if any
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    IActionResult onActivated(T player, ActivationContext context);

    /**
     * Provide some context of the activation instant sent from the client
     */
    interface ActivationContext {
        /**
         * @return The block the player is looking at, if any
         */
        Optional<BlockPos> targetBlock();

        /**
         * @return The creature the player is looking at, if any
         */
        Optional<Entity> targetEntity();
    }
}
