package de.teamlapen.vampirism.common.core;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.registries.factions.DeferredFaction;
import de.teamlapen.factions.api.registries.factions.DeferredFactionRegister;
import de.teamlapen.factions.api.util.SafeCast;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.world.items.IRefinementItem;
import de.teamlapen.factions.common.factions.PlayableFactionBuilder;
import de.teamlapen.factions.common.factions.minions.MinionData;
import de.teamlapen.factions.common.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.common.tags.*;
import de.teamlapen.vampirism.common.util.HunterVillage;
import de.teamlapen.vampirism.common.util.LordTitles;
import de.teamlapen.vampirism.common.util.VampireVillage;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.common.world.entity.minion.MinionEntryBuilder;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModFactions {
    private static final DeferredFactionRegister FACTIONS = DeferredFactionRegister.create(REFERENCE.MODID);
    private static final DeferredRegister<IMinionEntry<?, ?>> MINIONS = DeferredRegister.create(FactionRegistries.Keys.MINION, REFERENCE.MODID);

    public static final DeferredFaction<IVampirePlayer, IPlayableFaction<IVampirePlayer>> VAMPIRE = FACTIONS.registerFaction(VampirismFactions.Keys.VAMPIRE.getPath(), () -> new PlayableFactionBuilder<>(SafeCast.<Supplier<AttachmentType<IVampirePlayer>>>cast(ModAttachments.VAMPIRE_PLAYER))
            .color(Color.MAGENTA_DARK.getRGB())
            .chatColor(ChatFormatting.DARK_PURPLE)
            .highestLevel(REFERENCE.HIGHEST_VAMPIRE_LEVEL)
            .refinementItem(IRefinementItem.AccessorySlotType.AMULET, ModItems.AMULET::get)
            .refinementItem(IRefinementItem.AccessorySlotType.RING, ModItems.RING::get)
            .refinementItem(IRefinementItem.AccessorySlotType.OBI_BELT, ModItems.OBI_BELT::get)
            .addTag(Registries.BIOME, ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME)
            .addTag(Registries.POINT_OF_INTEREST_TYPE, ModPoiTypeTags.IS_VAMPIRE)
            .addTag(Registries.VILLAGER_PROFESSION, ModProfessionTags.IS_VAMPIRE)
            .addTag(Registries.ENTITY_TYPE, ModEntityTags.VAMPIRE)
            .addTag(FactionRegistries.Keys.TASK, ModTaskTags.IS_VAMPIRE)
            .addTag(FactionRegistries.Keys.FACTION, ModFactionTags.IS_VAMPIRE)
            .addTag(Registries.DATA_COMPONENT_TYPE, ModDataComponentTags.VAMPIRE_FOOD)
            .addTag(VampirismRegistries.Keys.VAMPIRE_BOOK, ModVampireBookTags.IS_VAMPIRE)
            .village(VampireVillage::vampireVillage)
            .lord(builder -> builder
                    .lordLevel(REFERENCE.HIGHEST_VAMPIRE_LORD)
                    .lordTitle(new LordTitles.VampireTitles()))
            .build());

    public static final DeferredFaction<IHunterPlayer, IPlayableFaction<IHunterPlayer>> HUNTER = FACTIONS.registerFaction(VampirismFactions.Keys.HUNTER.getPath(), () -> new PlayableFactionBuilder<>(SafeCast.<Supplier<AttachmentType<IHunterPlayer>>>cast(ModAttachments.HUNTER_PLAYER))
            .color(Color.BLUE.getRGB())
            .chatColor(ChatFormatting.BLUE)
            .highestLevel(REFERENCE.HIGHEST_HUNTER_LEVEL)
            .addTag(Registries.BIOME, ModBiomeTags.HasFaction.IS_HUNTER_BIOME)
            .addTag(Registries.POINT_OF_INTEREST_TYPE, ModPoiTypeTags.IS_HUNTER)
            .addTag(Registries.VILLAGER_PROFESSION, ModProfessionTags.IS_HUNTER)
            .addTag(Registries.ENTITY_TYPE, ModEntityTags.HUNTER)
            .addTag(FactionRegistries.Keys.TASK, ModTaskTags.IS_HUNTER)
            .addTag(FactionRegistries.Keys.FACTION, ModFactionTags.IS_HUNTER)
            .addTag(Registries.DATA_COMPONENT_TYPE, ModDataComponentTags.HUNTER_FOOD)
            .addTag(VampirismRegistries.Keys.VAMPIRE_BOOK, ModVampireBookTags.IS_HUNTER)
            .village(HunterVillage::hunterVillage)
            .lord(builder -> builder
                    .lordTitle(new LordTitles.HunterTitles())
                    .lordLevel(REFERENCE.HIGHEST_HUNTER_LORD).build())
            .build());

    public static final DeferredHolder<IMinionEntry<?, ?>, IMinionEntry<IVampirePlayer, VampireMinionEntity.VampireMinionData>> VAMPIRE_MINION = MINIONS.register(VampirismFactions.Keys.VAMPIRE.getPath(), () ->
            new MinionEntryBuilder<>(VAMPIRE, VampireMinionEntity.VampireMinionData::new)
                    .commandBuilder(ModEntities.VAMPIRE_MINION, builder -> builder
                            .with("name", "Vampire", StringArgumentType.string(), MinionData::setName, StringArgumentType::getString)
                            .with("texture", -1, IntegerArgumentType.integer(-1, IBasicVampire.TYPES), VampireMinionEntity.VampireMinionData::setType, IntegerArgumentType::getInteger)
                            .with("use_lord_skin", false, BoolArgumentType.bool(), VampireMinionEntity.VampireMinionData::setUseLordSkin, BoolArgumentType::getBool)).build());

    public static final DeferredHolder<IMinionEntry<?, ?>, IMinionEntry<IHunterPlayer, HunterMinionEntity.HunterMinionData>> HUNTER_MINION = MINIONS.register(VampirismFactions.Keys.HUNTER.getPath(), () ->
            new MinionEntryBuilder<>(HUNTER, HunterMinionEntity.HunterMinionData::new)
                    .commandBuilder(ModEntities.HUNTER_MINION, builder -> builder
                            .with("name", "Hunter", StringArgumentType.string(), MinionData::setName, StringArgumentType::getString)
                            .with("texture", -1, IntegerArgumentType.integer(-1, IBasicHunter.TYPES), HunterMinionEntity.HunterMinionData::setType, IntegerArgumentType::getInteger)
                            .with("hat", 0, IntegerArgumentType.integer(-1, 3), HunterMinionEntity.HunterMinionData::setHat, IntegerArgumentType::getInteger)
                            .with("use_lord_skin", false, BoolArgumentType.bool(), HunterMinionEntity.HunterMinionData::setUseLordSkin, BoolArgumentType::getBool)).build());

    static void register(IEventBus bus) {
        FACTIONS.register(bus);
        MINIONS.register(bus);
    }

}
