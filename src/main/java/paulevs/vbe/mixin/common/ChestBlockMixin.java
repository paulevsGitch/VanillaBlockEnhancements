package paulevs.vbe.mixin.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.DoubleChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.maths.BlockPos;
import net.modificationstation.stationapi.api.block.BeforeBlockRemoved;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.ChestPart;
import paulevs.vbe.utils.LevelUtil;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends BlockWithEntity implements BeforeBlockRemoved {
	protected ChestBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		if (!VBE.ENHANCED_CHESTS.getValue()) return;
		try {
			builder.add(Properties.FACING, VBEBlockProperties.CHEST_PART);
		}
		catch (Exception ignore) {}
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (!VBE.ENHANCED_CHESTS.getValue()) return super.getPlacementState(context);
		
		Level level = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();
		Direction facing = Direction.fromRotation(player == null ? 0 : player.yaw);
		
		BlockState chest = getDefaultState().with(Properties.FACING, facing);
		if (player != null && player.isChild()) {
			return chest.with(VBEBlockProperties.CHEST_PART, ChestPart.SINGLE);
		}
		
		Direction side = facing.rotateCounterclockwise(Axis.Y);
		BlockPos sidePos = pos.offset(side);
		BlockState sideState = level.getBlockState(sidePos);
		if (
			sideState.isOf(this) &&
			sideState.get(VBEBlockProperties.CHEST_PART) == ChestPart.SINGLE &&
			sideState.get(Properties.FACING) == facing
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
			sideState.get(Properties.FACING) == facing
		) {
			sideState = sideState.with(VBEBlockProperties.CHEST_PART, ChestPart.RIGHT);
			LevelUtil.setBlockSilent(level, sidePos.getX(), sidePos.getY(), sidePos.getZ(), sideState);
			return chest.with(VBEBlockProperties.CHEST_PART, ChestPart.LEFT);
		}
		
		return chest.with(VBEBlockProperties.CHEST_PART, ChestPart.SINGLE);
	}
	
	@Override
	public void beforeBlockRemoved(Level level, int x, int y, int z) {
		if (!VBE.ENHANCED_CHESTS.getValue()) return;
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		ChestPart part = state.get(VBEBlockProperties.CHEST_PART);
		if (part == ChestPart.SINGLE) return;
		Direction facing = state.get(Properties.FACING);
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
		if (!VBE.ENHANCED_CHESTS.getValue()) return;
		info.setReturnValue(true);
	}
	
	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
	private void vbe_canUse(Level level, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
		if (!VBE.ENHANCED_CHESTS.getValue()) return;
		info.setReturnValue(true);
		
		if (level.isRemote) {
			return;
		}
		
		Inventory inventory = (Inventory) level.getBlockEntity(x, y, z);
		BlockState state = level.getBlockState(x, y, z);
		
		ChestPart part = state.get(VBEBlockProperties.CHEST_PART);
		if (part == ChestPart.SINGLE) {
			player.openChestScreen(inventory);
			return;
		}
		
		Direction facing = state.get(Properties.FACING);
		Direction side = part == ChestPart.RIGHT ? facing.rotateCounterclockwise(Axis.Y) : facing.rotateClockwise(Axis.Y);
		x += side.getOffsetX();
		y += side.getOffsetY();
		z += side.getOffsetZ();
		
		state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) {
			player.openChestScreen(inventory);
			return;
		}
		
		Inventory sideInventory = (Inventory) level.getBlockEntity(x, y, z);
		
		switch (part) {
			case LEFT -> inventory = new DoubleChestInventory("Large chest", inventory, sideInventory);
			case RIGHT -> inventory = new DoubleChestInventory("Large chest", sideInventory, inventory);
		}
		
		player.openChestScreen(inventory);
	}
	
	@Environment(value= EnvType.CLIENT)
	@Inject(method = "getTexture(Lnet/minecraft/level/BlockView;IIII)I", at = @At("HEAD"), cancellable = true)
	private void vbe_getTextureForSide(BlockView view, int x, int y, int z, int side, CallbackInfoReturnable<Integer> info) {
		if (!VBE.ENHANCED_CHESTS.getValue()) return;
		if (side < 2) return;
		if (!(view instanceof BlockStateView blockStateView)) return;
		BlockState state = blockStateView.getBlockState(x, y, z);
		Direction facing = state.get(Properties.FACING);
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
