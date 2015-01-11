package de.teamlapen.vampirism.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class VampireEntityEventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityCreature && VampireMob.get((EntityCreature) event.entity) == null) {
			VampireMob.register((EntityCreature) event.entity);
		}
	}

}
