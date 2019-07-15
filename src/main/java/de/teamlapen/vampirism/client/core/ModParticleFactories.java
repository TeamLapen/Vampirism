package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.render.particle.*;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;

public class ModParticleFactories {

    public static void registerFactories() {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(ModParticles.flying_blood, new FlyingBloodParticle.Factory());
        manager.registerFactory(ModParticles.flying_blood_entity, new FlyingBloodEntityParticle.Factory());
        manager.registerFactory(ModParticles.halloween, new HalloweenParticle.Factory());
        manager.registerFactory(ModParticles.heal, new HealingParticle.Factory());
        manager.registerFactory(ModParticles.generic, new GenericParticle.Factory());
    }
}
