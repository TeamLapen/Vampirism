package de.teamlapen.vampirism.common.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.util.serialization.ModCodecs;
import de.teamlapen.vampirism.common.world.entity.ai.memory.HurtByEntities;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiSystem;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ModMemoryTypes {

    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> ACTION_ACTIVE = unit("action.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> ACTION_COOLDOWN = unit("action.cooldown");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<HurtByEntities>> HURT_BY_ENTITIES = MEMORY_MODULES.register("hurt_by_entities", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Set<UUID>>> ALLIES = MEMORY_MODULES.register("allies", () -> new MemoryModuleType<>(Optional.of(ModCodecs.set(UUIDUtil.CODEC))));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<AiSystem<?>>> AI_SYSTEM = MEMORY_MODULES.register("ai_system", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Long>> ACTION_ACTIVE_SINCE = longInt("action.active_since");

    //<editor-fold desc="Dracula">
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> DRACULA_PHASE_1 = unit("dracula.phase1");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> DRACULA_PHASE_2 = unit("dracula.phase2");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> DRACULA_PHASE_3 = unit("dracula.phase3");

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Long>> SURROUNDED_SINCE = longInt("dracula.surrounded_timer");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> SURROUNDED_COOLDOWN = unit("dracula.surrounded_cooldown");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<List<LivingEntity>>> NEAREST_ATTACKABLE = MEMORY_MODULES.register("nearest_attackable", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<NearestVisibleLivingEntities>> NEAREST_VISIBLE_ATTACKABLE = MEMORY_MODULES.register("nearest_visible_attackable", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> KNOCKED_BACK = unit("dracula.knocked_back");
    //<editor-fold desc="Action">
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> SUMMON_PROTECTOR_ACTIVE = unit("action.summon.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> SUMMON_PROTECTOR_COOLDOWN = unit("action.summon.cooldown");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<List<UUID>>> SUMMONS = MEMORY_MODULES.register("action.summon.summons", () -> new MemoryModuleType<>(Optional.of(UUIDUtil.CODEC.listOf())));

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> SUMMON_VAMPIRE_BATS_ACTIVE = unit("action.vampire_bat.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> SUMMON_VAMPIRE_BATS_COOLDOWN = unit("action.vampire_bat.cooldown");

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> FLYING_SWORD_ACTIVE = unit("action.flying_sword.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> FLYING_SWORD_COOLDOWN = unit("action.flying_sword.cooldown");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> FLYING_SWORD_EQUIPPED = unit("action.flying_sword.equipped");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> FLYING_SWORD_SHOT = unit("action.flying_sword.shot");

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> FLYING_NEEDLE_ACTIVE = unit("action.flying_needle.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> FLYING_NEEDLE_COOLDOWN = unit("action.flying_needle.cooldown");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<List<UUID>>> FLYING_NEEDLES = MEMORY_MODULES.register("action.flying_needle.needles", () -> new MemoryModuleType<>(Optional.of(UUIDUtil.CODEC.listOf())));

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> BACKSTAB_ACTIVE = unit("action.backstab.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> BACKSTAB_COOLDOWN = unit("action.backstab.cooldown");

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> BLOOD_PROJECTILES_ACTIVE = unit("action.blood_projectiles.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> BLOOD_PROJECTILES_COOLDOWN = unit("action.blood_projectiles.cooldown");

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> REGENERATION_ACTIVE = unit("action.regeneration.active");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> REGENERATION_COOLDOWN = unit("action.regeneration.cooldown");
    //</editor-fold>
    //</editor-fold>


    static void register(IEventBus bus) {
        MEMORY_MODULES.register(bus);
    }

    private static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> unit(String name) {
        return MEMORY_MODULES.register(name, () -> new MemoryModuleType<>(Optional.of(Unit.CODEC)));
    }

    @SuppressWarnings("SameParameterValue")
    private static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Integer>> integer(String name) {
        return MEMORY_MODULES.register(name, () -> new MemoryModuleType<>(Optional.of(Codec.INT)));
    }

    private static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Long>> longInt(String name) {
        return MEMORY_MODULES.register(name, () -> new MemoryModuleType<>(Optional.of(Codec.LONG)));
    }
}
