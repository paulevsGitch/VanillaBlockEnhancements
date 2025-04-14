package paulevs.vbe.mixin.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
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
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.TopBottom;
import paulevs.vbe.block.VBEBlockTags;
import paulevs.vbe.utils.LevelUtil;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin extends Block {
	@Shadow public abstract boolean canPlaceAt(Level arg, int i, int j, int k);
	
	public DoorBlockMixin(int i, Material arg) {
		super(i, arg);
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		builder.add(
			Properties.FACING,
			VBEBlockProperties.TOP_BOTTOM,
			VBEBlockProperties.OPENED,
			VBEBlockProperties.INVERTED
		);
	}
	
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void vbe_onDoorInit(int id, Material material, CallbackInfo info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		NO_AMBIENT_OCCLUSION[this.id] = true;
		LIGHT_OPACITY[this.id] = 0;
	}
	
	@Environment(value= EnvType.CLIENT)
	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	private void vbe_getRenderType(CallbackInfoReturnable<Integer> info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
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
		LevelUtil.setBlockSilent(level, x, y, z, state);
		
		state = level.getBlockState(x, py, z);
		if (state.isOf(this)) {
			LevelUtil.setBlockSilent(level, x, py, z, state.with(VBEBlockProperties.OPENED, opened));
		}
		
		level.updateArea(x, y1, z, x, y2, z);
		level.playLevelEvent(player, 1003, x, y, z, 0);
		
		vbe_updateSideDoor(level, x, y1, z, level.getBlockState(x, y1, z));
	}
	
	@Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
	private void vbe_fixTexture(int i, int j, CallbackInfoReturnable<Integer> info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
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
			level.setBlockState(x, y1, z, States.AIR.get());
			level.setBlockState(x, y2, z, States.AIR.get());
			if (part == TopBottom.BOTTOM) {
				level.updateArea(x, y1, z, x, y2, z);
			}
			else level.updateArea(x, y1, z, x, y2, z);
			return;
		}
		
		if (part == TopBottom.BOTTOM && !canPlaceAt(level, x, y, z)) {
			level.setBlockState(x, y1, z, States.AIR.get());
			level.setBlockState(x, y2, z, States.AIR.get());
			level.updateArea(x, y1, z, x, y2, z);
			this.drop(level, x, y1, z, 0);
			return;
		}
		
		if (Block.BY_ID[blockID].getEmitsRedstonePower()) {
			boolean opened = level.hasRedstonePower(x, y1, z) || level.hasRedstonePower(x, y2, z);
			opened |= vbe_hasConnectedPower(level, x, y, z, state);
			
			if (opened != state.get(VBEBlockProperties.OPENED)) {
				state = state.with(VBEBlockProperties.OPENED, opened);
				level.setBlockState(x, y, z, state);
				level.setBlockState(x, py, z, stateConnected.with(VBEBlockProperties.OPENED, opened));
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
	public void vbe_updateBoundingBox(BlockView view, int x, int y, int z, CallbackInfo info) {
		if (!VBE.ENHANCED_DOORS.getValue()) return;
		info.cancel();
		
		if (!(view instanceof BlockStateView level)) return;
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		
		TopBottom part = state.get(VBEBlockProperties.TOP_BOTTOM);
		Direction d = state.get(Properties.FACING);
		
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
	}
	
	@Unique
	private void vbe_updateSideDoor(Level level, int x, int y, int z, BlockState state) {
		boolean inverted = state.get(VBEBlockProperties.INVERTED);
		Direction offset = state.get(Properties.FACING);
		offset = inverted ? offset.rotateClockwise(Axis.Y) : offset.rotateCounterclockwise(Axis.Y);
		
		x += offset.getOffsetX();
		z += offset.getOffsetZ();
		
		BlockState sideStateBottom = level.getBlockState(x, y, z);
		if (!sideStateBottom.isOf(this)) return;
		BlockState sideStateTop = level.getBlockState(x, y + 1, z);
		if (!sideStateTop.isOf(this)) return;
		
		boolean opened = state.get(VBEBlockProperties.OPENED);
		if (opened != sideStateBottom.get(VBEBlockProperties.OPENED) || opened != sideStateTop.get(VBEBlockProperties.OPENED)) {
			level.setBlockState(x, y, z, sideStateBottom.with(VBEBlockProperties.OPENED, opened));
			level.setBlockState(x, y + 1, z, sideStateTop.with(VBEBlockProperties.OPENED, opened));
			level.updateArea(x, y, z, x, y + 1, z);
			level.playLevelEvent(null, 1003, x, y, z, 0);
		}
	}
	
	@Unique
	private boolean vbe_hasConnectedPower(Level level, int x, int y, int z, BlockState state) {
		boolean inverted = state.get(VBEBlockProperties.INVERTED);
		Direction offset = state.get(Properties.FACING);
		offset = inverted ? offset.rotateClockwise(Axis.Y) : offset.rotateCounterclockwise(Axis.Y);
		
		x += offset.getOffsetX();
		z += offset.getOffsetZ();
		
		return level.hasRedstonePower(x, y, z) || level.hasRedstonePower(x, y + 1, z);
	}
}
