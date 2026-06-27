package com.example.firstmod.Item;

import com.example.firstmod.ExampleMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    public static final RegistryObject<Item> CHOCOLATE = ITEMS.register("chocolate", ()->new Item(new Item.Properties()));
    public static void register(IEventBus eventBus){ ITEMS.register(eventBus); }
}
