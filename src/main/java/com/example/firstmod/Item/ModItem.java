package com.example.firstmod.Item;

import com.example.firstmod.ExampleMod;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    public static final RegistryObject<Item> CHOCOLATE = ITEMS.register("chocolate", ()->new Item(new Item.Properties()
            .stacksTo(64)
            .food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationMod(1f)
                    .build())));
    public static void register(IEventBus eventBus){ ITEMS.register(eventBus); }

    // Interaction behaviour (animation, taming, etc.) is intentionally left unspecified for now.
    @Mod.EventBusSubscriber(modid = ExampleMod.MODID)
    public static class ChocolateInteractions {
        @SubscribeEvent
        public static void onChocolateInteract(PlayerInteractEvent.EntityInteract event) {
            Entity target = event.getTarget();
            if (!(target instanceof Wolf) && !(target instanceof Cat)) return;

            Player player = event.getEntity();
            ItemStack stack = player.getItemInHand(event.getHand());
            if (!stack.is(CHOCOLATE.get())) return;

            event.setCanceled(true);

            // An untamed cat refuses the chocolate and flees instead of eating it.
            if (target instanceof Cat cat && !cat.isTame()) {
                Vec3 away = cat.position().subtract(player.position());
                if (away.lengthSqr() > 1.0E-4) {
                    away = away.normalize();
                } else {
                    away = new Vec3(1, 0, 0);
                }
                cat.push(away.x * 0.5, 0.1, away.z * 0.5);
                Vec3 fleeTarget = cat.position().add(away.scale(10));
                cat.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.5);
                return;
            }

            if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(CHOCOLATE.get())),
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                    8, 0.3, 0.3, 0.3, 0.05);

            // Chocolate is toxic to wolves, mirroring the parrot/cookie effect - it is always lethal.
            if (target instanceof Wolf wolf) {
                boolean wasTame = wolf.isTame();
                DamageSource damageSource = wasTame ? DamageSource.GENERIC : DamageSource.playerAttack(player);
                wolf.hurt(damageSource, Float.MAX_VALUE);

                // The wolf dies instantly, so its own "alert the pack" AI never gets to run.
                // Replicate vanilla's nearby-wolf-pack aggro manually instead.
                if (!wasTame) {
                    AABB alertArea = wolf.getBoundingBox().inflate(16.0, 10.0, 16.0);
                    for (Wolf nearby : serverLevel.getEntitiesOfClass(Wolf.class, alertArea,
                            w -> w != wolf && !w.isTame() && w.getTarget() == null && !w.isAlliedTo(player))) {
                        nearby.setTarget(player);
                    }
                }
            }
        }
    }
}
