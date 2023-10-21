package paulevs.vbe.mixin.client;

import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicBlockRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRendererAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import paulevs.vbe.render.VBEBlockRenderer;

@Mixin(value = ArsenicBlockRenderer.class, remap = false)
public class ArsenicBlockRendererMixin {
	@Shadow @Final private BlockRendererAccessor blockRendererAccessor;
	
	@ModifyVariable(method = "renderWorld", at = @At("STORE"), ordinal = 0)
	private BlockState vbe_changeBreakingState(BlockState state) {
		return VBEBlockRenderer.getBreakingState(state, blockRendererAccessor);
	}
}
