package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ModDebugEntries {

    public static final Identifier PASSIVE_LEVELING = VIdentifier.mod("passive_leveling");

    public static void registerDebugEntries(RegisterDebugEntriesEvent event) {
        event.register(PASSIVE_LEVELING, new PassiveLevelingEntry());
        event.includeInProfile(PASSIVE_LEVELING, DebugScreenProfile.DEFAULT, DebugScreenEntryStatus.NEVER);
    }

    private static class PassiveLevelingEntry implements DebugScreenEntry {

        @Override
        public void display(@NonNull DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
            Player player = Minecraft.getInstance().player;
            if (player == null || !Helper.isVampire(player)) return;

            VampirePlayer vampire = VampirePlayer.get(player);
            if (!VampireLeveling.canLevelPassively(vampire.getLevel())) {
                displayer.addLine("Passive leveling: unavailable");
                return;
            }
            displayer.addLine("Passive leveling: " + vampire.getPassiveLevelBlood() + " / " + VampireLeveling.getPassiveLevelingBlood(vampire.getLevel() + 1) + " blood");
        }
    }
}