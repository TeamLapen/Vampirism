package de.teamlapen.faction.common.factions.minions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.util.FIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class MinionWorldData extends SavedData implements ValueIOSerializable {
    public static final SavedDataType<MinionWorldData> TYPE = new SavedDataType<>(FIdentifier.mod("minion_data"), MinionWorldData::new, MinionWorldData::makeCodec);

    @NotNull
    public static MinionWorldData getData(@NotNull ServerLevel world) {
        return getData(world.getServer());
    }

    @NotNull
    public static MinionWorldData getData(final @NotNull MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(TYPE);
    }


    @NotNull
    public static Optional<MinionWorldData> getData(Level world) {
        if (world instanceof ServerLevel) {
            return Optional.of(getData(((ServerLevel) world).getServer()));
        }
        return Optional.empty();
    }

    private final MinecraftServer server;
    private final Object2ObjectOpenHashMap<UUID, PlayerMinionController> controllers = new Object2ObjectOpenHashMap<>();

    public MinionWorldData(ServerLevel level) {
        this.server = level.getServer();
    }

    private static Codec<MinionWorldData> makeCodec(ServerLevel level) {
        return CompoundTag.CODEC.flatXmap(tag -> {
            MinionWorldData minionWorldData = new MinionWorldData(level);
            ProblemReporter.Collector reporter = new ProblemReporter.Collector();

            minionWorldData.deserialize(TagValueInput.create(reporter, level.registryAccess(), tag));
            return !reporter.isEmpty() ? DataResult.error(() -> "Deserialisation error in minion data: " + reporter.getReport()) : DataResult.success(minionWorldData);
        }, data -> {
            ProblemReporter.Collector reporter = new ProblemReporter.Collector();
            var tag = TagValueOutput.createWithContext(reporter, data.server.registryAccess());

            data.serialize(tag);
            return !reporter.isEmpty() ? DataResult.error(() -> "Serialisation error in minion data: " + reporter.getReport()) : DataResult.success(tag.buildResult());
        });
    }

    @Override
    public void serialize(ValueOutput output) {
        var controller = output.childrenList("controller");
        for (var entry : controllers.object2ObjectEntrySet()) {
            ValueOutput valueOutput = controller.addChild();
            valueOutput.store("uuid", UUIDUtil.CODEC, entry.getKey());
            entry.getValue().serialize(valueOutput);
        }
    }

    @Override
    public void deserialize(ValueInput input) {

        input.childrenList("controller").stream().flatMap(ValueInput.ValueInputList::stream).forEach(x -> {
            x.read("uuid", UUIDUtil.CODEC).ifPresent(id -> {
                var controller = controllers.computeIfAbsent(id, (y) -> new PlayerMinionController(server, id));
                controller.deserialize(x);
            });
        });
    }

    @Nullable
    public PlayerMinionController getController(UUID lordID) {
        return controllers.get(lordID);
    }

    @NotNull
    public PlayerMinionController getOrCreateController(@NotNull ILordPlayer<?> lord) {
        UUID id = lord.asEntity().getUUID();
        if (controllers.containsKey(id)) {
            return controllers.get(id);
        } else {
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.setMaxMinions(lord.getFaction(), lord.getMaxMinions());
            controllers.put(id, c);
            return c;
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    /**
     * Only for debugging. Removes controller from saved data not from entities. Reload world afterward
     */
    @VisibleForDebug
    public void purgeController(UUID lordID) {
        controllers.remove(lordID);
    }

    /**
     * Tick server side
     */
    public void tick() {
        controllers.object2ObjectEntrySet().fastForEach(entry -> entry.getValue().tick());
    }

    public Map<UUID, PlayerMinionController> getControllers() {
        return Collections.unmodifiableMap(controllers);
    }
}
