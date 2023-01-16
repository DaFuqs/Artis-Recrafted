package de.dafuqs.artis.block;

import de.dafuqs.artis.api.ArtisTableType;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ArtisTableBEBlock extends ArtisTableBlock implements BlockEntityProvider {
    
    public ArtisTableBEBlock(ArtisTableType type, Settings settings) {
        super(type, settings);
    }
    
    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, @NotNull World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ArtisTableBlockEntity(getType(), pos, state);
    }

}
