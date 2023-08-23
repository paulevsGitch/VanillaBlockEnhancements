package paulevs.vbe.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.maths.MutableBlockPos;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.FenceConnector;
import paulevs.vbe.block.StairsShape;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
	@Shadow private BlockView blockView;
	
	@Shadow public abstract boolean renderFullCube(BaseBlock arg, int i, int j, int k);
	
	@Unique private final MutableBlockPos vbe_blockPos = new MutableBlockPos(0, 0, 0);
	@Unique private FenceBlock vbe_fenceBlock;
	
	@Inject(method = "renderFence", at = @At("HEAD"))
	private void vbe_renderFence(BaseBlock block, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		vbe_fenceBlock = (FenceBlock) block;
		vbe_blockPos.set(x, y, z);
	}
	
	@WrapOperation(
		method = "renderFence",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/level/BlockView;getBlockId(III)I")
	)
	private int vbe_renderFence(BlockView view, int x, int y, int z, Operation<Integer> original) {
		if (view instanceof BlockStateView blockStateView) {
			FenceConnector connector = FenceConnector.cast(vbe_fenceBlock);
			BlockState state = blockStateView.getBlockState(x, y, z);
			Direction face = Direction.fromVector(x - vbe_blockPos.x, y - vbe_blockPos.y, z - vbe_blockPos.z);
			return connector.vbe_canConnect(state, face) ? vbe_fenceBlock.id : 0;
		}
		return original.call(view, x, y, z);
	}
	
	@Inject(method = "renderStairs", at = @At("HEAD"), cancellable = true)
	private void vbe_renderStairs(BaseBlock block, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		if (this.blockView instanceof BlockStateView blockStateView && block instanceof StairsShape) {
			StairsShape.cast(block).vbe_getStairsShape(blockStateView.getBlockState(x, y, z)).forEach(shape -> {
				block.minX = shape.minX;
				block.minY = shape.minY;
				block.minZ = shape.minZ;
				block.maxX = shape.maxX;
				block.maxY = shape.maxY;
				block.maxZ = shape.maxZ;
				this.renderFullCube(block, x, y, z);
			});
			info.setReturnValue(true);
		}
	}
}
