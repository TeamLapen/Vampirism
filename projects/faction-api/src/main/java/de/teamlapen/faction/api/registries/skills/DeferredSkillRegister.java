package de.teamlapen.faction.api.registries.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.FactionProperties;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.skills.SkillProperties;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DeferredSkillRegister extends DeferredRegister<ISkill<?>> {

    protected DeferredSkillRegister(String namespace) {
        super(FactionRegistries.Keys.SKILL, namespace);
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> DeferredSkillRegister create(String namespace) {
        return new DeferredSkillRegister(namespace);
    }

    @Deprecated
    @Override
    public <I extends ISkill<?>> DeferredHolder<ISkill<?>, I> register(String name, Supplier<? extends I> sup) {
        return super.register(name, sup);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <T extends IFactionPlayer<T> & ISkillPlayer<T>, I extends ISkill<T>> DeferredSkill<T, I> registerSkill(String name, Function<SkillProperties<T>,? extends I> sup) {
        return (DeferredSkill<T, I>) (Object) super.register(name, key -> sup.apply(new SkillProperties<T>().setId(ResourceKey.create(FactionRegistries.Keys.SKILL, key))));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <I extends ISkill<?>> DeferredSkill<?, I> registerGenericSkill(String name, Function<SkillProperties<?>,? extends I> sup) {
        return (DeferredSkill<?, I>) this.registerSkill(name, (Function) sup);
    }

    @Override
    @Deprecated
    public <I extends ISkill<?>> DeferredHolder<ISkill<?>, I> register(String name, Function<Identifier, ? extends I> func) {
        return super.register(name, func);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <I extends ISkill<?>> DeferredHolder<ISkill<?>, I> createHolder(ResourceKey<? extends Registry<ISkill<?>>> registryKey, Identifier key) {
        return (DeferredHolder<ISkill<?>, I>) (Object) DeferredSkill.createSkill(key);
    }
}
