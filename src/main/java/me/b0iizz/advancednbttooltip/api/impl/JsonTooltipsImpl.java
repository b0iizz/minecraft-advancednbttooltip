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
package me.b0iizz.advancednbttooltip.api.impl;

import com.google.gson.*;
import me.b0iizz.advancednbttooltip.api.CustomTooltip;
import me.b0iizz.advancednbttooltip.api.JsonTooltips;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.api.impl.builtin.MultipleFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The implementation of {@link JsonTooltips}
 *
 * @author B0IIZZ
 */
public final class JsonTooltipsImpl implements JsonTooltips {

	/**
	 * Only for internal use.
	 */
	@Deprecated
	public static final JsonTooltips INSTANCE = new JsonTooltipsImpl();

	private final Map<String, Class<? extends TooltipFactory>> tooltipFactories = new HashMap<>();
	private final Map<String, Class<? extends TooltipCondition>> tooltipConditions = new HashMap<>();

	private final Gson gson = new GsonBuilder()
			.registerTypeAdapter(TooltipFactory.class, (JsonDeserializer<?>) this::deserializeFactory)
			.registerTypeAdapter(TooltipCondition.class, (JsonDeserializer<?>) this::deserializeCondition)
			.registerTypeAdapter(CustomTooltip.class, (JsonDeserializer<?>) this::deserializeCustomTooltip).create();

	@Override
	public void registerFactory(Class<? extends TooltipFactory> factoryClass) {
		if (!factoryClass.isAnnotationPresent(TooltipCode.class)) {
			System.err.println("Tried registering factory %s without @%s".formatted(factoryClass.getSimpleName(),
					TooltipCode.class.getSimpleName()));
			return;
		}
		tooltipFactories.putIfAbsent(factoryClass.getAnnotation(TooltipCode.class).value(), factoryClass);
	}

	@Override
	public void registerCondition(Class<? extends TooltipCondition> conditionClass) {
		if (!conditionClass.isAnnotationPresent(TooltipCode.class)) {
			System.err.println("Tried registering condition %s without @%s".formatted(conditionClass.getSimpleName(),
					TooltipCode.class.getSimpleName()));
			return;
		}
		tooltipConditions.putIfAbsent(conditionClass.getAnnotation(TooltipCode.class).value(), conditionClass);
	}

	@Override
	public Gson getGson() {
		return gson;
	}

	private JsonTooltipsImpl() {
	}

	private TooltipFactory deserializeFactory(JsonElement element, Type type, JsonDeserializationContext ctx) {
		Objects.requireNonNull(element);
		if (element.isJsonNull())
			return TooltipFactory.EMPTY;
		if (element.isJsonPrimitive())
			return TooltipFactory.of(element.getAsString());
		if (element.isJsonArray()) {
			JsonObject toParse = new JsonObject();
			toParse.add("texts", element);
			return parseClass(toParse, MultipleFactory.class, ctx);
		}

		String id = getId(element);

		if (id.equals("empty"))
			return TooltipFactory.EMPTY;

		return parseClass(element, Optional.ofNullable(tooltipFactories.get(id))
				.orElseThrow(() -> new JsonSyntaxException("Unknown text-factory id \"%s\"".formatted(id))), ctx);

	}

	private TooltipCondition deserializeCondition(JsonElement element, Type type, JsonDeserializationContext ctx) {
		if (element.isJsonNull())
			return TooltipCondition.FALSE;
		if (element.isJsonPrimitive())
			return element.getAsBoolean() ? TooltipCondition.TRUE : TooltipCondition.FALSE;

		String id = getId(element);

		if (id.equals("true"))
			return TooltipCondition.TRUE;
		if (id.equals("false"))
			return TooltipCondition.FALSE;

		return parseClass(element, Optional.ofNullable(tooltipConditions.get(id))
				.orElseThrow(() -> new JsonSyntaxException("Unknown condition id \"%s\"".formatted(id))), ctx);
	}

	private String getId(JsonElement element) {
		try {
			return element.getAsJsonObject().get("id").getAsString();
		} catch (Exception e) {
			throw new JsonSyntaxException("Exception parsing field id", e);
		}
	}

	private CustomTooltip deserializeCustomTooltip(JsonElement element, Type type, JsonDeserializationContext ctx) {
		CustomTooltip result = new CustomTooltipImpl();
		if (element.isJsonNull() || element.isJsonArray() || element.isJsonPrimitive())
			return result;

		JsonSyntaxException toThrow = null;

		try {
			result.addText(ctx.deserialize(element.getAsJsonObject().get("text"), TooltipFactory.class));
		} catch (RuntimeException toWrap) {
			toThrow = new JsonSyntaxException("Exception deserializing CustomTooltip", toWrap);
		}

		try {
			result.addCondition(ctx.deserialize(element.getAsJsonObject().get("condition"), TooltipCondition.class));
		} catch (RuntimeException toWrap) {
			if (toThrow == null)
				toThrow = new JsonSyntaxException("Exception deserializing CustomTooltip", toWrap);
			else
				toThrow.addSuppressed(toWrap);
		}

		if (toThrow != null)
			throw toThrow;

		return result;
	}

	private <T> T parseClass(JsonElement element, Class<T> clazz, JsonDeserializationContext ctx) {
		Objects.requireNonNull(element, "element");
		Objects.requireNonNull(clazz, "clazz");

		if (!element.isJsonObject())
			throw new JsonSyntaxException("Element %s is not a json object!".formatted(element));
		T result;
		try {
			result = clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				 | NoSuchMethodException e) {
			throw new JsonSyntaxException("Exception instantiating %s".formatted(clazz.getSimpleName()), e);
		}

		JsonSyntaxException toThrow = null;

		List<Field> fields = Arrays.stream(clazz.getFields()).filter(
						field -> field.isAnnotationPresent(Required.class) || field.isAnnotationPresent(Suggested.class))
				.toList();
		for (Field field : fields) {
			String name = getNameForField(field);
			try {
				Object value = parseField(name, field.getGenericType(), element, ctx,
						field.isAnnotationPresent(Required.class));
				if (value != null)
					trySetField(field, result, value);
			} catch (RuntimeException e) {
				if (toThrow == null)
					toThrow = new JsonSyntaxException("Exception parsing %s".formatted(clazz.getSimpleName()), e);
				else
					toThrow.addSuppressed(e);
			}
		}

		if (toThrow != null)
			throw toThrow;
		return result;
	}

	private Object parseField(String name, Type fieldType, JsonElement element, JsonDeserializationContext ctx,
							  boolean required) {
		Object value = null;
		try {
			value = ctx.deserialize(element.getAsJsonObject().get(name), fieldType);
		} catch (JsonParseException exception) {
			throw new JsonSyntaxException("Exception deserializing field \"%s\"".formatted(name), exception);
		}
		if (value == null && required)
			throw new JsonSyntaxException("Missing field \"%s\"".formatted(name));
		return value;
	}

	private void trySetField(Field field, Object targetObject, Object value) {
		field.setAccessible(true);
		try {
			field.set(targetObject, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Exception setting field %s".formatted(getNameForField(field)), e);
		}
	}

	private String getNameForField(Field field) {
		String name = "";
		if (field.getAnnotation(Suggested.class) != null)
			name = field.getAnnotation(Suggested.class).value();
		else if (field.getAnnotation(Required.class) != null)
			name = field.getAnnotation(Required.class).value();
		if (name.isEmpty())
			name = field.getName();
		return name;
	}

}
