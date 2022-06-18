package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.container.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainer {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, REFERENCE.MODID);

    public static final RegistryObject<ContainerType<HunterTrainerContainer>> HUNTER_TRAINER = CONTAINERS.register("hunter_trainer", () -> new ContainerType<>(HunterTrainerContainer::new));
    public static final RegistryObject<ContainerType<AlchemicalCauldronContainer>> ALCHEMICAL_CAULDRON = CONTAINERS.register("alchemical_cauldron", () -> new ContainerType<>(AlchemicalCauldronContainer::new));
    public static final RegistryObject<ContainerType<HunterBasicContainer>> HUNTER_BASIC = CONTAINERS.register("hunter_basic", () -> new ContainerType<>(HunterBasicContainer::new));
    public static final RegistryObject<ContainerType<HunterTableContainer>> HUNTER_TABLE = CONTAINERS.register("hunter_table", () -> new ContainerType<>(new HunterTableContainer.Factory()));
    public static final RegistryObject<ContainerType<WeaponTableContainer>> WEAPON_TABLE = CONTAINERS.register("weapon_table", () -> new ContainerType<>(new WeaponTableContainer.Factory()));
    public static final RegistryObject<ContainerType<AltarInfusionContainer>> ALTAR_INFUSION = CONTAINERS.register("altar_infusion", () -> new ContainerType<>(AltarInfusionContainer::new));
    public static final RegistryObject<ContainerType<BloodGrinderContainer>> BLOOD_GRINDER = CONTAINERS.register("blood_grinder", () -> new ContainerType<>(BloodGrinderContainer::new));
    public static final RegistryObject<ContainerType<MinionContainer>> MINION = CONTAINERS.register("minion", () -> new ContainerType<>(new MinionContainer.Factory()));
    public static final RegistryObject<ContainerType<TaskBoardContainer>> TASK_MASTER = CONTAINERS.register("task_master", () -> new ContainerType<>(TaskBoardContainer::new));
    public static final RegistryObject<ContainerType<PotionTableContainer>> EXTENDED_POTION_TABLE = CONTAINERS.register("extended_potion_table", () -> new ContainerType<>(new PotionTableContainer.Factory()));
    public static final RegistryObject<ContainerType<VampirismContainer>> VAMPIRISM = CONTAINERS.register("vampirism", () -> new ContainerType<>(VampirismContainer::new));
    public static final RegistryObject<ContainerType<AlchemyTableContainer>> ALCHEMICAL_TABLE = CONTAINERS.register("alchemical_table", () -> new ContainerType<>(AlchemyTableContainer::new));


    static void registerContainer(IEventBus bus) {
        CONTAINERS.register(bus);
    }
}
