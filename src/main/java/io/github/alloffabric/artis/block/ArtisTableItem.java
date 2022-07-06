package io.github.alloffabric.artis.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

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
