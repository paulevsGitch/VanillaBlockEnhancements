package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.world.FlattenedWorldManager;
import net.modificationstation.stationapi.impl.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockProperties.TopBottom;

@Mixin(FlattenedWorldManager.class)
public class FlattenedWorldManagerMixin {
	@Inject(method = "loadChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/modificationstation/stationapi/impl/world/chunk/ChunkSection;getLightArray(Lnet/minecraft/level/LightType;)Lnet/modificationstation/stationapi/impl/world/chunk/NibbleArray;",
		shift = Shift.AFTER
	))
	private static void vbe_fixLoadedBlocks(
		Level level, CompoundTag chunkTag, CallbackInfoReturnable<Chunk> info,
		@Local ChunkSection chunkSection
	) {
		for (short i = 0; i < 4096; i++) {
			byte dx = (byte) (i & 15);
			byte dy = (byte) ((i >> 4) & 15);
			byte dz = (byte) (i >> 8);
			BlockState state = chunkSection.getBlockState(dx, dy, dz);
			if (state.getBlock() instanceof DoorBlock door && VBE.ENHANCED_DOORS.getValue()) {
				if (state != door.getDefaultState()) continue;
				int meta = chunkSection.getMeta(dx, dy, dz);
				if (meta == 0) continue;
				boolean bottom = meta < 8;
				boolean open = (meta & 4) == 0;
				Direction dir = Direction.fromHorizontal(meta & 3).getOpposite();
				chunkSection.setBlockState(
					dx, dy, dz,
					state
						.with(VBEBlockProperties.TOP_BOTTOM, bottom ? TopBottom.BOTTOM : TopBottom.TOP)
						.with(Properties.HORIZONTAL_FACING, dir)
						.with(VBEBlockProperties.OPENED, open)
				);
			}
			else if (state.getBlock() instanceof StairsBlock stairs && VBE.ENHANCED_STAIRS.getValue()) {
				if (state != stairs.getDefaultState()) continue;
				int meta = chunkSection.getMeta(dx, dy, dz);
				if (meta == 0) continue;
				Direction dir = switch (meta & 3) {
					case 0 -> Direction.SOUTH;
					case 2 -> Direction.WEST;
					case 3 -> Direction.EAST;
					default -> Direction.NORTH;
				};
				chunkSection.setBlockState(
					dx, dy, dz,
					state.with(Properties.HORIZONTAL_FACING, dir)
				);
			}
			else if (state.getBlock() instanceof TrapdoorBlock stairs && VBE.ENHANCED_TRAPDOORS.getValue()) {
				if (state != stairs.getDefaultState()) continue;
				int meta = chunkSection.getMeta(dx, dy, dz);
				if (meta == 0) continue;
				Direction dir = switch (meta & 3) {
					case 1 -> Direction.EAST;
					case 2 -> Direction.SOUTH;
					case 3 -> Direction.NORTH;
					default -> Direction.WEST;
				};
				boolean open = (meta & 4) != 0;
				chunkSection.setBlockState(
					dx, dy, dz,
					state.with(Properties.HORIZONTAL_FACING, dir).with(VBEBlockProperties.OPENED, open)
				);
			}
		}
	}
}
