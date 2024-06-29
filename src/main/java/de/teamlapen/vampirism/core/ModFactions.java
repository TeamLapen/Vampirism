package de.teamlapen.vampirism.core;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntry;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.neutral.INeutralPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.api.registries.DeferredFaction;
import de.teamlapen.vampirism.api.registries.DeferredFactionRegister;
import de.teamlapen.vampirism.core.tags.*;
import de.teamlapen.vampirism.entity.factions.LordPlayerBuilder;
import de.teamlapen.vampirism.entity.factions.PlayableFactionBuilder;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.MinionEntryBuilder;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.util.HunterVillage;
import de.teamlapen.vampirism.util.LordTitles;
import de.teamlapen.vampirism.util.VampireVillage;
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
    private static final DeferredRegister<IMinionEntry<?, ?>> MINIONS = DeferredRegister.create(VampirismRegistries.Keys.MINION, REFERENCE.MODID);

    public static final DeferredFaction<IVampirePlayer, IPlayableFaction<IVampirePlayer>> VAMPIRE = FACTIONS.registerFaction(VampirismFactions.Keys.VAMPIRE.getPath(), () -> new PlayableFactionBuilder<>((Supplier<AttachmentType<IVampirePlayer>>) (Object) ModAttachments.VAMPIRE_PLAYER)
            .color(Color.MAGENTA_DARK.getRGB())
            .chatColor(ChatFormatting.DARK_PURPLE)
            .name("text.vampirism.vampire")
            .namePlural("text.vampirism.vampires")
            .highestLevel(REFERENCE.HIGHEST_VAMPIRE_LEVEL)
            .refinementItem(IRefinementItem.AccessorySlotType.AMULET, ModItems.AMULET::get)
            .refinementItem(IRefinementItem.AccessorySlotType.RING, ModItems.RING::get)
            .refinementItem(IRefinementItem.AccessorySlotType.OBI_BELT, ModItems.OBI_BELT::get)
            .addTag(Registries.BIOME, ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME)
            .addTag(Registries.POINT_OF_INTEREST_TYPE, ModPoiTypeTags.IS_VAMPIRE)
            .addTag(Registries.VILLAGER_PROFESSION, ModProfessionTags.IS_VAMPIRE)
            .addTag(Registries.ENTITY_TYPE, ModEntityTags.VAMPIRE)
            .addTag(VampirismRegistries.Keys.TASK, ModTaskTags.IS_VAMPIRE)
            .addTag(VampirismRegistries.Keys.FACTION, ModFactionTags.IS_VAMPIRE)
            .village(VampireVillage.vampireVillage().build())
            .lord(new LordPlayerBuilder<IVampirePlayer>()
                    .lordTitle(new LordTitles.VampireTitles()).lordLevel(REFERENCE.HIGHEST_VAMPIRE_LORD).build())
            .build());

    public static final DeferredFaction<IHunterPlayer, IPlayableFaction<IHunterPlayer>> HUNTER = FACTIONS.registerFaction(VampirismFactions.Keys.HUNTER.getPath(), () -> new PlayableFactionBuilder<>((Supplier<AttachmentType<IHunterPlayer>>) (Object) ModAttachments.HUNTER_PLAYER)
            .color(Color.BLUE.getRGB())
            .chatColor(ChatFormatting.BLUE)
            .name("text.vampirism.hunter")
            .namePlural("text.vampirism.hunters")
            .highestLevel(REFERENCE.HIGHEST_HUNTER_LEVEL)
            .addTag(Registries.BIOME, ModBiomeTags.HasFaction.IS_HUNTER_BIOME)
            .addTag(Registries.POINT_OF_INTEREST_TYPE, ModPoiTypeTags.IS_HUNTER)
            .addTag(Registries.VILLAGER_PROFESSION, ModProfessionTags.IS_HUNTER)
            .addTag(Registries.ENTITY_TYPE, ModEntityTags.HUNTER)
            .addTag(VampirismRegistries.Keys.TASK, ModTaskTags.IS_HUNTER)
            .addTag(VampirismRegistries.Keys.FACTION, ModFactionTags.IS_HUNTER)
            .village(HunterVillage.hunterVillage().build())
            .lord(new LordPlayerBuilder<IHunterPlayer>()
                    .lordTitle(new LordTitles.HunterTitles()).lordLevel(REFERENCE.HIGHEST_HUNTER_LORD).build())
            .build());

    public static final DeferredFaction<INeutralPlayer, IPlayableFaction<INeutralPlayer>> NEUTRAL = FACTIONS.registerFaction(VampirismFactions.Keys.NEUTRAL.getPath(), () -> new PlayableFactionBuilder<>((Supplier<AttachmentType<INeutralPlayer>> )(Object) ModAttachments.NEUTRAL_PLAYER)
            .name("text.vampirism.neutral")
            .namePlural("text.vampirism.neutral")
            .build());

    public static final DeferredHolder<IMinionEntry<?, ?>, IMinionEntry<IVampirePlayer, VampireMinionEntity.VampireMinionData>> VAMPIRE_MINION = MINIONS.register(VampirismFactions.Keys.VAMPIRE.getPath(), () ->
            new MinionEntryBuilder<>(VAMPIRE, VampireMinionEntity.VampireMinionData::new)
                    .commandBuilder(new MinionEntryBuilder.MinionCommandBuilder<IVampirePlayer, VampireMinionEntity.VampireMinionData>(ModEntities.VAMPIRE_MINION::get)
                            .with("name", "Vampire", StringArgumentType.string(), MinionData::setName, StringArgumentType::getString)
                            .with("texture", -1, IntegerArgumentType.integer(-1, IBasicVampire.TYPES), VampireMinionEntity.VampireMinionData::setType, IntegerArgumentType::getInteger)
                            .with("use_lord_skin", false, BoolArgumentType.bool(), VampireMinionEntity.VampireMinionData::setUseLordSkin, BoolArgumentType::getBool)).build());

    public static final DeferredHolder<IMinionEntry<?, ?>, IMinionEntry<IHunterPlayer, HunterMinionEntity.HunterMinionData>> HUNTER_MINION = MINIONS.register(VampirismFactions.Keys.HUNTER.getPath(), () ->
            new MinionEntryBuilder<>(HUNTER, HunterMinionEntity.HunterMinionData::new)
                    .commandBuilder(new MinionEntryBuilder.MinionCommandBuilder<IHunterPlayer, HunterMinionEntity.HunterMinionData>(ModEntities.HUNTER_MINION::get)
                            .with("name", "Hunter", StringArgumentType.string(), MinionData::setName, StringArgumentType::getString)
                            .with("texture", -1, IntegerArgumentType.integer(-1, IBasicHunter.TYPES), HunterMinionEntity.HunterMinionData::setType, IntegerArgumentType::getInteger)
                            .with("hat", 0, IntegerArgumentType.integer(-1, 3), HunterMinionEntity.HunterMinionData::setHat, IntegerArgumentType::getInteger)
                            .with("use_lord_skin", false, BoolArgumentType.bool(), HunterMinionEntity.HunterMinionData::setUseLordSkin, BoolArgumentType::getBool)).build());

    static void register(IEventBus bus) {
        FACTIONS.register(bus);
        MINIONS.register(bus);
    }

}
