package paulevs.vbe.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(TorchBlock.class)
public class TorchBlockMixin {
	@Unique private static final ArrayList<?> VBE_COLLISIONS = new ArrayList<>();
	
	@Inject(method = "isFloorSupport", at = @At("HEAD"), cancellable = true)
	private void vbe_isFloorSupport(Level level, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		BlockState state = level.getBlockState(x, y, z);
		Block block = state.getBlock();
		if (block instanceof FenceBlock) info.setReturnValue(true);
		else if (block.material.blocksMovement()) {
			Box collider = Box.createAndCache(0, y + 0.99, 0, 0, y + 1.75, 0);
			boolean collide = true;
			for (byte i = 0; i < 4 && collide; i++) {
				collider.minX = x + (i & 1) * 0.125 + 0.4375;
				collider.minZ = z + (i >> 1) * 0.125 + 0.4375;
				collider.maxX = collider.minX;
				collider.maxZ = collider.minZ;
				block.doesBoxCollide(level, x, y, z, collider, VBE_COLLISIONS);
				collide = !VBE_COLLISIONS.isEmpty();
				VBE_COLLISIONS.clear();
			}
			if (collide) info.setReturnValue(true);
		}
	}
	
	private static boolean vbe_isSupport(Level level, int x, int y, int z, double px, double py, double pz, Block block) {
		Box collider = Box.createAndCache(x - 0.0625, y + 0.99, z - 0.0625, x - 0.0625, y + 1.75, z - 0.0625);
		block.doesBoxCollide(level, x, y, z, collider, VBE_COLLISIONS);
		boolean result = !VBE_COLLISIONS.isEmpty();
		VBE_COLLISIONS.clear();
		return result;
	}
}
