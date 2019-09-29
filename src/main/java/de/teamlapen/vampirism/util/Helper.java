package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;


public class Helper {


    private final static Logger LOGGER = LogManager.getLogger();

    private static Method reflectionMethodExperiencePoints;

    /**
     * Checks if the entity can get sundamage at it's current position.
     * It is recommend to cache the value for a few ticks.
     *
     * @param entity
     * @return
     */
    public static boolean gettingSundamge(LivingEntity entity) {
        entity.getEntityWorld().getProfiler().startSection("vampirism_checkSundamage");
        if (entity instanceof PlayerEntity && entity.isSpectator()) return false;
        if (VampirismAPI.sundamageRegistry().getSundamageInDim(entity.getEntityWorld().getDimension().getType())) {
            if (!entity.getEntityWorld().isRaining()) {
                float angle = entity.getEntityWorld().getCelestialAngle(1.0F);
                //TODO maybe use this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)
                if (angle > 0.78 || angle < 0.24) {
                    BlockPos pos = new BlockPos(entity.posX, entity.posY + MathHelper.clamp(entity.getHeight() / 2.0F, 0F, 2F), entity.posZ);

                    if (canBlockSeeSun(entity.getEntityWorld(), pos)) {
                        try {
                            Biome biome = entity.getEntityWorld().getBiome(pos);
                            if (VampirismAPI.sundamageRegistry().getSundamageInBiome(biome)) {
                                if (!TotemTileEntity.isInsideVampireAreaCached(entity.getEntityWorld().getDimension(), new BlockPos(entity.posX, entity.posY + 1, entity.posZ))) { //For some reason client returns different value for #getPosition than server
                                    entity.getEntityWorld().getProfiler().endSection();
                                    return true;
                                }


                            }
                        } catch (NullPointerException e) {
                            //Strange thing which happen in 1.7.10, not sure about 1.8
                        }

                    }
                }

            }
        }
        entity.getEntityWorld().getProfiler().endSection();

        return false;
    }

    public static boolean canBlockSeeSun(World world, BlockPos pos) {
        if (pos.getY() >= world.getSeaLevel()) {
            return world.canBlockSeeSky(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());

            if (!world.canBlockSeeSky(blockpos)) {
                return false;
            } else {
                int liquidBlocks = 0;
                for (blockpos = blockpos.down(); blockpos.getY() > pos.getY(); blockpos = blockpos.down()) {
                    BlockState iblockstate = world.getBlockState(blockpos);
                    if (iblockstate.getBlock().getOpacity(iblockstate, world, blockpos) > 0) {
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

    @Nonnull
    public static EnumStrength getGarlicStrength(Entity e) {
        return getGarlicStrengthAt(e.getEntityWorld(), e.getPosition());
    }

    @Nonnull
    public static EnumStrength getGarlicStrengthAt(World world, BlockPos pos) {
        return VampirismAPI.getGarlicChunkHandler(world).getStrengthAtChunk(new ChunkPos(pos));
    }

    public static boolean canBecomeVampire(PlayerEntity player) {
        return FactionPlayerHandler.get(player).canJoin(VReference.VAMPIRE_FACTION);
    }

    public static boolean canTurnPlayer(IVampire biter, @Nullable PlayerEntity target) {
        if (biter instanceof IVampirePlayer) {
            return PermissionAPI.hasPermission(((IVampirePlayer) biter).getRepresentingPlayer(), Permissions.INFECT_PLAYER);
        } else {
            return !VampirismConfig.SERVER.disableMobBiteInfection.get();
        }
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
        if (skills == null) return true;
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
            return ModBiomes.vampire_forest.getRegistryName().equals(e.getEntityWorld().getBiome(e.getPosition()).getRegistryName());
        } catch (NullPointerException e1) {
            //http://openeye.openmods.info/crashes/8cef4d710e41adf9be8362e57ad70d28
            LOGGER.error("Nullpointer when checking biome. This is strange and should not happen", e1);
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

    public static int getExperiencePoints(LivingEntity entity, PlayerEntity player) {
        try {
            if (reflectionMethodExperiencePoints == null) {
                reflectionMethodExperiencePoints = ObfuscationReflectionHelper.findMethod(LivingEntity.class, SRGNAMES.MobEntity_getExperiencePoints, PlayerEntity.class); //mcpbot cannot find the mapping for LivingEntity#getExperiencePoints, but MobEntity overrides it
            }
            return (int) reflectionMethodExperiencePoints.invoke(entity, player);
        } catch (Exception e) {
            LOGGER.error("Failed to get experience points", e);
        }
        return 0;
    }

}
