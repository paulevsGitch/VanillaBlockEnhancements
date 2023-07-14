package paulevs.vbe.utils;

import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.maths.MathHelper;
import net.minecraft.util.maths.Vec3f;

public class LevelUtil {
	public static HitResult getHit(Level level, PlayerBase player) {
		double dist = 5.0;
		float toRadians = (float) Math.PI / 180;
		float pitch = player.prevPitch + (player.pitch - player.prevPitch);
		
		double x = player.prevX + (player.x - player.prevX);
		double y = player.prevY + (player.y - player.prevY) + 1.62 - (double) player.standingEyeHeight;
		double z = player.prevZ + (player.z - player.prevZ);
		Vec3f pos = Vec3f.getFromCacheAndSet(x, y, z);
		
		float yaw = player.prevYaw + (player.yaw - player.prevYaw);
		yaw = -yaw * toRadians - (float) Math.PI;
		float cosYaw = MathHelper.cos(yaw);
		float sinYaw = MathHelper.sin(yaw);
		float cosPitch = -MathHelper.cos(-pitch * toRadians);
		
		Vec3f dir = pos.add(
			sinYaw * cosPitch * dist,
			(MathHelper.sin(-pitch * ((float) Math.PI / 180))) * dist,
			cosYaw * cosPitch * dist
		);
		
		return level.getHitResult(pos, dir, false);
	}
}