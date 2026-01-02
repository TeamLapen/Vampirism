package de.teamlapen.vampirism.common.integration;

public interface ITerraBlenderBiomeProvider {

    boolean isUsingTerraBlender();

    ITerraBlenderBiomeProvider FALLBACK = () -> false;
}
