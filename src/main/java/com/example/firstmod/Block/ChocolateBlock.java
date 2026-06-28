package com.example.firstmod.Block;

import com.example.firstmod.Item.ModItem;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class ChocolateBlock extends Block {
    public ChocolateBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .strength(2.0F, 6.0F)
                .sound(SoundType.STONE));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                               @Nullable BlockEntity blockEntity, ItemStack tool) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);

        if (isStoneTierPickaxeOrBetter(tool)) {
            dropResources(state, level, pos, blockEntity, player, tool);
        } else if (!level.isClientSide) {
            int count = 1 + level.random.nextInt(3);
            popResource(level, pos, new ItemStack(ModItem.CHOCOLATE.get(), count));
        }
    }

    private static boolean isStoneTierPickaxeOrBetter(ItemStack tool) {
        return tool.getItem() instanceof PickaxeItem pickaxe
                && pickaxe.getTier().getLevel() >= Tiers.STONE.getLevel();
    }
}
