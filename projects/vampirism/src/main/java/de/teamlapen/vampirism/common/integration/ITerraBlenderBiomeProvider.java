package de.teamlapen.vampirism.common.integration;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ITerraBlenderBiomeProvider {

    boolean isUsingTerraBlender();

    ITerraBlenderBiomeProvider FALLBACK = () -> false;
}
