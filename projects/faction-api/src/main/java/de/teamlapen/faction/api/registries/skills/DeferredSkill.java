package de.teamlapen.faction.api.registries.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class DeferredSkill<Z extends ISkillPlayer<Z>, L extends ISkill<Z>> extends DeferredHolder<L, L> {

    protected DeferredSkill(ResourceKey<L> key) {
        super(key);
    }

    public static <Z extends ISkillPlayer<Z>, L extends ISkill<Z>> DeferredSkill<Z, L> createSkill(ResourceKey<L> key) {
        return new DeferredSkill<>(key);
    }

    @SuppressWarnings("unchecked")
    public static <Z extends ISkillPlayer<Z>, L extends ISkill<Z>> DeferredSkill<Z, L> createSkill(Identifier key) {
        return createSkill((ResourceKey<L>) ResourceKey.create(FactionRegistries.Keys.SKILL, key));
    }

    @SuppressWarnings("unchecked")
    public ResourceKey<ISkill<?>> getRawKey() {
        return (ResourceKey<ISkill<?>>) super.getKey();
    }
}
