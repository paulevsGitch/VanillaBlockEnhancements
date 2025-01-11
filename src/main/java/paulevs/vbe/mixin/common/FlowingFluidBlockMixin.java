package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluidBlock.class)
public abstract class FlowingFluidBlockMixin /*extends FluidBlock*/ {
	@WrapOperation(method = "onScheduledTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/level/Level;getBlockMeta(III)I"
	))
	private int vbe_fixWrongMeta(Level level, int x, int y, int z, Operation<Integer> original) {
		return original.call(level, x, y - 1, z);
	}
}
