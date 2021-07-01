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
package me.b0iizz.advancednbttooltip.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gson.Gson;

import me.b0iizz.advancednbttooltip.api.impl.JsonTooltipsImpl;

/**
 * API access point to all things related to .json generated tooltips.
 * 
 * @author B0IIZZ
 */
public interface JsonTooltips {

	/**
	 * Specifies the id of the {@link TooltipCondition} or {@link TooltipFactory}
	 * 
	 * @author B0IIZZ
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface TooltipIdentifier {
		/**
		 * @return The id of the {@link TooltipCondition} or {@link TooltipFactory} in
		 *         the .json
		 */
		String value();
	}

	/**
	 * Marks a field in a {@link TooltipCondition} or {@link TooltipFactory} as
	 * required by the object generated.
	 * 
	 * @author B0IIZZ
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface Required {
		/**
		 * @return The name of the field in the .json
		 */
		String value() default "";
	}

	/**
	 * Marks a field in a {@link TooltipCondition} or {@link TooltipFactory} as
	 * optional by the object generated.
	 * 
	 * @author B0IIZZ
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface Optional {
		/**
		 * @return The name of the field in the .json
		 */
		String value() default "";
	}


	/**
	 * @return The GSON Parser to parse tooltips
	 */
	public Gson getGson();
	
	/**
	 * Registers a new {@link TooltipFactory}
	 * 
	 * @param factoryClass the class of the {@link TooltipFactory}
	 */
	public void registerFactory(Class<? extends TooltipFactory> factoryClass);

	/**
	 * Registers a new {@link TooltipCondition}
	 * 
	 * @param conditionClass the class of the {@link TooltipCondition}
	 */
	public void registerCondition(Class<? extends TooltipCondition> conditionClass);

	/**
	 * @return The instance of the implementation of {@link JsonTooltips}
	 */
	@SuppressWarnings("deprecation")
	public static JsonTooltips getInstance() {
		return JsonTooltipsImpl.INSTANCE;
	}
	
}
