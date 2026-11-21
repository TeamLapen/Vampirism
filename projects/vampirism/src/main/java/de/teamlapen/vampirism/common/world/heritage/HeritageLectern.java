package de.teamlapen.vampirism.common.world.heritage;

import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundOpenLecternHeritagePacket;
import de.teamlapen.vampirism.common.world.items.HeritageBookItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Heritage books can be placed on vanilla lecterns. Placement, rendering, comparator output and dropping the book when
 * the lectern is broken stay vanilla; only opening the book and taking it back out are handled here, because the
 * vanilla lectern menu can only display written books.
 * <p>
 * Reading a displayed heritage is always allowed: the client asks for it on every right-click on a lectern with a book,
 * independent of protection mods or plugins. Taking the book is only allowed after the normal lectern interaction went
 * through, because that is where protection mods and plugins block access. The server remembers that lectern for the
 * player, like the open menu does in vanilla.
 */
public final class HeritageLectern {
    private static final Map<UUID, GlobalPos> TAKEABLE_LECTERNS = new HashMap<>();

    private HeritageLectern() {
    }

    /**
     * Opens the heritage screen in read-only mode. Sent by the client before the normal interaction, which arrives at
     * the server right after this and may then allow taking the book.
     */
    public static void read(ServerPlayer player, BlockPos pos) {
        // every click has to pass the protection checks again before the book can be taken
        forget(player);
        if (player.isWithinBlockInteractionRange(pos, 1.0) && getReadableLectern(player, pos).isPresent()) {
            player.connection.send(new ClientboundOpenLecternHeritagePacket(pos, false));
            player.awardStat(Stats.INTERACT_WITH_LECTERN);
            HeritageLibrary.onBookRead(player.level(), pos);
        }
    }

    /**
     * Called instead of the vanilla lectern menu once the normal lectern interaction went through.
     *
     * @return true if the lectern holds a heritage book, which the player may now take
     */
    public static boolean tryOpen(Level level, BlockPos pos, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !(level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) || !lectern.getBook().is(ModItems.HERITAGE_BOOK)) {
            return false;
        }
        TAKEABLE_LECTERNS.put(serverPlayer.getUUID(), GlobalPos.of(level.dimension(), pos));
        serverPlayer.connection.send(new ClientboundOpenLecternHeritagePacket(pos, true));
        return true;
    }

    public static void forget(Player player) {
        TAKEABLE_LECTERNS.remove(player.getUUID());
    }

    /**
     * An unnamed heritage book cannot show anything, so it is not placed. The item's own use then tells the player to
     * name it.
     */
    public static boolean shouldRejectPlacement(ItemStack stack) {
        return stack.is(ModItems.HERITAGE_BOOK) && HeritageBookItem.getTargetName(stack) == null;
    }

    /**
     * @return the lectern at the given position if it holds a heritage book and the player is close enough to read it
     */
    public static Optional<LecternBlockEntity> getReadableLectern(ServerPlayer player, BlockPos pos) {
        if (player.level().getBlockEntity(pos) instanceof LecternBlockEntity lectern && lectern.getBook().is(ModItems.HERITAGE_BOOK) && Container.stillValidBlockEntity(lectern, player)) {
            return Optional.of(lectern);
        }
        return Optional.empty();
    }

    /**
     * Takes the book through the "Take Book" button of a vanilla lectern menu, so the vanilla checks apply and servers
     * that hook into taking lectern books see it like any other lectern.
     */
    public static void takeBook(ServerPlayer player, BlockPos pos) {
        if (!GlobalPos.of(player.level().dimension(), pos).equals(TAKEABLE_LECTERNS.get(player.getUUID()))) {
            return;
        }
        getReadableLectern(player, pos).ifPresent(lectern -> {
            forget(player);
            lectern.createMenu(0, player.getInventory(), player).clickMenuButton(player, LecternMenu.BUTTON_TAKE_BOOK);
        });
    }
}
