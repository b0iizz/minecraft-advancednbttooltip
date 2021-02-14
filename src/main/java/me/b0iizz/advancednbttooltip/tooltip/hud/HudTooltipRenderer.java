package me.b0iizz.advancednbttooltip.tooltip.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private static final Map<Class<? extends Entity>, EntityHandler> entityHandlers = new HashMap<>();

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
		entityHandlers.putIfAbsent(entityClass,
				new EntityHandler(predicate.and(e -> entityClass.isAssignableFrom(e.getClass())), (e, hitPos) -> {
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
			
			List<Text> tooltip = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
			
			int lineLimit = ConfigManager.getHudTooltipLineLimt();
			if(tooltip.size() > lineLimit && lineLimit > 0) {
				tooltip = tooltip.stream().limit(lineLimit).collect(Collectors.toCollection(ArrayList::new));
				tooltip.add(new LiteralText("..."));
			}
			
			List<OrderedText> lines = Lists.transform(tooltip, Text::asOrderedText);
			
			int width = this.client.getWindow().getScaledWidth();
			int height = this.client.getWindow().getScaledHeight();
			
			int expectedWidth = TooltipRenderingUtils.getWidth(this.client.textRenderer, lines) + 20;
			int expectedHeight = TooltipRenderingUtils.getHeight(this.client.textRenderer, lines);
			
			HudTooltipPosition position = ConfigManager.getHudTooltipPosition();
			
			int x = position.getX().get(expectedWidth, width, 10);
			int y = position.getY().get(expectedHeight, height, 10);
			
			this.client.getItemRenderer().renderGuiItemIcon(stack, x - 8, y - 3);
			TooltipRenderingUtils.drawTooltip(this.client.textRenderer, matrices, Lists.transform(tooltip, Text::asOrderedText), x + 14, y, width, height, ConfigManager.getHudTooltipColor());
		}
	}

	private ItemStack getItemFromEntity(Entity e, Vec3d hitPos) {
		return entityHandlers.values().stream().filter(handler -> handler.predicate.test(e))
				.map(handler -> handler.stackSupplier.apply(e, hitPos))
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
					return entityHandlers.values().stream().anyMatch(handler -> handler.predicate.test(entityx));
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

	private static class EntityHandler {
		public final Predicate<Entity> predicate;
		public final BiFunction<Entity, Vec3d, ItemStack> stackSupplier;

		public EntityHandler(Predicate<Entity> predicate, BiFunction<Entity, Vec3d, ItemStack> stackSupplier) {
			this.predicate = predicate;
			this.stackSupplier = stackSupplier;
		}

	}

}
