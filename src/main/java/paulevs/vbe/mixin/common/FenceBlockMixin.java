package paulevs.vbe.mixin.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.FenceConnector;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockTags;
import paulevs.vbe.block.VBEHalfSlabBlock;

@Mixin(FenceBlock.class)
public class FenceBlockMixin extends Block implements FenceConnector {
	public FenceBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void vbe_onFenceInit(int id, int texture, CallbackInfo info) {
		NO_AMBIENT_OCCLUSION[this.id] = true;
	}
	
	@Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
	private void vbe_getCollisionShape(Level level, int x, int y, int z, CallbackInfoReturnable<Box> info) {
		vbe_updateState(level, x, y, z);
		info.setReturnValue(Box.createAndCache(x + minX, y, z + minZ, x + maxX, y + 1.5, z + maxZ));
	}
	
	@Override
	@Environment(value= EnvType.CLIENT)
	public Box getOutlineShape(Level level, int x, int y, int z) {
		vbe_updateState(level, x, y, z);
		return super.getOutlineShape(level, x, y, z);
	}
	
	@Override
	public void updateBoundingBox(BlockView blockView, int x, int y, int z) {
		if (blockView instanceof Level level) {
			vbe_updateState(level, x, y, z);
		}
	}
	
	@Override
	public boolean vbe_canConnect(BlockState state, Direction face) {
		if (state.isIn(VBEBlockTags.FENCE_CONNECT)) return true;
		Block block = state.getBlock();
		if (block instanceof VBEHalfSlabBlock) {
			return state.get(VBEBlockProperties.DIRECTION).getOpposite() == face;
		}
		return block instanceof FenceBlock || (block.isFullOpaque() && block.isFullCube());
	}
	
	@Unique
	private void vbe_updateState(Level level, int x, int y, int z) {
		float x1 = vbe_canConnect(level.getBlockState(x - 1, y, z), Direction.NORTH) ? 0.0F : 0.375F;
		float x2 = vbe_canConnect(level.getBlockState(x + 1, y, z), Direction.SOUTH) ? 1.0F : 0.625F;
		float z1 = vbe_canConnect(level.getBlockState(x, y, z - 1), Direction.EAST) ? 0.0F : 0.375F;
		float z2 = vbe_canConnect(level.getBlockState(x, y, z + 1), Direction.WEST) ? 1.0F : 0.625F;
		setBoundingBox(x1, 0.0F, z1, x2, 1.0F, z2);
	}
	
	@Inject(method = "canPlaceAt", at = @At(value = "HEAD"), cancellable = true)
	private void vbe_canPlaceAt(Level level, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(true);
	}
}
