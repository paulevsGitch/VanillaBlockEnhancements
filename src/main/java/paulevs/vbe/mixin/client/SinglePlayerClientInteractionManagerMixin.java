package paulevs.vbe.mixin.client;

import net.minecraft.client.BaseClientInteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SinglePlayerClientInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.VBEFullSlabBlock;

@Mixin(SinglePlayerClientInteractionManager.class)
public class SinglePlayerClientInteractionManagerMixin extends BaseClientInteractionManager {
	public SinglePlayerClientInteractionManagerMixin(Minecraft minecraft) {
		super(minecraft);
	}
	
	@Inject(method = "activateBlock", at = @At(value = "HEAD"))
	private void activateBlock(int i, int j, int k, int l, CallbackInfoReturnable<Boolean> info) {
		VBEFullSlabBlock.player = this.minecraft.player;
	}
}
