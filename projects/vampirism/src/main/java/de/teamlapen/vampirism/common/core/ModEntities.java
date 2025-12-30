package de.teamlapen.vampirism.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.common.advancements.criterion.FactionSubPredicate;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import de.teamlapen.faction.common.event.PlayerEventHandlerEvent;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VEnums;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.entity.convertible.Converter;
import de.teamlapen.vampirism.common.advancements.critereon.DraculaCriterion;
import de.teamlapen.vampirism.common.util.serialization.conditions.EntityExistsCondition;
import de.teamlapen.vampirism.common.world.entity.*;
import de.teamlapen.vampirism.common.world.entity.converted.*;
import de.teamlapen.vampirism.common.world.entity.converted.converter.DefaultConverter;
import de.teamlapen.vampirism.common.world.entity.converted.converter.SpecialConverter;
import de.teamlapen.vampirism.common.world.entity.hunter.*;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.common.world.entity.vampire.*;
import de.teamlapen.vampirism.misc.sit.SitEntity;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Handles all entity registrations and reference.
 */
public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends Converter>> CONVERTING_HELPER = DeferredRegister.create(VampirismRegistries.Keys.ENTITY_CONVERTER, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, REFERENCE.MODID);
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, REFERENCE.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<BasicHunterEntity>> HUNTER = registerEntityType("hunter", BasicHunterEntity::new, VEnums.HUNTER_CATEGORY.getValue(), (d) -> d.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<HunterTrainerEntity>> HUNTER_TRAINER = registerEntityType("hunter_trainer", HunterTrainerEntity::new, VEnums.HUNTER_CATEGORY.getValue(), (b) -> b.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedHunterEntity>> ADVANCED_HUNTER = registerEntityType("advanced_hunter", AdvancedHunterEntity::new, VEnums.HUNTER_CATEGORY.getValue(), b -> b.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<VampireBaronEntity>> VAMPIRE_BARON = registerEntityType("vampire_baron", VampireBaronEntity::new, VEnums.VAMPIRE_CATEGORY.getValue(), b ->b.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<BasicVampireEntity>> VAMPIRE = registerEntityType("vampire", BasicVampireEntity::new, VEnums.VAMPIRE_CATEGORY.getValue(), b -> b.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedVampireEntity>> ADVANCED_VAMPIRE = registerEntityType("advanced_vampire", AdvancedVampireEntity::new, VEnums.VAMPIRE_CATEGORY.getValue(), b -> b.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCreatureEntity<?>>> CONVERTED_CREATURE = registerEntityType("converted_creature", ConvertedCreatureEntity::new, MobCategory.CREATURE , EntityType.Builder::noSummon);
    public static final DeferredHolder<EntityType<?>, EntityType<DummyBittenAnimalEntity>> DUMMY_CREATURE = registerEntityType("dummy_creature", DummyBittenAnimalEntity::new, MobCategory.CREATURE);
    public static final DeferredHolder<EntityType<?>, EntityType<BlindingBatEntity>> BLINDING_BAT = registerEntityType("blinding_bat", BlindingBatEntity::new, MobCategory.AMBIENT, x -> x.sized(0.5F, 0.9F));
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedHunterEntity.IMob>> ADVANCED_HUNTER_IMOB = registerEntityType("advanced_hunter_imob", AdvancedHunterEntity.IMob::new, VEnums.HUNTER_CATEGORY.getValue(),x -> x.sized(0.6f, 1.95f).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedVampireEntity.IMob>> ADVANCED_VAMPIRE_IMOB = registerEntityType("advanced_vampire_imob", AdvancedVampireEntity.IMob::new, VEnums.VAMPIRE_CATEGORY.getValue(), x -> x.sized(0.6f, 1.95f).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCreatureEntity.IMob<?>>> CONVERTED_CREATURE_IMOB = registerEntityType("converted_creature_imob", ConvertedCreatureEntity.IMob::new, MobCategory.CREATURE, EntityType.Builder::noSummon);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedSheepEntity>> CONVERTED_SHEEP = registerEntityType("converted_sheep", ConvertedSheepEntity::new, MobCategory.CREATURE, x -> x.sized(0.9F, 1.3F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCowEntity>> CONVERTED_COW = registerEntityType("converted_cow", ConvertedCowEntity::new, MobCategory.CREATURE, x -> x.sized(0.9F, 1.4F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<CrossbowArrowEntity>> CROSSBOW_ARROW = registerEntityType("crossbow_arrow", CrossbowArrowEntity::new, MobCategory.MISC, x -> x.sized(0.5F, 0.5F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<DarkBloodProjectileEntity>> DARK_BLOOD_PROJECTILE = registerEntityType("dark_blood_projectile", DarkBloodProjectileEntity::new, MobCategory.MISC, x -> x.sized(0.6F, 0.6F).fireImmune().noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<DummyHunterTrainerEntity>> HUNTER_TRAINER_DUMMY = registerEntityType("hunter_trainer_dummy", DummyHunterTrainerEntity::new, MobCategory.MISC, x -> x.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<AreaParticleCloud>> PARTICLE_CLOUD = registerEntityType("particle_cloud", AreaParticleCloud::new, MobCategory.MISC, x -> x.sized(6.0F, 0.5F).fireImmune().noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<SoulOrbEntity>> SOUL_ORB = registerEntityType("soul_orb", SoulOrbEntity::new, MobCategory.MISC, x -> x.sized(0.25F, 0.25F).fireImmune().noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableItemEntity>> THROWABLE_ITEM = registerEntityType("throwable_item", ThrowableItemEntity::new, MobCategory.MISC, x -> x.sized(0.25F, 0.25F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<BasicVampireEntity.IMob>> VAMPIRE_IMOB = registerEntityType("vampire_imob", BasicVampireEntity.IMob::new, VEnums.VAMPIRE_CATEGORY.getValue(), x -> x.sized(0.6f, 1.95f).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<BasicHunterEntity.IMob>> HUNTER_IMOB = registerEntityType("hunter_imob", BasicHunterEntity.IMob::new, VEnums.HUNTER_CATEGORY.getValue(), x -> x.sized(0.6f, 1.95f).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<AggressiveVillagerEntity>> VILLAGER_ANGRY = registerEntityType("villager_angry", AggressiveVillagerEntity::new, MobCategory.CREATURE, x -> x.sized(0.6F, 1.95F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedVillagerEntity>> VILLAGER_CONVERTED = registerEntityType("villager_converted", ConvertedVillagerEntity::new, VEnums.VAMPIRE_CATEGORY.getValue(), x -> x.sized(0.6F, 1.95F));
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedHorseEntity>> CONVERTED_HORSE = registerEntityType("converted_horse", ConvertedHorseEntity::new, MobCategory.CREATURE, x -> x.sized(1.3964844F, 1.6F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<VampireMinionEntity>> VAMPIRE_MINION = registerEntityType("vampire_minion", VampireMinionEntity::new, MobCategory.CREATURE, x -> x.sized(0.6f, 1.95f).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedDonkeyEntity>> CONVERTED_DONKEY = registerEntityType("converted_donkey", ConvertedDonkeyEntity::new, MobCategory.CREATURE, x -> x.sized(1.3964844F, 1.5F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedMuleEntity>> CONVERTED_MULE = registerEntityType("converted_mule", ConvertedMuleEntity::new, MobCategory.CREATURE, x -> x.sized(1.3964844F, 1.5F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<HunterMinionEntity>> HUNTER_MINION = registerEntityType("hunter_minion", HunterMinionEntity::new, MobCategory.CREATURE, x -> x.sized(0.6f, 1.95f).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<VampireTaskMasterEntity>> TASK_MASTER_VAMPIRE = registerEntityType("task_master_vampire", VampireTaskMasterEntity::new, VEnums.VAMPIRE_CATEGORY.getValue(), x -> x.sized(0.6f, 1.95f));
    public static final DeferredHolder<EntityType<?>, EntityType<HunterTaskMasterEntity>> TASK_MASTER_HUNTER = registerEntityType("task_master_hunter", HunterTaskMasterEntity::new, VEnums.HUNTER_CATEGORY.getValue(), x -> x.sized(0.6f, 1.95f));
    public static final DeferredHolder<EntityType<?>, EntityType<SitEntity>> SIT_DUMMY = registerEntityType("dummy_sit_entity", SitEntity::new, MobCategory.MISC, x -> x.sized(0.0001f, 0.0001f).setTrackingRange(256).setUpdateInterval(20).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedFoxEntity>> CONVERTED_FOX = registerEntityType("converted_fox", ConvertedFoxEntity::new, MobCategory.CREATURE, x -> x.sized(0.6F, 0.7F).immuneTo(Blocks.SWEET_BERRY_BUSH).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedGoatEntity>> CONVERTED_GOAT = registerEntityType("converted_goat", ConvertedGoatEntity::new, MobCategory.CREATURE, x -> x.sized(0.9F, 1.3F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<VulnerableRemainsDummyEntity>> VULNERABLE_REMAINS_DUMMY = registerEntityType("vulnerable_remains_dummy", VulnerableRemainsDummyEntity::new, MobCategory.MISC, x -> x.sized(1.02f, 1.02f).setTrackingRange(10).setUpdateInterval(20).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<RemainsDefenderEntity>> REMAINS_DEFENDER = registerEntityType("remains_defender", RemainsDefenderEntity::new, MobCategory.MISC, x -> x.sized(0.3f, 0.3f).setTrackingRange(10).setUpdateInterval(20).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<GhostEntity>> GHOST = registerEntityType("ghost", GhostEntity::new, VEnums.VAMPIRE_CATEGORY.getValue(), x -> x.sized(0.35F, 0.5F).setTrackingRange(10).setUpdateInterval(20).fireImmune());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCamelEntity>> CONVERTED_CAMEL = registerEntityType("converted_camel", ConvertedCamelEntity::new, MobCategory.CREATURE, x -> x.sized(1.7F, 2.375F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCatEntity>> CONVERTED_CAT = registerEntityType("converted_cat", ConvertedCatEntity::new, MobCategory.CREATURE, x -> x.sized(0.6F, 0.7F).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<Boat>> DARK_SPRUCE_BOAT = registerEntityType("dark_spruce_boat", EntityType.boatFactory(ModItems.DARK_SPRUCE_BOAT::get), MobCategory.MISC, x -> x.sized(1.375f,0.5625f).noLootTable().eyeHeight(0.5625f).clientTrackingRange(10).noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<Boat>> CURSED_SPRUCE_BOAT = registerEntityType("cursed_spruce_boat", EntityType.boatFactory(ModItems.CURSED_SPRUCE_BOAT::get), MobCategory.MISC, x -> x.sized(1.375f,0.5625f).eyeHeight(0.5625f).clientTrackingRange(10).noLootTable().noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ChestBoat>> DARK_SPRUCE_CHEST_BOAT = registerEntityType("dark_spruce_chest_boat", EntityType.chestBoatFactory(ModItems.DARK_SPRUCE_CHEST_BOAT::get), MobCategory.MISC, x -> x.sized(1.375f,0.5625f).eyeHeight(0.5625f).clientTrackingRange(10).noLootTable().noSummon());
    public static final DeferredHolder<EntityType<?>, EntityType<ChestBoat>> CURSED_SPRUCE_CHEST_BOAT = registerEntityType("cursed_spruce_chest_boat", EntityType.chestBoatFactory(ModItems.CURSED_SPRUCE_CHEST_BOAT::get), MobCategory.MISC, x -> x.sized(1.375f,0.5625f).eyeHeight(0.5625f).clientTrackingRange(10).noLootTable().noSummon());


    public static final DeferredHolder<MapCodec<? extends Converter>, MapCodec<? extends Converter>> DEFAULT_CONVERTER = CONVERTING_HELPER.register("default", () -> DefaultConverter.CODEC);
    public static final DeferredHolder<MapCodec<? extends Converter>, MapCodec<? extends Converter>> SPECIAL_CONVERTER = CONVERTING_HELPER.register("special", () -> SpecialConverter.CODEC);


    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<DraculaCriterion>> DRACULA_PREDICATE = ENTITY_SUB_PREDICATES.register("dracula", () -> DraculaCriterion.CODEC);

    @SuppressWarnings("unused")
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<? extends ICondition>> ENTITY_EXISTS = CONDITIONS.register("entity_exists", () -> EntityExistsCondition.CODEC);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Item>> ITEM_DATA = DATA_SERIALIZER.register("item", () -> (EntityDataSerializer.ForValueType<Item>) (() -> ByteBufCodecs.registry(Registries.ITEM)));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Holder<Item>>> ITEM_HOLDER = DATA_SERIALIZER.register("item_holder", () -> (EntityDataSerializer.ForValueType<Holder<Item>>) (() -> ByteBufCodecs.holderRegistry(Registries.ITEM)));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<UUID>>> OPTIONAL_UUID = DATA_SERIALIZER.register("optional_uuid", () -> (EntityDataSerializer.ForValueType<Optional<UUID>>) (() -> ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC)));

    static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
        CONVERTING_HELPER.register(bus);
        ENTITY_SUB_PREDICATES.register(bus);
        CONDITIONS.register(bus);
        DATA_SERIALIZER.register(bus);
    }

    static void onRegisterSpawns(@NotNull RegisterSpawnPlacementsEvent event) {
        event.register(ADVANCED_HUNTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ADVANCED_VAMPIRE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BLINDING_BAT.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlindingBatEntity::spawnPredicate, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DUMMY_CREATURE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DummyBittenAnimalEntity::spawnPredicate, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_CREATURE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_SHEEP.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedSheepEntity::checkConvertedSheepSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_COW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCowEntity::checkConvertedCowSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HUNTER_TRAINER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HUNTER_TRAINER_DUMMY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VAMPIRE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VAMPIRE_BARON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaronEntity::spawnPredicateBaron, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HUNTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VILLAGER_ANGRY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VILLAGER_CONVERTED.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_HORSE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedHorseEntity::checkConvertedHorseSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_DONKEY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedDonkeyEntity::checkConvertedDonkeySpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_MULE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedMuleEntity::checkConvertedMuleSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_FOX.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedFoxEntity::checkConvertedFoxSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_GOAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedGoatEntity::checkConvertedGoatSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_CAMEL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCamelEntity::checkConvertedCamelSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_CAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCatEntity::checkConvertedCatSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
    }

    static void onRegisterEntityTypeAttributes(@NotNull EntityAttributeCreationEvent event) {
        event.put(ADVANCED_HUNTER.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(ADVANCED_HUNTER_IMOB.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(ADVANCED_VAMPIRE.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(ADVANCED_VAMPIRE_IMOB.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(BLINDING_BAT.get(), Bat.createAttributes().build());
        event.put(CONVERTED_CREATURE.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_CREATURE_IMOB.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_HORSE.get(), ConvertedHorseEntity.getAttributeBuilder().build());
        event.put(CONVERTED_SHEEP.get(), ConvertedSheepEntity.getAttributeBuilder().build());
        event.put(CONVERTED_COW.get(), ConvertedCowEntity.getAttributeBuilder().build());
        event.put(CONVERTED_DONKEY.get(), ConvertedDonkeyEntity.getAttributeBuilder().build());
        event.put(CONVERTED_MULE.get(), ConvertedMuleEntity.getAttributeBuilder().build());
        event.put(DUMMY_CREATURE.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(HUNTER.get(), BasicHunterEntity.getAttributeBuilder().build());
        event.put(HUNTER_IMOB.get(), BasicHunterEntity.getAttributeBuilder().build());
        event.put(HUNTER_TRAINER.get(), HunterTrainerEntity.getAttributeBuilder().build());
        event.put(HUNTER_TRAINER_DUMMY.get(), HunterTrainerEntity.getAttributeBuilder().build());
        event.put(VAMPIRE.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(VAMPIRE_IMOB.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(VAMPIRE_BARON.get(), VampireBaronEntity.getAttributeBuilder().build());
        event.put(VILLAGER_ANGRY.get(), AggressiveVillagerEntity.getAttributeBuilder().build());
        event.put(VILLAGER_CONVERTED.get(), ConvertedVillagerEntity.getAttributeBuilder().build());
        event.put(HUNTER_MINION.get(), HunterMinionEntity.getAttributeBuilder().build());
        event.put(VAMPIRE_MINION.get(), VampireMinionEntity.getAttributeBuilder().build());
        event.put(TASK_MASTER_HUNTER.get(), HunterTaskMasterEntity.getAttributeBuilder().build());
        event.put(TASK_MASTER_VAMPIRE.get(), VampireTaskMasterEntity.getAttributeBuilder().build());
        event.put(CONVERTED_FOX.get(), ConvertedFoxEntity.createAttributes().build());
        event.put(CONVERTED_GOAT.get(), ConvertedGoatEntity.createAttributes().build());
        event.put(VULNERABLE_REMAINS_DUMMY.get(), VulnerableRemainsDummyEntity.createAttributes().build());
        event.put(REMAINS_DEFENDER.get(), RemainsDefenderEntity.createAttributes().build());
        event.put(GHOST.get(), GhostEntity.createAttributes().build());
        event.put(CONVERTED_CAMEL.get(), ConvertedCamelEntity.getAttributeBuilder().build());
        event.put(CONVERTED_CAT.get(), ConvertedCatEntity.createAttributes().build());
    }

    static void onModifyEntityTypeAttributes(@NotNull EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.SUNDAMAGE);
        event.add(EntityType.PLAYER, ModAttributes.BLOOD_EXHAUSTION);
        event.add(EntityType.PLAYER, ModAttributes.NEONATAL_DURATION);
        event.add(EntityType.PLAYER, ModAttributes.DBNO_DURATION);
    }

    static void registerPlayerEventHandler(PlayerEventHandlerEvent event) {
        event.addAttachmentListener(ModAttachments.VAMPIRE_PLAYER);
        event.addAttachmentListener(ModAttachments.HUNTER_PLAYER);
    }

    public static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category) {
        return registerEntityType(name, factory, category, UnaryOperator.identity());
    }

    public static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder) {
        return ENTITY_TYPES.registerEntityType(name, factory, category, b -> builder.apply(b.clientTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true)));
    }

    public static @NotNull Set<EntityType<?>> getAllEntities() {
        return ENTITY_TYPES.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toUnmodifiableSet());
    }
}
