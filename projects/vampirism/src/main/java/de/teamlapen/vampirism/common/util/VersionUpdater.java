package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.REFERENCE;
import net.neoforged.neoforge.event.ModMismatchEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VersionUpdater {

    @Nullable
    private ArtifactVersion oldVersion;
    @Nullable
    private ArtifactVersion newVersion;

    public void catchModVersionMismatch(ModMismatchEvent event) {
        event.getVersionDifference(REFERENCE.MODID).ifPresent(info -> {
            oldVersion = info.oldVersion();
            newVersion = info.newVersion();
        });
    }

    public void checkVersionUpdated(ServerStartingEvent event) {
        if (oldVersion != null && newVersion != null) {
            update(oldVersion, newVersion);
        }
    }

    private void update(@NotNull ArtifactVersion oldVersion, ArtifactVersion newVersion) {
    }
}
