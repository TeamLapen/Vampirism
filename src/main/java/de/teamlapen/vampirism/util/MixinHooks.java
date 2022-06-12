package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MixinHooks {
    /**
     * JigsawPieces in this list only will be generated once per village
     * <p>
     * holds {@link net.minecraft.world.gen.feature.jigsaw.JigsawPiece#toString()}'s
     */
    private static final List<String> onlyOneStructure = Lists.newArrayList();
    /**
     * Used by mixins as a fast check if reduced blood vision is enabled
     */
    public static boolean enforcingGlowing_bloodVision = false;

    public static void addSingleInstanceStructure(List<ResourceLocation> structures) {
        onlyOneStructure.addAll(structures.stream().map(MixinHooks::singleJigsawString).collect(Collectors.toList()));
        addSingleInstanceStructureToRepurposedStructures(structures);
    }

    /**
     * adds structures to the limit list of Repurposed Structures
     */
    private static void addSingleInstanceStructureToRepurposedStructures(List<ResourceLocation> structures) {
        try {
            if (ModList.get().isLoaded("repurposed_structures")) {
                // get piece count list from rs
                // https://github.com/TelepathicGrunt/RepurposedStructures/blob/e1bfd9e79bade3d020fcdd443b387f96d209a519/src/main/java/com/telepathicgrunt/repurposedstructures/world/structures/pieces/StructurePiecesBehavior.java#L90
                // noinspection unchecked
                Map<ResourceLocation, Integer> repurposed_structures_limit = (Map<ResourceLocation, Integer>) Class.forName("com.telepathicgrunt.repurposedstructures.world.structures.pieces.StructurePiecesBehavior").getField("PIECES_COUNT").get(null);
                repurposed_structures_limit.putAll(structures.stream().collect(HashMap::new, (map, loc) -> map.put(loc, 1), HashMap::putAll));
            }
        } catch (Exception error) {
            LogManager.getLogger().error("Could not add village building to RepurposedStructures limit list", error);
        }
    }

    public static boolean checkStructures(List<? super AbstractVillagePiece> pieces, JigsawPiece jigsawPiece) {
        if (!onlyOneStructure.contains(jigsawPiece.toString())) return false;
        return pieces.stream().anyMatch(structurePiece -> onlyOneStructure.stream().anyMatch(string -> ((AbstractVillagePiece) structurePiece).getElement().toString().equals(string)));
    }

    public static float calculateVampireSlayerEnchantments(Entity entity, ItemStack item) {
        if (!(entity instanceof PlayerEntity)) return 0;
        if (!Helper.isVampire(entity)) return 0;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
        if (!enchantments.containsKey(ModEnchantments.VAMPIRESLAYER.get())) return 0;
        return ModEnchantments.VAMPIRESLAYER.get().getDamageBonus(enchantments.get(ModEnchantments.VAMPIRESLAYER.get()), VReference.VAMPIRE_CREATURE_ATTRIBUTE);
    }

    private static String singleJigsawString(ResourceLocation resourceLocation) {
        return "Single[Left[" + resourceLocation.toString() + "]]";
    }

}
