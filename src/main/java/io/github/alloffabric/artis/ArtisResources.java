package io.github.alloffabric.artis;

import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

import static net.devtech.arrp.json.loot.JLootTable.*;

public class ArtisResources {
	
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("artis:resources");
	public static final JLang translations = JLang.lang();
	public static final HashMap<Identifier, JTag> blockTags = new HashMap<>();
	
	public static void registerDataFor(ArtisTableType artisTableType, ArtisTableBlock block) {
		// loot table (drops)
		RESOURCE_PACK.addLootTable(block.getLootTableId(),
				loot("minecraft:block")
						.pool(pool()
								.rolls(1)
								.entry(entry()
										.type("minecraft:item")
										.name(Registry.ITEM.getId(block.asItem()).toString()))
								.condition(condition("minecraft:survives_explosion"))));
		
		// localisation
		String tableIdPath = artisTableType.getId().getPath();
		translations.entry("block." + Artis.MODID + "." + tableIdPath, artisTableType.getName());
		translations.entry("rei.category." + tableIdPath, artisTableType.getName() + " Crafting");
		
		// block tags (like mineable / break by tool, if set via the config)
		for(Identifier identifier : artisTableType.getBlockTags()) {
			if(blockTags.containsKey(identifier)) {
				blockTags.get(identifier).add(artisTableType.getId());
			} else {
				blockTags.put(identifier, JTag.tag().add(artisTableType.getId()));
			}
		}
		
		// block and item models
		JBlockModel blockModel = JState.model(new Identifier(Artis.MODID, "block/table" + (artisTableType.hasColor() ? "_overlay" : "")));
		JModel model = JModel.model(new Identifier(Artis.MODID, "block/table" + (artisTableType.hasColor() ? "_overlay" : "")));
		RESOURCE_PACK.addBlockState(JState.state(JState.variant(blockModel)), new Identifier(Artis.MODID, tableIdPath));
		RESOURCE_PACK.addModel(model, new Identifier(Artis.MODID, "item/" + tableIdPath));
	}
	
	public static void registerPack() {
		RESOURCE_PACK.addLang(new Identifier(Artis.MODID, "en_us"), translations);
		for(Map.Entry<Identifier, JTag> tags : blockTags.entrySet()) {
			RESOURCE_PACK.addTag(tags.getKey(), tags.getValue());
		}
		
		RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
	}
	
}
