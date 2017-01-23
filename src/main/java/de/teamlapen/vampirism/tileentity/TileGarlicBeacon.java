package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;

/**
 * 1.10
 *
 * @author maxanier
 */
public class TileGarlicBeacon extends TileEntity {
    private int id;
    private EnumStrength strength = EnumStrength.MEDIUM;
    private int r = 1;
    private boolean registered = false;

    @Override
    public void invalidate() {
        super.invalidate();
        unregister();
    }

    public void onTouched(EntityPlayer player) {
        VampirePlayer vampire = VampirePlayer.get(player);
        if (vampire.getLevel() > 0) {
            DamageHandler.affectVampireGarlicDirect(vampire, strength);
        }
    }

    @Override
    public void validate() {
        super.validate();
        register();
    }

    private void register() {
        if (registered) {
            return;
        }
        int baseX = (getPos().getX() >> 4);
        int baseZ = (getPos().getZ() >> 4);
        ChunkPos[] chunks = new ChunkPos[(2 * r + 1) * (2 * r + 1)];
        int i = 0;
        for (int x = -r; x <= +r; x++) {
            for (int z = -r; z <= r; z++) {
                chunks[i++] = new ChunkPos(x + baseX, z + baseZ);
            }
        }
        id = VampirismAPI.getGarlicChunkHandler(getWorld()).registerGarlicBlock(strength, chunks);
        registered = true;

    }

    private void unregister() {
        if (registered) {
            VampirismAPI.getGarlicChunkHandler(getWorld()).removeGarlicBlock(id);
        }
    }
}
