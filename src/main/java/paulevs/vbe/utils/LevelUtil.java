package paulevs.vbe.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.Level;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.maths.MCMath;
import net.minecraft.util.maths.Vec3D;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.impl.packet.FlattenedBlockChangeS2CPacket;
import net.modificationstation.stationapi.impl.world.chunk.ChunkSection;
import net.modificationstation.stationapi.impl.world.chunk.FlattenedChunk;

public class LevelUtil {
	public static HitResult raycast(Level level, PlayerEntity player) {
		double dist = 5.0;
		float toRadians = (float) Math.PI / 180;
		float pitch = player.prevPitch + (player.pitch - player.prevPitch);
		
		double x = player.prevX + (player.x - player.prevX);
		double y = player.prevY + (player.y - player.prevY) + 1.62 - (double) player.standingEyeHeight;
		double z = player.prevZ + (player.z - player.prevZ);
		Vec3D pos = Vec3D.getFromCacheAndSet(x, y, z);
		
		float yaw = player.prevYaw + (player.yaw - player.prevYaw);
		yaw = -yaw * toRadians - (float) Math.PI;
		float cosYaw = MCMath.cos(yaw);
		float sinYaw = MCMath.sin(yaw);
		float cosPitch = -MCMath.cos(-pitch * toRadians);
		
		Vec3D dir = pos.add(
			sinYaw * cosPitch * dist,
			(MCMath.sin(-pitch * ((float) Math.PI / 180))) * dist,
			cosYaw * cosPitch * dist
		);
		
		return level.getHitResult(pos, dir, false);
	}
	
	public static void setMetaSilent(Level level, int x, int y, int z, int meta) {
		FlattenedChunk chunk = (FlattenedChunk) level.getChunkFromCache(x >> 4, z >> 4);
		int index = level.getSectionIndex(y);
		ChunkSection section = chunk.sections[index];
		if (section == null) {
			section = new ChunkSection(index);
			chunk.sections[index] = section;
		}
		section.setMeta(x & 15, y & 15, z & 15, meta);
	}
	
	public static void setBlockSilent(Level level, int x, int y, int z, BlockState state) {
		FlattenedChunk chunk = (FlattenedChunk) level.getChunkFromCache(x >> 4, z >> 4);
		int index = level.getSectionIndex(y);
		ChunkSection section = chunk.sections[index];
		if (section == null) {
			section = new ChunkSection(index);
			chunk.sections[index] = section;
		}
		section.setBlockState(x & 15, y & 15, z & 15, state);
	}
	
	public static void setBlockSilent(Level level, int x, int y, int z, BlockState state, int meta) {
		FlattenedChunk chunk = (FlattenedChunk) level.getChunkFromCache(x >> 4, z >> 4);
		int index = level.getSectionIndex(y);
		ChunkSection section = chunk.sections[index];
		if (section == null) {
			section = new ChunkSection(index);
			chunk.sections[index] = section;
		}
		section.setBlockState(x & 15, y & 15, z & 15, state);
		section.setMeta(x & 15, y & 15, z & 15, meta);
	}
	
	public static void setBlockForceUpdate(Level level, int x, int y, int z, BlockState state) {
		level.setBlockState(x, y, z, state);
		updateBlock(level, x, y, z);
	}
	
	public static void updateBlock(Level level, int x, int y, int z) {
		if (level.isRemote) return;
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
		@SuppressWarnings("deprecation")
		MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
		for (Object obj : server.serverPlayerConnectionManager.players) {
			PlayerEntity player = (PlayerEntity) obj;
			if (player.level != level) continue;
			updateBlock(player, x, y, z);
		}
	}
	
	@Environment(EnvType.SERVER)
	private static void updateBlock(PlayerEntity player, int x, int y, int z) {
		PacketHelper.sendTo(player, new FlattenedBlockChangeS2CPacket(x, y, z, player.level));
	}
}
