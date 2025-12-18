package de.teamlapen.vampirism.common.world.attachments;

import de.teamlapen.factions.common.factions.minions.MinionDamageSource;
import de.teamlapen.factions.common.factions.minions.MinionEntity;
import de.teamlapen.vampirism.common.core.ModDamageTypes;
import de.teamlapen.vampirism.common.util.DBNODamageSource;
import de.teamlapen.vampirism.common.util.PlayerAttackDamageSourceBypassArmor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModDamageSources {

    private final Registry<DamageType> damageTypes;
    private final DamageSource sunDamage;
    private final DamageSource vampireOnFire;
    private final DamageSource vampireInFire;
    private final DamageSource holyWater;
    private final DamageSource noBlood;
    private final DamageSource mother;
    private final DamageSource bleeding;
    private final DamageSource leaveFaction;

    public ModDamageSources(RegistryAccess access) {
        this.damageTypes = access.lookupOrThrow(Registries.DAMAGE_TYPE);
        this.sunDamage = init(ModDamageTypes.SUN_DAMAGE);
        this.vampireOnFire = init(ModDamageTypes.VAMPIRE_ON_FIRE);
        this.vampireInFire = init(ModDamageTypes.VAMPIRE_IN_FIRE);
        this.holyWater = init(ModDamageTypes.HOLY_WATER);
        this.noBlood = init(ModDamageTypes.NO_BLOOD);
        this.mother = init(ModDamageTypes.MOTHER);
        this.bleeding = init(ModDamageTypes.BLEEDING);
        this.leaveFaction = init(ModDamageTypes.LEAVE_FACTION);
    }

    private DamageSource init(ResourceKey<DamageType> key) {
        return new DamageSource(this.damageTypes.getOrThrow(key));
    }

    public DamageSource sunDamage() {
        return this.sunDamage;
    }

    public DamageSource vampireOnFire() {
        return this.vampireOnFire;
    }

    public DamageSource vampireInFire() {
        return this.vampireInFire;
    }

    public DamageSource holyWater() {
        return this.holyWater;
    }

    public DamageSource noBlood() {
        return this.noBlood;
    }

    public DamageSource mother() {
        return this.mother;
    }

    public DamageSource stake(LivingEntity attacker) {
        return new DamageSource(this.damageTypes.getOrThrow(ModDamageTypes.STAKE), attacker);
    }

    public MinionDamageSource minion(@NotNull MinionEntity<?> entity) {
        return new MinionDamageSource(this.damageTypes.getOrThrow(ModDamageTypes.MINION), entity);
    }

    public DBNODamageSource dbno(@Nullable Component originalSource) {
        return new DBNODamageSource(this.damageTypes.getOrThrow(ModDamageTypes.DBNO), originalSource);
    }

    public PlayerAttackDamageSourceBypassArmor getPlayerAttackWithBypassArmor(@NotNull Player attacker) {
        return new PlayerAttackDamageSourceBypassArmor(this.damageTypes.getOrThrow(DamageTypes.PLAYER_ATTACK), attacker);
    }

    public DamageSource bleeding() {
        return this.bleeding;
    }

    public DamageSource leaveFaction() {
        return leaveFaction;
    }
}
