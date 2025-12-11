package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class ModDataPacks {

    private static final String VAMPIRISM_2D_PACK_ID = "vampirism2dtextures";
    private static final String BUILTIN_COMPAT_ID = "modcompat";
    public static final PackLocationInfo VAMPIRISM_2D_PACK = new PackLocationInfo(VAMPIRISM_2D_PACK_ID, Component.literal("Vanilla Style Vampirism"), PackSource.BUILT_IN, Optional.empty());
    public static final PackLocationInfo BUILTIN_COMPAT = new PackLocationInfo(BUILTIN_COMPAT_ID, Component.literal("Vampirism builtin mod compatibility data"), PackSource.DEFAULT, Optional.empty());

    public static void registerPackRepository(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            getJarContents("packs/" + VAMPIRISM_2D_PACK_ID).ifPresent(pack -> {
                event.addRepositorySource(consumer -> {
                    consumer.accept(new Pack(VAMPIRISM_2D_PACK, new PathPackResources.PathResourcesSupplier(pack.getPrimaryPath()), new Pack.Metadata(VAMPIRISM_2D_PACK.title(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), false), new PackSelectionConfig(false, Pack.Position.TOP, false)));
                });
            });
        }
        if (event.getPackType() == PackType.SERVER_DATA) {
            getJarContents("packs/" + BUILTIN_COMPAT_ID).ifPresent(pack -> {
                event.addRepositorySource(consumer -> {
                    consumer.accept(new Pack(BUILTIN_COMPAT, new PathPackResources.PathResourcesSupplier(pack.getPrimaryPath()), new Pack.Metadata(BUILTIN_COMPAT.title(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true), new PackSelectionConfig(false, Pack.Position.TOP, false)));
                });
            });
        }
    }

    private static Optional<JarContents> getJarContents(String pathString) {
        for (Path contentRoot : ModList.get().getModFileById(REFERENCE.MODID).getFile().getContents().getContentRoots()) {
            var path = contentRoot.resolve(pathString);
            try {
                return Optional.of(JarContents.ofPath(path));
            } catch (IOException ignored) {
            }
        }

        return Optional.empty();
    }
}
