package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.ListTag;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.state.property.Property;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.impl.world.FlattenedWorldManager;
import net.modificationstation.stationapi.impl.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.VBEBlockFixer;

import java.util.Collection;

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
		VBEBlockFixer.fixChunkSection(chunkSection);
	}
	
	@Inject(method = "loadChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/level/Level;sectionCoordToIndex(I)I",
		shift = Shift.AFTER
	))
	private static void vbe_fixBlocksWithoutProperties(
		Level level, CompoundTag chunkTag, CallbackInfoReturnable<Chunk> info,
		@Local(name = "sectionTag") CompoundTag sectionTag
	) {
		if (!sectionTag.containsKey("block_states")) return;
		CompoundTag states = sectionTag.getCompoundTag("block_states");
		ListTag palette = states.getListTag("palette");
		for (short i = 0; i < palette.size(); i++) {
			CompoundTag tag = (CompoundTag) palette.get(i);
			if (tag.containsKey("Properties")) continue;
			Identifier id = Identifier.of(tag.getString("Name"));
			Block block = BlockRegistry.INSTANCE.get(id);
			if (block == null) continue;
			Collection<Property<?>> properties = block.getStateManager().getProperties();
			if (properties.isEmpty()) continue;
			BlockState state = block.getDefaultState();
			CompoundTag propertiesTag = new CompoundTag();
			tag.put("Properties", propertiesTag);
			for (Property<?> property : state.getProperties()) {
				propertiesTag.put(property.getName(), state.get(property).toString());
			}
		}
	}
}
