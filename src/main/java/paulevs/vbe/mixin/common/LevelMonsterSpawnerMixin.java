package paulevs.vbe.mixin.common;

import net.minecraft.level.Level;
import net.minecraft.level.LevelMonsterSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LevelMonsterSpawner.class)
public class LevelMonsterSpawnerMixin {
	@Inject(method = "spawnMonsters", at = @At("HEAD"), cancellable = true)
	private static void vbe_disableBedSpawn(Level level, List list, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(false);
	}
}
