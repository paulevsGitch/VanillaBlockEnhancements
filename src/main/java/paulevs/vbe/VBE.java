package paulevs.vbe;

import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;

public class VBE {
	public static final Namespace NAMESPACE = Namespace.of("vbe");
	
	public static Identifier id(String name) {
		return NAMESPACE.id(name);
	}
}
