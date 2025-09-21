package de.teamlapen.vampirism.client.renderer.entities.state;

import de.teamlapen.vampirism.common.entity.player.VampirismPlayerAttributes;
import net.minecraft.world.entity.ambient.Bat;

public interface IPlayerRenderState {

    VampirismPlayerAttributes vampirism$attributes();

    void vampirism$attributes(VampirismPlayerAttributes attributes);

    Bat vampirism$bat();

    void vampirism$bat(Bat bat);

}
