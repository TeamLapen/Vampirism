package de.teamlapen.factions.common.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.skills.IActionSkill;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.factions.api.util.REFERENCE;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.ClearCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SkillCallbacks implements AddCallback<ISkill<?>>, ClearCallback<ISkill<?>> {


    private static final Map<Holder<? extends IAction<?>>, ISkill<?>> ACTION_TO_SKILL_MAP = new HashMap<>();
    private static final Map<Holder<? extends IAction<?>>, ISkill<?>> ACTION_TO_SKILL_MAP_READ_ONLY = Collections.unmodifiableMap(ACTION_TO_SKILL_MAP);

    @Override
    public void onAdd(@NotNull Registry<ISkill<?>> registry, int id, @NotNull ResourceKey<ISkill<?>> key, @NotNull ISkill<?> value) {
        if (value instanceof IActionSkill<?> actionSkill) {
            ACTION_TO_SKILL_MAP.put(actionSkill.actionHolder(), actionSkill);
        } else if (value instanceof DefaultSkill<?> defaultSkill) {
            defaultSkill.getActionHolder().forEach(action -> ACTION_TO_SKILL_MAP.put(action, new EmptyActionSkill<>(action)));
        }
    }

    @Override
    public void onClear(@NotNull Registry<ISkill<?>> registry, boolean full) {
        if (full) {
            ACTION_TO_SKILL_MAP.clear();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> Map<IAction<T>, ISkill<T>> getActionSkillMap() {
        return (Map<IAction<T>, ISkill<T>>) (Object) ACTION_TO_SKILL_MAP_READ_ONLY;
    }

    public record EmptyActionSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>>(Holder<? extends IAction<T>> actionHolder) implements IActionSkill<T> {
        private static final TagKey<ISkillTree> key = TagKey.create(FactionRegistries.Keys.SKILL_TREE, ResourceLocation.fromNamespaceAndPath(REFERENCE.MOD_ID, "empty"));

        @Override
        public @Nullable Component getDescription() {
            return null;
        }

        @Override
        public TagKey<? extends IFaction<?>> factions() {
            return this.actionHolder.value().factions();
        }

        @Override
        public String getDescriptionId() {
            return this.actionHolder.value().getDescriptionId();
        }

        @Override
        public void onDisable(T player) {

        }

        @Override
        public void onEnable(T player) {

        }

        @Override
        public Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> allowedSkillTrees() {
            return Either.right(key);
        }
    }
}
