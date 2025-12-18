package de.teamlapen.vampirism.client.renderer.entities.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;

public class AvatarLikeRenderState extends HumanoidRenderState {

    public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();
    public boolean isSpectator;
    public boolean showHat = true;
    public boolean showJacket = true;
    public boolean showLeftPants = true;
    public boolean showRightPants = true;
    public boolean showLeftSleeve = true;
    public boolean showRightSleeve = true;
    public float fallFlyingTimeInTicks;
}
