package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.FireBlock;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.block.VBELogBlock;
import paulevs.vbe.block.VanillaLeavesWrapper;

import java.util.Random;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	@Inject(method = "canBurnBlock", at = @At("HEAD"), cancellable = true)
	private void vbe_canBurnBlock(BlockView view, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		if (view instanceof BlockStateView blockStateView) {
			BlockState state = blockStateView.getBlockState(x, y, z);
			if (state.getBlock() instanceof VBELogBlock || state.getBlock() instanceof VanillaLeavesWrapper) {
				info.setReturnValue(true);
			}
			else if (state.isOf(VBEBlocks.OAK_SLAB_FULL) || state.isOf(VBEBlocks.OAK_SLAB_HALF)) {
				info.setReturnValue(true);
			}
		}
	}
	
	@Inject(method = "getBurningTime(Lnet/minecraft/level/Level;IIII)I", at = @At("HEAD"), cancellable = true)
	private void vbe_getBurningTime(Level level, int x, int y, int z, int time, CallbackInfoReturnable<Integer> info) {
		BlockState state = level.getBlockState(x, y, z);
		if (state.getBlock() instanceof VBELogBlock
			|| state.getBlock() instanceof VanillaLeavesWrapper
			|| state.isOf(VBEBlocks.OAK_SLAB_FULL)
			|| state.isOf(VBEBlocks.OAK_SLAB_HALF)
		) {
			info.setReturnValue(Math.max(time, 5));
		}
	}
	
	@Inject(method = "fireTick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0))
	private void vbe_fireTick(
		Level level, int x, int y, int z, int spreadChance, Random random, int burnDestroyChance, CallbackInfo info,
		@Local(ordinal = 5) LocalIntRef delayChance
	) {
		BlockState state = level.getBlockState(x, y, z);
		if (state.getBlock() instanceof VBELogBlock
			|| state.isOf(VBEBlocks.OAK_SLAB_FULL)
			|| state.isOf(VBEBlocks.OAK_SLAB_HALF)
		) {
			delayChance.set(5);
		}
		else if (state.getBlock() instanceof VanillaLeavesWrapper) {
			delayChance.set(30);
		}
	}
}
