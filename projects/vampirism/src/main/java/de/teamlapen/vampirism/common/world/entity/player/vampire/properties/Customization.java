package de.teamlapen.vampirism.common.world.entity.player.vampire.properties;

import de.teamlapen.sync.PropertyParentSync;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.resources.Identifier;

import java.util.stream.Collectors;

public class Customization extends PropertyParentSync {

    private Identifier eyeType = VIdentifier.EMPTY;
    private Identifier fangType = VIdentifier.EMPTY;
    private boolean glowingEyes;
    private IWingsEntity.Texture wingsTexture = IWingsEntity.Texture.DEFAULT;
    private final VampirePlayer player;

    public Customization(VampirePlayer parent) {
        super(parent);
        this.player = parent;
    }

    @Override
    protected void registerProperties() {
        registerProperty(VIdentifier.mod("eye_type")).simple(VIdentifier.EMPTY, this::eyeType, this::setEyeType);
        registerProperty(VIdentifier.mod("fang_type")).simple(VIdentifier.EMPTY, this::fangType, this::setFangType);
        registerProperty(VIdentifier.mod("glowing_eyes")).simple(false, this::glowingEyes, this::setGlowingEyes);
        registerProperty(VIdentifier.mod("wings_texture")).simple(IWingsEntity.Texture.DEFAULT, this::wingsTexture, this::setWingsTexture);
    }

    public void setEyeType(Identifier eyeType) {
        this.eyeType = eyeType;
    }

    public void setFangType(Identifier fangType) {
        this.fangType = fangType;
    }

    public void setGlowingEyes(boolean glowingEyes) {
        this.glowingEyes = glowingEyes;
    }

    public Identifier eyeType() {
        return this.eyeType;
    }

    public Identifier fangType() {
        return this.fangType;
    }

    public boolean glowingEyes() {
        return this.glowingEyes;
    }

    public IWingsEntity.Texture wingsTexture() {
        return this.wingsTexture;
    }

    public void setWingsTexture(IWingsEntity.Texture wingsTexture) {
        var wings = VampirismMod.services().wingsManager().getAvailableWings(this.player.asEntity()).collect(Collectors.toSet());
        if (!wings.contains(wingsTexture)) return;
        this.wingsTexture = wingsTexture;
    }
}
