package de.teamlapen.lib.util;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class QualifiedVersion extends DefaultArtifactVersion {

    private final TYPE type;

    public QualifiedVersion(String version) {
        super(version);
        String qualifier = getQualifier();
        TYPE type = TYPE.RELEASE;
        if (qualifier != null) {
            if (qualifier.contains("alpha")) {
                type = TYPE.ALPHA;
            } else if (qualifier.contains("beta")) {
                type = TYPE.BETA;
            } else if (qualifier.contains("test")) {
                type = TYPE.TEST;
            } else if (qualifier.contains("build")) {
                type = TYPE.DEV;
            }
        }
        this.type = type;
    }

    public boolean isTestVersion() {
        return type == TYPE.TEST;
    }

    public boolean isAlphaVersion() {
        return type == TYPE.ALPHA;
    }

    public boolean isBetaVersion() {
        return type == TYPE.BETA;
    }

    public boolean isReleaseVersion() {
        return type == TYPE.RELEASE;
    }

    public boolean isDevVersion() {
        return type == TYPE.DEV;
    }

    enum TYPE {
        RELEASE, BETA, ALPHA, TEST, DEV
    }
}
