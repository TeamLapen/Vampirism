package de.teamlapen.vampirism.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
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
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.world.gen.biome.VampireBiome;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Map;


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
    public static boolean gettingSundamge(LivingEntity entity, IWorld world, @Nullable IProfiler profiler) {
        if (profiler != null) profiler.startSection("vampirism_checkSundamage");
        if (entity instanceof PlayerEntity && entity.isSpectator()) return false;
        if (VampirismAPI.sundamageRegistry().getSundamageInDim(world.getDimension().getType())) {
            if (!(world instanceof World) || !((World) world).isRaining()) {
                float angle = world.getCelestialAngle(1.0F);
                //TODO maybe use this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)
                if (angle > 0.78 || angle < 0.24) {
                    BlockPos pos = new BlockPos(entity.posX, entity.posY + MathHelper.clamp(entity.getHeight() / 2.0F, 0F, 2F), entity.posZ);
                    if (canBlockSeeSun(world, pos)) {
                        try {
                            Biome biome = world.getBiome(pos);
                            if (VampirismAPI.sundamageRegistry().getSundamageInBiome(biome)) {
                                if (!TotemTileEntity.isInsideVampireAreaCached(world.getDimension(), new BlockPos(entity.posX, entity.posY + 1, entity.posZ))) { //For some reason client returns different value for #getPosition than server
                                    if (profiler != null) profiler.endSection();
                                    return true;
                                }
                            }
                        } catch (NullPointerException e) {
                            //Strange thing which happen in 1.7.10, not sure about 1.14
                        }

                    }
                }

            }
        }
        if (profiler != null) profiler.endSection();

        return false;
    }

    public static boolean canBlockSeeSun(IWorld world, BlockPos pos) {
        if (pos.getY() >= world.getSeaLevel()) {
            return world.isSkyLightMax(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
            if (!world.isSkyLightMax(blockpos)) {
                return false;
            } else {
                int liquidBlocks = 0;
                for (blockpos = blockpos.down(); blockpos.getY() > pos.getY(); blockpos = blockpos.down()) {
                    BlockState iblockstate = world.getBlockState(blockpos);
                    if (iblockstate.getOpacity(world, blockpos) > 0) {
                        if (iblockstate.getMaterial().isLiquid()) {
                            liquidBlocks++;
                            if (liquidBlocks >= VampirismConfig.BALANCE.vpSundamageWaterBlocks.get()) {
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
    public static EnumStrength getGarlicStrength(Entity e, IWorld world) {
        return getGarlicStrengthAt(world, e.getPosition());
    }

    @Nonnull
    public static EnumStrength getGarlicStrengthAt(IWorld world, BlockPos pos) {
        return VampirismAPI.getGarlicChunkHandler(world).getStrengthAtChunk(new ChunkPos(pos));
    }

    public static boolean canBecomeVampire(PlayerEntity player) {
        return FactionPlayerHandler.getOpt(player).map(v -> v.canJoin(VReference.VAMPIRE_FACTION)).orElse(false);
    }

    public static boolean canTurnPlayer(IVampire biter, @Nullable PlayerEntity target) {
        if (biter instanceof IVampirePlayer) {
            if (!VampirismConfig.SERVER.playerCanTurnPlayer.get()) return false;
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

    public static boolean isHunter(PlayerEntity entity) {
        return VampirismAPI.getFactionPlayerHandler((entity)).map(h -> VReference.HUNTER_FACTION.equals(h.getCurrentFaction())).orElse(false);
    }

    public static boolean isVampire(PlayerEntity entity) {
        return VampirismAPI.getFactionPlayerHandler((entity)).map(h -> VReference.VAMPIRE_FACTION.equals(h.getCurrentFaction())).orElse(false);
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
            return e.getEntityWorld().getBiome(e.getPosition()) instanceof VampireBiome;
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
        if (requiredSkill == null) return true;
        return playerHandler.getCurrentFactionPlayer().map(fp -> fp.getSkillHandler()).map(s -> s.isSkillEnabled(requiredSkill)).orElse(false);
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

    /**
     * Returns false on client side
     * Determines the gender of the player by checking the skin and assuming 'slim'->female.
     *
     * @param p Player
     * @return True if female
     */
    public static boolean attemptToGuessGenderSafe(PlayerEntity p) {
        if (p instanceof ServerPlayerEntity) { //Could extend to also support client side, but have to use proxy then
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textureMap = ((ServerPlayerEntity) p).server.getMinecraftSessionService().getTextures(p.getGameProfile(), false);
            if (textureMap.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                MinecraftProfileTexture skinTexture = textureMap.get(MinecraftProfileTexture.Type.SKIN);
                return "slim".equals(skinTexture.getMetadata("model"));
            }
        }
        return false;
    }

}
