package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;

public class ModParticleDescriptionProvider extends ParticleDescriptionProvider {

    protected ModParticleDescriptionProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        this.spriteSet(ModParticles.SWORD_CHARGE.get(),
                VIdentifier.mc("glitter_1")
        );
        this.spriteSet(ModParticles.SWORD_CHARGED.get(),
                VIdentifier.mc("critical_hit")
        );
        this.spriteSet(ModParticles.PEDESTAL.get(),
                VIdentifier.mc("glitter_1")
        );
        this.spriteSet(ModParticles.MOTHER.get(),
                VIdentifier.mc("critical_hit")
        );
        this.spriteSet(ModParticles.ALTAR_INFUSION.get(),
                VIdentifier.mc("critical_hit")
        );
        this.spriteSet(ModParticles.FLYING_BLOOD_ENTITY.get(),
                VIdentifier.mc("critical_hit")
        );
        this.spriteSet(ModParticles.GENERIC.get(),
                VIdentifier.mc("generic_4"), 
                VIdentifier.mc("spell_1"),
                VIdentifier.mc("spell_4"), 
                VIdentifier.mc("spell_6"), 
                VIdentifier.mc("effect_4"), 
                VIdentifier.mc("effect_6"),
                VIdentifier.mc("drip_hang")
        );
        this.spriteSet(ModParticles.SANGUINARE.get(),
                VIdentifier.mod("sanguinare")
        );
        this.spriteSet(ModParticles.DARK_SPRUCE_OAK_LEAVES.get(),
                VIdentifier.mod("dark_needle"),
                8,
                false
        );
        this.spriteSet(ModParticles.BLOOD_SHRED.get(),
                VIdentifier.mod("shred"),
                4,false);
    }
}
