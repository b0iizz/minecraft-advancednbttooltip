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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.util.NBTPath;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * An enumeration of built-in {@link TooltipFactory TooltipFactories}
 * 
 * @author B0IIZZ
 */
public enum BuiltInFactory {
	/**
	 * A factory which creates a simple {@link LiteralText} <br>
	 * <b>Parameters: </b><br>
	 * {@link String} text - The text to be displayed<br>
	 */
	LITERAL(LiteralFactory::new),
	/**
	 * A factory which can create {@link Formatting} on other factories <br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipFactory} factory - The text to be displayed<br>
	 * {@link Formatting Formatting[]} formatting - A formatting<br>
	 */
	FORMATTED(FormattedFactory::new),
	/**
	 * A factory which creates a simple {@link TranslatableText} <br>
	 * <b>Parameters: </b><br>
	 * {@link String} translationKey - The translation key used<br>
	 * {@link TooltipFactory} argument_provider - <i>(optional)</i> A factory which
	 * creates arguments for the translated text as lines<br>
	 */
	TRANSLATED(TranslatedFactory::new),
	/**
	 * A factory which creates a simple {@link LiteralText} containing the value of
	 * a specified {@link NBTPath} <br>
	 * <b>Parameters: </b><br>
	 * {@link NBTPath} path - The path to the value in an {@link Item} tag<br>
	 */
	NBT(NBTFactory::new),
	/**
	 * A factory which creates a simple {@link LiteralText} containing the value of
	 * a specified {@link NBTPath} <br>
	 * <b>Parameters: </b><br>
	 * {@link NBTPath} path - The path to the value in an {@link Item} tag<br>
	 */
	NBT_REROUTE(NBTRerouteFactory::new),
	/**
	 * A factory which creates a simple {@link LiteralText} containing the size of a
	 * specified {@link NBTPath NBT-Element} <br>
	 * <b>Parameters: </b><br>
	 * {@link NBTPath} path - The path to the value in an {@link Item} tag<br>
	 */
	NBT_SIZE(NBTSizeFactory::new),
	/**
	 * A factory which creates two different tooltips depending on a
	 * {@link TooltipCondition condition} <br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipFactory} successFactory - The {@link TooltipFactory} which will
	 * create the Tooltip when the condition is met<br>
	 * {@link TooltipFactory} failFactory - The {@link TooltipFactory} which will
	 * create the Tooltip when the condition is not met<br>
	 * {@link TooltipCondition} condition - The condition which decides which
	 * factory produces the tooltip.<br>
	 */
	CONDITIONAL(ConditionalFactory::new),
	/**
	 * Combines multiple {@link TooltipFactory TooltipFactories} under each other
	 * together. <br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipFactory TooltipFactory[]} multiple - An array of factories to
	 * be appended under on another<br>
	 */
	MULTIPLE(MultipleFactory::new),
	/**
	 * Combines multiple {@link TooltipFactory TooltipFactories} next to each other
	 * together. <br>
	 * <b>Parameters: </b><br>
	 * {@link Boolean boolean} separate - If mix should combine lines individually
	 * or put everything on one line <br>
	 * {@link TooltipFactory TooltipFactory[]} mix - An array of factories to be
	 * appended next to on another<br>
	 */
	MIX(MixFactory::new),
	/**
	 * Returns a line with minecraft effect formatting According to id, duration and
	 * strength.<br>
	 * <b>Parameters: </b><br>
	 * {@link Byte byte} rawId - The raw potion id<br>
	 * {@link Integer int} duration - The duration in ticks<br>
	 * {@link Integer int} strength - The strength<br>
	 * <br>
	 */
	EFFECT(EffectFactory::new),
	/**
	 * Returns the result of the given factory, but all characters after a limit are
	 * removed.<br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipFactory TooltipFactory} text - The Factory which will be
	 * limited in length<br>
	 * {@link Integer int} length - The length in characters<br>
	 * <br>
	 */
	LIMIT(LimitFactory::new),
	/**
	 * Returns the result of the given factory, but all lines after a limit are
	 * removed.<br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipFactory TooltipFactory} text - The Factory which will be
	 * limited in length<br>
	 * {@link Integer int} length - The length in lines<br>
	 * <br>
	 */
	LIMIT_LINES(LimitLinesFactory::new),
	/**
	 * The built-in signs tooltip
	 */
	BUILTIN_SIGNS(BuiltInSignsFactory::new),
	/**
	 * The built-in hideflags tooltip
	 */
	BUILTIN_HIDEFLAGS(BuiltInHideFlagsFactory::new);

	/**
	 * Creates a pre-defined {@link TooltipFactory Factory}.
	 * 
	 * @param factoryName : See {@link BuiltInFactory}
	 * @param args        : The appropriate arguments for the specific factory.
	 * @return A new {@link TooltipFactory}
	 */
	public static final TooltipFactory forName(String factoryName, Object... args) {
		return valueOf(factoryName).create(args);
	}

	private final Function<Object[], TooltipFactory> constructor;

	private BuiltInFactory(Function<Object[], TooltipFactory> constructor) {
		this.constructor = constructor;
	}

	/**
	 * Creates a new instance of the built-in {@link TooltipFactory}
	 * 
	 * @param args The arguments required for the {@link TooltipFactory}
	 * @return the new {@link TooltipFactory}
	 */
	public TooltipFactory create(Object... args) {
		return constructor.apply(args);
	}

	/**
	 * See {@link BuiltInFactory#LITERAL}
	 * 
	 * @author B0IIZZ
	 */
	protected static class LiteralFactory implements TooltipFactory {

		final String text;

		/**
		 * @param text The text to be displayed
		 */
		public LiteralFactory(String text) {
			this.text = text;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public LiteralFactory(Object... args) {
			this((String) args[0]);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return Arrays.asList(new LiteralText(text));
		}

	}

	/**
	 * See {@link BuiltInFactory#FORMATTED}
	 * 
	 * @author B0IIZZ
	 */
	protected static class FormattedFactory implements TooltipFactory {

		final TooltipFactory factory;
		final Formatting[] formatting;

		/**
		 * @param factory    The {@link TooltipFactory} to be formatted
		 * @param formatting The used {@link Formatting formatting}
		 */
		public FormattedFactory(TooltipFactory factory, Formatting[] formatting) {
			this.factory = factory;
			this.formatting = formatting;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public FormattedFactory(Object... args) {
			this((TooltipFactory) args[0], (Formatting[]) args[1]);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return factory.createTooltip(item, tag, context).stream()
					.<Text>map((text) -> text.shallowCopy().formatted(formatting)).collect(Collectors.toList());
		}

	}

	/**
	 * See {@link BuiltInFactory#TRANSLATED}
	 * 
	 * @author B0IIZZ
	 */
	protected static class TranslatedFactory implements TooltipFactory {

		final String translationKey;
		final TooltipFactory argument_provider;

		/**
		 * @param translationKey The translation key to be translated
		 */
		public TranslatedFactory(String translationKey) {
			this.translationKey = translationKey;
			this.argument_provider = TooltipFactory.EMPTY;
		}

		/**
		 * @param translationKey    The translation key to be translated
		 * @param argument_provider A {@link TooltipFactory} to provide the arguments
		 *                          inside of the translation
		 */
		public TranslatedFactory(String translationKey, TooltipFactory argument_provider) {
			this.translationKey = translationKey;
			this.argument_provider = argument_provider;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public TranslatedFactory(Object... args) {
			this((String) args[0], args.length > 1 ? (TooltipFactory) args[1] : TooltipFactory.EMPTY);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			List<Text> args = argument_provider.createTooltip(item, tag, context);
			return Arrays.asList(new TranslatableText(translationKey, args.toArray()));
		}

	}

	/**
	 * See {@link BuiltInFactory#NBT}
	 * 
	 * @author B0IIZZ
	 */
	protected static class NBTFactory implements TooltipFactory {

		final NBTPath path;
		final boolean traverseCompound;
		final boolean traverseList;
		final boolean colored;

		/**
		 * @param path    The path to the NBT-value to be copied
		 * @param flags   The NBT tree traversal flags {@code 0x1 = Traverse {@link
		 *                CompoundTag}} {@code 0x2 = Traverse {@link AbstractListTag}}
		 * @param colored Whether the tooltip should be colored
		 */
		public NBTFactory(NBTPath path, int flags, boolean colored) {
			this.path = path;
			this.traverseCompound = (flags & 0x1) != 0;
			this.traverseList = (flags & 0x2) != 0;
			this.colored = colored;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public NBTFactory(Object... args) {
			this((NBTPath) args[0], args.length > 1 ? (int) args[1] : 0, args.length > 2 ? (boolean) args[2] : false);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return path.getAll(tag).stream().map(this::fromTag).flatMap(List::stream).collect(Collectors.toList());
		}

		private List<Text> fromTag(Tag tag) {
			if (tag instanceof CompoundTag) {
				if (!traverseCompound)
					return Arrays
							.asList(new LiteralText("{...}").formatted(colored ? Formatting.YELLOW : Formatting.RESET));
				return ((CompoundTag) tag).getKeys().stream().flatMap(key -> Stream.concat(
						Stream.of(new LiteralText(key + ": ").formatted(colored ? Formatting.GRAY : Formatting.RESET)),
						fromTag(((CompoundTag) tag).get(key)).stream().map(this::indent))).collect(Collectors.toList());
			} else if (tag instanceof AbstractListTag) {
				if (!traverseList)
					return Arrays
							.asList(new LiteralText("[...]").formatted(colored ? Formatting.YELLOW : Formatting.RESET));
				return ((AbstractListTag<Tag>) tag).stream()
						.flatMap(e -> Stream.concat(fromTag(e).stream(), Stream.of(new LiteralText(""))))
						.map(this::indent).collect(Collectors.toList());
			} else
				return Arrays.asList(
						new LiteralText(tag.asString()).formatted(colored ? Formatting.YELLOW : Formatting.RESET));
		}

		private MutableText indent(Text text) {
			return new LiteralText(" ").append(text);
		}

	}

	/**
	 * See {@link BuiltInFactory#NBT_REROUTE}
	 * 
	 * @author B0IIZZ
	 */
	protected static class NBTRerouteFactory implements TooltipFactory {

		final NBTPath path;
		final TooltipFactory child;

		/**
		 * @param path    The path to the NBT-value to be defined as new root
		 * @param child   The {@link TooltipFactory} to have the root tag redefined.
		 */
		public NBTRerouteFactory(NBTPath path, TooltipFactory child) {
			this.path = path;
			this.child = child;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public NBTRerouteFactory(Object... args) {
			this((NBTPath) args[0], (TooltipFactory) args[1]);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return path.getAll(tag).stream().filter(t -> t.getType() == NbtType.COMPOUND).map(t -> (CompoundTag) t)
					.flatMap(t -> this.child.createTooltip(item, t, context).stream()).collect(Collectors.toList());
		}

	}

	/**
	 * See {@link BuiltInFactory#NBT}
	 * 
	 * @author B0IIZZ
	 */
	protected static class NBTSizeFactory implements TooltipFactory {

		final NBTPath path;

		/**
		 * @param path The path to the NBT-value
		 */
		public NBTSizeFactory(NBTPath path) {
			this.path = path;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public NBTSizeFactory(Object... args) {
			this((NBTPath) args[0]);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return Arrays.asList(new LiteralText(fromTag(path.getOptional(tag).orElse(StringTag.of("-")))));
		}

		private String fromTag(Tag tag) {
			if (tag instanceof CompoundTag) {
				return ((CompoundTag) tag).getSize() + "";
			} else if (tag instanceof AbstractListTag) {
				return ((AbstractListTag<?>) tag).size() + "";
			} else
				return "1";
		}

	}

	/**
	 * See {@link BuiltInFactory#CONDITIONAL}
	 * 
	 * @author B0IIZZ
	 */
	protected static class ConditionalFactory implements TooltipFactory {

		final TooltipFactory successFactory;
		final TooltipFactory failFactory;

		final TooltipCondition condition;

		/**
		 * @param successFactory The {@link TooltipFactory} which will create the
		 *                       Tooltip when the condition is met
		 * @param failFactory    The {@link TooltipFactory} which will create the
		 *                       Tooltip when the condition is not met
		 * @param condition      A {@link TooltipCondition}
		 */
		public ConditionalFactory(TooltipFactory successFactory, TooltipFactory failFactory,
				TooltipCondition condition) {
			this.successFactory = successFactory;
			this.failFactory = failFactory;
			this.condition = condition;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public ConditionalFactory(Object... args) {
			this((TooltipFactory) args[0], (TooltipFactory) args[1], (TooltipCondition) args[2]);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return (condition.isConditionMet(item, tag, context) ? successFactory : failFactory).createTooltip(item,
					tag, context);
		}

	}

	/**
	 * See {@link BuiltInFactory#MULTIPLE}
	 * 
	 * @author B0IIZZ
	 */
	protected static class MultipleFactory implements TooltipFactory {

		final TooltipFactory[] multiple;

		/**
		 * @param multiple An array of {@link TooltipFactory TooltipFactories} which
		 *                 will be appended under one another.
		 */
		public MultipleFactory(TooltipFactory[] multiple) {
			this.multiple = multiple;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public MultipleFactory(Object... args) {
			this(Arrays.copyOf(args, args.length, TooltipFactory[].class));
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			ArrayList<Text> res = new ArrayList<>();
			for (TooltipFactory factory : multiple) {
				res.addAll(factory.createTooltip(item, tag, context));
			}
			return res;
		}

	}

	/**
	 * See {@link BuiltInFactory#MIX}
	 * 
	 * @author B0IIZZ
	 */
	protected static class MixFactory implements TooltipFactory {

		final boolean separate;
		final TooltipFactory[] mix;

		/**
		 * @param separate If mix should combine lines individually or put everything on
		 *                 one line
		 * @param mix      An array of {@link TooltipFactory TooltipFactories} which
		 *                 will be appended next to one another.
		 */
		public MixFactory(boolean separate, TooltipFactory[] mix) {
			this.separate = separate;
			this.mix = mix;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public MixFactory(Object... args) {
			this((boolean) args[0],
					Arrays.copyOf((Object[]) args[1], ((Object[]) args[1]).length, TooltipFactory[].class));
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			List<List<Text>> tooltips = new ArrayList<>();

			for (TooltipFactory factory : mix) {
				tooltips.add(factory.createTooltip(item, tag, context));
			}

			if (separate) {
				int maxL = tooltips.stream().mapToInt(List::size).reduce(Integer::max).orElse(0);

				Text[] res = new Text[maxL];

				for (int i = 0; i < maxL; i++) {
					final int idx = i;
					res[i] = tooltips.stream().flatMap(list -> {
						if (idx < list.size())
							return Stream.of(list.get(idx));
						if (!list.isEmpty())
							return Stream.of(list.get(list.size() - 1));
						return Stream.empty();
					}).map(Text::shallowCopy).reduce(MutableText::append).orElse(new LiteralText(""));
				}

				return Arrays.asList(res);
			} else {
				return tooltips.stream().flatMap(List::stream).map(Text::shallowCopy).reduce(MutableText::append)
						.<List<Text>>map(Arrays::asList).orElseGet(() -> Arrays.asList());
			}
		}

	}

	/**
	 * See {@link BuiltInFactory#EFFECT}
	 * 
	 * @author B0IIZZ
	 */
	protected static class EffectFactory implements TooltipFactory {

		final TooltipFactory rawId;
		final TooltipFactory duration;
		final TooltipFactory strength;

		/**
		 * See {@link BuiltInFactory#EFFECT}
		 * 
		 * @param rawId    The raw potion id
		 * @param duration The duration of the effect
		 * @param strength The strength of the effect
		 * 
		 */
		public EffectFactory(byte rawId, int duration, int strength) {
			this((i, t, c) -> Arrays.asList(new LiteralText(Byte.toString(rawId))),
					(i, t, c) -> Arrays.asList(new LiteralText(Integer.toString(duration))),
					(i, t, c) -> Arrays.asList(new LiteralText(Integer.toString(strength))));
		}

		/**
		 * 
		 * @param rawId    The raw potion id
		 * @param duration The duration of the effect
		 * @param strength The strength of the effect
		 * 
		 */
		public EffectFactory(TooltipFactory rawId, TooltipFactory duration, TooltipFactory strength) {
			this.rawId = rawId;
			this.duration = duration;
			this.strength = strength;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public EffectFactory(Object... args) {
			this((TooltipFactory) args[0], (TooltipFactory) args[1], args.length > 2 ? (TooltipFactory) args[2] : null);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			List<Text> rawIds = rawId.createTooltip(item, tag, context);
			List<Text> durations = duration.createTooltip(item, tag, context);
			List<Text> strengths = strength != null ? strength.createTooltip(item, tag, context)
					: Stream.generate(() -> new LiteralText("0")).limit(rawIds.size()).collect(Collectors.toList());

			int numEffects = Math.max(rawIds.size(),
					Math.max(durations.size(), strength == null ? 0 : strengths.size()));

			List<Text> result = new ArrayList<>();

			for (int i = 0; i < numEffects; i++) {
				byte rawId = 0;
				int duration = 0, strength = 0;
				try {
					rawId = new BigDecimal(rawIds.get(i).asString().trim().replaceAll("[A-Za-z]$", "")).byteValue();
					duration = new BigDecimal(durations.get(i).asString().trim().replaceAll("[A-Za-z]$", ""))
							.intValue();
					strength = new BigDecimal(strengths.get(i).asString().trim().replaceAll("[A-Za-z]$", ""))
							.intValue();
				} catch (Throwable t) {
					t.printStackTrace();
					continue;
				}

				StatusEffect eff = StatusEffect.byRawId(rawId);
				StatusEffectInstance inst = new StatusEffectInstance(eff, duration, strength);

				MutableText line = new TranslatableText(inst.getTranslationKey());

				if (inst.getAmplifier() > 0) {
					line = new TranslatableText("potion.withAmplifier",
							new Object[] { line, new TranslatableText("potion.potency." + inst.getAmplifier()) });
				}
				if (inst.getDuration() > 20) {
					line = new TranslatableText("potion.withDuration",
							new Object[] { line, StatusEffectUtil.durationToString(inst, 1) });
				}

				result.add(line.formatted(eff.getType().getFormatting()));
			}
			return result;
		}

	}

	/**
	 * See {@link BuiltInFactory#LIMIT}
	 * 
	 * @author B0IIZZ
	 */
	protected static class LimitFactory implements TooltipFactory {

		final TooltipFactory factory;
		final int length;

		/**
		 * See {@link BuiltInFactory#LIMIT}
		 * 
		 * @param text   The limited factory
		 * @param length The length limit
		 */
		public LimitFactory(TooltipFactory text, int length) {
			this.factory = text;
			this.length = length;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public LimitFactory(Object... args) {
			this((TooltipFactory) args[0], (int) args[1]);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return factory.createTooltip(item, tag, context).stream()
					.map(text -> new LiteralText(text.asTruncatedString(length)).setStyle(text.getStyle()))
					.collect(Collectors.toList());
		}

	}

	/**
	 * See {@link BuiltInFactory#LIMIT_LINES}
	 * 
	 * @author B0IIZZ
	 */
	protected static class LimitLinesFactory implements TooltipFactory {

		final TooltipFactory factory;
		final int length;

		/**
		 * See {@link BuiltInFactory#LIMIT_LINES}
		 * 
		 * @param text   The limited factory
		 * @param length The length limit
		 */
		public LimitLinesFactory(TooltipFactory text, int length) {
			this.factory = text;
			this.length = length;
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public LimitLinesFactory(Object... args) {
			this((TooltipFactory) args[0], (int) args[1]);
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			return factory.createTooltip(item, tag, context).stream().limit(length).collect(Collectors.toList());
		}

	}

	/**
	 * See {@link BuiltInFactory#BUILTIN_SIGNS}
	 * 
	 * @author B0IIZZ
	 */
	protected static class BuiltInSignsFactory implements TooltipFactory {

		/**
		 * See {@link BuiltInFactory#BUILTIN_SIGNS}
		 */
		public BuiltInSignsFactory() {
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public BuiltInSignsFactory(Object... args) {
			this();
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			final String startText = "----------------";
			final String endText = "----------------";

			List<Text> result = new ArrayList<>();

			CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");

			int preferredWidth = MinecraftClient.getInstance().textRenderer.getWidth(endText);

			result.add(new LiteralText(startText).formatted(Formatting.GRAY));

			for (int i = 1; i < 5; i++) {
				boolean hasText = blockEntityTag.contains("Text" + i);
				String text = hasText ? Text.Serializer.fromJson(blockEntityTag.getString("Text" + i)).asString() : "";
				while (preferredWidth >= MinecraftClient.getInstance().textRenderer.getWidth(' ' + text + ' '))
					text = ' ' + text + ' ';
				result.add(new LiteralText(text).formatted(Formatting.GRAY));
			}

			result.add(new LiteralText(endText).formatted(Formatting.GRAY));

			return result;
		}

	}

	/**
	 * See {@link BuiltInFactory#BUILTIN_HIDEFLAGS}
	 * 
	 * @author B0IIZZ
	 */
	protected static class BuiltInHideFlagsFactory implements TooltipFactory {

		/**
		 * See {@link BuiltInFactory#BUILTIN_HIDEFLAGS}
		 */
		public BuiltInHideFlagsFactory() {
		}

		/**
		 * @param args The arguments required for the {@link TooltipFactory}
		 */
		public BuiltInHideFlagsFactory(Object... args) {
			this();
		}

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			List<Text> result = new ArrayList<>();

			int hideFlags = tag.getInt("HideFlags");

			result.add(new TranslatableText("text." + AdvancedNBTTooltips.modid + ".tooltip.hideflag")
					.formatted(Formatting.GRAY));

			for (int i = 0; i < ItemStack.TooltipSection.values().length; i++) {
				if (((1 << i) & hideFlags) > 0) {
					LiteralText line = new LiteralText(" -");
					line.append(new TranslatableText("text." + AdvancedNBTTooltips.modid + ".hideflag."
							+ ItemStack.TooltipSection.values()[i].name().toLowerCase()));
					result.add(line.formatted(Formatting.GRAY, Formatting.ITALIC));
				}
			}

			return result;
		}

	}

}
