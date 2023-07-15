package paulevs.vbe.mixin.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BeforeBlockRemoved;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.util.math.BlockPos;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.ChestPart;
import paulevs.vbe.utils.LevelUtil;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends BlockWithEntity implements BeforeBlockRemoved {
	protected ChestBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@Override
	public void appendProperties(Builder<BaseBlock, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.FACING, VBEBlockProperties.CHEST_PART);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Level level = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerBase player = context.getPlayer();
		Direction facing = Direction.fromRotation(player == null ? 0 : player.yaw);
		
		BlockState chest = getDefaultState().with(VBEBlockProperties.FACING, facing);
		if (player != null && player.isChild()) {
			return chest.with(VBEBlockProperties.CHEST_PART, ChestPart.SINGLE);
		}
		
		Direction side = facing.rotateCounterclockwise(Axis.Y);
		BlockPos sidePos = pos.offset(side);
		BlockState sideState = level.getBlockState(sidePos);
		if (
			sideState.isOf(this) &&
			sideState.get(VBEBlockProperties.CHEST_PART) == ChestPart.SINGLE &&
			sideState.get(VBEBlockProperties.FACING) == facing
		) {
			sideState = sideState.with(VBEBlockProperties.CHEST_PART, ChestPart.LEFT);
			LevelUtil.setBlockSilent(level, sidePos.getX(), sidePos.getY(), sidePos.getZ(), sideState);
			return chest.with(VBEBlockProperties.CHEST_PART, ChestPart.RIGHT);
		}
		
		side = facing.rotateClockwise(Axis.Y);
		sidePos = pos.offset(side);
		sideState = level.getBlockState(sidePos);
		if (
			sideState.isOf(this) &&
			sideState.get(VBEBlockProperties.CHEST_PART) == ChestPart.SINGLE &&
			sideState.get(VBEBlockProperties.FACING) == facing
		) {
			sideState = sideState.with(VBEBlockProperties.CHEST_PART, ChestPart.RIGHT);
			LevelUtil.setBlockSilent(level, sidePos.getX(), sidePos.getY(), sidePos.getZ(), sideState);
			return chest.with(VBEBlockProperties.CHEST_PART, ChestPart.LEFT);
		}
		
		return chest.with(VBEBlockProperties.CHEST_PART, ChestPart.SINGLE);
	}
	
	@Override
	public void beforeBlockRemoved(Level level, int x, int y, int z) {
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		ChestPart part = state.get(VBEBlockProperties.CHEST_PART);
		if (part == ChestPart.SINGLE) return;
		Direction facing = state.get(VBEBlockProperties.FACING);
		Direction side = part == ChestPart.RIGHT ? facing.rotateCounterclockwise(Axis.Y) : facing.rotateClockwise(Axis.Y);
		x += side.getOffsetX();
		y += side.getOffsetY();
		z += side.getOffsetZ();
		state = level.getBlockState(x, y, z);
		if (state.isOf(this)) {
			LevelUtil.setBlockSilent(level, x, y, z, state.with(VBEBlockProperties.CHEST_PART, ChestPart.SINGLE));
		}
	}
	
	@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
	private void vbe_canPlaceAt(Level level, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(true);
	}
	
	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
	private void vbe_canUse(Level level, int x, int y, int z, PlayerBase player, CallbackInfoReturnable<Boolean> info) {
		if (level.isClientSide) { // isRemote
			info.setReturnValue(true);
			return;
		}
		
		InventoryBase inventoryBase = (InventoryBase) level.getBlockEntity(x, y, z);
		BlockState state = level.getBlockState(x, y, z);
		
		ChestPart part = state.get(VBEBlockProperties.CHEST_PART);
		if (part == ChestPart.SINGLE) {
			player.openChestScreen(inventoryBase);
			info.setReturnValue(true);
			return;
		}
		
		Direction facing = state.get(VBEBlockProperties.FACING);
		Direction side = part == ChestPart.RIGHT ? facing.rotateCounterclockwise(Axis.Y) : facing.rotateClockwise(Axis.Y);
		x += side.getOffsetX();
		y += side.getOffsetY();
		z += side.getOffsetZ();
		
		state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) {
			player.openChestScreen(inventoryBase);
			info.setReturnValue(true);
			return;
		}
		
		InventoryBase sideInventory = (InventoryBase) level.getBlockEntity(x, y, z);
		
		switch (part) {
			case LEFT -> inventoryBase = new DoubleChest("Large chest", inventoryBase, sideInventory);
			case RIGHT -> inventoryBase = new DoubleChest("Large chest", sideInventory, inventoryBase);
		}
		
		player.openChestScreen(inventoryBase);
	}
	
	@Environment(value= EnvType.CLIENT)
	@Inject(method = "getTextureForSide(Lnet/minecraft/level/BlockView;IIII)I", at = @At("HEAD"), cancellable = true)
	private void vbe_getTextureForSide(BlockView view, int x, int y, int z, int side, CallbackInfoReturnable<Integer> info) {
		if (side < 2) return;
		if (!(view instanceof BlockStateView blockStateView)) return;
		BlockState state = blockStateView.getBlockState(x, y, z);
		Direction facing = state.get(VBEBlockProperties.FACING);
		if (facing.getId() != side && facing.getOpposite().getId() != side) {
			info.setReturnValue(this.texture);
			return;
		}
		ChestPart part = state.get(VBEBlockProperties.CHEST_PART);
		int offset = facing.getId() == side ? 0 : 1;
		switch (part) {
			case RIGHT -> offset = facing.getId() == side ? 31 : 16;
			case LEFT -> offset = facing.getId() == side ? 32 : 15;
		}
		info.setReturnValue(this.texture + offset);
	}
}