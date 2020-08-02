package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.BloodBottleItem;
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
    public final static CollectResourcesTask collect_blood = getNull();

    public static void register(IForgeRegistry<IMinionTask<?>> registry) {
        registry.register(new StayTask().setRegistryName(REFERENCE.MODID, "stay"));
        registry.register(new DefendAreaTask().setRegistryName(REFERENCE.MODID, "defend_area"));
        registry.register(new SimpleMinionTask().setRegistryName(REFERENCE.MODID, "follow_lord"));
        registry.register(new CollectResourcesTask(VReference.HUNTER_FACTION, 1200, Arrays.asList(new WeightedRandomItem<>(new ItemStack(ModItems.garlic_bread), 10), new WeightedRandomItem<>(new ItemStack(Items.IRON_INGOT, 3), 25), new WeightedRandomItem<>(new ItemStack(Items.GOLD_INGOT, 2), 10), new WeightedRandomItem<>(new ItemStack(ModItems.item_garlic, 2), 15), new WeightedRandomItem<>(new ItemStack(Items.COAL, 5), 20))).setRegistryName(REFERENCE.MODID, "collect_hunter_items"));
        registry.register(new CollectResourcesTask(VReference.VAMPIRE_FACTION, 1200, Arrays.asList(new WeightedRandomItem<>(BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT), 10), new WeightedRandomItem<>(new ItemStack(ModItems.human_heart), 1))).setRegistryName(REFERENCE.MODID, "collect_blood"));
        registry.register(new SimpleMinionTask() {
            @Override
            public boolean isAvailable(IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
                return false;
            }
        }.setRegistryName(REFERENCE.MODID, "nothing"));
        registry.register(new SimpleMinionTask().setRegistryName(REFERENCE.MODID, "protect_lord"));
    }
}
