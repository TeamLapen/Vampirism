package de.teamlapen.vampirism.common.integration;

import com.google.common.base.Preconditions;
import de.teamlapen.vampirism.common.util.Services;
import net.neoforged.fml.ModContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class IntegrationServices extends Services {

    @Nullable
    private ITerraBlenderBiomeProvider terraBlenderBiomeProvider;

    public IntegrationServices(ModContainer container) {
        super(container);
    }

    public boolean isUsingTerraBlender() {
        return Objects.requireNonNullElse(this.terraBlenderBiomeProvider, ITerraBlenderBiomeProvider.FALLBACK).isUsingTerraBlender();
    }

    //<editor-fold desc="Setter">

    @ApiStatus.Internal
    public void useTerraBlender(ITerraBlenderBiomeProvider provider) {
        Preconditions.checkArgument(this.terraBlenderBiomeProvider == null, "TerraBlender has already been set");
        this.terraBlenderBiomeProvider = provider;
    }

    //</editor-fold>
}
