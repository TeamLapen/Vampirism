package de.teamlapen.vampirism.common.util;

import de.teamlapen.faction.common.util.SpawnUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * General Utility Class
 * @deprecated move stuff into dedicated context relevant classes
 */
@Deprecated
public class UtilLib {

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
        return player.level().clip(new ClipContext(vector1, vector2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
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

    /**
     * @param world           World
     * @param box             Area where the creature should spawn
     * @param e               Entity that has a EntityType<? extends EntityLiving>
     * @param maxTry          Max position tried
     * @param avoidedEntities Avoid being to close or seen by these entities. If no valid spawn location is found, this is ignored
     * @param reason          Spawn reason
     * @return Successful spawn
     */
    public static boolean spawnEntityInWorld(@NotNull ServerLevel world, @NotNull AABB box, @NotNull Entity e, int maxTry, @NotNull List<? extends LivingEntity> avoidedEntities, @NotNull EntitySpawnReason reason) {
        return SpawnUtil.spawnEntityInWorld(world, box, e, maxTry, avoidedEntities, reason);
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
    public static Entity spawnEntityInWorld(@NotNull ServerLevel world, @NotNull AABB box, @NotNull EntityType<?> entityType, int maxTry, @NotNull List<? extends LivingEntity> avoidedEntities, @NotNull EntitySpawnReason reason) {
        return SpawnUtil.spawnEntityInWorld(world, box, entityType, maxTry, avoidedEntities, reason);
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


        if (entity.level().hasChunkAt(blockPos)) {
            boolean flag1 = false;

            while (!flag1 && blockPos.getY() > 0) {
                BlockState blockState = entity.level().getBlockState(blockPos.below());
                if (blockState.blocksMotion()) {
                    flag1 = true;
                } else {
                    entity.setPosRaw(x, --ty, z);
                    blockPos = blockPos.below();
                }
            }

            if (flag1) {
                entity.setPos(entity.getX(), entity.getY(), entity.getZ());

                if (entity.level().noCollision(entity) && !entity.level().containsAnyLiquid(entity.getBoundingBox())) {
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
                entity.level().addParticle(ParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
            }

            if (sound) {
                entity.level().playLocalSound(d3, d4, d5, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F, false);
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
            RandomSource ran = world.getRandom();
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
            e.level().addParticle(particle, d7, d8, d9, f, f1, f2);
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
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(player.nameAndId()) != null;
    }

    public static boolean isSameInstanceAsServer() {
        return ServerLifecycleHooks.getCurrentServer() != null;
    }

    @Nullable
    public static StructureStart getStructureStartAt(@NotNull Entity entity, @NotNull Structure s) {
        return getStructureStartAt(entity.level(), entity.blockPosition(), s);
    }

    @NotNull
    public static Optional<StructureStart> getStructureStartAt(@NotNull Entity entity, @NotNull TagKey<Structure> s) {
        return getStructureStartAt(entity.level(), entity.blockPosition(), s);
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
            Registry<Structure> registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            return serverLevel.structureManager().startsForStructure(ChunkPos.containing(pos), structure -> {
                return registry.get(registry.getId(structure)).map(a -> a.is(structureTag)).orElse(false);
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

    public static boolean checkRegistryObjectExistence(ResourceKey<? extends Registry<?>> key, Object obj) {
        if (obj instanceof String string) {
            Identifier id = Identifier.tryParse(string);
            if (id != null) {
                if (ServerLifecycleHooks.getCurrentServer() != null) {
                    return ServerLifecycleHooks.getCurrentServer().registryAccess().lookupOrThrow(key).containsKey(id);
                }
                // we need to return true here because the config is validated when it is registered, where the server is not available
                return true;
            }
        }
        return false;
    }

    /**
     * Replace an entity with a new one. Removes the old ones, adds the new one to the same world. Fires the respective Forge event
     *
     * @param old         To be removed
     * @param replacement To be added
     */
    public static void replaceEntity(@NotNull LivingEntity old, @NotNull LivingEntity replacement) {
        SpawnUtil.replaceEntity(old, replacement);
    }

    /**
     * Creates a LinkedHashSet from the given elements.
     * It isn't a {@link SortedSet} but should keep the order anyway
     */

    public static boolean matchesItem(@NotNull Ingredient ingredient, @NotNull ItemStack searchStack) {
        return ingredient.test(searchStack);
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

    public static int renderMultiLine(@NotNull Font fontRenderer, @NotNull GuiGraphicsExtractor graphics, @NotNull Component text, int textLength, int x, int y, int color) {
        int d = 0;
        for (FormattedCharSequence sequence : fontRenderer.split(text, textLength)) {
            graphics.text(fontRenderer, sequence, x, y + d, color, false);
            d += fontRenderer.lineHeight;
        }
        return d;
    }

    public static <T> T getRandomElementOr(List<T> list, Supplier<T> ifEmpty) {
        if (list.isEmpty()) {
            return ifEmpty.get();
        }
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public static int indexOf(Object[] array, Object obj) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }
}
