package de.teamlapen.lib.lib.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General Utility Class
 */
public class UtilLib {

    private final static Logger LOGGER = LogManager.getLogger();
    private final static Pattern oldFormatPattern = Pattern.compile("%[sd]");

    public static String entityToString(Entity e) {
        if (e == null) {
            return "Entity is null";
        }
        return e.toString();
    }

    public static boolean doesBlockHaveSolidTopSurface(Level worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).isFaceSturdy(worldIn, pos, Direction.UP);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawTexturedModalRect(Matrix4f matrix, float zLevel, int x, int y, int textureX, int textureY, int width, int height, int texWidth, int texHeight) {
        float f = 1 / (float) texWidth;
        float f1 = 1 / (float) texHeight;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x, y + height, zLevel).uv((float) (textureX) * f, (float) (textureY + height) * f1).endVertex();
        buffer.vertex(matrix, x + width, y + height, zLevel).uv((float) (textureX + width) * f, (float) (textureY + height) * f1).endVertex();
        buffer.vertex(matrix, x + width, y, zLevel).uv((float) (textureX + width) * f, (float) (textureY) * f1).endVertex();
        buffer.vertex(matrix, x, y, zLevel).uv((float) (textureX) * f, (float) (textureY) * f1).endVertex();
        tesselator.end();
    }

    /**
     * Gets players looking spot (blocks only).
     *
     * @param restriction Max distance or 0 for player reach distance or -1 for not restricted
     * @return The position as a MovingObjectPosition, null if not existent cf: https ://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs /util/CommonUtils.java
     */
    public static HitResult getPlayerLookingSpot(Player player, double restriction) {
        float scale = 1.0F;
        float pitch = player.xRotO + (player.getXRot() - player.xRotO) * scale;
        float yaw = player.yRotO + (player.getYRot() - player.yRotO) * scale;
        double x = player.xo + (player.getX() - player.xo) * scale;
        double y = player.yo + (player.getY() - player.yo) * scale + 1.62D;
        double z = player.zo + (player.getZ() - player.zo) * scale;
        Vec3 vector1 = new Vec3(x, y, z);
        float cosYaw = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -Mth.cos(-pitch * 0.017453292F);
        float sinPitch = Mth.sin(-pitch * 0.017453292F);
        float pitchAdjustedSinYaw = sinYaw * cosPitch;
        float pitchAdjustedCosYaw = cosYaw * cosPitch;
        double distance = 500D;
        if (restriction == 0 && player instanceof ServerPlayer) {
            distance = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() - 0.5f;
        } else if (restriction > 0) {
            distance = restriction;
        }

        Vec3 vector2 = vector1.add(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
        return player.getCommandSenderWorld().clip(new ClipContext(vector1, vector2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }

    public static BlockPos getRandomPosInBox(Level w, AABB box) {
        int x = (int) box.minX + w.random.nextInt((int) (box.maxX - box.minX) + 1);
        int z = (int) box.minZ + w.random.nextInt((int) (box.maxZ - box.minZ) + 1);
        int y = w.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 5;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        while (y > box.minY && !w.getBlockState(pos).isRedstoneConductor(w, pos)) {
            pos.set(x, --y, z);
        }

        if (y < box.minY || y > box.maxY - 1) {
            pos.set(x, (int) box.minY + w.random.nextInt((int) (box.maxY - box.minY) + 1), z);
        }
        return pos.above();
    }

    /**
     * @return Number of chunks loaded by players
     */
    public static int countPlayerLoadedChunks(Level world) {
        List<ChunkPos> chunks = Lists.newArrayList();
        int i = 0;

        for (Player player : world.players()) {
            if (!player.isSpectator()) {
                int x = Mth.floor(player.getX() / 16.0D);
                int z = Mth.floor(player.getZ() / 16.0D);

                for (int dx = -8; dx <= 8; ++dx) {
                    for (int dz = -8; dz <= 8; ++dz) {
                        ChunkPos chunkpos = new ChunkPos(dx + x, dz + z);

                        if (!chunks.contains(chunkpos)) {
                            ++i;
                            chunks.add(chunkpos);


                        }
                    }
                }
            }
        }
        return i;
    }

    /**
     * Returns an approximate absolute (world) position of the held item.
     * This assumes a ModelBiped like model and a normal item.
     *
     * @param entity   Assumes a ModelBiped like creature
     * @param mainHand If main hand position
     * @return Absolute position in the world
     */
    public static @Nonnull
    Vec3 getItemPosition(LivingEntity entity, boolean mainHand) {
        boolean left = (mainHand ? entity.getMainArm() : entity.getMainArm().getOpposite()) == HumanoidArm.LEFT;
        boolean firstPerson = entity instanceof Player && ((Player) entity).isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson();
        Vec3 dir = firstPerson ? entity.getForward() : Vec3.directionFromRotation(new Vec2(entity.getXRot(), entity.yBodyRot));
        dir = dir.yRot((float) (Math.PI / 5f) * (left ? 1f : -1f)).scale(0.75f);
        return dir.add(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());

    }

    public static <T extends Mob> Entity spawnEntityBehindEntity(LivingEntity entity, EntityType<T> toSpawn, MobSpawnType reason) {

        BlockPos behind = getPositionBehindEntity(entity, 2);
        Mob e = toSpawn.create(entity.getCommandSenderWorld());
        if (e == null) return null;
        Level world = entity.getCommandSenderWorld();
        e.setPos(behind.getX(), entity.getY(), behind.getZ());

        if (e.checkSpawnRules(world, reason) && e.checkSpawnObstruction(world)) {
            entity.getCommandSenderWorld().addFreshEntity(e);
            return e;
        } else {
            int y = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, behind).getY();
            e.setPos(behind.getX(), y, behind.getZ());
            if (e.checkSpawnRules(world, reason) && e.checkSpawnObstruction(world)) {
                world.addFreshEntity(e);
                if (world instanceof ServerLevel) onInitialSpawn((ServerLevel) world, e, reason);
                return e;
            }
        }
        e.remove(Entity.RemovalReason.DISCARDED);
        return null;
    }

    /**
     * Call {@link Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)} if applicable
     */
    private static void onInitialSpawn(ServerLevel world, Entity e, MobSpawnType reason) {
        if (e instanceof Mob) {
            ((Mob) e).finalizeSpawn(world, e.getCommandSenderWorld().getCurrentDifficultyAt(e.blockPosition()), reason, null, null);
        }
    }

    public static BlockPos getPositionBehindEntity(LivingEntity p, float distance) {
        float yaw = p.yHeadRot;
        float cosYaw = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        double x = p.getX() + sinYaw * distance;
        double z = p.getZ() + cosYaw * distance;
        return new BlockPos(x, p.getY(), z);
    }

    /**
     * @param world           World
     * @param box             Area where the creature should spawn
     * @param e               Entity that has a EntityType<? extends EntityLiving>
     * @param maxTry          Max position tried
     * @param avoidedEntities Avoid being to close or seen by these entities. If no valid spawn location is found, this is ignored
     * @param reason          Spawn reason
     * @return Successful spawn
     */
    public static boolean spawnEntityInWorld(ServerLevel world, AABB box, Entity e, int maxTry, @Nonnull List<? extends LivingEntity> avoidedEntities, MobSpawnType reason) {
        if (!world.hasChunksAt((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ)) {
            return false;
        }
        boolean flag = false;
        int i = 0;
        BlockPos backupPos = null; //
        while (!flag && i++ < maxTry) {
            BlockPos c = getRandomPosInBox(world, box); //TODO select a better location (more viable)
            if (world.noCollision(new AABB(c))) {
                if (world.isAreaLoaded(c, 5) && NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(e.getType()), world, c, e.getType())) {//I see no other way
                    e.setPos(c.getX(), c.getY() + 0.2, c.getZ());
                    if (SpawnPlacements.checkSpawnRules(e.getType(), world, reason, c, world.getRandom())  && !(e instanceof Mob) || (((Mob) e).checkSpawnRules(world, reason) && ((Mob) e).checkSpawnObstruction(e.getCommandSenderWorld()))) {
                        backupPos = c; //Store the location in case we do not find a better one
                        for (LivingEntity p : avoidedEntities) {

                            if (!(p.distanceToSqr(e) < 500 && p.hasLineOfSight(e))) {
                                flag = true;
                            }
                        }
                    }
                }
            }
        }
        if (!flag && backupPos != null) {
            //If we did not find a "hidden" position, use the last valid position (if available)
            e.setPos(backupPos.getX(), backupPos.getY() + 0.2, backupPos.getZ());
            flag = true;
        }

        if (flag) {
            world.addFreshEntity(e);
            onInitialSpawn(world, e, reason);
            return true;
        }
        return false;
    }

    /**
     * @param world           World
     * @param box             Area where the creature should spawn
     * @param entityType      EntityType of entity to be created
     * @param maxTry          Max position tried
     * @param avoidedEntities Avoid being to close or seen by these entities. If no valid spawn location is found, this is ignored
     * @param reason          Spawn reason
     * @return The spawned creature or null if not successful
     */
    @Nullable
    public static Entity spawnEntityInWorld(ServerLevel world, AABB box, EntityType<?> entityType, int maxTry, @Nonnull List<? extends LivingEntity> avoidedEntities, MobSpawnType reason) {
        Entity e = entityType.create(world);
        if (spawnEntityInWorld(world, box, e, maxTry, avoidedEntities, reason)) {
            return e;
        } else {
            if (e != null) {
                e.remove(Entity.RemovalReason.DISCARDED);
            }
            return null;
        }
    }

    /**
     * Teleports the entity
     *
     * @param sound If a teleport sound should be played
     * @return Whether the teleport was successful or not
     */
    public static boolean teleportTo(Mob entity, double x, double y, double z, boolean sound) {
        double d3 = entity.getX();
        double d4 = entity.getY();
        double d5 = entity.getZ();
        entity.setPosRaw(x, y, z);
        boolean flag = false;
        BlockPos blockPos = entity.blockPosition(); //getPos
        double ty = y;


        if (entity.getCommandSenderWorld().hasChunkAt(blockPos)) {
            boolean flag1 = false;

            while (!flag1 && blockPos.getY() > 0) {
                BlockState blockState = entity.getCommandSenderWorld().getBlockState(blockPos.below());
                if (blockState.getMaterial().blocksMotion())
                    flag1 = true;
                else {
                    entity.setPosRaw(x, --ty, z);
                    blockPos = blockPos.below();
                }
            }

            if (flag1) {
                entity.setPos(entity.getX(), entity.getY(), entity.getZ());

                if (entity.getCommandSenderWorld().noCollision(entity) && !entity.getCommandSenderWorld().containsAnyLiquid(entity.getBoundingBox()))
                    flag = true;
            }
        }

        if (!flag) {
            entity.setPos(d3, d4, d5);
            return false;
        } else {
            short short1 = 128;

            for (int l = 0; l < short1; ++l) {
                double d6 = l / (short1 - 1.0D);
                float f = (entity.getRandom().nextFloat() - 0.5F) * 0.2F;
                float f1 = (entity.getRandom().nextFloat() - 0.5F) * 0.2F;
                float f2 = (entity.getRandom().nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (entity.getX() - d3) * d6 + (entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth() * 2.0D;
                double d8 = d4 + (entity.getY() - d4) * d6 + entity.getRandom().nextDouble() * entity.getBbHeight();
                double d9 = d5 + (entity.getZ() - d5) * d6 + (entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth() * 2.0D;
                entity.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
            }

            if (sound) {
                entity.getCommandSenderWorld().playLocalSound(d3, d4, d5, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F, false);
                entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
            }

            return true;
        }
    }

    /**
     * Spawn multiple particles, with a small offset between
     */
    public static void spawnParticles(Level world, ParticleOptions particle, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int amount, float maxOffset) {
        double x = xCoord;
        double y = yCoord;
        double z = zCoord;
        for (int i = 0; i < amount; i++) {
            world.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
            Random ran = world.random;
            x = xCoord + (ran.nextGaussian() * maxOffset);
            y = yCoord + (ran.nextGaussian() * maxOffset);
            z = zCoord + (ran.nextGaussian() * maxOffset);
        }
    }

    public static void spawnParticlesAroundEntity(LivingEntity e, ParticleOptions particle, double maxDistance, int amount) {


        short short1 = (short) amount;
        for (int l = 0; l < short1; ++l) {
            double d6 = l / (short1 - 1.0D) - 0.5D;
            float f = (e.getRandom().nextFloat() - 0.5F) * 0.2F;
            float f1 = (e.getRandom().nextFloat() - 0.5F) * 0.2F;
            float f2 = (e.getRandom().nextFloat() - 0.5F) * 0.2F;
            double d7 = e.getX() + (maxDistance) * d6 + (e.getRandom().nextDouble() - 0.5D) * e.getBbWidth() * 2.0D;
            double d8 = e.getY() + (maxDistance / 2) * d6 + e.getRandom().nextDouble() * e.getHealth();
            double d9 = e.getZ() + (maxDistance) * d6 + (e.getRandom().nextDouble() - 0.5D) * e.getBbWidth() * 2.0D;
            e.getCommandSenderWorld().addParticle(particle, d7, d8, d9, f, f1, f2);
        }
    }

    /**
     * Sends the component message to all players except the given one.
     * Only use on server or common side
     */
    public static void sendMessageToAllExcept(Player player, Component message) {
        for (Player o : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (!o.equals(player)) {
                o.sendMessage(message, Util.NIL_UUID);
            }
        }
    }

    public static void sendMessageToAll(Component message) {
        sendMessageToAllExcept(null, message);
    }

    /**
     * Checks if the target entity is in the field of view (180 degree) of the base entity. Only works reliable for players (due to server-client sync)
     *
     * @param alsoRaytrace Raytrace first
     */
    public static boolean canReallySee(LivingEntity entity, LivingEntity target, boolean alsoRaytrace) {
        if (alsoRaytrace && !entity.hasLineOfSight(target)) {
            return false;
        }
        Vec3 look1 = new Vec3(-Math.sin(entity.yHeadRot / 180 * Math.PI), 0, Math.cos(entity.yHeadRot / 180 * Math.PI));
        Vec3 dist = new Vec3(target.getX() - entity.getX(), 0, target.getZ() - entity.getZ());
        //look1.y = 0;
        look1 = look1.normalize();
        dist = dist.normalize();

        //Check if the vector is left or right of look1
        double alpha = Math.acos(look1.dot(dist));
        return alpha < Math.PI / 1.8;

    }

    /**
     * Returns null, but makes it look like non-null
     * <p>
     * If this causes issues when compiling with IntelliJ check the following link and rebuild the entire project afterwards
     * <p>
     * https://github.com/TeamLapen/Vampirism#intellij
     * <p>
     * Make sure Settings -> Build, Execution, Deployment -> Compiler -> 'Add runtime assertions for not-null-annotated methods and parameters' is disabled (Unfortunately required)
     */
    @Deprecated
    @SuppressWarnings("ConstantConditions")
    public static @Nonnull
    <T> T getNull() {
        return null;
    }

    /**
     * Stores the given pos with in the compoundtag using base.
     * Can be retrieved again with {@link UtilLib#readPos(CompoundTag, String)}
     */
    public static void write(CompoundTag nbt, String base, BlockPos pos) {
        nbt.putInt(base + "_x", pos.getX());
        nbt.putInt(base + "_y", pos.getY());
        nbt.putInt(base + "_z", pos.getZ());
    }

    /**
     * Reads a position written by {@link UtilLib#write(CompoundTag, String, BlockPos)}.
     */
    public static BlockPos readPos(CompoundTag nbt, String base) {
        return new BlockPos(nbt.getInt(base + "_x"), nbt.getInt(base + "_y"), nbt.getInt(base + "_z"));
    }

    /**
     * Prefixes each of the strings with the given prefix
     */
    public static String[] prefix(String prefix, String... strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = prefix + strings[i];
        }
        return result;
    }

    /**
     * Creates a predicate which checks for the given class
     */
    public static <T> Predicate<T> getPredicateForClass(final Class<T> clazz) {
        return clazz::isInstance;
    }

    /**
     * Return a square bounding box around the given center with the given distance
     *
     * @param fullY If it should reach from yDisplay 0 to 265 or use the distance for yDisplay as well
     */
    public static AABB createBB(BlockPos center, int distance, boolean fullY) {
        return new AABB(center.getX() - distance, fullY ? 0 : center.getY() - distance, center.getZ() - distance, center.getX() + distance, fullY ? 256 : center.getY() + distance, center.getZ() + distance);
    }

    public static boolean isNonNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) return false;
        }
        return true;
    }

    private static ChunkPos isBiomeAt(ServerLevel world, int x, int z, List<Biome> biomes) {
        Pair<BlockPos, Holder<Biome>> pos = (world.getChunkSource()).getGenerator().getBiomeSource().findBiomeHorizontal(x, world.getSeaLevel(), z, 32, h -> biomes.contains(h.value()), new Random(), world.getChunkSource().getGenerator().climateSampler());
        if (pos != null) {
            return new ChunkPos(pos.getFirst().getX() >> 4, pos.getFirst().getZ() >> 4);
        }
        return null;
    }

    /**
     * Search for a vampire biome by checking every second chunk starting at the player and moving in circles to the outside
     *
     * @param center  Pos to start with
     * @param maxDist Max radius
     */
    public static ChunkPos findNearBiome(ServerLevel world, BlockPos center, int maxDist, List<Biome> biomes) {
        long start = System.currentTimeMillis();
        maxDist = (maxDist / 20) * 20;//Round it
        long maxop = (((long) maxDist) * maxDist + maxDist) / 2;
        ChunkPos loc;
        for (int i = 0; i < maxDist; i += 4) {
            int cx = -i;
            for (int cz = -i; cz <= i; cz++) {
                if (cz % 4 != 0) continue;
                loc = isBiomeAt(world, center.getX() + (cx << 4), center.getZ() + (cz << 4), biomes);
                if (loc != null) {
                    LOGGER.trace("Took {} ms to find a vampire biome {} {}", (int) (System.currentTimeMillis() - start), loc.x, loc.z);
                    return loc;
                }
                if (cz == i && cx < 0) {
                    cz = -i;
                    cx = i;
                }
            }
            int cz = -i;
            for (int cx2 = -i + 1; cx2 < i; cx2++) {
                if (cx2 % 4 != 0) continue;
                loc = isBiomeAt(world, center.getX() + (cx2 << 4), center.getZ() + (cz << 4), biomes);
                if (loc != null) {
                    LOGGER.trace("Took {} ms to find a vampire biome {} {}", (int) (System.currentTimeMillis() - start), loc.x, loc.z);
                    return loc;
                }
                if (cx == i - 1 && cz < 0) {
                    cz = i;
                    cx = i - 1;
                }
            }
            if ((i * 10) % maxDist == 0) {
                long op = (((long) i) * i + i) / 2;
                double perc = ((double) op / maxop) * 100;
                LOGGER.trace("Search {} percent finished", (int) perc);
            }

        }
        LOGGER.trace("Took {} ms to not find a vampire biome", (int) (System.currentTimeMillis() - start));
        return null;
    }

    public static boolean isPlayerOp(Player player) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(player.getGameProfile()) != null;
    }

    public static boolean isSameInstanceAsServer() {
        return ServerLifecycleHooks.getCurrentServer() != null;
    }

    public static String translate(String key, Object... format) {
        String pattern = ForgeI18n.getPattern(key);
        if (format.length == 0) {
            return pattern;
        } else {
            try {
                pattern = replaceDeprecatedFormatter(pattern);
                return ForgeI18n.parseFormat(pattern, Arrays.stream(format).map(o -> o instanceof Component ? ((Component) o).getString() : o).toArray());
            } catch (IllegalArgumentException e) {
                LOGGER.error("Illegal format found `{}`", pattern);
                return pattern;
            }
        }

    }

    private static String replaceDeprecatedFormatter(String text) {
        StringBuilder sb = null;
        Matcher m = oldFormatPattern.matcher(text);
        int i = 0;
        while (m.find()) {
            String t;
            t = "{" + i++ + "}";

            if (sb == null) {
                sb = new StringBuilder(text.length());
            }
            m.appendReplacement(sb, t);
        }
        if (sb == null) {
            return text;
        } else {
            m.appendTail(sb);
            return sb.toString();
        }
    }


    /**
     * Rotate voxel. Credits to JTK222|Lukas
     * Cache the result
     */
    public static VoxelShape rotateShape(VoxelShape shape, RotationAmount rotation) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            x1 = (x1 * 16) - 8;
            x2 = (x2 * 16) - 8;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;

            if (rotation == RotationAmount.NINETY)
                rotatedShapes.add(blockBox(8 - z1, y1 * 16, 8 + x1, 8 - z2, y2 * 16, 8 + x2));
            else if (rotation == RotationAmount.HUNDRED_EIGHTY)
                rotatedShapes.add(blockBox(8 - x1, y1 * 16, 8 - z1, 8 - x2, y2 * 16, 8 - z2));
            else if (rotation == RotationAmount.TWO_HUNDRED_SEVENTY)
                rotatedShapes.add(blockBox(8 + z1, y1 * 16, 8 - x1, 8 + z2, y2 * 16, 8 - x2));
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(() -> Block.box(0, 0, 0, 16, 16, 16));
    }

    public static VoxelShape blockBox(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2) {
        return Block.box(Math.min(pX1, pX2), Math.min(pY1, pY2), Math.min(pZ1, pZ2), Math.max(pX1, pX2), Math.max(pY1, pY2), Math.max(pZ1, pZ2));
    }

    public static boolean isInsideStructure(Entity entity, ResourceKey<ConfiguredStructureFeature<?, ?>> s) {
        StructureStart start = getStructureStartAt(entity, s);
        return start != null && start.isValid();
    }

    public static boolean isInsideStructure(Level w, BlockPos p, ResourceKey<ConfiguredStructureFeature<?, ?>> s) {
        StructureStart start = getStructureStartAt(w, p, s);
        return start != null && start.isValid();
    }

    @Nullable
    public static StructureStart getStructureStartAt(Entity entity, ResourceKey<ConfiguredStructureFeature<?, ?>> s) {
        return getStructureStartAt(entity.getCommandSenderWorld(), entity.blockPosition(), s);
    }

    @Nullable
    public static StructureStart getStructureStartAt(Entity entity,StructureFeature<?> s) {
        return getStructureStartAt(entity.getCommandSenderWorld(), entity.blockPosition(), s);
    }

    public static boolean isInsideStructure(Level w, BlockPos p, StructureFeature<?> s) {
        StructureStart start = getStructureStartAt(w, p, s);
        return start != null && start.isValid();
    }

    @Nullable
    public static StructureStart getStructureStartAt(Level w, BlockPos pos, ResourceKey<ConfiguredStructureFeature<?, ?>> s) {
        if (w instanceof ServerLevel && w.isLoaded(pos)) {
            return getStructureStartAt((ServerLevel) w, pos, s);
        }
        return null;
    }

    public static boolean isInsideStructure(Entity entity,  StructureFeature<?> s) {
        StructureStart start = getStructureStartAt(entity, s);
        return start != null && start.isValid();
    }

    @Nonnull
    public static StructureStart getStructureStartAt(ServerLevel w, BlockPos pos, ResourceKey<ConfiguredStructureFeature<?, ?>> s) {
        ConfiguredStructureFeature<?, ?> configuredstructurefeature = w.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(s);
        return configuredstructurefeature == null ? StructureStart.INVALID_START : w.structureFeatureManager().getStructureAt(pos, configuredstructurefeature);
    }

    @Nullable
    public static StructureStart getStructureStartAt(Level w, BlockPos pos, StructureFeature<?> s) {
        if (w instanceof ServerLevel && w.isLoaded(pos)) {
            return getStructureStartAt((ServerLevel) w, pos, s);
        }
        return null;
    }

    public static StructureStart getStructureStartAt(ServerLevel w, BlockPos pos, StructureFeature<?> feature){
        for(StructureStart structurestart : w.structureFeatureManager().startsForFeature(SectionPos.of(pos), t -> t.feature.equals(feature))) {
            if(structurestart.getBoundingBox().isInside(pos)){
                return structurestart;
            }
        }

        return StructureStart.INVALID_START;
    }


    /**
     * Makes sure the given stack has a NBT Tag Compound
     *
     * @return The stacks NBT Tag
     */
    public static CompoundTag checkNBT(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        return stack.getTag();
    }

    public static float[] getColorComponents(int color) {
        int i = (color & 16711680) >> 16;
        int j = (color & 65280) >> 8;
        int k = (color & 255);
        return new float[]{(float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F};
    }

    @Nonnull
    public static int[] bbToInt(@Nonnull AABB bb) {
        return new int[]{(int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ};
    }

    @Nonnull
    public static int[] mbToInt(@Nonnull BoundingBox bb) {
        return new int[]{bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ()};
    }

    @Nonnull
    public static AABB intToBB(@Nonnull int[] array) {
        return new AABB(array[0], array[1], array[2], array[3], array[4], array[5]);
    }

    @Nonnull
    public static BoundingBox intToMB(@Nonnull int[] array) {
        return new BoundingBox(array[0], array[1], array[2], array[3], array[4], array[5]);
    }

    @Nonnull
    public static BoundingBox AABBtoMB(@Nonnull AABB bb) {
        return new BoundingBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
    }

    @Nonnull
    public static AABB MBtoAABB(@Nonnull BoundingBox bb) {
        return new AABB(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());
    }

    /**
     * Draws a TextComponent split over multiple lines
     *
     * @return The height of the rendered text
     */
    public static int renderMultiLine(Font fontRenderer, PoseStack stack, Component text, int textLength, int x, int y, int color) {
        int d = 0;
        for (FormattedCharSequence sequence : fontRenderer.split(text, textLength)) {
            fontRenderer.draw(stack, sequence, x, y + d, color);
            d += fontRenderer.lineHeight;
        }
        return d;
    }

    @Nullable
    public static DyeColor getColorForItem(Item item) {
        if (!item.builtInRegistryHolder().is(Tags.Items.DYES) ) return null;
        Optional<DyeColor> color = Arrays.stream(DyeColor.values()).filter(dye -> item.builtInRegistryHolder().is(dye.getTag())).findFirst();
        if (color.isPresent()) return color.get();
        LOGGER.warn("Could not determine color of {}", item.getRegistryName());
        return null;
    }

    public static boolean isValidResourceLocation(String loc) {
        return ResourceLocation.tryParse(loc) != null;
    }

    /**
     * Replace an entity with a new one. Removes the old ones, adds the new one to the same world. Fires the respective Forge event
     *
     * @param old         To be removed
     * @param replacement To be added
     */
    public static void replaceEntity(LivingEntity old, LivingEntity replacement) {
        Level w = old.getCommandSenderWorld();
        MinecraftForge.EVENT_BUS.post(new LivingConversionEvent.Post(old, replacement));
        w.addFreshEntity(replacement);
        old.remove(Entity.RemovalReason.DISCARDED);
    }

    /**
     * Creates a LinkedHashSet from the given elements.
     * It isn't a {@link SortedSet} but should keep the order anyway
     */
    @SafeVarargs
    public static <T> Set<T> newSortedSet(T... elements) {
        Set<T> s = new LinkedHashSet<>();
        Collections.addAll(s, elements);
        return s;
    }

    public enum RotationAmount {
        NINETY,
        HUNDRED_EIGHTY,
        TWO_HUNDRED_SEVENTY
    }
}
