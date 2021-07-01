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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import me.b0iizz.advancednbttooltip.api.CustomTooltip;
import me.b0iizz.advancednbttooltip.api.JsonTooltips;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.api.impl.builtin.LiteralFactory;
import me.b0iizz.advancednbttooltip.api.impl.builtin.MultipleFactory;

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
			.registerTypeAdapter(TooltipFactory.class, wrap(this::deserializeFactory))
			.registerTypeAdapter(TooltipCondition.class, wrap(this::deserializeCondition))
			.registerTypeAdapter(CustomTooltip.class, wrap(this::deserializeCustomTooltip)).create();

	@Override
	public void registerFactory(Class<? extends TooltipFactory> factoryClass) {
		if (!factoryClass.isAnnotationPresent(TooltipIdentifier.class)) {
			System.err.println(
					"Tried registering factory %s without @TooltipIdentitifer".formatted(factoryClass.getSimpleName()));
			return;
		}
		tooltipFactories.putIfAbsent(factoryClass.getAnnotation(TooltipIdentifier.class).value(), factoryClass);
	}

	@Override
	public void registerCondition(Class<? extends TooltipCondition> conditionClass) {
		if (!conditionClass.isAnnotationPresent(TooltipIdentifier.class)) {
			System.err.println("Tried registering condition %s without @TooltipIdentitifer"
					.formatted(conditionClass.getSimpleName()));
			return;
		}
		tooltipConditions.putIfAbsent(conditionClass.getAnnotation(TooltipIdentifier.class).value(), conditionClass);
	}

	@Override
	public Gson getGson() {
		return gson;
	}
	
	private JsonTooltipsImpl() {}

	private TooltipFactory deserializeFactory(JsonElement element) {
		if (element.isJsonNull())
			return TooltipFactory.EMPTY;
		if (element.isJsonPrimitive())
			return new LiteralFactory(element.getAsString());
		if (element.isJsonArray())
			return new MultipleFactory(StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
					.map(this::deserializeFactory).toArray(TooltipFactory[]::new));

		String id;
		try {
			id = element.getAsJsonObject().get("id").getAsString();
		} catch (Exception e) {
			throw new JsonSyntaxException("Error parsing id for %s".formatted(element.toString()), e);
		}

		if (id.equals("empty"))
			return TooltipFactory.EMPTY;

		return parseClass(element, java.util.Optional.ofNullable(tooltipFactories.get(id))
				.orElseThrow(() -> new JsonSyntaxException("Invalid TooltipFactory id \"%s\"".formatted(id))));

	}

	private TooltipCondition deserializeCondition(JsonElement element) {
		if (element.isJsonNull())
			return TooltipCondition.FALSE;
		if (element.isJsonPrimitive())
			return element.getAsBoolean() ? TooltipCondition.TRUE : TooltipCondition.FALSE;

		String id;
		try {
			id = element.getAsJsonObject().get("id").getAsString();
		} catch (Exception e) {
			throw new JsonSyntaxException("Error parsing id for %s".formatted(element.toString()), e);
		}

		if (id.equals("true"))
			return TooltipCondition.TRUE;
		if (id.equals("false"))
			return TooltipCondition.FALSE;
		
		return parseClass(element, java.util.Optional.ofNullable(tooltipConditions.get(id))
				.orElseThrow(() -> new JsonSyntaxException("Invalid TooltipCondition id \"%s\"".formatted(id))));
	}

	private CustomTooltip deserializeCustomTooltip(JsonElement element) {
		CustomTooltip result = new CustomTooltipImpl();
		if (element.isJsonNull() || element.isJsonArray() || element.isJsonPrimitive())
			return result;

		try {
			result.addText(gson.fromJson(element.getAsJsonObject().get("text"), TooltipFactory.class));
		} catch (Exception e) {
			throw new JsonSyntaxException("Error parsing text!", e);
		}

		try {
			result.addCondition(gson.fromJson(element.getAsJsonObject().get("condition"), TooltipCondition.class));
		} catch (Exception e) {
			throw new JsonSyntaxException("Error parsing condition!", e);
		}

		return result;
	}

	private <T> T parseClass(JsonElement element, Class<T> clazz) {
		Objects.requireNonNull(clazz);
		if (!element.isJsonObject())
			throw new JsonSyntaxException("Element %s is not of required type object!".formatted(element));
		try {
			T result = clazz.getConstructor().newInstance();

			List<Field> required = Arrays.stream(clazz.getFields())
					.filter(field -> field.isAnnotationPresent(Required.class)).toList();
			for (Field field : required) {
				String name = field.getAnnotation(Required.class).value();
				if (name.isEmpty())
					name = field.getName();

				Object value = gson.fromJson(element.getAsJsonObject().get(name), field.getGenericType());

				if (value == null)
					throw new JsonSyntaxException("Missing field \"%s\" in %s!".formatted(name, element.toString()));

				field.setAccessible(true);
				try {
					field.set(result, value);
				} catch (Exception e) {
					throw new RuntimeException("Error setting Field %s".formatted(name), e);
				}
			}
			;

			List<Field> optional = Arrays.stream(clazz.getFields())
					.filter(field -> field.isAnnotationPresent(Optional.class)).toList();
			for (Field field : optional) {
				String name = field.getAnnotation(Optional.class).value();
				if (name.isEmpty())
					name = field.getName();

				Object value = gson.fromJson(element.getAsJsonObject().get(name), field.getGenericType());

				if (value == null)
					continue;

				field.setAccessible(true);
				try {
					field.set(result, value);
				} catch (Exception e) {
					throw new RuntimeException("Error setting Field %s".formatted(name), e);
				}
			}
			;

			return result;
		} catch (Exception e) {
			throw new JsonSyntaxException("Exception parsing " + clazz.getSimpleName(), e);
		}
	}

	private static <T> JsonDeserializer<T> wrap(Function<JsonElement, T> toWrap) {
		return (json, type, ctx) -> toWrap.apply(json);
	}

}
