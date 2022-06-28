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

/**
 * A tooltip
 *
 * @author B0IIZZ
 */
@SuppressWarnings("UnusedReturnValue")
public interface CustomTooltip extends TooltipCondition, TooltipFactory {

	/**
	 * Adds a {@link TooltipFactory factory} which will be shown with the tooltip.
	 *
	 * @param text The factory to be added.
	 * @return The original {@link CustomTooltip} object. Used for
	 * chaining.
	 */
	CustomTooltip addText(TooltipFactory text);

	/**
	 * Adds a {@link TooltipCondition condition} which has to be met to show the
	 * tooltip.
	 *
	 * @param condition The condition to be added.
	 * @return The original {@link CustomTooltip} object. Used for
	 * chaining.
	 */
	CustomTooltip addCondition(TooltipCondition condition);

}
