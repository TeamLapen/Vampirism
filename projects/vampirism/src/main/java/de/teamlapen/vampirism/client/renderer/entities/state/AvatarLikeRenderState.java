package de.teamlapen.vampirism.client.renderer.entities.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;

public class AvatarLikeRenderState extends HumanoidRenderState {

    public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();
}
