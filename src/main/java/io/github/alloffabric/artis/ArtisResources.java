package io.github.alloffabric.artis;

import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.devtech.arrp.json.loot.JLootTable.*;

public class ArtisResources {
	
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("artis:artis_resources");
	public static final JLang translations = JLang.lang();
	
	public static void registerDataFor(ArtisTableType artisTableType, ArtisTableBlock block) {
		RESOURCE_PACK.addLootTable(block.getLootTableId(),
				loot("minecraft:block")
						.pool(pool()
								.rolls(1)
								.entry(entry()
										.type("minecraft:item")
										.name(Registry.ITEM.getId(block.asItem()).toString()))
								.condition(condition("minecraft:survives_explosion"))));
		
		String tableIdPath = artisTableType.getId().getPath();
		translations.entry("block." + Artis.MODID + "." + tableIdPath, artisTableType.getName());
		translations.entry("rei.category." + tableIdPath, artisTableType.getName() + " Crafting");
		
		RESOURCE_PACK.addBlockState(JState.state(JState.variant(JState.model("minecraft:block/acacia_planks"))), new Identifier(Artis.MODID, tableIdPath));
		RESOURCE_PACK.addModel(JModel.model().textures(new JTextures().layer0("minecraft:block/dirt")), new Identifier(Artis.MODID, "block/" + tableIdPath));
		RESOURCE_PACK.addModel(JModel.model().textures(new JTextures().layer0("minecraft:block/stone")), new Identifier(Artis.MODID, "item/" + tableIdPath));
	}
	
	public static void registerPack() {
		RESOURCE_PACK.addLang(new Identifier(Artis.MODID, "en_us"), translations);
		RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
	}
	
	
}
