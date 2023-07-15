package paulevs.vbe.block;

import net.minecraft.block.BaseBlock;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import paulevs.vbe.VBE;

public class VBEBlockTags {
	public static final TagKey<BaseBlock> REQUIRES_POWER = get("requires_power");
	
	private static TagKey<BaseBlock> get(String name) {
		return TagKey.of(BlockRegistry.KEY, VBE.id(name));
	}
}
