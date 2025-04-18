package paulevs.vbe.mixin.common;

import net.minecraft.achievement.Achievements;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.technical.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.block.VBELogBlock;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Shadow public ItemStack stack;
	
	@Inject(method = "onPlayerCollision", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/level/Level;playSound(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V",
		shift = Shift.BEFORE
	))
	private void vbe_fixAchievement(PlayerEntity player, CallbackInfo info) {
		if (stack.getType() instanceof BlockItem blockItem && blockItem.getBlock() instanceof VBELogBlock) {
			player.incrementStat(Achievements.MINE_WOOD);
		}
	}
}
