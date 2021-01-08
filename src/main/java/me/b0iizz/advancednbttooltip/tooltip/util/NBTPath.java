package me.b0iizz.advancednbttooltip.tooltip.util;

import java.util.Arrays;
import java.util.Optional;

import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * 
 * A class that represents a path inside of a {@link CompoundTag}
 * 
 * @author B0IIZZ
 */
public final class NBTPath {

	private static final String arrayMatcher = "([a-zA-Z]+)(\\[(\\d+)\\])?";

	/**
	 * An empty path which is parent to all other paths
	 */
	public static final NBTPath ROOT = new NBTPath();

	private final String[] path;

	/**
	 * Constructs a new NBTPath using the specified path. Children are separated by
	 * <code>'.'</code> and list items are indexed using
	 * <code>'[<i>index</i>]'</code>
	 * 
	 * @param path a path separated by <code>'.'</code>
	 */
	public NBTPath(String path) {
		this(path.split("\\."));
	}

	private NBTPath() {
		this(new String[0]);
	}

	private NBTPath(String[] path) {
		this.path = path;
	}

	/**
	 * Creates a child NBTPath using the specified name
	 * 
	 * @param name the name of the child
	 * @return The child NBTPath of the current path
	 */
	public NBTPath child(String name) {
		String[] newPath = Arrays.copyOf(path, path.length + 1);
		newPath[newPath.length - 1] = name;
		return new NBTPath(newPath);
	}

	/**
	 * Checks whether the path exists in the given {@link CompoundTag}
	 * 
	 * @param root the root {@link CompoundTag} to be checked
	 * @return <code>true</code> when an element with the current path exists in the
	 *         {@link CompoundTag} or else <code>false</code>
	 */
	public boolean exists(CompoundTag root) {
		return unsafeSearch(root) != null;
	}

	/**
	 * Gets the element with the current path for a given {@link CompoundTag}
	 * 
	 * @param root the root {@link CompoundTag} to be checked
	 * @return a <code>{@link Tag}</code> when an element with the current path
	 *         exists in the {@link CompoundTag} or else <code>null</code>
	 */
	public Tag get(CompoundTag root) {
		return getOptional(root).get();
	}

	/**
	 * Creates an optional for the current path in a given {@link CompoundTag}
	 * 
	 * @param root the root {@link CompoundTag} to be checked
	 * @return An {@link Optional} containing the state of this path in the given
	 *         {@link CompoundTag}
	 */
	public Optional<Tag> getOptional(CompoundTag root) {
		Tag result = unsafeSearch(root);
		return result == null ? Optional.empty() : Optional.of(result);
	}

	/**
	 * Gets the parent NBTPath of this path
	 * 
	 * @return The parent of the current path or <code>ROOT</code>
	 */
	public NBTPath getParent() {
		if (path.length > 0) {
			return new NBTPath(Arrays.copyOf(path, path.length - 1));
		}
		return ROOT;
	}

	private Tag unsafeSearch(CompoundTag root) {
		Tag result = root;
		try {
			for (int i = 0; i < path.length; i++) {
				String elementName = path[i].replaceAll(arrayMatcher, "$1");
				if (result instanceof CompoundTag && ((CompoundTag) result).contains(elementName)) {
					result = ((CompoundTag) result).get(elementName);
				} else
					return null;
				String idxStr = path[i].replaceAll(arrayMatcher, "$3");
				if (!idxStr.isEmpty() && result instanceof AbstractListTag<?>) {
					int idx = Integer.parseInt(idxStr);
					result = (Tag) ((AbstractListTag<?>) result).get(idx);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}
}
