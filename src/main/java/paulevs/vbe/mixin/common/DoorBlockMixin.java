package paulevs.vbe.mixin.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlockFixer;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.TopBottom;
import paulevs.vbe.block.VBEBlockTags;
import paulevs.vbe.utils.LevelUtil;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin extends Block {
	@Unique private static boolean vbe_stopUpdate;
	
	@Shadow public abstract boolean canPlaceAt(Level arg, int i, int j, int k);
	
	public DoorBlockMixin(int i, Material arg) {
		super(i, arg);
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		builder.add(
			Properties.HORIZONTAL_FACING,
			VBEBlockProperties.TOP_BOTTOM,
			VBEBlockProperties.OPENED,
			VBEBlockProperties.INVERTED
		);
	}
	
	@Override
	public void onBlockPlaced(Level level, int x, int y, int z) {
		BlockState state1 = level.getBlockState(x, y, z);
		if (!state1.isOf(this)) return;
		int meta = level.getBlockMeta(x, y, z);
		BlockState state2 = VBEBlockFixer.fixDoor(state1, meta);
		if (state2 != state1) {
			//vbe_stopUpdate = true;
			LevelUtil.setBlockSilent(level, x, y, z, state2);
			//level.updateBlock(x, y, z);
			//vbe_stopUpdate = false;
		}
	}
	
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void vbe_onDoorInit(int id, Material material, CallbackInfo info) {
		NO_AMBIENT_OCCLUSION[this.id] = true;
		LIGHT_OPACITY[this.id] = 0;
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		setDefaultState(getDefaultState()
			.with(Properties.HORIZONTAL_FACING, Direction.EAST)
			.with(VBEBlockProperties.TOP_BOTTOM, TopBottom.BOTTOM)
			.with(VBEBlockProperties.OPENED, true)
			.with(VBEBlockProperties.INVERTED, false)
		);
	}
	
	@Environment(value= EnvType.CLIENT)
	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	private void vbe_getRenderType(CallbackInfoReturnable<Integer> info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		if (this != WOOD_DOOR && this != IRON_DOOR) return;
		info.setReturnValue(0);
	}
	
	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
	private void vbe_canUse(Level level, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		info.setReturnValue(true);
		
		BlockState state = level.getBlockState(x, y, z);
		if (state.isIn(VBEBlockTags.REQUIRES_POWER)) return;
		if (!state.isOf(this)) return;
		
		TopBottom part = state.get(VBEBlockProperties.TOP_BOTTOM);
		int py = part == TopBottom.TOP ? y - 1 : y + 1;
		int y1 = Math.min(y, py);
		int y2 = Math.max(y, py);
		
		boolean opened = !state.get(VBEBlockProperties.OPENED);
		opened |= level.hasRedstonePower(x, y1, z) || level.hasRedstonePower(x, y2, z);
		opened |= vbe_hasConnectedPower(level, x, y1, z, state);
		state = state.with(VBEBlockProperties.OPENED, opened);
		
		if (level.getBlockState(x, y, z) == state) return;
		LevelUtil.setBlockSilent(level, x, y, z, state, VBEBlockFixer.getDoorMeta(state));
		
		state = level.getBlockState(x, py, z);
		if (state.isOf(this)) {
			BlockState state2 = state.with(VBEBlockProperties.OPENED, opened);
			LevelUtil.setBlockSilent(level, x, py, z, state2, VBEBlockFixer.getDoorMeta(state2));
		}
		
		level.updateArea(x, y1, z, x, y2, z);
		level.playLevelEvent(player, 1003, x, y, z, 0);
		
		state = level.getBlockState(x, y1, z);
		if (!state.isOf(this)) return;
		vbe_updateSideDoor(level, x, y1, z, state);
	}
	
	@Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
	private void vbe_fixTexture(int i, int j, CallbackInfoReturnable<Integer> info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		if (this != WOOD_DOOR && this != IRON_DOOR) return;
		info.setReturnValue(0);
	}
	
	@Inject(method = "onAdjacentBlockUpdate", at = @At("HEAD"), cancellable = true)
	private void vbe_onAdjacentBlockUpdate(Level level, int x, int y, int z, int blockID, CallbackInfo info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		info.cancel();
		
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		
		int py = y;
		TopBottom part = state.get(VBEBlockProperties.TOP_BOTTOM);
		
		switch (part) {
			case TOP -> py = y - 1;
			case BOTTOM -> py = y + 1;
		}
		
		int y1 = Math.min(y, py);
		int y2 = Math.max(y, py);
		
		BlockState stateConnected = level.getBlockState(x, py, z);
		
		if (!stateConnected.isOf(this)) {
			LevelUtil.setBlockForceUpdate(level, x, y1, z, States.AIR.get());
			LevelUtil.setBlockForceUpdate(level, x, y2, z, States.AIR.get());
			if (part == TopBottom.BOTTOM) {
				level.updateArea(x, y1, z, x, y2, z);
			}
			else level.updateArea(x, y1, z, x, y2, z);
			return;
		}
		
		if (part == TopBottom.BOTTOM && !canPlaceAt(level, x, y, z)) {
			LevelUtil.setBlockForceUpdate(level, x, y1, z, States.AIR.get());
			LevelUtil.setBlockForceUpdate(level, x, y2, z, States.AIR.get());
			level.updateArea(x, y1, z, x, y2, z);
			this.drop(level, x, y1, z, 0);
			return;
		}
		
		if (Block.BY_ID[blockID].getEmitsRedstonePower()) {
			boolean opened = level.hasRedstonePower(x, y1, z) || level.hasRedstonePower(x, y2, z);
			opened |= vbe_hasConnectedPower(level, x, y, z, state);
			
			if (opened != state.get(VBEBlockProperties.OPENED)) {
				state = state.with(VBEBlockProperties.OPENED, opened);
				LevelUtil.setBlockForceUpdate(level, x, y, z, state);
				LevelUtil.setBlockForceUpdate(level, x, py, z, stateConnected.with(VBEBlockProperties.OPENED, opened));
				level.updateArea(x, y1, z, x, y2, z);
				level.playLevelEvent(null, 1003, x, y1, z, 0);
			}
		}
		
		vbe_updateSideDoor(level, x, y1, z, state);
	}
	
	@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
	private void vbe_canPlaceAt(Level level, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		info.setReturnValue(level.canSuffocate(x, y - 1, z));
	}
	
	@Inject(method = "updateBoundingBox", at = @At("HEAD"), cancellable = true)
	private void vbe_updateBoundingBox(BlockView view, int x, int y, int z, CallbackInfo info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		info.cancel();
		
		if (!(view instanceof BlockStateView level)) return;
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		
		TopBottom part = state.get(VBEBlockProperties.TOP_BOTTOM);
		Direction d = state.get(Properties.HORIZONTAL_FACING);
		
		if (state.get(VBEBlockProperties.OPENED)) {
			if (state.get(VBEBlockProperties.INVERTED)) d = d.rotateCounterclockwise(Axis.Y);
			else d = d.rotateClockwise(Axis.Y);
		}
		
		float y1 = part == TopBottom.BOTTOM ? 0 : -1;
		float y2 = part == TopBottom.BOTTOM ? 2 : 1;
		float min = 3F / 16F;
		float max = 1F - min;
		
		if (this != WOOD_DOOR && this != IRON_DOOR) {
			y1 = 0.0F;
			y2 = 1.0F;
		}
		
		switch (d.getAxis()) {
			case X -> this.setBoundingBox(d.getOffsetX() < 0 ? 0 : max, y1, 0.0F, d.getOffsetX() < 0 ? min : 1, y2, 1.0F);
			case Z -> this.setBoundingBox(0.0F, y1, d.getOffsetZ() < 0 ? 0 : max, 1.0F, y2, d.getOffsetZ() < 0 ? min : 1);
		}
	}
	
	// Doubled code for mod compat
	@Environment(EnvType.CLIENT)
	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	private void vbe_getOutlineShape(Level level, int x, int y, int z, CallbackInfoReturnable<Box> info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		info.cancel();
		
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		
		TopBottom part = state.get(VBEBlockProperties.TOP_BOTTOM);
		Direction d = state.get(Properties.HORIZONTAL_FACING);
		
		if (state.get(VBEBlockProperties.OPENED)) {
			if (state.get(VBEBlockProperties.INVERTED)) d = d.rotateCounterclockwise(Axis.Y);
			else d = d.rotateClockwise(Axis.Y);
		}
		
		float y1 = part == TopBottom.BOTTOM ? 0 : -1;
		float y2 = part == TopBottom.BOTTOM ? 2 : 1;
		float min = 3F / 16F;
		float max = 1F - min;
		
		switch (d.getAxis()) {
			case X -> this.setBoundingBox(d.getOffsetX() < 0 ? 0 : max, y1, 0.0F, d.getOffsetX() < 0 ? min : 1, y2, 1.0F);
			case Z -> this.setBoundingBox(0.0F, y1, d.getOffsetZ() < 0 ? 0 : max, 1.0F, y2, d.getOffsetZ() < 0 ? min : 1);
		}
		
		info.setReturnValue(super.getOutlineShape(level, x, y, z));
	}
	
	@Unique
	private void vbe_updateSideDoor(Level level, int x, int y, int z, BlockState state) {
		boolean inverted = state.get(VBEBlockProperties.INVERTED);
		Direction offset = state.get(Properties.HORIZONTAL_FACING);
		offset = inverted ? offset.rotateClockwise(Axis.Y) : offset.rotateCounterclockwise(Axis.Y);
		
		x += offset.getOffsetX();
		z += offset.getOffsetZ();
		
		BlockState sideStateBottom = level.getBlockState(x, y, z);
		if (!sideStateBottom.isOf(this) || sideStateBottom.get(VBEBlockProperties.INVERTED) == inverted) return;
		BlockState sideStateTop = level.getBlockState(x, y + 1, z);
		if (!sideStateTop.isOf(this)) return;
		
		boolean opened = state.get(VBEBlockProperties.OPENED);
		if (opened != sideStateBottom.get(VBEBlockProperties.OPENED) || opened != sideStateTop.get(VBEBlockProperties.OPENED)) {
			BlockState state2 = sideStateBottom.with(VBEBlockProperties.OPENED, opened);
			LevelUtil.setMetaSilent(level, x, y, z, VBEBlockFixer.getDoorMeta(state2));
			LevelUtil.setBlockForceUpdate(level, x, y, z, state2);
			
			state2 = sideStateTop.with(VBEBlockProperties.OPENED, opened);
			LevelUtil.setMetaSilent(level, x, y + 1, z, VBEBlockFixer.getDoorMeta(state2));
			LevelUtil.setBlockForceUpdate(level, x, y + 1, z, sideStateTop.with(VBEBlockProperties.OPENED, opened));
			
			level.updateArea(x, y, z, x, y + 1, z);
			level.playLevelEvent(null, 1003, x, y, z, 0);
		}
	}
	
	@Unique
	private boolean vbe_hasConnectedPower(Level level, int x, int y, int z, BlockState state) {
		boolean inverted = state.get(VBEBlockProperties.INVERTED);
		Direction offset = state.get(Properties.HORIZONTAL_FACING);
		offset = inverted ? offset.rotateClockwise(Axis.Y) : offset.rotateCounterclockwise(Axis.Y);
		
		x += offset.getOffsetX();
		z += offset.getOffsetZ();
		
		return level.hasRedstonePower(x, y, z) || level.hasRedstonePower(x, y + 1, z);
	}
}
