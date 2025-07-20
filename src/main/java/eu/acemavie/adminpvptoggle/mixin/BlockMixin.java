package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "onPlaced", at = @At("HEAD"), cancellable = true)
    private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        Block block = state.getBlock();
        if (!(world.getBlockState(pos.down()).isAir())) return;
        if(placer == null) return;
        if (world.isClient) return;
        if(placer instanceof PlayerEntity player){
            if(block == Blocks.ANVIL || block == Blocks.CHIPPED_ANVIL || block == Blocks.DAMAGED_ANVIL) {
                BlockPos base = pos.down();
                Box box = new Box(
                        base.getX() - 1, base.getY(),     base.getZ() - 1,
                        base.getX() + 2, base.getY() - 384, base.getZ() + 2
                );
                boolean playerUnderneath = !world.getEntitiesByClass(PlayerEntity.class, box, other -> !other.getUuid().equals(player.getUuid()) && !other.isSpectator()).isEmpty();
                if (playerUnderneath) {
                    if (Adminpvptoggle.stateManager.isPvPDisabled(player.getName().getString())) {
                        player.sendMessage(Text.of("§cYou cannot smash other people's skulls with anvils while your PvP is disabled!"), true);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        ci.cancel();
                    }
                }
            }else if(block == Blocks.SAND || block == Blocks.GRAVEL || state.isIn(BlockTags.CONCRETE_POWDER)) {
                BlockPos base = pos.down();
                Box box = new Box(
                        base.getX() - 1, base.getY(),     base.getZ() - 1,
                        base.getX() + 2, base.getY() - 384, base.getZ() + 2
                );

                boolean playerUnderneath = !world.getEntitiesByClass(PlayerEntity.class, box, other -> !other.getUuid().equals(player.getUuid()) && !other.isSpectator()).isEmpty();
                if (playerUnderneath) {
                    if (Adminpvptoggle.stateManager.isPvPDisabled(player.getName().getString())) {
                        player.sendMessage(Text.of("§cBurying people alive is against the geneva convention while your PvP is disabled!"), true);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        ci.cancel();
                    }
                }
            }


        }
    }
}
