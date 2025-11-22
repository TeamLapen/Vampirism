package de.teamlapen.factions.common.world;

import de.teamlapen.factions.common.core.FactionDamageTypes;
import de.teamlapen.factions.common.minions.MinionDamageSource;
import de.teamlapen.factions.common.minions.MinionEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageSources {

    private final Registry<DamageType> damageTypes;
    private final DamageSource leaveFaction;


    public ModDamageSources(RegistryAccess access) {
        this.damageTypes = access.lookupOrThrow(Registries.DAMAGE_TYPE);
        this.leaveFaction = init(FactionDamageTypes.LEAVE_FACTION);
    }

    private DamageSource init(ResourceKey<DamageType> key) {
        return new DamageSource(this.damageTypes.getOrThrow(key));
    }

    public MinionDamageSource minion(MinionEntity<?> entity) {
        return new MinionDamageSource(this.damageTypes.getOrThrow(FactionDamageTypes.MINION), entity);
    }

    public DamageSource leaveFaction() {
        return leaveFaction;
    }

}
