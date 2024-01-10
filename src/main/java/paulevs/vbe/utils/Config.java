package paulevs.vbe.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
	private final Map<String, ConfigEntry<?>> entries = new HashMap<>();
	private final Map<String, String> preEntries = new HashMap<>();
	private final List<String> order = new ArrayList<>();
	private final File file;
	
	public Config(String name) {
		//noinspection deprecation
		this.file = new File(FabricLoader.getInstance().getConfigDirectory(), "vbe/" + name + ".cfg");
		if (file.exists()) load();
	}
	
	public void save() {
		if (!file.exists()) writeFile();
		else if (entries.size() != preEntries.size()) writeFile();
	}
	
	public ConfigEntry<Boolean> addEntry(String name, boolean value, String... comments) {
		String stored = preEntries.get(name);
		if (stored != null) value = Boolean.parseBoolean(stored);
		ConfigEntry<Boolean> entry = new ConfigEntry<>(name, value, List.of(comments));
		entries.put(name, entry);
		order.add(name);
		return entry;
	}
	
	public ConfigEntry<Float> addEntry(String name, float value, String... comments) {
		String stored = preEntries.get(name);
		if (stored != null) value = Float.parseFloat(stored);
		ConfigEntry<Float> entry = new ConfigEntry<>(name, value, List.of(comments));
		entries.put(name, entry);
		order.add(name);
		return entry;
	}
	
	public ConfigEntry<Integer> addEntry(String name, int value, String... comments) {
		String stored = preEntries.get(name);
		if (stored != null) value = Integer.parseInt(stored);
		ConfigEntry<Integer> entry = new ConfigEntry<>(name, value, List.of(comments));
		entries.put(name, entry);
		order.add(name);
		return entry;
	}
	
	public ConfigEntry<String> addEntry(String name, String value, String... comments) {
		String stored = preEntries.get(name);
		if (stored != null) value = stored;
		ConfigEntry<String> entry = new ConfigEntry<>(name, value, List.of(comments));
		entries.put(name, entry);
		order.add(name);
		return entry;
	}
	
	private void writeFile() {
		int max = entries.size() - 1;
		try {
			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file);
			for (int i = 0; i < order.size(); i++) {
				entries.get(order.get(i)).append(writer);
				if (i < max) writer.append('\n');
			}
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
	
	private void load() {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file.toPath());
		}
		catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
		if (lines == null) return;
		lines.stream().filter(line -> line.length() > 2 && line.charAt(0) != '#').forEach(line -> {
			int split = line.indexOf('=');
			String name = line.substring(0, split).trim();
			String value = line.substring(split + 1).trim();
			preEntries.put(name, value);
		});
	}
}
