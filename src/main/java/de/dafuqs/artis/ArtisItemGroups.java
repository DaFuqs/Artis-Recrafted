package de.dafuqs.artis;

import de.dafuqs.artis.api.*;
import io.github.alloffabric.artis.Artis;
import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

public class ArtisItemGroups {

    public static final ItemGroup ARTIS_GROUP = FabricItemGroup.builder(new Identifier(Artis.MODID, "group"))
            .displayName(Text.translatable("itemGroup.artis.group"))
            .icon(() -> {
                if (!ArtisBlocks.ARTIS_TABLE_TYPES.isEmpty()) {
                    ArtisTableType firstTableType = ArtisBlocks.ARTIS_TABLE_TYPES.get(0);
                    if (firstTableType instanceof ArtisExistingItemType) {
                        return new ItemStack(Registries.ITEM.get(firstTableType.getId()));
                    } else {
                        return new ItemStack(Registries.BLOCK.get(firstTableType.getId()).asItem());
                    }
                } else {
                    return new ItemStack(Items.CRAFTING_TABLE);
                }
            })
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                for (ArtisTableType tableType : ArtisBlocks.ARTIS_TABLE_TYPES) {
                    if (tableType instanceof ArtisExistingItemType) {
                        entries.add(Registries.ITEM.get(tableType.getId()));
                    } else {
                        entries.add(Registries.BLOCK.get(tableType.getId()).asItem());
                    }
                }
            })
            .build();

}
