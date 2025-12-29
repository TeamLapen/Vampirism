package de.teamlapen.vampirism.api.util;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.factions.actions.ILastingAction;
import de.teamlapen.factions.api.factions.refinements.IRefinement;
import de.teamlapen.factions.api.factions.skills.ISkill;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RegUtil {

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> Holder<ILastingAction<T>> holder(ILastingAction<T> action) {
        return (Holder<ILastingAction<T>>) (Object) FactionRegistries.ACTION.get().wrapAsHolder(action);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IAction<?>> Holder<T> holder(T action) {
        return (Holder<T>) FactionRegistries.ACTION.get().wrapAsHolder(action);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ISkill<?>> Holder<T> holder(T skill) {
        return (Holder<T>) FactionRegistries.SKILL.get().wrapAsHolder(skill);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ISkill<?>> Holder<T> holderAnon(T skill) {
        return (Holder<T>) FactionRegistries.SKILL.get().wrapAsHolder(skill);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IRefinement> Holder<T> holder(T refinement) {
        return (Holder<T>) FactionRegistries.REFINEMENT.get().wrapAsHolder(refinement);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFaction<?>> Holder<T> holder(T faction) {
        return (Holder<T>) FactionRegistries.FACTION.get().wrapAsHolder(faction);
    }
}
