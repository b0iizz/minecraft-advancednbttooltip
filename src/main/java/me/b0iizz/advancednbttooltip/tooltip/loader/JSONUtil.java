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
package me.b0iizz.advancednbttooltip.tooltip.loader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * A small utility class for json parsing for custom tooltips
 * 
 * @author B0IIZZ
 */
class JSONUtil {

	public JSONUtil() {
		throw new UnsupportedOperationException();
	}
	
	// Error messages
	private static final String FIELD_NOT_FOUND = "Could not find required field: %s";
	private static final String WRONG_TYPE = "Required field %s is not of expected type %s!";

	/**
	 * @return All possible error messages that can be caused by this utility class
	 */
	public static String[] getAllErrorMessages() {
		return new String[] { FIELD_NOT_FOUND, WRONG_TYPE };
	}

	/**
	 * Extracts a child JsonElement of the JsonObject and casts it to a desired type
	 * 
	 * @param <T>   The desired type the JSON element will be cast to
	 * @param obj   a JsonObject
	 * @param name  the name of the required child
	 * @param clazz The class of the desired type <code>&lt;T&gt;</code>
	 * @return A cast version of <code>obj.get(name)</code>
	 * 
	 * @throws TooltipLoaderException when the field is not found or not of the
	 *                                desired type
	 */
	public static <T> T require(JsonObject obj, String name, Class<T> clazz) {
		if (!obj.has(name))
			throw new TooltipLoaderException(String.format(FIELD_NOT_FOUND, name));
		return suggest(obj, name, clazz)
				.orElseThrow(() -> new TooltipLoaderException(String.format(WRONG_TYPE, name, clazz.getSimpleName())));
	}

	/**
	 * Extracts a child JsonElement of the JsonObject and casts it to a desired type
	 * if that is possible
	 * 
	 * @param <T>   The desired type the JSON element will be cast to
	 * @param obj   a JsonObject
	 * @param name  the name of the required child
	 * @param clazz The class of the desired type <code>&lt;T&gt;</code>
	 * @return An Optional of a cast version of <code>obj.get(name)</code> if the
	 *         child could successfully retrieved and cast
	 */
	public static <T> Optional<T> suggest(JsonObject obj, String name, Class<T> clazz) {
		return get(obj, name).filter(JsonPredicate.of(clazz).predicate).map(JsonMapper.of(clazz).mapper)
				.map((u) -> (T) u);
	}

	private static Optional<JsonElement> get(JsonObject obj, String name) {
		return obj.has(name) ? Optional.of(obj.get(name)) : Optional.empty();
	}

	private static enum JsonMapper {
		ARRAY(JsonElement::getAsJsonArray, JsonArray.class), BIGDECIMAL(JsonElement::getAsBigDecimal, BigDecimal.class),
		BIGINTEGER(JsonElement::getAsBigInteger, BigInteger.class),
		BOOLEAN(JsonElement::getAsBoolean, Boolean.class, boolean.class),
		BYTE(JsonElement::getAsByte, Byte.class, byte.class),
		CHARACTER((e) -> e.getAsString().charAt(0), Character.class, char.class),
		DOUBLE(JsonElement::getAsDouble, Double.class, double.class),
		FLOAT(JsonElement::getAsFloat, Float.class, float.class), IDENTITY((e) -> e),
		INTEGER(JsonElement::getAsInt, Integer.class, int.class), LONG(JsonElement::getAsLong, Long.class, long.class),
		NULL(JsonElement::getAsJsonNull, JsonNull.class, Void.TYPE, null),
		NUMBER(JsonElement::getAsNumber, Number.class), OBJECT(JsonElement::getAsJsonObject, JsonObject.class),
		SHORT(JsonElement::getAsShort, Short.class, short.class), STRING(JsonElement::getAsString, String.class);

		private static final Map<Class<?>, JsonMapper> classToMapper = new HashMap<>();

		private JsonMapper(Function<JsonElement, ?> mapper, Class<?>... classes) {
			this.mapper = mapper;
			this.applicableClasses = classes;

		}

		public final Class<?>[] applicableClasses;

		public final Function<JsonElement, ?> mapper;

		public static JsonMapper of(Class<?> clazz) {
			return classToMapper.getOrDefault(clazz, IDENTITY);
		}

		static {
			for (JsonMapper p : values()) {
				for (Class<?> clazz : p.applicableClasses)
					classToMapper.putIfAbsent(clazz, p);
			}
		}
	}

	private static enum JsonPredicate {
		ANY((e) -> true), ARRAY(JsonElement::isJsonArray, JsonArray.class),
		BOOLEAN(((Predicate<JsonElement>) JsonElement::isJsonPrimitive)
				.and((prim -> prim.getAsJsonPrimitive().isBoolean())), Boolean.class, boolean.class),
		NULL(JsonElement::isJsonNull, JsonNull.class, Void.TYPE, null),
		NUMBER(((Predicate<JsonElement>) JsonElement::isJsonPrimitive)
				.and((prim -> prim.getAsJsonPrimitive().isNumber())), Number.class, BigInteger.class, BigDecimal.class,
				Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, byte.class, short.class,
				int.class, long.class, float.class, double.class),
		OBJECT(JsonElement::isJsonObject, JsonObject.class),
		PRIMITIVE(JsonElement::isJsonPrimitive, JsonPrimitive.class),
		STRING(((Predicate<JsonElement>) JsonElement::isJsonPrimitive)
				.and((prim -> prim.getAsJsonPrimitive().isString())), String.class, Character.class, char.class);

		private static final Map<Class<?>, JsonPredicate> classToPredicate = new HashMap<>();

		private JsonPredicate(Predicate<JsonElement> predicate, Class<?>... classes) {
			this.predicate = predicate;
			this.applicableClasses = classes;

		}

		public final Class<?>[] applicableClasses;

		public final Predicate<JsonElement> predicate;

		public static JsonPredicate of(Class<?> clazz) {
			return classToPredicate.getOrDefault(clazz, ANY);
		}

		static {
			for (JsonPredicate p : values()) {
				for (Class<?> clazz : p.applicableClasses)
					classToPredicate.putIfAbsent(clazz, p);
			}
		}
	}

	public static final class LegacyUtil {
		// Error messages
		private static final String FIELD_NOT_FOUND = "Could not find required field: %s";
		private static final String WRONG_TYPE = "Required field %s is not of expected type %s!";

		private static final Optional<JsonElement> optional(JsonObject obj, String name) {
			return obj.has(name) ? Optional.of(obj.get(name)) : Optional.empty();
		}

		public static final Optional<JsonElement> optional(JsonObject obj, String name, String type) {
			return optional(obj, name).filter(JsonType.of(type).predicate);
		}

		private static final JsonElement getField(JsonObject obj, String name) {
			return optional(obj, name)
					.orElseThrow(() -> new TooltipLoaderException(String.format(FIELD_NOT_FOUND, name)));
		}

		public static final JsonElement require(JsonObject obj, String name, String type) {
			return Optional.of(getField(obj, name)).filter(JsonType.of(type).predicate)
					.orElseThrow(() -> new TooltipLoaderException(String.format(WRONG_TYPE, name, type)));
		}

		private static final Predicate<JsonElement> primitive = JsonElement::isJsonPrimitive;

		private static enum JsonType {
			// Original types
			ANY((e) -> true), OBJECT(JsonElement::isJsonObject), ARRAY(JsonElement::isJsonArray),
			NULL(JsonElement::isJsonNull), PRIMITIVE(primitive),
			STRING(primitive.and(((prim) -> ((JsonPrimitive) prim).isString()))),
			BOOLEAN(primitive.and(((prim) -> ((JsonPrimitive) prim).isBoolean()))),
			NUMBER(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber()))),
			// Copy of String
			CHAR(primitive.and(((prim) -> ((JsonPrimitive) prim).isString()))),
			CHARACTER(primitive.and(((prim) -> ((JsonPrimitive) prim).isString()))),
			// Copy of Boolean
			BOOL(primitive.and(((prim) -> ((JsonPrimitive) prim).isBoolean()))),
			// Copy of Number
			BYTE(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber()))),
			SHORT(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber()))),
			INT(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber()))),
			INTEGER(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber()))),
			LONG(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber()))),
			FLOAT(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber()))),
			DOUBLE(primitive.and(((prim) -> ((JsonPrimitive) prim).isNumber())));

			public final Predicate<JsonElement> predicate;

			private JsonType(Predicate<JsonElement> predicate) {
				this.predicate = predicate;
			}

			public static JsonType of(String name) {
				return valueOf(name.toUpperCase());
			}

		}
	}
}
