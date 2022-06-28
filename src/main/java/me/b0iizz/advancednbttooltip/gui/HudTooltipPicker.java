package me.b0iizz.advancednbttooltip.gui;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("ClassCanBeRecord")
public final class HudTooltipPicker {

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
	 * Sets up the HUD item picking
	 */
	public static void setup() {
		registerHandler(ItemEntity.class, e -> ConfigManager.getDroppedItemToggle(), (item, pos) -> item.getStack());
		registerHandler(ItemFrameEntity.class, e -> ConfigManager.getItemFrameToggle(),
				(frame, pos) -> frame.getHeldItemStack());
		registerHandler(ArmorStandEntity.class, e -> ConfigManager.getArmorStandToggle(), new ArmorStandHandler());
	}

	protected final MinecraftClient client;

	public HudTooltipPicker(MinecraftClient client) {
		this.client = client;
	}

	public Optional<ItemStack> getItem(float tickDelta) {
		return Optional.ofNullable(raycast(tickDelta))
				.map(hitResult -> getItemFromEntity(hitResult.getEntity(), hitResult.getPos()));
	}

	private EntityHitResult raycast(float tickDelta) {
		Entity cameraEntity = this.client.getCameraEntity();
		if (cameraEntity == null)
			return null;
		Vec3d raycastStart = cameraEntity.getCameraPosVec(tickDelta);
		double maxReach = this.client.interactionManager.getReachDistance();
		double reach = maxReach;
		if (this.client.interactionManager.hasExtendedReach()) {
			maxReach = 6.0D;
			reach = maxReach;
		}

		if (this.client.crosshairTarget != null && this.client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			reach = this.client.crosshairTarget.getPos().distanceTo(raycastStart);
		}

		double reachSq = reach * reach;

		Vec3d cameraDirection = cameraEntity.getRotationVec(1.0F);
		Vec3d raycastEnd = raycastStart.add(cameraDirection.x * reach, cameraDirection.y * reach,
				cameraDirection.z * reach);
		Box raycastCollider = cameraEntity.getBoundingBox().stretch(cameraDirection.multiply(reach)).expand(1.0D, 1.0D,
				1.0D);
		return ProjectileUtil.raycast(cameraEntity, raycastStart, raycastEnd,
				raycastCollider, (entityx) -> entityHandlers.stream()
						.anyMatch(handler -> handler.getLeft().test(entityx)), reachSq);
	}

	private ItemStack getItemFromEntity(Entity e, Vec3d hitPos) {
		return entityHandlers.stream().filter(handler -> handler.getLeft().test(e))
				.map(handler -> handler.getRight().apply(e, hitPos))
				.filter(stack -> stack != null && stack != ItemStack.EMPTY).findFirst().orElse(ItemStack.EMPTY);
	}

	private static final class ArmorStandHandler implements BiFunction<ArmorStandEntity, Vec3d, ItemStack> {

		public ItemStack apply(ArmorStandEntity e, Vec3d hitPos) {
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

	}

}
