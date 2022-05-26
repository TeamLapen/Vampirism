package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.render.particle.FlyingBloodEntityParticle;
import de.teamlapen.vampirism.client.render.particle.FlyingBloodParticle;
import de.teamlapen.vampirism.client.render.particle.GenericParticle;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModParticleFactories {

    public static void registerFactoriesUnsafe() {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        manager.register(ModParticles.flying_blood.get(), new FlyingBloodParticle.Factory());
        manager.register(ModParticles.flying_blood_entity.get(), new FlyingBloodEntityParticle.Factory());
        manager.register(ModParticles.generic.get(), new GenericParticle.Factory());
    }
}
