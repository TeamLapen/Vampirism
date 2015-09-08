package de.teamlapen.vampirism.generation.villages;

import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;

/**
 * Replaces blocks used to build village with blocks defined in the VillageBiomes.cfg
 * 
 * @author WILLIAM
 *
 */
public class BiomeBlockReplacer implements IEventListener {

	private static boolean checkCondition(BiomeGenBase biome, String condition) {
		if (biome == null || condition == null)
			return false;
		try {
			if (condition.startsWith("b:")) {
				String identifier = condition.substring("b:".length());
				return identifier.equalsIgnoreCase(biome.biomeName) || identifier.equals(String.valueOf(biome.biomeID));
			}
			if (condition.startsWith("t:")) {
				String identifier = condition.substring("t:".length());
				Type type = Type.valueOf(identifier);
				return type != null && BiomeDictionary.isBiomeOfType(biome, type);
			}
			return false;
		} catch (NullPointerException e) {
			Logger.w("BiomeBlockReplacer", "NullPointerException when replacing blocks:");
			e.printStackTrace();
			Logger.w("BiomeBlockReplacer", "Biome class: " + biome.getClass() + "; Condition: " + condition);
			return false;
		}
	}

	private Map<Block, List<Pair<String, IBlockState>>> replacements;


	public BiomeBlockReplacer() {
		replacements = ConfigHandler.getReplacements();
	}

	@Override
	@SubscribeEvent
	public void invoke(Event event) {

		if (event instanceof BiomeEvent.GetVillageBlockID) {
			BiomeEvent.GetVillageBlockID ev = (BiomeEvent.GetVillageBlockID) event;
			if (replacements.containsKey(ev.original.getBlock())) {
				for (Pair<String, IBlockState> pair : replacements.get(ev.original.getBlock())) {
					if (checkCondition(ev.biome, pair.left)) {
						ev.replacement = pair.right;
						ev.setResult(Event.Result.DENY);
					}
				}
			}

		}
//		if (event instanceof BiomeEvent.GetVillageBlockMeta) {
//			BiomeEvent.GetVillageBlockMeta ev = (BiomeEvent.GetVillageBlockMeta) event;
//			boolean replaced = false;
//			if (metadata.containsKey(ev.original)) {
//				for (Pair<String, Integer> pair : metadata.get(ev.original)) {
//					if (checkCondition(ev.biome, pair.left)) {
//						ev.replacement = pair.right;
//						ev.setResult(Event.Result.DENY);
//						replaced = true;
//					}
//				}
//			}
//			if (!replaced && replacements.containsKey(ev.original)) {
//				for (Pair<String, Block> pair : replacements.get(ev.original)) {
//					if (checkCondition(ev.biome, pair.left)) {
//						ev.setResult(Event.Result.ALLOW);
//					}
//				}
//			}
//		}
	}
}
