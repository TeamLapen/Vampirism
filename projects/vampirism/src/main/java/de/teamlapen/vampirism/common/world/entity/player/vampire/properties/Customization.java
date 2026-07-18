package de.teamlapen.vampirism.common.world.entity.player.vampire.properties;

import de.teamlapen.sync.PropertyParentSync;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;

import java.util.stream.Collectors;

public class Customization extends PropertyParentSync {

    private int eyeType;
    private int fangType;
    private boolean glowingEyes;
    private IWingsEntity.Texture wingsTexture = IWingsEntity.Texture.DEFAULT;
    private final VampirePlayer player;

    public Customization(VampirePlayer parent) {
        super(parent);
        this.player = parent;
    }

    @Override
    protected void registerProperties() {
        registerProperty(VIdentifier.mod("eye_type")).simple(0, this::eyeType, this::setEyeType);
        registerProperty(VIdentifier.mod("fang_type")).simple(0, this::fangType, this::setFangType);
        registerProperty(VIdentifier.mod("glowing_eyes")).simple(false, this::glowingEyes, this::setGlowingEyes);
        registerProperty(VIdentifier.mod("wings_texture")).simple(IWingsEntity.Texture.DEFAULT, this::wingsTexture, this::setWingsTexture);
    }

    public boolean setEyeType(int eyeType) {
        if (eyeType >= REFERENCE.EYE_TYPE_COUNT || eyeType < 0) {
            return false;
        }
        this.eyeType = eyeType;
        return true;
    }

    public boolean setFangType(int fangType) {
        if (fangType >= REFERENCE.FANG_TYPE_COUNT || fangType < 0) {
            return false;
        }
        this.fangType = fangType;
        return true;
    }

    public void setGlowingEyes(boolean glowingEyes) {
        this.glowingEyes = glowingEyes;
    }

    public int eyeType() {
        return this.eyeType;
    }

    public int fangType() {
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
