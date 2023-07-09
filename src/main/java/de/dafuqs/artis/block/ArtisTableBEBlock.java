package de.dafuqs.artis.block;

import de.dafuqs.artis.api.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.screen.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class ArtisTableBEBlock extends ArtisTableBlock implements BlockEntityProvider {
	
	public ArtisTableBEBlock(ArtisCraftingRecipeType type, Settings settings) {
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
