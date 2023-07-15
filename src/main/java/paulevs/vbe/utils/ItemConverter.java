package paulevs.vbe.utils;

import net.minecraft.block.BaseBlock;
import net.minecraft.item.BaseItem;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class ItemConverter {
	public static int getID(int id, int damage) {
		if (id == BaseBlock.STONE_SLAB.id) return VBEBlocks.getSlabByMeta(damage).id;
		if (id == BaseItem.woodDoor.id) return VBEItems.OAK_DOOR.id;
		if (id == BaseItem.ironDoor.id) return VBEItems.IRON_DOOR.id;
		return -1;
	}
}
