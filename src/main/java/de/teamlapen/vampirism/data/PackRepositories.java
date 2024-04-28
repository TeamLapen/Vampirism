package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class PackRepositories {

    private static final String VAMPIRISM_2D_PACK_ID = "vampirism2dtextures";
    private static final String BUILTIN_COMPAT_ID = "modcompat";
    public static final PackLocationInfo VAMPIRISM_2D_PACK = new PackLocationInfo(VAMPIRISM_2D_PACK_ID, Component.literal("Vanilla Style Vampirism"), PackSource.BUILT_IN, Optional.empty());
    public static final PackLocationInfo BUILTIN_COMPAT = new PackLocationInfo(BUILTIN_COMPAT_ID, Component.literal("Vampirism builtin mod compatibility data"), PackSource.DEFAULT, Optional.empty());

    public static void registerPackRepository(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(s -> s.accept(Pack.readMetaAndCreate(VAMPIRISM_2D_PACK, new PathPackResources.PathResourcesSupplier(ModList.get().getModFileById(REFERENCE.MODID).getFile().findResource("packs/" + VAMPIRISM_2D_PACK_ID)), PackType.CLIENT_RESOURCES, new PackSelectionConfig(false, Pack.Position.TOP, false))));
        }
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(s -> s.accept(new Pack(BUILTIN_COMPAT, new PathPackResources.PathResourcesSupplier(ModList.get().getModFileById(REFERENCE.MODID).getFile().findResource("packs/" + BUILTIN_COMPAT_ID)), new Pack.Metadata(BUILTIN_COMPAT.title(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true), new PackSelectionConfig(false, Pack.Position.TOP, false))));
        }
    }
}
