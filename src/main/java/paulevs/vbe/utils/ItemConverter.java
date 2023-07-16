package paulevs.vbe.utils;

import net.minecraft.block.BaseBlock;
import net.minecraft.item.BaseItem;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class ItemConverter {
	public static int getID(int id, int damage) {
		if (id == BaseBlock.STONE_SLAB.id) return VBEBlocks.getHalfSlabByMeta(damage).id;
		if (id == BaseBlock.LOG.id) return VBEBlocks.getLogByMeta(damage).id;
		if (id == BaseBlock.LEAVES.id) return VBEBlocks.getLeavesByMeta(damage).id;
		if (id == BaseItem.woodDoor.id) return VBEItems.OAK_DOOR.id;
		if (id == BaseItem.ironDoor.id) return VBEItems.IRON_DOOR.id;
		return -1;
	}
}
