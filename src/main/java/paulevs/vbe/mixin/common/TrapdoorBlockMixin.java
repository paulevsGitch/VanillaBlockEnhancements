package paulevs.vbe.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.maths.BlockPos;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.TopBottom;
import paulevs.vbe.block.VBEBlockTags;
import paulevs.vbe.utils.LevelUtil;

@Mixin(TrapdoorBlock.class)
public class TrapdoorBlockMixin extends Block {
	public TrapdoorBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		if (!VBE.ENHANCED_TRAPDOORS.getValue()) return;
		builder.add(
			Properties.HORIZONTAL_FACING,
			VBEBlockProperties.TOP_BOTTOM,
			VBEBlockProperties.OPENED
		);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (!VBE.ENHANCED_TRAPDOORS.getValue()) return super.getPlacementState(context);
		Direction facing = context.getSide().getOpposite();
		PlayerEntity player = context.getPlayer();
		
		if (facing.getAxis() == Axis.Y) {
			facing = Direction.fromRotation(player == null ? 0 : player.yaw);
		}
		
		BlockState state = getDefaultState().with(Properties.HORIZONTAL_FACING, facing);
		
		BlockPos pos = context.getBlockPos();
		Level level = context.getWorld();
		boolean opened = level.hasRedstonePower(pos.getX(), pos.getY(), pos.getZ());
		state = state.with(VBEBlockProperties.OPENED, opened);
		
		if (player != null) {
			HitResult hit = LevelUtil.raycast(level, player);
			float dy = (float) (hit.pos.y - Math.floor(hit.pos.y));
			state = state.with(VBEBlockProperties.TOP_BOTTOM, dy > 0.5F ? TopBottom.TOP : TopBottom.BOTTOM);
		}
		
		return state;
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void vbe_onInit(int id, Material material, CallbackInfo info) {
		if (!VBE.ENHANCED_TRAPDOORS.getValue()) return;
		setDefaultState(getDefaultState()
			.with(Properties.HORIZONTAL_FACING, Direction.WEST)
			.with(VBEBlockProperties.TOP_BOTTOM, TopBottom.BOTTOM)
			.with(VBEBlockProperties.OPENED, false)
		);
	}
	
	@Inject(method = "onAdjacentBlockUpdate", at = @At("HEAD"), cancellable = true)
	private void vbe_onAdjacentBlockUpdate(Level level, int x, int y, int z, int blockID, CallbackInfo info) {
		if (!VBE.ENHANCED_TRAPDOORS.getValue()) return;
		info.cancel();
		
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		
		if (Block.BY_ID[blockID].getEmitsRedstonePower()) {
			boolean opened = level.hasRedstonePower(x, y, z);
			if (opened != state.get(VBEBlockProperties.OPENED)) {
				state = state.with(VBEBlockProperties.OPENED, opened);
				LevelUtil.setBlockSilent(level, x, y, z, state);
				level.updateBlock(x, y, z);
				level.playLevelEvent(null, 1003, x, y, z, 0);
			}
		}
	}
	
	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
	private void vbe_canUse(Level level, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
		if (!VBE.ENHANCED_TRAPDOORS.getValue()) return;
		info.setReturnValue(true);
		
		BlockState state = level.getBlockState(x, y, z);
		if (state.isIn(VBEBlockTags.REQUIRES_POWER)) return;
		if (!state.isOf(this)) return;
		
		boolean opened = !state.get(VBEBlockProperties.OPENED) || level.hasRedstonePower(x, y, z);
		BlockState changed = state.with(VBEBlockProperties.OPENED, opened);
		
		if (changed == state) return;
		
		LevelUtil.setBlockSilent(level, x, y, z, changed);
		level.updateBlock(x, y, z);
		level.playLevelEvent(null, 1003, x, y, z, 0);
	}
	
	@Inject(method = "updateBoundingBox", at = @At("HEAD"), cancellable = true)
	public void vbe_updateBoundingBox(BlockView view, int x, int y, int z, CallbackInfo info) {
		if (!VBE.ENHANCED_TRAPDOORS.getValue()) return;
		info.cancel();
		
		if (!(view instanceof BlockStateView level)) return;
		BlockState state = level.getBlockState(x, y, z);
		if (!state.isOf(this)) return;
		
		TopBottom part = state.get(VBEBlockProperties.TOP_BOTTOM);
		float min = 3F / 16F;
		float max = 1F - min;
		
		if (!state.get(VBEBlockProperties.OPENED)) {
			float y1 = part == TopBottom.BOTTOM ? 0.0F : max;
			float y2 = part == TopBottom.BOTTOM ? min : 1.0F;
			this.setBoundingBox(0, y1, 0, 1, y2, 1);
			return;
		}
		
		Direction d = state.get(Properties.HORIZONTAL_FACING);
		
		switch (d.getAxis()) {
			case X -> this.setBoundingBox(d.getOffsetX() < 0 ? 0 : max, 0.0F, 0.0F, d.getOffsetX() < 0 ? min : 1, 1.0F, 1.0F);
			case Z -> this.setBoundingBox(0.0F, 0.0F, d.getOffsetZ() < 0 ? 0 : max, 1.0F, 1.0F, d.getOffsetZ() < 0 ? min : 1);
		}
	}
	
	@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
	private void vbe_canPlaceAt(Level level, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> info) {
		if (!VBE.ENHANCED_TRAPDOORS.getValue()) return;
		info.setReturnValue(true);
	}
}
