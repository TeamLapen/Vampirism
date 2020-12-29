package de.teamlapen.vampirism.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBiomeReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class Helper {


    private final static Logger LOGGER = LogManager.getLogger();
    private final static ResourceLocation EMPTY_ID = new ResourceLocation("null", "null");
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
        RegistryKey<World> worldKey = Helper.getWorldKey(world);
        if (VampirismAPI.sundamageRegistry().getSundamageInDim(worldKey)) {
            if (!(world instanceof World) || !((World) world).isRaining()) {
                float angle = world.func_242415_f(1.0F);
                //TODO maybe use this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)
                if (angle > 0.78 || angle < 0.24) {
                    BlockPos pos = new BlockPos(entity.getPosX(), entity.getPosY() + MathHelper.clamp(entity.getHeight() / 2.0F, 0F, 2F), entity.getPosZ());
                    if (canBlockSeeSun(world, pos)) {
                        try {
                            ResourceLocation biome = getBiomeId(world, pos);
                            if (VampirismAPI.sundamageRegistry().getSundamageInBiome(biome)) {
                                if (!TotemHelper.isInsideVampireAreaCached(worldKey, new BlockPos(entity.getPosX(), entity.getPosY() + 1, entity.getPosZ()))) { //For some reason client returns different value for #getPosition than server
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
            return world.canSeeSky(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
            if (!world.canSeeSky(blockpos)) {
                return false;
            } else {
                int liquidBlocks = 0;
                for (blockpos = blockpos.down(); blockpos.getY() > pos.getY(); blockpos = blockpos.down()) {
                    BlockState state = world.getBlockState(blockpos);
                    if (state.getMaterial().isLiquid()) { // if fluid than it propagates the light until `vpSundamageWaterBlocks`
                        liquidBlocks++;
                        if (liquidBlocks >= VampirismConfig.BALANCE.vpSundamageWaterBlocks.get()) {
                            return false;
                        }
                    } else if (state.isSolid() && (state.isSolidSide(world, pos, Direction.DOWN) || state.isSolidSide(world, pos, Direction.UP))) { //solid block blocks the light (fence is solid too?)
                        return false;
                    } else if (state.getOpacity(world, blockpos) > 0) { //if not solid, but propagates no light
                        return false;
                    }
                }
                return true;
            }
        }
    }

    @Nonnull
    public static EnumStrength getGarlicStrength(Entity e, RegistryKey<World> world) {
        return getGarlicStrengthAt(world, e.getPosition());
    }

    @Nonnull
    public static EnumStrength getGarlicStrengthAt(RegistryKey<World> world, BlockPos pos) {
        return VampirismAPI.getGarlicChunkHandler(world).getStrengthAtChunk(new ChunkPos(pos));
    }

    @Nonnull
    public static RegistryKey<World> getWorldKey(IWorld world) {
        return world instanceof World ? ((World) world).getDimensionKey() : world instanceof IServerWorld ? ((IServerWorld) world).getWorld().getDimensionKey() : World.OVERWORLD;
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
        World w = e.getEntityWorld();
        Biome b = w.getBiome(e.getPosition());
        ResourceLocation biomeId = getBiomeId(w, b);
        Objects.requireNonNull(biomeId, "Cannot determine id of local biome");
        return ModBiomes.vampire_forest.getRegistryName().equals(biomeId) || ModBiomes.vampire_forest_hills.getRegistryName().equals(biomeId);
    }

    public static ResourceLocation getBiomeId(Entity e) {
        return getBiomeId(e.getEntityWorld(), e.getPosition());
    }

    public static ResourceLocation getBiomeId(IBiomeReader world, BlockPos pos) {
        return getBiomeId(world, world.getBiome(pos));
    }

    public static ResourceLocation getBiomeId(IBiomeReader world, Biome biome) {
        return world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome);
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
        return playerHandler.getCurrentFactionPlayer().map(ISkillPlayer::getSkillHandler).map(s -> s.isSkillEnabled(requiredSkill)).orElse(false);
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
        return 3; //return at least some XP points if there is an issue
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

    public static <T extends Entity> Optional<T> createEntity(@Nonnull EntityType<T> type, @Nonnull World world) {
        T e = type.create(world);
        if (e == null) {
            LOGGER.warn("Failed to create entity of type {}", type.getRegistryName());
            return Optional.empty();
        }
        return Optional.of(e);
    }

    @Nonnull
    public static ResourceLocation getIDSafe(ForgeRegistryEntry<?> registryObject) {
        ResourceLocation id = registryObject.getRegistryName();
        if (id == null) {
            LOGGER.warn("RegistryName not set for {}", registryObject);
            return EMPTY_ID;
        }
        return id;
    }

}
