package paulevs.vbe;

import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;

public class VBE {
	public static final ModID MOD_ID = ModID.of("vbe");
	
	public static Identifier id(String name) {
		return MOD_ID.id(name);
	}
}
