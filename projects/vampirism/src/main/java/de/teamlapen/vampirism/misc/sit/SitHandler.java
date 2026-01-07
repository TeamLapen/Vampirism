/*
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */

package de.teamlapen.vampirism.misc.sit;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class SitHandler {

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;

        //BreakEvent gets a World in its constructor, so the cast is safe
        SitEntity entity = SitUtil.getSitEntity((Level) event.getLevel(), event.getPos());

        if (entity != null) {
            entity.discard();
        }
    }
}