package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FlowingFluidBlock.class)
public abstract class FlowingFluidBlockMixin extends FluidBlock {
	@Shadow protected abstract void setBlockWithUpdate(Level arg, int i, int j, int k);
	
	public FlowingFluidBlockMixin(int id, Material material) {
		super(id, material);
	}
	
	@WrapOperation(method = "onScheduledTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/level/Level;getBlockMeta(III)I"
	))
	private int vbe_fixWrongMeta(Level level, int x, int y, int z, Operation<Integer> original) {
		return original.call(level, x, y - 1, z);
	}
	
	@WrapOperation(
		method = "onScheduledTick",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/level/Level;getBlockMeta(III)I")
	)
	private int vbe_renderFence(Level level, int x, int y, int z, Operation<Integer> original) {
		return level.getBlockMeta(x, y - 1, z);
	}
	
	@Inject(method = "onScheduledTick", at = @At("HEAD"), cancellable = true)
	private void vbe_onScheduledTick(Level level, int x, int y, int z, Random random, CallbackInfo info) {
		if (this.material != Material.LAVA || level.dimension.id != -1) return;
		
		if (level.getMaterial(x - 1, y, z) != Material.LAVA) return;
		if (level.getMaterial(x + 1, y, z) != Material.LAVA) return;
		if (level.getMaterial(x, y, z - 1) != Material.LAVA) return;
		if (level.getMaterial(x, y, z + 1) != Material.LAVA) return;
		
		BlockState state = level.getBlockState(x, y - 1, z);
		
		if (
			!(state.getMaterial() == Material.LAVA && level.getBlockMeta(x, y - 1, z) == 0) &&
			!(state.isOpaque() && state.getBlock().isFullCube())
		) return;
		
		level.setBlockMeta(x, y, z, 0);
		setBlockWithUpdate(level, x, y, z);
		info.cancel();
	}
}
