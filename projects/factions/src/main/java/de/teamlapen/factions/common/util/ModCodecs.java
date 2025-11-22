package de.teamlapen.factions.common.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ModCodecs {

    public static Codec<AABB> AABB = Codec.DOUBLE.listOf(6,6).xmap(x -> new AABB(x.get(0),x.get(1),x.get(2),x.get(3),x.get(4),x.get(5)), x -> List.of(x.minX,x.minY,x.minZ,x.maxX,x.maxY,x.maxZ));

    @SuppressWarnings({"RedundantCast", "unchecked"})
    public static Codec<Holder<? extends IPlayableFaction<?>>> playableFaction() {
        return ModRegistries.FACTIONS.holderByNameCodec().xmap(s -> (Holder<? extends IPlayableFaction<?>>) (Object) s, s -> (Holder<IFaction<?>>) (Object) s);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public static Codec<HolderSet<? extends IPlayableFaction<?>>> playableFactionSet() {
        return RegistryCodecs.homogeneousList(FactionRegistries.Keys.FACTION)
                .flatXmap(x -> DataResult.success((HolderSet<? extends IPlayableFaction<?>>) (Object) x), x -> DataResult.success((HolderSet<IFaction<?>>) (Object)x));
    }

    @SuppressWarnings("unchecked")
    public static Codec<Holder<? extends IFaction<?>>> faction() {
        return (Codec<Holder<? extends IFaction<?>>>) (Object) ModRegistries.FACTIONS.holderByNameCodec();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ISkillPlayer<@NotNull T>> Codec<Holder<ISkill<@NotNull T>>> skills() {
        return ModRegistries.SKILLS.holderByNameCodec().comapFlatMap(skill -> {
            try {
                return DataResult.success((Holder<ISkill<@NotNull T>>) (Object) skill);
            } catch (Exception e) {
                return DataResult.error(() -> "Could not find skill " + skill);
            }
        }, (Holder<ISkill<@NotNull T>> skill) -> (Holder<ISkill<?>>) (Object) skill);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> Codec<EntityType<T>> entityCodec() {
        return BuiltInRegistries.ENTITY_TYPE.byNameCodec().comapFlatMap(entityType -> {
            try {
                return DataResult.success((EntityType<T>) entityType);
            } catch (Exception e) {
                return DataResult.error(() -> "Could not find entity type " + entityType);
            }
        }, (EntityType<T> x) -> x);
    }

}
