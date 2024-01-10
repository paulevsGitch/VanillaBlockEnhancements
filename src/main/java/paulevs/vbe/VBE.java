package paulevs.vbe;

import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import paulevs.vbe.utils.Config;
import paulevs.vbe.utils.ConfigEntry;

public class VBE {
	public static final Namespace NAMESPACE = Namespace.of("vbe");
	public static final Config COMMON_CONFIG = new Config("common");
	
	public static final ConfigEntry<Boolean> ENABLE_SHIFT_CLICK = COMMON_CONFIG.addEntry(
		"enableShiftClick",
		true,
		"Allows to place blocks with shift-clicking on blocks with GUI.",
		"If set to false attempt to place block on workbench/furnace will open GUI."
	);
	
	public static final ConfigEntry<Boolean> DISABLE_WORKBENCH_DROP = COMMON_CONFIG.addEntry(
		"disableWorkbenchDrop",
		true,
		"Will place items from workbench into player inventory",
		"instead of dropping them on the ground after closing GUI."
	);
	
	public static final ConfigEntry<Boolean> ENHANCED_CHESTS = COMMON_CONFIG.addEntry(
		"enhancedChests",
		true,
		"Allow to place chest near each other, enhances chest rotation and add new states to chests.",
		"!! WARNING !! changing this will cause data corruption in existing worlds!"
	);
	
	public static final ConfigEntry<Boolean> ENHANCED_DOORS = COMMON_CONFIG.addEntry(
		"enhancedDoors",
		true,
		"Changes doors behaviour: no open/close issues, double doors will open at once, add new states to doors.",
		"!! WARNING !! changing this will cause data corruption in existing worlds!"
	);
	
	public static final ConfigEntry<Boolean> ENHANCED_TRAPDOORS = COMMON_CONFIG.addEntry(
		"enhancedTrapdoors",
		true,
		"Changes trapdoors behaviour: no open/close issues, not require handle block,",
		"can be place on top and bottom, add new states to trapdoors.",
		"!! WARNING !! changing this will cause data corruption in existing worlds!"
	);
	
	public static final ConfigEntry<Boolean> ENHANCED_SLABS = COMMON_CONFIG.addEntry(
		"enhancedSlabs",
		true,
		"Changes slabs: slabs can be placed vertically, full slabs have directions, full slabs will break by parts, add new states to slabs",
		"!! WARNING !! changing this will cause data corruption in existing worlds!"
	);
	
	public static final ConfigEntry<Boolean> BETTER_SLABS_RECIPE = COMMON_CONFIG.addEntry(
		"betterSlabsRecipe",
		true,
		"Allows to craft 6 slabs instead of 3"
	);
	
	public static Identifier id(String name) {
		return NAMESPACE.id(name);
	}
	
	static {
		COMMON_CONFIG.save();
	}
}
