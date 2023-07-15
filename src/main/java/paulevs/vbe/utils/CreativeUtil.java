package paulevs.vbe.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BaseBlock;
import net.minecraft.entity.player.PlayerBase;
import paulevs.bhcreative.BHCreative;
import paulevs.bhcreative.util.BlockSelectAPI;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class CreativeUtil {
	private static final boolean NOT_INSTALLED = !FabricLoader.getInstance().isModLoaded("bhcreative");
	
	public static boolean isCreative(PlayerBase player) {
		if (NOT_INSTALLED) return false;
		return BHCreative.isInCreative(player);
	}
	
	public static void registerBlockConverters() {
		if (NOT_INSTALLED) return;
		BlockSelectAPI.registerConverter(VBEBlocks.STONE_SLAB_FULL, state -> VBEBlocks.STONE_SLAB_HALF.asItem());
		BlockSelectAPI.registerConverter(VBEBlocks.COBBLESTONE_SLAB_FULL, state -> VBEBlocks.COBBLESTONE_SLAB_HALF.asItem());
		BlockSelectAPI.registerConverter(VBEBlocks.OAK_SLAB_FULL, state -> VBEBlocks.OAK_SLAB_HALF.asItem());
		BlockSelectAPI.registerConverter(VBEBlocks.SANDSTONE_SLAB_FULL, state -> VBEBlocks.SANDSTONE_SLAB_HALF.asItem());
		BlockSelectAPI.registerConverter(BaseBlock.WOOD_DOOR, state -> VBEItems.OAK_DOOR);
		BlockSelectAPI.registerConverter(BaseBlock.IRON_DOOR, state -> VBEItems.IRON_DOOR);
	}
}
