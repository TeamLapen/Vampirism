package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.IGarlicBlock;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;


public class Helper {
    public static void spawnParticlesAroundEntity(EntityLivingBase e, EnumParticleTypes particle, double maxDistance, int amount) {
        //TODO implement
    }

    /**
     * Checks if the entity can get sundamage at it's current position.
     * It is recommend to cache the value for a few ticks.
     *
     * @param entity
     * @return
     */
    public static boolean gettingSundamge(EntityLivingBase entity) {
        if (entity.worldObj != null) {
            entity.worldObj.theProfiler.startSection("vampirism_checkSundamage");
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) return false;
            if (VampirismAPI.sundamageRegistry().getSundamageInDim(entity.worldObj.provider.getDimension())) {
                if (!entity.worldObj.isRaining()) {
                    float angle = entity.worldObj.getCelestialAngle(1.0F);
                    //TODO maybe use this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)
                    if (angle > 0.78 || angle < 0.24) {
                        BlockPos pos = new BlockPos(entity.posX + 0.5, entity.posY + 0, entity.posZ + 0.5);

                        if (entity.worldObj.canBlockSeeSky(pos)) {
                            ResourceLocation biomeID = null;
                            try {
                                biomeID = entity.worldObj.getBiomeGenForCoords(pos).getRegistryName();
                                if (VampirismAPI.sundamageRegistry().getSundamageInBiome(biomeID)) {
                                    entity.worldObj.theProfiler.endSection();
                                    return true;
                                }
                            } catch (NullPointerException e) {
                                //Strange thing which happen in 1.7.10, not sure about 1.8
                            }

                        }
                    }

                }
            }
            entity.worldObj.theProfiler.endSection();
        }
        return false;
    }

    public static EnumGarlicStrength getGarlicStrength(BlockPos pos) {
        return EnumGarlicStrength.NONE;//TODO
    }

    public static EnumGarlicStrength gettingGarlicDamage(EntityLivingBase entity) {
        //TODO Check performance
        if (entity.worldObj == null) return EnumGarlicStrength.NONE;
        entity.worldObj.theProfiler.startSection("vampirism_checkGarlic");
        EnumGarlicStrength max = EnumGarlicStrength.NONE;
        BlockPos middle = entity.getPosition();
        int dist = Balance.general.GARLIC_CHECK_RANGE;
        int minX = middle.getX() - dist;
        int minY = middle.getY() - Balance.general.GARLIC_CHECK_VERTICAL_RANGE;
        if (minY < 0) minY = 0;
        int minZ = middle.getZ() - dist;
        int maxX = middle.getX() + dist + 1;
        int maxY = middle.getY() + Balance.general.GARLIC_CHECK_VERTICAL_RANGE + 1 + 1;
        int maxZ = middle.getZ() + dist + 1;
        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    if (entity.worldObj.getBlockState(blockpos.setPos(x, y, z)).getBlock() instanceof IGarlicBlock) {
                        max = ((IGarlicBlock) entity.worldObj.getBlockState(blockpos).getBlock()).getGarlicStrength(entity.worldObj, blockpos);
                        if (max == EnumGarlicStrength.STRONG) {
                            entity.worldObj.theProfiler.endSection();
                            return max;
                        }
                    }
                }
            }
        }
        entity.worldObj.theProfiler.endSection();
        return max;
    }

    public static boolean canBecomeVampire(EntityPlayer player) {
        return FactionPlayerHandler.get(player).canJoin(VReference.VAMPIRE_FACTION);
    }

    /**
     * Checks if
     *
     * @return If the given entity is a vampire (Either a player in the vampire faction or a vampire entity
     */
    public static boolean isVampire(Entity entity) {
        return VReference.VAMPIRE_FACTION.equals(VampirismAPI.factionRegistry().getFaction(entity));
    }

    public static boolean isHunter(Entity entity) {
        return VReference.HUNTER_FACTION.equals(VampirismAPI.factionRegistry().getFaction(entity));
    }

    /**
     * @return Checks if all given skills are enabled
     */
    public static boolean areSkillsEnabled(ISkillHandler skillHandler, ISkill... skills) {
        for (ISkill skill : skills) {
            if (!skillHandler.isSkillEnabled(skill)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEntityInVampireBiome(Entity e) {
        if (e == null || e.worldObj == null) return false;
        try {
            return ModBiomes.vampireForest.getRegistryName().equals(e.worldObj.getBiomeGenForCoords(e.getPosition()).getRegistryName());
        } catch (NullPointerException e1) {
            //http://openeye.openmods.info/crashes/8cef4d710e41adf9be8362e57ad70d28
            VampirismMod.log.e("Helper", e1, "Nullpointer when checking biome. This is strange and should not happen");
            return false;
        }
    }

    /**
     * Checks if the given {@link IFactionLevelItem} can be used by the given player
     */
    public static boolean canUseFactionItem(ItemStack stack, IFactionLevelItem item, IFactionPlayerHandler playerHandler) {
        IPlayableFaction usingFaction = item.getUsingFaction(stack);
        ISkill requiredSkill = item.getRequiredSkill(stack);
        int reqLevel = item.getMinLevel(stack);
        if (usingFaction != null && !playerHandler.isInFaction(usingFaction)) return false;
        if (playerHandler.getCurrentLevel() < reqLevel) return false;
        return !(requiredSkill != null && (playerHandler.getCurrentFactionPlayer() == null || !playerHandler.getCurrentFactionPlayer().getSkillHandler().isSkillEnabled(requiredSkill)));
    }
}
