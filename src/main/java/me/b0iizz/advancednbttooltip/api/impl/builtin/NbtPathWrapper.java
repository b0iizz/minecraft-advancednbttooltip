package me.b0iizz.advancednbttooltip.api.impl.builtin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtElement;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

class NbtPathWrapper {

	private static final Cache<String, Optional<NbtPathArgumentType.NbtPath>> CACHE = CacheBuilder.newBuilder()
			.initialCapacity(16).concurrencyLevel(1).maximumSize(256).build();

	public static List<NbtElement> getAll(String pathName, NbtElement root) {
		return getPath(pathName).map(path -> {
			try {
				return path.count(root) != 0 ? path.get(root) : Collections.<NbtElement>emptyList();
			} catch (CommandSyntaxException ignored) {
			}
			return Collections.<NbtElement>emptyList();
		}).orElse(Collections.emptyList());
	}

	public static Optional<NbtPathArgumentType.NbtPath> getPath(String path) {
		try {
			return CACHE.get(path, () -> getPathInternal(path));
		} catch (ExecutionException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	private static Optional<NbtPathArgumentType.NbtPath> getPathInternal(String path) throws CommandSyntaxException {
		return Optional.of(new NbtPathArgumentType().parse(new StringReader(path)));
	}

}
