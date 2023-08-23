package paulevs.vbe.mixin.client;

import net.minecraft.block.BaseBlock;
import net.minecraft.client.BaseClientInteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SinglePlayerClientInteractionManager;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.VBEFullSlabBlock;

@Mixin(SinglePlayerClientInteractionManager.class)
public class SinglePlayerClientInteractionManagerMixin extends BaseClientInteractionManager {
	public SinglePlayerClientInteractionManagerMixin(Minecraft minecraft) {
		super(minecraft);
	}
	
	@Inject(method = "activateBlock", at = @At(value = "HEAD"))
	private void vbe_activateBlock(int i, int j, int k, int l, CallbackInfoReturnable<Boolean> info) {
		VBEFullSlabBlock.player = this.minecraft.player;
	}
	
	@Redirect(method = "playerDigBlock", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/block/BaseBlock;activate(Lnet/minecraft/level/Level;IIILnet/minecraft/entity/player/PlayerBase;)V"
	))
	private void vbe_disableActivation(BaseBlock instance, Level i, int j, int k, int arg2, PlayerBase playerBase) {}
}
