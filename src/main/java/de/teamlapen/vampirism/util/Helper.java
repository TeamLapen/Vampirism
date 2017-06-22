package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;


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
        entity.getEntityWorld().profiler.startSection("vampirism_checkSundamage");
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) return false;
        if (VampirismAPI.sundamageRegistry().getSundamageInDim(entity.getEntityWorld().provider.getDimension())) {
            if (!entity.getEntityWorld().isRaining()) {
                float angle = entity.getEntityWorld().getCelestialAngle(1.0F);
                //TODO maybe use this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)
                if (angle > 0.78 || angle < 0.24) {
                    BlockPos pos = new BlockPos(entity.posX + 0.5, entity.posY + 0, entity.posZ + 0.5);

                    if (canBlockSeeSun(entity.getEntityWorld(), pos)) {
                        try {
                            Biome biome = entity.getEntityWorld().getBiome(pos);
                            if (VampirismAPI.sundamageRegistry().getSundamageInBiome(biome)) {
                                entity.getEntityWorld().profiler.endSection();
                                return true;
                            }
                        } catch (NullPointerException e) {
                            //Strange thing which happen in 1.7.10, not sure about 1.8
                        }

                    }
                }

            }
        }
        entity.getEntityWorld().profiler.endSection();

        return false;
    }

    public static boolean canBlockSeeSun(World world, BlockPos pos) {
        if (pos.getY() >= world.getSeaLevel()) {
            return world.canSeeSky(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());

            if (!world.canSeeSky(blockpos)) {
                return false;
            } else {
                int liquidBlocks = 0;
                for (blockpos = blockpos.down(); blockpos.getY() > pos.getY(); blockpos = blockpos.down()) {
                    IBlockState iblockstate = world.getBlockState(blockpos);

                    if (iblockstate.getBlock().getLightOpacity(iblockstate, world, blockpos) > 0) {
                        if (iblockstate.getMaterial().isLiquid()) {
                            liquidBlocks++;
                            if (liquidBlocks >= Balance.vp.SUNDAMAGE_WATER_BLOCKS) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }

                }

                return true;
            }
        }
    }

    public static EnumStrength getGarlicStrength(Entity e) {
        return getGarlicStrengthAt(e.getEntityWorld(), e.getPosition());
    }

    public static EnumStrength getGarlicStrengthAt(World world, BlockPos pos) {
        return VampirismAPI.getGarlicChunkHandler(world).getStrengthAtChunk(new ChunkPos(pos));
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
        if (e == null) return false;
        try {
            return ModBiomes.vampireForest.getRegistryName().equals(e.getEntityWorld().getBiome(e.getPosition()).getRegistryName());
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
