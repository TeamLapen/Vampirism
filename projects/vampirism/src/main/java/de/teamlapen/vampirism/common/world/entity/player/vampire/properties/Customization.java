package de.teamlapen.vampirism.common.world.entity.player.vampire.properties;

import de.teamlapen.sync.PropertyParentSync;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.resources.Identifier;

public class Customization extends PropertyParentSync {

    public static final Identifier NONE = VIdentifier.mod("none");

    public static final int DEFAULT_POSITION = -1;
    public static final int FACE_SIZE = 8;
    public static final int MAX_POSITION = FACE_SIZE * FACE_SIZE - 1;

    private Identifier eyeType = VIdentifier.EMPTY;
    private Identifier fangType = VIdentifier.EMPTY;
    private int eyePosition = DEFAULT_POSITION;
    private int fangPosition = DEFAULT_POSITION;
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
        registerProperty(VIdentifier.mod("eye_position")).simple(DEFAULT_POSITION, this::eyePosition, this::setEyePosition);
        registerProperty(VIdentifier.mod("fang_position")).simple(DEFAULT_POSITION, this::fangPosition, this::setFangPosition);
        registerProperty(VIdentifier.mod("glowing_eyes")).simple(false, this::glowingEyes, this::setGlowingEyes);
        registerProperty(VIdentifier.mod("wings_texture")).simple(IWingsEntity.Texture.CODEC).defaultValue(IWingsEntity.Texture.DEFAULT).provider(() -> this.wingsTexture).clientLoader(x -> {
            var diff = this.wingsTexture == x;
            this.wingsTexture = x;
            return diff;
        }).serverLoader(this::loadWingsTexture);
    }

    public void setEyeType(Identifier eyeType) {
        this.eyeType = eyeType;
    }

    public void setFangType(Identifier fangType) {
        this.fangType = fangType;
    }

    public void setEyePosition(int position) {
        if (!isValidPosition(position)) return;
        this.eyePosition = position;
    }

    public void setFangPosition(int position) {
        if (!isValidPosition(position)) return;
        this.fangPosition = position;
    }

    public int eyePosition() {
        return this.eyePosition;
    }

    public int fangPosition() {
        return this.fangPosition;
    }

    private static boolean isValidPosition(int position) {
        return position >= DEFAULT_POSITION && position <= MAX_POSITION;
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
        if (!VampirismMod.services().wingsManager().isAvailable(this.player.asEntity(), wingsTexture)) return;
        this.wingsTexture = wingsTexture;
    }

    /**
     * Loads the saved wings texture. Falls back to {@link IWingsEntity.Texture#DEFAULT} if the texture is no longer available (e.g. a seasonal texture out of season)
     */
    private void loadWingsTexture(IWingsEntity.Texture wingsTexture) {
        this.wingsTexture = VampirismMod.services().wingsManager().isAvailable(this.player.asEntity(), wingsTexture) ? wingsTexture : IWingsEntity.Texture.DEFAULT;
    }
}
