/*	MIT License
	
	Copyright (c) 2020-present b0iizz
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
*/
package me.b0iizz.advancednbttooltip.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

/**
 * 
 * A class that represents a path inside of a {@link NbtCompound}
 * 
 * @author B0IIZZ
 */
public final class NBTPath {

	private static final String elementPattern = "([a-zA-Z0-9_]+)(\\[(\\d*)\\])?";

	/**
	 * An empty path which is parent to all other paths
	 */
	public static final NBTPath ROOT = new NBTPath();

	private final NBTPath parent;
	private final String name;

	private final Function<NbtElement, List<NbtElement>> searchFunc;

	/**
	 * Constructs a new NBTPath using the specified path. Children are separated by
	 * <code>'.'</code> and list items are indexed using
	 * <code>'[<i>index</i>]'</code>
	 * 
	 * @param path a path separated by <code>'.'</code>
	 */
	public NBTPath(String path) {
		this(ROOT, path.split("\\."));
	}

	/**
	 * Constructs a new NBTPath using the specified path. Children are separated by
	 * <code>'.'</code> and list items are indexed using
	 * <code>'[<i>index</i>]'</code>
	 * 
	 * @param parent the parent of the path
	 * @param path   a path separated by <code>'.'</code>
	 */
	public NBTPath(NBTPath parent, String path) {
		this(parent == null ? ROOT : parent, path.split("\\."));
	}

	private NBTPath() {
		this(null, new String[0]);
	}

	/**
	 * @param parent The parent
	 * @param path   The path
	 */
	protected NBTPath(NBTPath parent, String[] path) {
		path = Arrays.stream(path).filter(s -> !s.isEmpty()).toArray(String[]::new);
		if (path.length < 2) {
			this.parent = parent;
		} else {
			this.parent = new NBTPath(parent, Arrays.copyOf(path, path.length - 1));
		}
		if (path == null || path.length == 0) {
			this.name = "TAG_ROOT";
		} else {
			this.name = path[path.length - 1];
		}

		if (!this.name.matches(elementPattern))
			System.err.printf("Format error in path %s%n", this.name);

		String rest = this.name.replaceAll(elementPattern, "$1");
		String arrS = this.name.replaceAll(elementPattern, "$2");
		String idxS = this.name.replaceAll(elementPattern, "$3");

		if (arrS.isEmpty()) {
			this.searchFunc = (tag) -> {
				return Optional.ofNullable(((NbtCompound) tag).get(rest)).map(Collections::singletonList).orElseGet(Collections::emptyList);
			};
		} else if (idxS.isEmpty()) {
			this.searchFunc = (tag) -> {
				return Optional.ofNullable((List<NbtElement>) ((NbtCompound) tag).get(rest)).orElseGet(Collections::emptyList);
			};
		} else {
			int tmp = 0;
			try {
				tmp = Integer.parseInt(idxS);
			} catch (Throwable t) {
				System.err.printf("Error parsing index %s of %s%n", idxS, this.name);
			}
			final int idx = tmp;
			this.searchFunc = (tag) -> {
				return Collections.singletonList(((List<NbtElement>) ((NbtCompound) tag).get(rest)).get(idx));
			};
		}
	}

	/**
	 * Creates a child NBTPath using the specified name
	 * 
	 * @param name the name of the child
	 * @return The child NBTPath of the current path
	 */
	public NBTPath child(String name) {
		return new NBTPath(this, name);
	}

	/**
	 * Checks whether the path exists in the given {@link NbtCompound}
	 * 
	 * @param root the root {@link NbtCompound} to be checked
	 * @return <code>true</code> when an element with the current path exists in the
	 *         {@link NbtCompound} or else <code>false</code>
	 */
	public boolean exists(NbtCompound root) {
		return unsafeSearch(root) != null;
	}

	/**
	 * Gets an element with the current path for a given {@link NbtCompound}
	 * 
	 * @param root the root {@link NbtCompound} to be checked
	 * @return a <code>{@link NbtElement}</code> when an element with the current path
	 *         exists in the {@link NbtCompound} or else <code>null</code>
	 */
	public NbtElement get(NbtCompound root) {
		return getOptional(root).get();
	}

	/**
	 * Creates an optional for the current path in a given {@link NbtCompound}
	 * 
	 * @param root the root {@link NbtCompound} to be checked
	 * @return An {@link Optional} containing the state of this path in the given
	 *         {@link NbtCompound}
	 */
	public Optional<NbtElement> getOptional(NbtCompound root) {
		List<NbtElement> all = getAll(root);
		return all.isEmpty() ? Optional.empty() : Optional.of(all.get(0));
	}

	/**
	 * Gets all the elements with the current path for a given {@link NbtCompound}
	 * 
	 * @param root the root {@link NbtCompound} to be checked
	 * @return a <code>{@link NbtElement}</code> when an element with the current path
	 *         exists in the {@link NbtCompound} or else <code>null</code>
	 */
	public List<NbtElement> getAll(NbtCompound root) {
		return unsafeSearch(root);
	}

	/**
	 * Gets the parent NBTPath of this path
	 * 
	 * @return The parent of the current path or <code>ROOT</code>
	 */
	public NBTPath parent() {
		return this.parent;
	}

	@Override
	public String toString() {
		NBTPath elem = this;
		String res = elem.name;
		while ((elem = elem.parent) != null) {
			res = elem.name + "." + res;
		}
		return res;
	}

	private List<NbtElement> unsafeSearch(NbtCompound root) {
		try {
			if (this.parent != null) {
				List<NbtElement> res = new ArrayList<>();
				for (NbtElement t : this.parent.unsafeSearch(root)) {
					try {
						res.addAll(searchFunc.apply(t));
					} catch (Throwable ignored) {
					}
				}
				return res;
			} else
				return Collections.singletonList(root);
		} catch (Throwable t) {
			return Collections.emptyList();
		}
	}
}
