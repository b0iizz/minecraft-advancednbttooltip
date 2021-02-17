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
package me.b0iizz.advancednbttooltip.tooltip.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import me.b0iizz.advancednbttooltip.config.ModConfig.HudTooltipPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * A class for rendering tooltips on the in-game HUD
 * 
 * @author B0IIZZ
 */
public class HudTooltipRenderer {

	private static final List<Pair<Predicate<Entity>, BiFunction<Entity, Vec3d, ItemStack>>> entityHandlers = new ArrayList<>();

	/**
	 * @param <T>           The Handled Entity
	 * @param entityClass   The class of the handled entity
	 * @param stackSupplier A function which supplies an ItemStack using the
	 *                      targeted Entity and the hitPosition
	 */
	public static <T extends Entity> void registerHandler(Class<T> entityClass,
			BiFunction<T, Vec3d, ItemStack> stackSupplier) {
		registerHandler(entityClass, e -> true, stackSupplier);
	}

	/**
	 * @param <T>           The Handled Entity
	 * @param entityClass   The class of the handled entity
	 * @param predicate     A predicate to decide whether the handler should be
	 *                      active
	 * @param stackSupplier A function which supplies an ItemStack using the
	 *                      targeted Entity and the hitPosition
	 */
	public static <T extends Entity> void registerHandler(Class<T> entityClass, Predicate<Entity> predicate,
			BiFunction<T, Vec3d, ItemStack> stackSupplier) {
		entityHandlers.add(new Pair<>(predicate.and(e -> entityClass.isAssignableFrom(e.getClass())), (e, hitPos) -> {
			try {
				return stackSupplier.apply(entityClass.cast(e), hitPos);
			} catch (Throwable t) {
				return ItemStack.EMPTY;
			}
		}));
	}

	/**
	 * Registers the default entity handlers
	 */
	public static void registerDefaultHandlers() {
		registerHandler(ItemEntity.class, e -> ConfigManager.getDroppedItemToggle(), (item, pos) -> item.getStack());
		registerHandler(ItemFrameEntity.class, e -> ConfigManager.getItemFrameToggle(),
				(frame, pos) -> frame.getHeldItemStack());
		registerHandler(ArmorStandEntity.class, e -> ConfigManager.getArmorStandToggle(),
				HudTooltipRenderer::handleArmorStand);
	}

	private MinecraftClient client;

	/**
	 * @param client The client this renderer should draw with
	 */
	public HudTooltipRenderer(MinecraftClient client) {
		this.client = client;
	}

	/**
	 * @param stack     the current MatrixStack
	 * @param tickDelta the number of unprocessed ticks
	 */
	public void draw(MatrixStack stack, float tickDelta) {
		EntityHitResult raycast = customRaycast(tickDelta);
		if (raycast != null) {
			drawItemInfo(stack, getItemFromEntity(raycast.getEntity(), raycast.getPos()));
		}

	}

	private void drawItemInfo(MatrixStack matrices, ItemStack stack) {
		if (stack != null && stack != ItemStack.EMPTY) {
			List<Text> tooltip = stack.getTooltip(this.client.player,
					HudTooltipContext.valueOf(this.client.options.advancedItemTooltips));

			int lineLimit = ConfigManager.getHudTooltipLineLimt();
			if (tooltip.size() > lineLimit && lineLimit > 0) {
				tooltip = tooltip.stream().limit(lineLimit).collect(Collectors.toCollection(ArrayList::new));
				tooltip.add(new LiteralText("..."));
			}

			List<OrderedText> lines = Lists.transform(tooltip, Text::asOrderedText);

			int width = this.client.getWindow().getScaledWidth();
			int height = this.client.getWindow().getScaledHeight();

			int expectedWidth = TooltipRenderingUtils.getWidth(this.client.textRenderer, lines);
			int expectedHeight = TooltipRenderingUtils.getHeight(this.client.textRenderer, lines);

			HudTooltipPosition position = ConfigManager.getHudTooltipPosition();

			int x = position.getX().get(expectedWidth, width, 10);
			int y = position.getY().get(expectedHeight, height, 10);

			TooltipRenderingUtils.drawItem(stack, this.client.getItemRenderer(), this.client.textRenderer, matrices,
					lines, x, y, expectedWidth, expectedHeight, ConfigManager.getHudTooltipZIndex().getZ(),
					ConfigManager.getHudTooltipColor());
		}
	}

	private ItemStack getItemFromEntity(Entity e, Vec3d hitPos) {
		return entityHandlers.stream().filter(handler -> handler.getLeft().test(e))
				.map(handler -> handler.getRight().apply(e, hitPos))
				.filter(stack -> stack != null && stack != ItemStack.EMPTY).findFirst().orElse(ItemStack.EMPTY);
	}

	private EntityHitResult customRaycast(float tickDelta) {
		Entity cameraEntity = this.client.getCameraEntity();
		Vec3d raycastStart = cameraEntity.getCameraPosVec(tickDelta);
		double maxReach = (double) this.client.interactionManager.getReachDistance();
		double reach = maxReach;
		if (this.client.interactionManager.hasExtendedReach()) {
			maxReach = 6.0D;
			reach = maxReach;
		}

		if (this.client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			reach = this.client.crosshairTarget.getPos().distanceTo(raycastStart);
		}

		double reachSq = reach * reach;

		Vec3d cameraDirection = cameraEntity.getRotationVec(1.0F);
		Vec3d raycastEnd = raycastStart.add(cameraDirection.x * reach, cameraDirection.y * reach,
				cameraDirection.z * reach);
		Box raycastCollider = cameraEntity.getBoundingBox().stretch(cameraDirection.multiply(reach)).expand(1.0D, 1.0D,
				1.0D);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(cameraEntity, raycastStart, raycastEnd,
				raycastCollider, (entityx) -> {
					return entityHandlers.stream().anyMatch(handler -> handler.getLeft().test(entityx));
				}, reachSq);
		return entityHitResult;
	}

	private static ItemStack handleArmorStand(ArmorStandEntity e, Vec3d hitPos) {
		ItemStack result = e.getMainHandStack();
		result = result != null ? result : ItemStack.EMPTY;

		// Check for main-hand stack
		if (result != ItemStack.EMPTY)
			return result;

		// select by armor item by height
		float size = 1.975f;
		if (e.isBaby())
			size *= 0.5f;
		float percentHeight = (float) hitPos.subtract(e.getPos()).getY() / size;
		if (percentHeight <= 1.0f) {
			ItemStack stack = e.getEquippedStack(EquipmentSlot.HEAD);
			result = stack != null && stack != ItemStack.EMPTY ? stack : result;
		}
		if (percentHeight <= 0.8f) {
			ItemStack stack = e.getEquippedStack(EquipmentSlot.CHEST);
			result = stack != null && stack != ItemStack.EMPTY ? stack : result;
		}
		if (percentHeight <= 0.4f) {
			ItemStack stack = e.getEquippedStack(EquipmentSlot.LEGS);
			result = stack != null && stack != ItemStack.EMPTY ? stack : result;
		}
		if (percentHeight <= 0.2f) {
			ItemStack stack = e.getEquippedStack(EquipmentSlot.FEET);
			result = stack != null && stack != ItemStack.EMPTY ? stack : result;
		}
		return result;
	}

	@SuppressWarnings("javadoc")
	public static enum HudTooltipContext implements TooltipContext {
		NORMAL, ADVANCED;

		@Override
		public boolean isAdvanced() {
			return this.ordinal() >= ADVANCED.ordinal();
		}

		public static HudTooltipContext valueOf(boolean isAdvanced) {
			return isAdvanced ? ADVANCED : NORMAL;
		}

	}

}
