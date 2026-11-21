package de.teamlapen.vampirism.common.world.heritage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModParticles;
import de.teamlapen.vampirism.common.world.items.HeritageBookItem;
import de.teamlapen.vampirism.common.world.items.component.HeritageResearch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Heritage books are researched on a lectern surrounded by bookshelves, laid out like for an enchanting table. The
 * research takes one Minecraft day and only runs while the lectern's chunk ticks and enough bookshelves are around.
 * Progress is stored in the book, so taking the book from the lectern pauses the research.
 * <p>
 * The progress shows at the bookshelves: more and more of them have been read, in a random order that stays the same
 * for a lectern. Bookshelves that were already read send purple glyphs to the book like an enchanting table. Once the
 * research is complete, the lectern gives off the particles of an ender chest until someone reads the book or it is
 * taken away.
 * <p>
 * Running away from one's heritage at a lectern is a short ritual: darkness falls, the glyphs leave the book and the
 * pages turn, until the connection breaks after {@value RUN_AWAY_DURATION} ticks.
 * <p>
 * Only lecterns with an unfinished or unread heritage book are tracked. They are checked every
 * {@value UPDATE_INTERVAL} ticks, in between they only spawn particles.
 */
public final class HeritageLibrary {
    /**
     * One Minecraft day
     */
    public static final int RESEARCH_DURATION = 24000;
    /**
     * The bookshelf power an enchanting table needs for the highest enchantments
     */
    private static final float REQUIRED_SHELF_POWER = 15;
    private static final int UPDATE_INTERVAL = 10;
    /**
     * Height above the lectern's position where the glyphs fly to
     */
    private static final double BOOK_HEIGHT = 1.1;
    private static final int RUN_AWAY_DURATION = 100;

    private HeritageLibrary() {
    }

    /**
     * Shows the ritual of running away from one's heritage at the lectern. The heritage has already changed.
     */
    public static void startRunAway(ServerLevel level, BlockPos pos, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, RUN_AWAY_DURATION + 40));
        level.playSound(null, pos.getX() + 0.5, pos.getY() + BOOK_HEIGHT, pos.getZ() + 0.5, SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.getDataStorage().computeIfAbsent(LibraryData.TYPE).runAways.add(new RunAway(pos.immutable(), player.getUUID()));
    }

    /**
     * Starts or resumes the research if the book that was just placed on the lectern still needs it.
     */
    public static void onBookPlaced(LecternBlockEntity lectern) {
        if (lectern.getLevel() instanceof ServerLevel level && HeritageBookItem.getTargetName(lectern.getBook()) != null && !HeritageBookItem.isResearched(lectern.getBook())) {
            level.getDataStorage().computeIfAbsent(LibraryData.TYPE).addResearching(lectern.getBlockPos());
        }
    }

    /**
     * Someone read the book on the lectern, so it no longer needs to draw attention.
     */
    public static void onBookRead(ServerLevel level, BlockPos pos) {
        LibraryData data = level.getDataStorage().get(LibraryData.TYPE);
        if (data != null) {
            data.removeUnread(pos);
        }
    }

    public static void tick(ServerLevel level) {
        LibraryData data = level.getDataStorage().get(LibraryData.TYPE);
        if (data == null || data.isEmpty()) {
            return;
        }
        if (level.getGameTime() % UPDATE_INTERVAL == 0) {
            data.update(level);
        }
        RandomSource random = level.getRandom();
        data.researching.forEach(pos -> {
            List<BlockPos> readShelves = data.readShelves.get(pos);
            if (readShelves != null) {
                showProgress(level, pos, readShelves, random);
            }
        });
        data.unread.forEach(pos -> {
            if (level.shouldTickBlocksAt(pos)) {
                showUnread(level, pos, random);
            }
        });
        data.runAways.removeIf(runAway -> !runAway.tick(level, random));
    }

    /**
     * @return whether the lectern is still researching
     */
    private static boolean updateResearch(ServerLevel level, BlockPos pos, LibraryData data) {
        data.readShelves.remove(pos);
        if (!level.shouldTickBlocksAt(pos)) {
            // continues once the chunk is loaded again
            return true;
        }
        if (!(level.getBlockEntity(pos) instanceof LecternBlockEntity lectern)) {
            return false;
        }
        ItemStack book = lectern.getBook();
        String subject = HeritageBookItem.getTargetName(book);
        int progress = HeritageBookItem.getResearchProgress(book);
        if (subject == null || progress >= RESEARCH_DURATION) {
            return false;
        }

        List<BlockPos> shelves = findShelves(level, pos);
        if (getShelfPower(level, pos, shelves) < REQUIRED_SHELF_POWER) {
            // paused until enough bookshelves are around
            return true;
        }

        progress = Math.min(progress + UPDATE_INTERVAL, RESEARCH_DURATION);
        book.set(ModDataComponents.HERITAGE_RESEARCH, new HeritageResearch(subject, progress));
        lectern.setChanged();
        if (progress >= RESEARCH_DURATION) {
            completeResearch(level, pos, shelves);
            data.unread.add(pos);
            return false;
        }
        data.readShelves.put(pos, getReadShelves(shelves, progress, pos.asLong() * 31 + subject.hashCode()));
        return true;
    }

    /**
     * @return whether the lectern still holds the researched book nobody has read yet
     */
    private static boolean isStillUnread(ServerLevel level, BlockPos pos) {
        if (!level.shouldTickBlocksAt(pos)) {
            return true;
        }
        return level.getBlockEntity(pos) instanceof LecternBlockEntity lectern && HeritageBookItem.isResearched(lectern.getBook());
    }

    /**
     * @return offsets of all usable bookshelves
     */
    private static List<BlockPos> findShelves(ServerLevel level, BlockPos pos) {
        return EnchantingTableBlock.BOOKSHELF_OFFSETS.stream()
                .filter(offset -> EnchantingTableBlock.isValidBookShelf(level, pos, offset))
                .toList();
    }

    private static float getShelfPower(ServerLevel level, BlockPos pos, List<BlockPos> shelves) {
        float power = 0;
        for (BlockPos offset : shelves) {
            BlockPos shelf = pos.offset(offset);
            power += level.getBlockState(shelf).getEnchantPowerBonus(level, shelf);
        }
        return power;
    }

    /**
     * @param seed fixes the order in which the bookshelves are read, so it does not change between updates
     */
    private static List<BlockPos> getReadShelves(List<BlockPos> shelves, int progress, long seed) {
        List<BlockPos> order = new ArrayList<>(shelves);
        Collections.shuffle(order, new Random(seed));
        int read = Math.max(1, Mth.ceil(order.size() * (float) progress / RESEARCH_DURATION));
        return List.copyOf(order.subList(0, read));
    }

    /**
     * Called every tick. The chances match the glyphs of an enchanting table.
     */
    private static void showProgress(ServerLevel level, BlockPos pos, List<BlockPos> readShelves, RandomSource random) {
        for (BlockPos offset : readShelves) {
            if (random.nextInt(16) == 0) {
                sendGlyph(level, pos, offset, random);
            }
        }
        if (random.nextInt(240) == 0) {
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 0.8F + random.nextFloat() * 0.4F);
        }
    }

    private static void completeResearch(ServerLevel level, BlockPos pos, List<BlockPos> shelves) {
        RandomSource random = level.getRandom();
        for (BlockPos offset : shelves) {
            sendGlyph(level, pos, offset, random);
        }
        level.sendParticles(ModParticles.HERITAGE_GLYPH.get(), pos.getX() + 0.5, pos.getY() + BOOK_HEIGHT, pos.getZ() + 0.5, 40, 0.5, 0.5, 0.5, 1.0);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Like the glyphs of an enchanting table: the particle starts at the bookshelf and flies to the book.
     */
    private static void sendGlyph(ServerLevel level, BlockPos pos, BlockPos offset, RandomSource random) {
        level.sendParticles(ModParticles.HERITAGE_GLYPH.get(), pos.getX() + 0.5, pos.getY() + BOOK_HEIGHT, pos.getZ() + 0.5, 0,
                offset.getX() + random.nextFloat() - 0.5, offset.getY() - random.nextFloat() + (1.0 - BOOK_HEIGHT), offset.getZ() + random.nextFloat() - 0.5, 1.0);
    }

    /**
     * The particles of an ender chest. The client shows them for blocks it picks at random around the player, which
     * happens about every tenth tick for a block close by.
     */
    private static void showUnread(ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) != 0) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            int flipX = random.nextInt(2) * 2 - 1;
            int flipZ = random.nextInt(2) * 2 - 1;
            double x = pos.getX() + 0.5 + 0.25 * flipX;
            double y = pos.getY() + random.nextFloat();
            double z = pos.getZ() + 0.5 + 0.25 * flipZ;
            double xa = random.nextFloat() * flipX;
            double ya = (random.nextFloat() - 0.5) * 0.125;
            double za = random.nextFloat() * flipZ;
            level.sendParticles(ParticleTypes.PORTAL, x, y, z, 0, xa, ya, za, 1.0);
        }
    }

    /**
     * The ritual of running away from one's heritage, shown at a lectern for a few seconds.
     */
    private static final class RunAway {
        private final BlockPos pos;
        private final UUID playerId;
        private int age;

        private RunAway(BlockPos pos, UUID playerId) {
            this.pos = pos;
            this.playerId = playerId;
        }

        /**
         * @return whether the ritual is still running
         */
        private boolean tick(ServerLevel level, RandomSource random) {
            double x = this.pos.getX() + 0.5;
            double y = this.pos.getY() + BOOK_HEIGHT;
            double z = this.pos.getZ() + 0.5;
            if (++this.age >= RUN_AWAY_DURATION) {
                level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 60, 0.3, 0.3, 0.3, 0.15);
                level.playSound(null, x, y, z, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 0.8F);
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(this.playerId);
                if (player != null) {
                    player.sendOverlayMessage(Component.translatable("message.vampirism.heritage_run_away.success"));
                }
                return false;
            }

            // the glyphs leave the book
            for (int i = 0; i < 2; i++) {
                double angle = random.nextDouble() * Mth.TWO_PI;
                double distance = 2 + random.nextDouble();
                double targetX = x + Math.cos(angle) * distance;
                double targetY = y + random.nextDouble() * 1.5 - 0.5;
                double targetZ = z + Math.sin(angle) * distance;
                level.sendParticles(ModParticles.HERITAGE_GLYPH.get(), targetX, targetY, targetZ, 0, x - targetX, y - targetY, z - targetZ, 1.0);
            }
            if (this.age % 4 == 0) {
                level.sendParticles(ParticleTypes.SOUL, x, y, z, 1, 0.2, 0.1, 0.2, 0.02);
            }
            if (random.nextInt(5) == 0) {
                level.playSound(null, x, y, z, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.2F + random.nextFloat() * 0.4F);
            }
            if (this.age % 30 == 0) {
                level.playSound(null, x, y, z, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1.0F, 0.8F + random.nextFloat() * 0.4F);
            }
            return true;
        }
    }

    /**
     * Lecterns in a level that hold an unfinished heritage book, or a researched one nobody has read yet. Stored so
     * the research and the particles continue after a restart without looking at every lectern. The read bookshelves
     * of lecterns that are currently researching and the running rituals are only kept in memory.
     */
    private static final class LibraryData extends SavedData {
        private static final Codec<LibraryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.listOf().fieldOf("researching").forGetter(data -> List.copyOf(data.researching)),
                BlockPos.CODEC.listOf().optionalFieldOf("unread", List.of()).forGetter(data -> List.copyOf(data.unread))
        ).apply(instance, LibraryData::new));
        private static final SavedDataType<LibraryData> TYPE = new SavedDataType<>(VIdentifier.mod("heritage_research"), LibraryData::new, CODEC);

        private final Set<BlockPos> researching;
        private final Set<BlockPos> unread;
        private final Map<BlockPos, List<BlockPos>> readShelves = new HashMap<>();
        private final List<RunAway> runAways = new ArrayList<>();

        private LibraryData() {
            this(List.of(), List.of());
        }

        private LibraryData(List<BlockPos> researching, List<BlockPos> unread) {
            this.researching = new HashSet<>(researching);
            this.unread = new HashSet<>(unread);
        }

        private boolean isEmpty() {
            return this.researching.isEmpty() && this.unread.isEmpty() && this.runAways.isEmpty();
        }

        private void addResearching(BlockPos pos) {
            // a book placed on the lectern replaces the one that waited to be read
            this.unread.remove(pos);
            this.researching.add(pos.immutable());
            this.setDirty();
        }

        private void removeUnread(BlockPos pos) {
            if (this.unread.remove(pos)) {
                this.setDirty();
            }
        }

        private void update(ServerLevel level) {
            // completing a research moves the lectern to the unread ones
            boolean changed = this.researching.removeIf(pos -> !updateResearch(level, pos, this));
            changed |= this.unread.removeIf(pos -> !isStillUnread(level, pos));
            if (changed) {
                this.setDirty();
            }
        }
    }
}
