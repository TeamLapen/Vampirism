package de.teamlapen.vampirism.data.reloadlistener.heritage;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class HeritageDefinitionReloadListener extends SimpleJsonResourceReloadListener<HeritageDefinition> {
    public static final Identifier ID = VIdentifier.mod("heritage_definitions");
    private Map<String, Identifier> definitionIdsByNpc = Map.of();

    public HeritageDefinitionReloadListener() {
        super(HeritageDefinition.CODEC, FileToIdConverter.json("heritage"));
    }

    public Optional<Identifier> getIdForNpc(String namedNpc) {
        return Optional.ofNullable(this.definitionIdsByNpc.get(namedNpc));
    }

    @Override
    protected void apply(@NotNull Map<Identifier, HeritageDefinition> definitions, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<String, Identifier> definitionIdsByNpc = new HashMap<>();
        definitions.forEach((id, definition) -> {
            Identifier previous = definitionIdsByNpc.putIfAbsent(definition.npc(), id);
            if (previous != null) {
                throw new IllegalStateException("Heritage definitions " + previous + " and " + id + " use the same named NPC key " + definition.npc());
            }
        });
        this.definitionIdsByNpc = Map.copyOf(definitionIdsByNpc);
    }
}
