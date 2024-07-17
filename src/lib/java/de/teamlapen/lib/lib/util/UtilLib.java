package de.teamlapen.lib.lib.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General Utility Class
 */
public class UtilLib {

    private final static Logger LOGGER = LogManager.getLogger();
    private final static Pattern oldFormatPattern = Pattern.compile("%[sd]");

    public static @NotNull String entityToString(@Nullable Entity e) {
        if (e == null) {
            return "Entity is null";
        }
        return e.toString();
    }

    public static boolean doesBlockHaveSolidTopSurface(@NotNull Level worldIn, @NotNull BlockPos pos) {
        return worldIn.getBlockState(pos).isFaceSturdy(worldIn, pos, Direction.UP);
    }

    /**
     * Gets players looking spot (blocks only).
     *
     * @param restriction Max distance or 0 for player reach distance or -1 for not restricted
     * @return The position as a MovingObjectPosition, a {@link  net.minecraft.world.phys.HitResult.Type#MISS} if not existent cf: https ://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs /util/CommonUtils.java
     */
    public static @NotNull HitResult getPlayerLookingSpot(@NotNull Player player, double restriction) {
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
            distance = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue() - 0.5f;
        } else if (restriction > 0) {
            distance = restriction;
        }

        Vec3 vector2 = vector1.add(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
        return player.getCommandSenderWorld().clip(new ClipContext(vector1, vector2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }

    public static @NotNull BlockPos getRandomPosInBox(@NotNull Level w, @NotNull AABB box) {
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
    public static int countPlayerLoadedChunks(@NotNull Level world) {
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
    public static @NotNull
    Vec3 getItemPosition(@NotNull LivingEntity entity, boolean mainHand) {
        boolean left = (mainHand ? entity.getMainArm() : entity.getMainArm().getOpposite()) == HumanoidArm.LEFT;
        boolean firstPerson = entity instanceof Player player && player.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson();
        Vec3 dir = firstPerson ? entity.getForward() : Vec3.directionFromRotation(new Vec2(entity.getXRot(), entity.yBodyRot));
        dir = dir.yRot((float) (Math.PI / 5f) * (left ? 1f : -1f)).scale(0.75f);
        return dir.add(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());

    }

    public static <T extends Mob> @Nullable Entity spawnEntityBehindEntity(@NotNull LivingEntity entity, @NotNull EntityType<T> toSpawn, @NotNull MobSpawnType reason) {

        BlockPos behind = getPositionBehindEntity(entity, 2);
        Mob e = toSpawn.create(entity.getCommandSenderWorld());
        if (e == null) return null;
        Level level = entity.getCommandSenderWorld();
        e.setPos(behind.getX(), entity.getY(), behind.getZ());

        if (e.checkSpawnRules(level, reason) && e.checkSpawnObstruction(level)) {
            entity.getCommandSenderWorld().addFreshEntity(e);
            return e;
        } else {
            int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, behind).getY();
            e.setPos(behind.getX(), y, behind.getZ());
            if (e.checkSpawnRules(level, reason) && e.checkSpawnObstruction(level)) {
                level.addFreshEntity(e);
                if (level instanceof ServerLevel serverLevel) onInitialSpawn(serverLevel, e, reason);
                return e;
            }
        }
        e.remove(Entity.RemovalReason.DISCARDED);
        return null;
    }

    /**
     * Call {@link Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)} if applicable
     */
    private static void onInitialSpawn(@NotNull ServerLevel level, Entity e, @NotNull MobSpawnType reason) {
        if (e instanceof Mob mob) {
            mob.finalizeSpawn(level, e.getCommandSenderWorld().getCurrentDifficultyAt(e.blockPosition()), reason, null);
        }
    }

    public static @NotNull BlockPos getPositionBehindEntity(@NotNull LivingEntity p, float distance) {
        float yaw = p.yHeadRot;
        float cosYaw = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        double x = p.getX() + sinYaw * distance;
        double z = p.getZ() + cosYaw * distance;
        return new BlockPos((int) x, (int) p.getY(), (int) z);
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
    public static boolean spawnEntityInWorld(@NotNull ServerLevel world, @NotNull AABB box, @NotNull Entity e, int maxTry, @NotNull List<? extends LivingEntity> avoidedEntities, @NotNull MobSpawnType reason) {
        if (!world.hasChunksAt((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ)) {
            return false;
        }
        boolean flag = false;
        int i = 0;
        BlockPos backupPos = null; //
        while (!flag && i++ < maxTry) {
            BlockPos c = getRandomPosInBox(world, box); //TODO select a better location (more viable)
            if (world.noCollision(new AABB(c))) {
                if (world.isAreaLoaded(c, 5) && SpawnPlacements.isSpawnPositionOk(e.getType(), world, c)) {//I see no other way
                    e.setPos(c.getX(), c.getY() + 0.2, c.getZ());
                    if (SpawnPlacements.checkSpawnRules(e.getType(), world, reason, c, world.getRandom()) && !(e instanceof Mob) || (((Mob) e).checkSpawnRules(world, reason) && ((Mob) e).checkSpawnObstruction(e.getCommandSenderWorld()))) {
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
    public static Entity spawnEntityInWorld(@NotNull ServerLevel world, @NotNull AABB box, @NotNull EntityType<?> entityType, int maxTry, @NotNull List<? extends LivingEntity> avoidedEntities, @NotNull MobSpawnType reason) {
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
    public static boolean teleportTo(@NotNull Mob entity, double x, double y, double z, boolean sound) {
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
                if (blockState.blocksMotion()) {
                    flag1 = true;
                } else {
                    entity.setPosRaw(x, --ty, z);
                    blockPos = blockPos.below();
                }
            }

            if (flag1) {
                entity.setPos(entity.getX(), entity.getY(), entity.getZ());

                if (entity.getCommandSenderWorld().noCollision(entity) && !entity.getCommandSenderWorld().containsAnyLiquid(entity.getBoundingBox())) {
                    flag = true;
                }
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
    public static void spawnParticles(@NotNull Level world, @NotNull ParticleOptions particle, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int amount, float maxOffset) {
        double x = xCoord;
        double y = yCoord;
        double z = zCoord;
        for (int i = 0; i < amount; i++) {
            world.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
            RandomSource ran = world.random;
            x = xCoord + (ran.nextGaussian() * maxOffset);
            y = yCoord + (ran.nextGaussian() * maxOffset);
            z = zCoord + (ran.nextGaussian() * maxOffset);
        }
    }

    public static void spawnParticlesAroundEntity(@NotNull LivingEntity e, @NotNull ParticleOptions particle, double maxDistance, int amount) {


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
    public static void sendMessageToAllExcept(Player player, @NotNull Component message) {
        for (Player o : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (!o.equals(player)) {
                o.sendSystemMessage(message);
            }
        }
    }

    public static void sendMessageToAll(@NotNull Component message) {
        sendMessageToAllExcept(null, message);
    }

    /**
     * Checks if the target entity is in the field of view (180 degree) of the base entity. Only works reliable for players (due to server-client sync)
     *
     * @param alsoRaytrace Raytrace first
     */
    public static boolean canReallySee(@NotNull LivingEntity entity, @NotNull LivingEntity target, boolean alsoRaytrace) {
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
     * Stores the given pos with in the compoundtag using base.
     * Can be retrieved again with {@link UtilLib#readPos(CompoundTag, String)}
     */
    public static void write(@NotNull CompoundTag nbt, String base, @NotNull BlockPos pos) {
        nbt.putInt(base + "_x", pos.getX());
        nbt.putInt(base + "_y", pos.getY());
        nbt.putInt(base + "_z", pos.getZ());
    }

    /**
     * Reads a position written by {@link UtilLib#write(CompoundTag, String, BlockPos)}.
     */
    public static @NotNull BlockPos readPos(@NotNull CompoundTag nbt, String base) {
        return new BlockPos(nbt.getInt(base + "_x"), nbt.getInt(base + "_y"), nbt.getInt(base + "_z"));
    }

    /**
     * Prefixes each of the strings with the given prefix
     */
    public static String @NotNull [] prefix(String prefix, String @NotNull ... strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = prefix + strings[i];
        }
        return result;
    }

    /**
     * Creates a predicate which checks for the given class
     */
    public static <T> @NotNull Predicate<T> getPredicateForClass(final @NotNull Class<T> clazz) {
        return clazz::isInstance;
    }

    /**
     * Return a square bounding box around the given center with the given distance
     *
     * @param fullY If it should reach from yDisplay 0 to 265 or use the distance for yDisplay as well
     */
    public static @NotNull AABB createBB(@NotNull BlockPos center, int distance, boolean fullY) {
        return new AABB(center.getX() - distance, fullY ? 0 : center.getY() - distance, center.getZ() - distance, center.getX() + distance, fullY ? 256 : center.getY() + distance, center.getZ() + distance);
    }

    public static boolean isNonNull(Object @NotNull ... objects) {
        for (Object o : objects) {
            if (o == null) return false;
        }
        return true;
    }

    public static boolean isPlayerOp(@NotNull Player player) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(player.getGameProfile()) != null;
    }

    public static boolean isSameInstanceAsServer() {
        return ServerLifecycleHooks.getCurrentServer() != null;
    }

//    public static @Nullable String translate(String key, Object @NotNull ... format) {
//        String pattern = Component.translatable(key).getString();
//        if (format.length == 0) {
//            return pattern;
//        } else {
//            try {
//                pattern = replaceDeprecatedFormatter(pattern);
//                return I18nExtension.parseFormat(pattern, Arrays.stream(format).map(o -> o instanceof Component component ? component.getString() : o).toArray());
//            } catch (IllegalArgumentException e) {
//                LOGGER.error("Illegal format found `{}`", pattern);
//                return pattern;
//            }
//        }
//
//    }

    private static @NotNull String replaceDeprecatedFormatter(@NotNull String text) {
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
    public static @NotNull VoxelShape rotateShape(@NotNull VoxelShape shape, RotationAmount rotation) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            x1 = (x1 * 16) - 8;
            x2 = (x2 * 16) - 8;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;

            if (rotation == RotationAmount.NINETY) {
                rotatedShapes.add(blockBox(8 - z1, y1 * 16, 8 + x1, 8 - z2, y2 * 16, 8 + x2));
            } else if (rotation == RotationAmount.HUNDRED_EIGHTY) {
                rotatedShapes.add(blockBox(8 - x1, y1 * 16, 8 - z1, 8 - x2, y2 * 16, 8 - z2));
            } else if (rotation == RotationAmount.TWO_HUNDRED_SEVENTY) {
                rotatedShapes.add(blockBox(8 + z1, y1 * 16, 8 - x1, 8 + z2, y2 * 16, 8 - x2));
            }
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(() -> Block.box(0, 0, 0, 16, 16, 16));
    }

    /**
     * modifies the rolls or pitch of the shape by 90 degree
     */
    public static @NotNull VoxelShape rollShape(@NotNull VoxelShape shape, @NotNull Direction direction) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            double yMin;
            double yMax;
            double zMin;
            double zMax;
            y1 = (y1 * 16) - 8;
            y2 = (y2 * 16) - 8;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;
            switch (direction) {
                case NORTH -> {
                    z1 = 8 - z1;
                    z2 = 8 - z2;
                    y1 = 8 + y1;
                    y2 = 8 + y2;
                    yMin = Math.min(y1, y2);
                    yMax = Math.max(y1, y2);
                    zMin = Math.min(z1, z2);
                    zMax = Math.max(z1, z2);
                    rotatedShapes.add(Block.box(x1 * 16, zMin, yMin, x2 * 16, zMax, yMax));
                }
                case SOUTH -> {
                    z1 = 8 + z1;
                    z2 = 8 + z2;
                    y1 = 8 - y1;
                    y2 = 8 - y2;
                    yMin = Math.min(y1, y2);
                    yMax = Math.max(y1, y2);
                    zMin = Math.min(z1, z2);
                    zMax = Math.max(z1, z2);
                    rotatedShapes.add(Block.box(x1 * 16, zMin, yMin, x2 * 16, zMax, yMax));
                }
            }

        });
        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(() -> Block.box(0, 0, 0, 16, 16, 16));
    }

    public static @NotNull VoxelShape blockBox(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2) {
        return Block.box(Math.min(pX1, pX2), Math.min(pY1, pY2), Math.min(pZ1, pZ2), Math.max(pX1, pX2), Math.max(pY1, pY2), Math.max(pZ1, pZ2));
    }

    @Nullable
    public static StructureStart getStructureStartAt(@NotNull Entity entity, @NotNull Structure s) {
        return getStructureStartAt(entity.getCommandSenderWorld(), entity.blockPosition(), s);
    }

    @NotNull
    public static Optional<StructureStart> getStructureStartAt(@NotNull Entity entity, @NotNull TagKey<Structure> s) {
        return getStructureStartAt(entity.getCommandSenderWorld(), entity.blockPosition(), s);
    }

    public static boolean isInsideStructure(Level w, @NotNull BlockPos p, @NotNull Structure s) {
        StructureStart start = getStructureStartAt(w, p, s);
        return start != null && start.isValid();
    }

    public static boolean isInsideStructure(Level w, @NotNull BlockPos p, @NotNull TagKey<Structure> s) {
        return getStructureStartAt(w, p, s).isPresent();
    }

    public static boolean isInsideStructure(@NotNull Entity entity, @NotNull Structure s) {
        StructureStart start = getStructureStartAt(entity, s);
        return start != null && start.isValid();
    }

    public static boolean isInsideStructure(@NotNull Entity entity, @NotNull TagKey<Structure> structures) {
        return getStructureStartAt(entity, structures).isPresent();
    }

    @Nullable
    public static StructureStart getStructureStartAt(Level level, @NotNull BlockPos pos, @NotNull Structure s) {
        if (level instanceof ServerLevel serverLevel && serverLevel.isLoaded(pos)) {
            return getStructureStartAt(serverLevel, pos, s);
        }
        return null;
    }

    public static @NotNull Optional<StructureStart> getStructureStartAt(Level level, @NotNull BlockPos pos, @NotNull TagKey<Structure> structureTag) {
        if (level instanceof ServerLevel serverLevel && serverLevel.isLoaded(pos)) {
            Registry<Structure> registry = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);
            return serverLevel.structureManager().startsForStructure(new ChunkPos(pos), structure -> {
                return registry.getHolder(registry.getId(structure)).map(a -> a.is(structureTag)).orElse(false);
            }).stream().findFirst();
        }
        return Optional.empty();
    }

    public static @NotNull StructureStart getStructureStartAt(@NotNull ServerLevel w, @NotNull BlockPos pos, @NotNull Structure structure) {
        for (StructureStart structurestart : w.structureManager().startsForStructure(SectionPos.of(pos), structure)) {
            if (structurestart.getBoundingBox().isInside(pos)) {
                return structurestart;
            }
        }

        return StructureStart.INVALID_START;
    }

    public static float[] getColorComponents(int color) {
        int i = (color & 16711680) >> 16;
        int j = (color & 65280) >> 8;
        int k = (color & 255);
        return new float[] {(float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F};
    }

    @NotNull
    public static int[] bbToInt(@NotNull AABB bb) {
        return new int[] {(int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ};
    }

    @NotNull
    public static int[] mbToInt(@NotNull BoundingBox bb) {
        return new int[] {bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ()};
    }

    @NotNull
    public static AABB intToBB(@NotNull int @NotNull [] array) {
        return new AABB(array[0], array[1], array[2], array[3], array[4], array[5]);
    }

    @NotNull
    public static BoundingBox intToMB(@NotNull int @NotNull [] array) {
        return new BoundingBox(array[0], array[1], array[2], array[3], array[4], array[5]);
    }

    @NotNull
    public static BoundingBox AABBtoMB(@NotNull AABB bb) {
        return new BoundingBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
    }

    @NotNull
    public static AABB MBtoAABB(@NotNull BoundingBox bb) {
        return new AABB(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());
    }

    @Nullable
    public static DyeColor getColorForItem(@NotNull Item item) {
        if (!item.builtInRegistryHolder().is(Tags.Items.DYES)) return null;
        Optional<DyeColor> color = Arrays.stream(DyeColor.values()).filter(dye -> item.builtInRegistryHolder().is(dye.getTag())).findFirst();
        if (color.isPresent()) return color.get();
        LOGGER.warn("Could not determine color of {}", BuiltInRegistries.ITEM.getKey(item));
        return null;
    }

    public static boolean isValidResourceLocation(@NotNull String loc) {
        return ResourceLocation.tryParse(loc) != null;
    }

    /**
     * Replace an entity with a new one. Removes the old ones, adds the new one to the same world. Fires the respective Forge event
     *
     * @param old         To be removed
     * @param replacement To be added
     */
    public static void replaceEntity(@NotNull LivingEntity old, @NotNull LivingEntity replacement) {
        Level w = old.getCommandSenderWorld();
        NeoForge.EVENT_BUS.post(new LivingConversionEvent.Post(old, replacement));
        old.remove(Entity.RemovalReason.DISCARDED);
        w.addFreshEntity(replacement);
    }

    /**
     * Creates a LinkedHashSet from the given elements.
     * It isn't a {@link SortedSet} but should keep the order anyway
     */
    @SafeVarargs
    public static <T> @NotNull Set<T> newSortedSet(T... elements) {
        Set<T> s = new LinkedHashSet<>();
        Collections.addAll(s, elements);
        return s;
    }

    public static boolean matchesItem(@NotNull Ingredient ingredient, @NotNull ItemStack searchStack) {
        return Arrays.stream(ingredient.getItems()).anyMatch(stack -> ItemStack.isSameItemSameComponents(stack, searchStack));
    }

    public enum RotationAmount {
        NINETY,
        HUNDRED_EIGHTY,
        TWO_HUNDRED_SEVENTY
    }

    public static int countItemWithComponent(@NotNull Inventory inventory, @NotNull ItemStack stack) {
        int i = 0;

        for (int j = 0; j < inventory.getContainerSize(); ++j) {
            ItemStack itemstack = inventory.getItem(j);
            if (ItemStack.isSameItemSameComponents(itemstack, stack)) {
                i += itemstack.getCount();
            }
        }

        return i;
    }

    public static void forEachBlockPos(AABB area, Consumer<BlockPos> action) {
        for (double x = area.minX; x <= area.maxX; x++) {
            for (double y = area.minY; y <= area.maxY; y++) {
                for (double z = area.minZ; z <= area.maxZ; z++) {
                    action.accept(new BlockPos((int) x, (int) y, (int) z));
                }
            }
        }
    }

    public static float horizontalDistance(BlockPos pos1, BlockPos pos2) {
        int i = pos2.getX() - pos1.getX();
        int j = pos2.getZ() - pos1.getZ();
        return Mth.sqrt((float) (i * i + j * j));
    }

    public static boolean never(BlockState state, BlockGetter block, BlockPos pos) {
        return false;
    }

    public static boolean always(BlockState state, BlockGetter block, BlockPos pos) {
        return true;
    }

    @Nullable
    public static Direction getDirection(BlockPos origin, BlockPos offset) {
        if (origin.getX() > offset.getX()) {
            return Direction.EAST;
        } else if (origin.getX() < offset.getX()) {
            return Direction.WEST;
        }

        if (origin.getZ() > offset.getZ()) {
            return Direction.SOUTH;
        } else if (origin.getZ() < offset.getZ()) {
            return Direction.NORTH;
        }

        if (origin.getY() > offset.getY()) {
            return Direction.UP;
        } else if (origin.getY() < offset.getY()) {
            return Direction.DOWN;
        }
        return null;
    }

    public static int renderMultiLine(@NotNull Font fontRenderer, @NotNull GuiGraphics graphics, @NotNull Component text, int textLength, int x, int y, int color) {
        int d = 0;
        for (FormattedCharSequence sequence : fontRenderer.split(text, textLength)) {
            graphics.drawString(fontRenderer, sequence, x, y + d, color, false);
            d += fontRenderer.lineHeight;
        }
        return d;
    }

    public static <T> T getRandomElement(List<T> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public static <T> T getRandomElementOr(List<T> list, Supplier<T> ifEmpty) {
        if (list.isEmpty()) {
            return ifEmpty.get();
        }
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public static CompoundTag tagOf(String key, int value) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(key, value);
        return tag;
    }

    public static CompoundTag tagOf(String key, String value) {
        CompoundTag tag = new CompoundTag();
        tag.putString(key, value);
        return tag;
    }
}
