package de.dafuqs.artis.block;

import de.dafuqs.artis.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class CondenserBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public CondenserBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CondenserBlockEntity condenserBlockEntity) {
                player.openHandledScreen(condenserBlockEntity);
            }
            return ActionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CondenserBlockEntity(pos, state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ArtisBlocks.CONDENSER_BLOCK_ENTITY, CondenserBlockEntity::tick);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        scatterContents(world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static void scatterContents(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CondenserBlockEntity condenser) {
            Vec3d posVec = Vec3d.ofCenter(condenser.getPos());
            spawnItemStackAsEntitySplitViaMaxCount(world, posVec, condenser.input.getResource().toStack(), condenser.input.amount);
            spawnItemStackAsEntitySplitViaMaxCount(world, posVec, condenser.fuel.getResource().toStack(), condenser.input.amount);
            spawnItemStackAsEntitySplitViaMaxCount(world, posVec, condenser.output.getResource().toStack(), condenser.output.amount);
            world.updateComparators(pos, block);
        }
    }

    static void spawnItemStackAsEntitySplitViaMaxCount(World world, Vec3d pos, ItemStack itemStack, long amount) {
        while (amount > 0) {
            int currentAmount = (int) Math.min(amount, itemStack.getMaxCount());
            ItemStack resultStack = itemStack.copy();
            resultStack.setCount(currentAmount);
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), resultStack);
            amount -= currentAmount;
        }
    }

}
