package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Arrays;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class MinionTasks {

    public final static SimpleMinionTask nothing = getNull();
    public final static StayTask stay = getNull();
    public final static DefendAreaTask defend_area = getNull();
    public final static SimpleMinionTask follow_lord = getNull();
    public final static CollectResourcesTask collect_hunter_items = getNull();
    public final static SimpleMinionTask protect_lord = getNull();

    public static void register(IForgeRegistry<IMinionTask<?>> registry) {
        registry.register(new StayTask().setRegistryName(REFERENCE.MODID, "stay"));
        registry.register(new DefendAreaTask().setRegistryName(REFERENCE.MODID, "defend_area"));
        registry.register(new SimpleMinionTask().setRegistryName(REFERENCE.MODID, "follow_lord"));
        registry.register(new CollectResourcesTask(100, Arrays.asList(new WeightedRandomItem<>(new ItemStack(ModItems.garlic_bread, 1), 1), new WeightedRandomItem<>(new ItemStack(Items.APPLE, 1), 3))).setRegistryName(REFERENCE.MODID, "collect_hunter_items"));
        registry.register(new SimpleMinionTask() {
            @Override
            public boolean isAvailable(IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
                return false;
            }
        }.setRegistryName(REFERENCE.MODID, "nothing"));
        registry.register(new SimpleMinionTask().setRegistryName(REFERENCE.MODID, "protect_lord"));
    }
}
