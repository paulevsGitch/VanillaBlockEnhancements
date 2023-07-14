package paulevs.vbe.mixin.client;

import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.BakedModelRendererImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import paulevs.vbe.block.VBEFullSlabBlock;

@Mixin(value = BakedModelRendererImpl.class, remap = false)
public class BakedModelRendererImplMixin {
	@ModifyVariable(method = "renderDamage", at = @At("HEAD"), argsOnly = true)
	private BlockState vbe_changeDamageState(BlockState state) {
		return VBEFullSlabBlock.getBreakingState(state);
	}
}
