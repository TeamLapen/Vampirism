package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.resource.ResourcePackLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModDataPacks {

    public static final Identifier VAMPIRISM_2D_PACK_ID = VIdentifier.mod("2dtextures");
    public static final Identifier BUILTIN_COMPAT_ID = VIdentifier.mod("modcompat");
    public static final Identifier UNDEAD_VAMPIRES = VIdentifier.mod("undeadvampires");

    private static Set<Identifier> ENABLED_PACKS = Set.of();

    public static boolean isEnabled(Identifier id) {
        return ENABLED_PACKS.contains(id);
    }

    public static void registerPackRepository(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            add(VAMPIRISM_2D_PACK_ID, event::addRepositorySource, PackSource.FEATURE, false);
        }
        if (event.getPackType() == PackType.SERVER_DATA) {
            add(UNDEAD_VAMPIRES, event::addRepositorySource, PackSource.FEATURE, false);
            add(BUILTIN_COMPAT_ID, event::addRepositorySource, PackSource.BUILT_IN, true);
        }
    }

    public static void datapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        ENABLED_PACKS = server.getPackRepository().getSelectedPacks().stream().map(Pack::getId).map(x -> x.split(":")).filter(x -> x.length == 2 && x[0].equals("mod/vampirism")).map(x -> x[1]).map(VIdentifier::mod).collect(Collectors.toSet());
    }

    private static void add(Identifier id, Consumer<RepositorySource> sourceConsumer, PackSource packSource, boolean isHidden) {
        getJarContents(id).ifPresent(resources -> {
            sourceConsumer.accept(consumer -> consumer.accept(new Pack(
                    info(id, packSource),
                    resources,
                    metadata(id, isHidden),
                    selection()
            )));
        });
    }

    private static PackLocationInfo info(Identifier id, PackSource packSource) {
        return new PackLocationInfo("mod/" + id, Component.translatable(id.toLanguageKey("pack")), packSource, Optional.empty());
    }

    private static Pack.Metadata metadata(Identifier id, boolean idHidden) {
        return new Pack.Metadata(Component.translatable(id.toLanguageKey("pack", "desc")), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), idHidden);
    }

    private static PackSelectionConfig selection() {
        return new PackSelectionConfig(false, Pack.Position.TOP, false);
    }

    private static Optional<Pack.ResourcesSupplier> getJarContents(Identifier pathString) {
        for (Path contentRoot : ModList.get().getModFileById(pathString.getNamespace()).getFile().getContents().getContentRoots()) {
            var path = contentRoot.resolve("packs/" + pathString.getPath());
            try {
                return Optional.of(ResourcePackLoader.createPackForJarContents(JarContents.ofPath(path)));
            } catch (IOException ignored) {
            }
        }

        return Optional.empty();
    }
}
