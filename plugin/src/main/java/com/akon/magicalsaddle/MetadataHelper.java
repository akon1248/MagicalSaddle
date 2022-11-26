package com.akon.magicalsaddle;

import com.akon.magicalsaddle.MagicalSaddle;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.ClassUtils;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

@UtilityClass
public class MetadataHelper {
	
	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(Metadatable metadatable, String key, Class<T> type, Plugin plugin) {
		return metadatable.getMetadata(key)
			.stream()
			.filter(value -> value.getOwningPlugin() == plugin && ClassUtils.primitiveToWrapper(type).isInstance(value.value()))
			.findAny()
			.map(value -> (T)value.value());
	}
	public Optional<?> get(Metadatable metadatable, String key, Plugin plugin) {
		return get(metadatable, key, Object.class, plugin);
	}
	
	public <T> Optional<T> get(Metadatable metadatable, String key, Class<T> type) {
		return get(metadatable, key, type, MagicalSaddle.getInstance());
	}
	
	public Optional<?> get(Metadatable metadatable, String key) {
		return get(metadatable, key, Object.class);
	}
	
	public void set(Metadatable metadatable, String key, Object value, Plugin plugin) {
		metadatable.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	public void set(Metadatable metadatable, String key, Object value) {
		set(metadatable, key, value, MagicalSaddle.getInstance());
	}

	public void remove(Metadatable metadatable, String key, Plugin plugin) {
		metadatable.removeMetadata(key, plugin);
	}

	public void remove(Metadatable metadatable, String key) {
		remove(metadatable, key, MagicalSaddle.getInstance());
	}

}
