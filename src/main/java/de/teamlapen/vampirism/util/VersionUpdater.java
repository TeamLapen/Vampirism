package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.neoforged.neoforge.event.ModMismatchEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VersionUpdater {

    @Nullable
    private static ArtifactVersion oldVersion;
    @Nullable
    private static ArtifactVersion newVersion;

    public static void catchModVersionMismatch(ModMismatchEvent event) {
        event.getVersionDifference(REFERENCE.MODID).ifPresent(info -> {
            oldVersion = info.oldVersion();
            newVersion = info.newVersion();
        });
    }

    public static void checkVersionUpdated(ServerStartingEvent event) {
        if (oldVersion != null && newVersion != null) {
            update(oldVersion, newVersion);
        }
    }

    private static void update(@NotNull ArtifactVersion oldVersion, ArtifactVersion newVersion) {
        if (oldVersion.getMajorVersion() == 1 && oldVersion.getMinorVersion() == 9 && newVersion.getMajorVersion() == 1 && newVersion.getMinorVersion() == 10) {
            update1_9to1_10();
        }
    }

    private static void update1_9to1_10() {
        double skillPointsPerLevel = VampirismConfig.BALANCE.skillPointsPerLevel.get();
        if (skillPointsPerLevel == 1) {
            skillPointsPerLevel = VampirismConfig.BALANCE.skillPointsPerLevel.getDefault();
        } else {
            skillPointsPerLevel *= 2;
        }
        VampirismConfig.BALANCE.skillPointsPerLevel.set(skillPointsPerLevel);
        double skillPointsPerLordLevel = VampirismConfig.BALANCE.skillPointsPerLordLevel.get();
        if (skillPointsPerLordLevel == 1) {
            skillPointsPerLordLevel = VampirismConfig.BALANCE.skillPointsPerLordLevel.getDefault();
        } else {
            skillPointsPerLordLevel *= 2;
        }
        VampirismConfig.BALANCE.skillPointsPerLordLevel.set(skillPointsPerLordLevel);
    }
}
