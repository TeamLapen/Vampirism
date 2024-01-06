package de.teamlapen.vampirism.world;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.core.ModAttachments;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LevelGarlic implements IGarlicChunkHandler {

    public static Optional<LevelGarlic> getOpt(@NotNull Level level) {
        return Optional.ofNullable(level.getData(ModAttachments.LEVEL_GARLIC));
    }

    private final HashMap<ChunkPos, EnumStrength> strengthHashMap = Maps.newHashMap();
    private final HashMap<Integer, Emitter> emitterHashMap = Maps.newHashMap();

    @NotNull
    @Override
    public EnumStrength getStrengthAtChunk(ChunkPos pos) {
        EnumStrength s = strengthHashMap.get(pos);
        return s == null ? EnumStrength.NONE : s;
    }

    @Override
    public int registerGarlicBlock(EnumStrength strength, ChunkPos @NotNull ... pos) {
        for (ChunkPos p : pos) {
            if (p == null) {
                throw new IllegalArgumentException("Garlic emitter position should not be null");
            }
        }
        Emitter e = new Emitter(strength, pos);
        int hash = e.hashCode();
        emitterHashMap.putIfAbsent(hash, e);
        rebuildStrengthMap();
        return hash;
    }

    @Override
    public void removeGarlicBlock(int id) {
        Emitter e = emitterHashMap.remove(id);
        rebuildStrengthMap();
    }

    private void rebuildStrengthMap() {
        strengthHashMap.clear();
        for (Emitter e : emitterHashMap.values()) {
            for (ChunkPos pos : e.pos) {
                EnumStrength old = strengthHashMap.get(pos);
                if (old == null || e.strength.isStrongerThan(old)) {
                    strengthHashMap.put(pos, e.strength);
                }

            }
        }
    }

    public void printDebug(@NotNull CommandSourceStack sender) {
        for (Emitter e : emitterHashMap.values()) {
            sender.sendSuccess(() -> Component.literal("E: " + e.toString()), true);
        }
        for (Map.Entry<ChunkPos, EnumStrength> e : strengthHashMap.entrySet()) {
            sender.sendSuccess(() -> Component.literal("S: " + e.toString()), true);
        }
    }

    @Override
    public void clearCache() {
        this.strengthHashMap.clear();
        this.emitterHashMap.clear();
    }

    private record Emitter(EnumStrength strength, ChunkPos[] pos) {
    }
}
