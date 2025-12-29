package de.teamlapen.vampirism.common.world.entity.player.vampire.properties;

import de.teamlapen.sync.PropertyParentSync;
import de.teamlapen.sync.PropertySync;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;

public class Customization extends PropertyParentSync {

    private int eyeType;
    private int fangType;
    private boolean glowingEyes;

    public Customization(PropertySync parent) {
        super(parent);
    }

    @Override
    protected void registerProperties() {
        registerProperty(VResourceLocation.mod("eye_type")).simple(0, this::eyeType, this::setEyeType);
        registerProperty(VResourceLocation.mod("fang_type")).simple(0, this::fangType, this::setFangType);
        registerProperty(VResourceLocation.mod("glowing_eyes")).simple(false, this::glowingEyes, this::setGlowingEyes);
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
}
