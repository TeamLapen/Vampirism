package de.teamlapen.vampirism.util;

import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.blocks.candle.CandleHolderBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.tags.ModBiomeTags;
import de.teamlapen.vampirism.core.tags.ModDamageTypeTags;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.items.VampireCloakItem;
import de.teamlapen.vampirism.items.crossbow.arrow.VampireKillerBehavior;
import de.teamlapen.vampirism.world.fog.FogLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class Helper {


    private final static Logger LOGGER = LogManager.getLogger();

    /**
     * Checks if the entity can get sundamage at its current position.
     * It is recommended to cache the value for a few ticks.
     */
    public static boolean gettingSundamge(LivingEntity entity, LevelAccessor world) {
        if (entity instanceof Player && entity.isSpectator()) return false;
        if (VampirismAPI.sundamageRegistry().hasSunDamage(world, entity.blockPosition())) {
            if (!(world instanceof Level) || !((Level) world).isRaining()) {
                //TODO maybe use this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)
                if (isDay(world)) {
                    BlockPos pos = new BlockPos((int) entity.getX(), (int) (entity.getY() + Mth.clamp(entity.getBbHeight() / 2.0F, 0F, 2F)), (int) entity.getZ());
                    if (canBlockSeeSun(world, pos)) {
                        return world instanceof Level && !FogLevel.get(((Level) world)).isInsideArtificialVampireFogArea(new BlockPos((int) entity.getX(), (int) (entity.getY() + 1), (int) entity.getZ()));
                    }
                }
            }
        }
        return false;
    }

    public static boolean isDay(LevelAccessor level) {
        float angle = level.getTimeOfDay(1.0F);
        return angle > 0.78 || angle < 0.24;
    }

    public static boolean canBlockSeeSun(@NotNull LevelAccessor world, @NotNull BlockPos pos) {
        if (pos.getY() >= world.getSeaLevel()) {
            return world.canSeeSky(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
            if (!world.canSeeSky(blockpos)) {
                return false;
            } else {
                int liquidBlocks = 0;
                for (blockpos = blockpos.below(); blockpos.getY() > pos.getY(); blockpos = blockpos.below()) {
                    BlockState state = world.getBlockState(blockpos);
                    if (state.liquid()) { // if fluid than it propagates the light until `vpSundamageWaterBlocks`
                        liquidBlocks++;
                        if (liquidBlocks >= VampirismConfig.BALANCE.vpSundamageWaterblocks.get()) {
                            return false;
                        }
                    } else if (state.canOcclude() && (state.isFaceSturdy(world, pos, Direction.DOWN) || state.isFaceSturdy(world, pos, Direction.UP))) { //solid block blocks the light (fence is solid too?)
                        return false;
                    } else if (state.getLightBlock() > 0) { //if not solid, but propagates no light
                        return false;
                    }
                }
                return true;
            }
        }
    }


    @NotNull
    public static EnumStrength getGarlicStrength(@NotNull Entity e, LevelAccessor world) {
        return getGarlicStrengthAt(world, e.blockPosition());
    }

    @NotNull
    public static EnumStrength getGarlicStrengthAt(LevelAccessor world, @NotNull BlockPos pos) {
        return world instanceof Level ? VampirismAPI.garlicHandler((Level) world).getStrengthAtChunk(new ChunkPos(pos)) : EnumStrength.NONE;
    }

    @NotNull
    public static ResourceKey<Level> getWorldKey(LevelAccessor world) {
        return world instanceof Level ? ((Level) world).dimension() : world instanceof ServerLevelAccessor ? ((ServerLevelAccessor) world).getLevel().dimension() : Level.OVERWORLD;
    }

    public static boolean canBecomeVampire(@NotNull Player player) {
        return FactionPlayerHandler.get(player).canJoin(ModFactions.VAMPIRE);
    }

    public static boolean canTurnPlayer(IVampire biter, @Nullable Player target) {
        if (target != null && (target.isCreative() || target.isSpectator())) return false;
        if (biter instanceof IVampirePlayer player) {
            if (!VampirismConfig.SERVER.playerCanTurnPlayer.get()) return false;
            return !(player instanceof ServerPlayer) || Permissions.INFECT_PLAYER.isAllowed((ServerPlayer) player);
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
        return IFaction.is(ModFactions.VAMPIRE, VampirismAPI.factionRegistry().getFaction(entity));
    }

    public static boolean isHunter(Entity entity) {
        return IFaction.is(ModFactions.HUNTER, VampirismAPI.factionRegistry().getFaction(entity));
    }

    public static boolean isHunter(@NotNull Player entity) {
        return IFaction.is(ModFactions.HUNTER, VampirismPlayerAttributes.get(entity).faction);
    }

    public static boolean isVampire(Player entity) {
        return IFaction.is(ModFactions.VAMPIRE, VampirismPlayerAttributes.get(entity).faction);
    }

    public static boolean appearsAsVampire(Entity entity, Entity viewer) {
        if (entity instanceof Player player) {
            return appearsAsVampire(player, viewer);
        } else {
            return isVampire(entity);
        }
    }

    public static boolean appearsAsVampire(Player player, Entity viewer) {
        return IFaction.is(ModFactions.VAMPIRE, viewedFaction(player, viewer));
    }

    public static Holder<? extends IFaction<?>> viewedFaction(Player player, Entity viewer) {
        return FactionPlayerHandler.getCurrentFactionPlayer(player).map(IFactionPlayer::getDisguise).map(s -> s.getViewedFaction(VampirismAPI.factionRegistry().getFaction(viewer))).orElse(null);
    }

    /**
     * @return Checks if all given skills are enabled
     */
    public static boolean areSkillsEnabled(@NotNull ISkillHandler<?> skillHandler, @NotNull List<Holder<ISkill<?>>> skills) {
        for (Holder<ISkill<?>> skill : skills) {
            if (!skillHandler.isSkillEnabled(skill)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEntityInVampireBiome(@Nullable Entity e) {
        if (e == null) return false;
        Level w = e.getCommandSenderWorld();
        return w.getBiome(e.blockPosition()).is(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME);
    }

    public static boolean isPosInVampireBiome(@NotNull BlockPos pos, @NotNull LevelAccessor level) {
        Holder<Biome> biome = level.getBiome(pos);
        return biome.is(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME);
    }

    /**
     * @return Whether the entity is in a vampire fog area (does not check for vampire biome)
     */
    public static boolean isEntityInArtificalVampireFogArea(@Nullable Entity e) {
        if (e == null) return false;
        Level w = e.getCommandSenderWorld();
        return FogLevel.get(w).isInsideArtificialVampireFogArea(e.blockPosition());
    }

    public static ResourceLocation getBiomeId(@NotNull Entity e) {
        return getBiomeId(e.getCommandSenderWorld(), e.blockPosition());
    }

    public static Holder<Biome> getBiome(@NotNull Entity e) {
        return e.getCommandSenderWorld().getBiome(e.blockPosition());
    }

    public static ResourceLocation getBiomeId(@NotNull CommonLevelAccessor world, @NotNull BlockPos pos) {
        return getBiomeId(world, world.getBiome(pos));
    }

    public static ResourceLocation getBiomeId(@NotNull CommonLevelAccessor world, @NotNull Holder<Biome> biome) {
        return biome.unwrap().map(ResourceKey::location, b -> world.registryAccess().lookupOrThrow(Registries.BIOME).getKey(b));
    }

    /**
     * Returns false on client side
     * Determines the gender of the player by checking the skin and assuming 'slim'->female.
     *
     * @param p Player
     * @return True if female
     */
    public static boolean attemptToGuessGenderSafe(Player p) {
        if (p instanceof ServerPlayer) { //Could extend to also support client side, but have to use proxy then
            MinecraftProfileTextures textureMap = ((ServerPlayer) p).server.getSessionService().getTextures(p.getGameProfile());
            if (textureMap.skin() != null) {
                return "slim".equals(textureMap.skin().getMetadata("model"));
            }
        }
        return false;
    }

    public static <T extends Entity> @NotNull Optional<T> createEntity(@NotNull EntityType<T> type, @NotNull Level world, EntitySpawnReason spawnReason) {
        T e = type.create(world, spawnReason);
        if (e == null) {
            LOGGER.warn("Failed to create entity of type {}", RegUtil.id(type));
            return Optional.empty();
        }
        return Optional.of(e);
    }

    /**
     * blockpos to nbt
     */
    public static @NotNull ListTag newDoubleNBTList(double @NotNull ... numbers) {
        ListTag listnbt = new ListTag();

        for (double d0 : numbers) {
            listnbt.add(DoubleTag.valueOf(d0));
        }

        return listnbt;
    }

    /**
     * Check if
     *
     * @return Whether the given damage source can kill a vampire player or go to DBNO state instead
     */
    public static boolean canKillVampires(@NotNull DamageSource source) {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (source.is(ModDamageTypeTags.VAMPIRE_IMMORTAL) || VampirismConfig.BALANCE.vpImmortalFromDamageSources.get().contains(source.getMsgId())) {
                if (source.getDirectEntity() instanceof LivingEntity) {
                    //Maybe use all IVampireFinisher??
                    return source.getDirectEntity() instanceof IHunterMob || ((LivingEntity) source.getDirectEntity()).getMainHandItem().getItem() instanceof StakeItem;
                } else if (source.getDirectEntity() instanceof CrossbowArrowEntity) {
                    return ((CrossbowArrowEntity) source.getDirectEntity()).getArrowType() instanceof VampireKillerBehavior;
                }
                return false;
            }
        }
        return true;
    }

    public static final List<Pair<CandleHolderBlock, CandleHolderBlock>> STANDING_AND_WALL_CANDLE_STICKS = List.of(Pair.of(ModBlocks.CANDLE_STICK.get(), ModBlocks.WALL_CANDLE_STICK.get()), Pair.of(ModBlocks.CANDLE_STICK_NORMAL.get(), ModBlocks.WALL_CANDLE_STICK_NORMAL.get()), Pair.of(ModBlocks.CANDLE_STICK_WHITE.get(), ModBlocks.WALL_CANDLE_STICK_WHITE.get()), Pair.of(ModBlocks.CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_GRAY.get()), Pair.of(ModBlocks.CANDLE_STICK_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_GRAY.get()), Pair.of(ModBlocks.CANDLE_STICK_BLACK.get(), ModBlocks.WALL_CANDLE_STICK_BLACK.get()), Pair.of(ModBlocks.CANDLE_STICK_BROWN.get(), ModBlocks.WALL_CANDLE_STICK_BROWN.get()), Pair.of(ModBlocks.CANDLE_STICK_RED.get(), ModBlocks.WALL_CANDLE_STICK_RED.get()), Pair.of(ModBlocks.CANDLE_STICK_ORANGE.get(), ModBlocks.WALL_CANDLE_STICK_ORANGE.get()), Pair.of(ModBlocks.CANDLE_STICK_YELLOW.get(), ModBlocks.WALL_CANDLE_STICK_YELLOW.get()), Pair.of(ModBlocks.CANDLE_STICK_LIME.get(), ModBlocks.WALL_CANDLE_STICK_LIME.get()), Pair.of(ModBlocks.CANDLE_STICK_GREEN.get(), ModBlocks.WALL_CANDLE_STICK_GREEN.get()), Pair.of(ModBlocks.CANDLE_STICK_CYAN.get(), ModBlocks.WALL_CANDLE_STICK_CYAN.get()), Pair.of(ModBlocks.CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_BLUE.get()), Pair.of(ModBlocks.CANDLE_STICK_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_BLUE.get()), Pair.of(ModBlocks.CANDLE_STICK_MAGENTA.get(), ModBlocks.WALL_CANDLE_STICK_MAGENTA.get()), Pair.of(ModBlocks.CANDLE_STICK_PURPLE.get(), ModBlocks.WALL_CANDLE_STICK_PURPLE.get()), Pair.of(ModBlocks.CANDLE_STICK_PINK.get(), ModBlocks.WALL_CANDLE_STICK_PINK.get()));
    public static final List<Pair<CandleHolderBlock, CandleHolderBlock>> STANDING_AND_WALL_CANDELABRAS = List.of(Pair.of(ModBlocks.CANDELABRA.get(), ModBlocks.WALL_CANDELABRA.get()), Pair.of(ModBlocks.CANDELABRA_NORMAL.get(), ModBlocks.WALL_CANDELABRA_NORMAL.get()), Pair.of(ModBlocks.CANDELABRA_WHITE.get(), ModBlocks.WALL_CANDELABRA_WHITE.get()), Pair.of(ModBlocks.CANDELABRA_LIGHT_GRAY.get(), ModBlocks.WALL_CANDELABRA_LIGHT_GRAY.get()), Pair.of(ModBlocks.CANDELABRA_GRAY.get(), ModBlocks.WALL_CANDELABRA_GRAY.get()), Pair.of(ModBlocks.CANDELABRA_BLACK.get(), ModBlocks.WALL_CANDELABRA_BLACK.get()), Pair.of(ModBlocks.CANDELABRA_BROWN.get(), ModBlocks.WALL_CANDELABRA_BROWN.get()), Pair.of(ModBlocks.CANDELABRA_RED.get(), ModBlocks.WALL_CANDELABRA_RED.get()), Pair.of(ModBlocks.CANDELABRA_ORANGE.get(), ModBlocks.WALL_CANDELABRA_ORANGE.get()), Pair.of(ModBlocks.CANDELABRA_YELLOW.get(), ModBlocks.WALL_CANDELABRA_YELLOW.get()), Pair.of(ModBlocks.CANDELABRA_LIME.get(), ModBlocks.WALL_CANDELABRA_LIME.get()), Pair.of(ModBlocks.CANDELABRA_GREEN.get(), ModBlocks.WALL_CANDELABRA_GREEN.get()), Pair.of(ModBlocks.CANDELABRA_CYAN.get(), ModBlocks.WALL_CANDELABRA_CYAN.get()), Pair.of(ModBlocks.CANDELABRA_LIGHT_BLUE.get(), ModBlocks.WALL_CANDELABRA_LIGHT_BLUE.get()), Pair.of(ModBlocks.CANDELABRA_BLUE.get(), ModBlocks.WALL_CANDELABRA_BLUE.get()), Pair.of(ModBlocks.CANDELABRA_MAGENTA.get(), ModBlocks.WALL_CANDELABRA_MAGENTA.get()), Pair.of(ModBlocks.CANDELABRA_PURPLE.get(), ModBlocks.WALL_CANDELABRA_PURPLE.get()), Pair.of(ModBlocks.CANDELABRA_PINK.get(), ModBlocks.WALL_CANDELABRA_PINK.get()));
    public static final List<CandleHolderBlock> HANGING_CHANDELIERS = List.of(ModBlocks.CHANDELIER.get(), ModBlocks.CHANDELIER_NORMAL.get(), ModBlocks.CHANDELIER_WHITE.get(), ModBlocks.CHANDELIER_LIGHT_GRAY.get(), ModBlocks.CHANDELIER_GRAY.get(), ModBlocks.CHANDELIER_BLACK.get(), ModBlocks.CHANDELIER_BROWN.get(), ModBlocks.CHANDELIER_RED.get(), ModBlocks.CHANDELIER_ORANGE.get(), ModBlocks.CHANDELIER_YELLOW.get(), ModBlocks.CHANDELIER_LIME.get(), ModBlocks.CHANDELIER_GREEN.get(), ModBlocks.CHANDELIER_CYAN.get(), ModBlocks.CHANDELIER_LIGHT_BLUE.get(), ModBlocks.CHANDELIER_BLUE.get(), ModBlocks.CHANDELIER_MAGENTA.get(), ModBlocks.CHANDELIER_PURPLE.get(), ModBlocks.CHANDELIER_PINK.get());

    public static final List<VampireCloakItem> VAMPIRE_CLOAKS = List.of(ModItems.VAMPIRE_CLOAK_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLUE.get(), ModItems.VAMPIRE_CLOAK_BROWN.get(), ModItems.VAMPIRE_CLOAK_CYAN.get(), ModItems.VAMPIRE_CLOAK_GRAY.get(), ModItems.VAMPIRE_CLOAK_GREEN.get(), ModItems.VAMPIRE_CLOAK_LIGHT_BLUE.get(), ModItems.VAMPIRE_CLOAK_LIGHT_GRAY.get(), ModItems.VAMPIRE_CLOAK_LIME.get(), ModItems.VAMPIRE_CLOAK_MAGENTA.get(), ModItems.VAMPIRE_CLOAK_ORANGE.get(), ModItems.VAMPIRE_CLOAK_PINK.get(), ModItems.VAMPIRE_CLOAK_PURPLE.get(), ModItems.VAMPIRE_CLOAK_RED.get(), ModItems.VAMPIRE_CLOAK_YELLOW.get(), ModItems.VAMPIRE_CLOAK_WHITE.get());
}
