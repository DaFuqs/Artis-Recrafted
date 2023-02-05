package io.github.alloffabric.artis.block;

import de.dafuqs.artis.block.*;
import net.fabricmc.api.*;
import net.minecraft.client.item.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import javax.annotation.Nullable;
import java.util.*;

public class ArtisTableItem extends BlockItem {

    public ArtisTableItem(ArtisTableBlock block, Settings settings) {
        super(block, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, @NotNull TooltipContext context) {
        if (context.isAdvanced())
            tooltip.add(Text.translatable("tooltip.artis.source").formatted(Formatting.BLUE, Formatting.ITALIC));
    }

}
