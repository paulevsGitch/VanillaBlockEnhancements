package paulevs.vbe.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint.ModID;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.MutableBlockPos;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.FenceConnector;
import paulevs.vbe.block.StairsShape;
import paulevs.vbe.render.BlockViewWrapper;
import paulevs.vbe.render.CustomBreakingRender;
import paulevs.vbe.render.VBEBlockRenderer;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
	@Shadow private BlockView blockView;
	
	@Shadow public abstract boolean renderFullCube(BaseBlock arg, int i, int j, int k);
	
	@Unique private final MutableBlockPos vbe_blockPos = new MutableBlockPos();
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
		if (view instanceof BlockStateView stateView) {
			FenceConnector connector = FenceConnector.cast(vbe_fenceBlock);
			BlockState state = stateView.getBlockState(x, y, z);
			Direction face = Direction.fromVector(x - vbe_blockPos.x, y - vbe_blockPos.y, z - vbe_blockPos.z);
			return connector.vbe_canConnect(state, face) ? vbe_fenceBlock.id : 0;
		}
		return original.call(view, x, y, z);
	}
	
	@Inject(method = "renderStairs", at = @At("HEAD"), cancellable = true)
	private void vbe_renderStairs(BaseBlock block, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		if (this.blockView instanceof BlockStateView stateView && block instanceof StairsShape stairs) {
			VBEBlockRenderer.renderStairs(stairs, stateView.getBlockState(x, y, z), x, y, z, BlockRenderer.class.cast(this));
			info.setReturnValue(true);
		}
	}
	
	@Inject(method = "renderWithOverride", at = @At("HEAD"))
	private void vbe_setWrappedBlockView(BaseBlock block, int x, int y, int z, int texture, CallbackInfo info) {
		blockView = VBEBlockRenderer.getBreakView(blockView, x, y, z);
	}
	
	@ModifyArg(method = "renderWithOverride", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/block/BlockRenderer;render(Lnet/minecraft/block/BaseBlock;III)Z"
	))
	public BaseBlock vbe_replaceBreakingBlock(BaseBlock block, @Local(index = 2) int x, @Local(index = 3) int y, @Local(index = 4) int z) {
		if (blockView instanceof BlockViewWrapper wrapper) {
			return wrapper.getBlockState(x, y, z).getBlock();
		}
		return block;
	}
	
	@Inject(method = "renderWithOverride", at = @At("TAIL"))
	private void vbe_returnBlockView(BaseBlock block, int x, int y, int z, int texture, CallbackInfo info) {
		if (blockView instanceof BlockViewWrapper wrapper) {
			blockView = wrapper.getOriginalView();
		}
	}
}
