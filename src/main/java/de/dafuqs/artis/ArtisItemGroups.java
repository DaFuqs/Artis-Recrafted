package de.dafuqs.artis;

import de.dafuqs.artis.api.*;
import net.fabricmc.fabric.api.client.itemgroup.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;

public class ArtisItemGroups {

    public static final ItemGroup ARTIS_GROUP = FabricItemGroupBuilder.build(new Identifier(Artis.MODID, "group"), () -> {
        if (!ArtisBlocks.ARTIS_TABLE_TYPES.isEmpty()) {
            ArtisTableType firstTableType = ArtisBlocks.ARTIS_TABLE_TYPES.get(0);
            if (firstTableType instanceof ArtisExistingItemType) {
                return new ItemStack(Registry.ITEM.get(firstTableType.getId()));
            } else {
                return new ItemStack(Registry.BLOCK.get(firstTableType.getId()).asItem());
            }
        } else {
            return new ItemStack(ArtisBlocks.CONDENSER_BLOCK);
        }
    });

}
