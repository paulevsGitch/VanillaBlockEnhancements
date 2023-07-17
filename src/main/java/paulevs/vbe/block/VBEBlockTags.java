package paulevs.vbe.block;

import net.minecraft.block.BaseBlock;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.tag.TagKey;
import paulevs.vbe.VBE;

public class VBEBlockTags {
	public static final TagKey<BaseBlock> REQUIRES_POWER = get("requires_power");
	public static final TagKey<BaseBlock> FENCE_CONNECT = get("fence_connect");
	public static final TagKey<BaseBlock> LEAVES = getDefault("leaves");
	public static final TagKey<BaseBlock> LOGS = getDefault("logs");
	
	private static TagKey<BaseBlock> get(String name) {
		return TagKey.of(BlockRegistry.KEY, VBE.id(name));
	}
	
	private static TagKey<BaseBlock> getDefault(String name) {
		return TagKey.of(BlockRegistry.KEY, Identifier.of(name));
	}
}
