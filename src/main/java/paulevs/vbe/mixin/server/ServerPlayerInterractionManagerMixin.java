package paulevs.vbe.mixin.server;

import net.minecraft.entity.player.PlayerBase;
import net.minecraft.server.ServerPlayerInterractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.block.VBEFullSlabBlock;

@Mixin(ServerPlayerInterractionManager.class)
public class ServerPlayerInterractionManagerMixin {
	@Shadow public PlayerBase player;
	
	@Inject(method = "activateBlock", at = @At(value = "HEAD"))
	private void activateBlock(int i, int j, int k, int l, CallbackInfo info) {
		VBEFullSlabBlock.player = this.player;
	}
}
