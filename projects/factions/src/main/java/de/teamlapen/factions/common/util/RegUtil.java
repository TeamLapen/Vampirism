package de.teamlapen.factions.common.util;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.factions.actions.ILastingAction;
import de.teamlapen.factions.api.factions.refinements.IRefinement;
import de.teamlapen.factions.api.factions.refinements.IRefinementSet;
import de.teamlapen.factions.api.factions.skills.ISkill;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.factions.skills.ISkillTree;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class RegUtil {

    public static ResourceLocation id(@NotNull IAction<?> action) {
        return ModRegistries.ACTIONS.getKey(action);
    }

    public static ResourceLocation id(@NotNull EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    public static ResourceLocation id(@NotNull Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation id(@NotNull IFaction<?> faction) {
        return ModRegistries.FACTIONS.getKey(faction);
    }

    public static ResourceLocation id(@NotNull ISkill<?> skill) {
        return ModRegistries.SKILLS.getKey(skill);
    }

    public static ResourceLocation id(@NotNull IMinionTask<?, ?> minionTask) {
        return ModRegistries.MINION_TASKS.getKey(minionTask);
    }

    public static ResourceLocation id(@NotNull IRefinement refinement) {
        return ModRegistries.REFINEMENTS.getKey(refinement);
    }

    public static ResourceLocation id(@NotNull IRefinementSet refinementSet) {
        return ModRegistries.REFINEMENT_SETS.getKey(refinementSet);
    }

    public static ResourceLocation id(@NotNull Level level, ISkillTree tree) {
        return level.registryAccess().lookupOrThrow(FactionRegistries.Keys.SKILL_TREE).getKey(tree);
    }

    public static Optional<ResourceKey<IAction<?>>> key(@NotNull IAction<?> action) {
        return ModRegistries.ACTIONS.getResourceKey(action);
    }

    public static boolean has(@NotNull IAction<?> action) {
        return ModRegistries.ACTIONS.containsValue(action);
    }

    public static boolean has(@NotNull ISkill<?> skill) {
        return ModRegistries.SKILLS.containsValue(skill);
    }

    public static boolean has(@NotNull IMinionTask<?, ?> minionTask) {
        return ModRegistries.MINION_TASKS.containsValue(minionTask);
    }

    public static boolean has(@NotNull IRefinement refinement) {
        return ModRegistries.REFINEMENTS.containsValue(refinement);
    }

    public static boolean has(@NotNull IRefinementSet refinementSet) {
        return ModRegistries.REFINEMENT_SETS.containsValue(refinementSet);
    }

    public static IAction<?> getAction(@NotNull ResourceLocation id) {
        return ModRegistries.ACTIONS.getValue(id);
    }

    public static ISkill<?> getSkill(@NotNull ResourceLocation id) {
        return ModRegistries.SKILLS.getValue(id);
    }

    public static IMinionTask<?, ?> getMinionTask(@NotNull ResourceLocation id) {
        return ModRegistries.MINION_TASKS.getValue(id);
    }

    public static IRefinement getRefinement(@NotNull ResourceLocation id) {
        return ModRegistries.REFINEMENTS.getValue(id);
    }

    public static IRefinementSet getRefinementSet(@NotNull ResourceLocation id) {
        return ModRegistries.REFINEMENT_SETS.getValue(id);
    }

    public static Holder<ISkillTree> getSkillTree(Level level, String asString) {
        return level.registryAccess().lookupOrThrow(FactionRegistries.Keys.SKILL_TREE).getOrThrow(ResourceKey.create(FactionRegistries.Keys.SKILL_TREE, ResourceLocation.parse(asString)));
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> Holder<IAction<T>> holder(IAction<T> action) {
        return (Holder<IAction<T>>) (Object) ModRegistries.ACTIONS.wrapAsHolder(action);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> Holder<ILastingAction<T>> holder(ILastingAction<T> action) {
        return (Holder<ILastingAction<T>>) (Object) ModRegistries.ACTIONS.wrapAsHolder(action);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFaction<?>> Holder<T> holder(T faction) {
        return (Holder<T>) ModRegistries.FACTIONS.wrapAsHolder(faction);
    }

    public static <T> @NotNull Collection<T> values(@NotNull Registry<T> registry) {
        return registry.stream().toList();
    }
}
