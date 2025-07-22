package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Inject(method = "placeFluid", at = @At("HEAD"), cancellable = true)
    private void onPlaceFluid(LivingEntity user, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player;

        if(user instanceof PlayerEntity){
            player = (PlayerEntity) user;
            ItemStack bucketStack = player.getMainHandStack();
            ItemStack offHandStack = player.getOffHandStack();
            if (bucketStack.getItem() == Items.LAVA_BUCKET || offHandStack.getItem() == Items.LAVA_BUCKET) {
                String name = player.getName().getString();
                if (Adminpvptoggle.pvpStateManager != null && Adminpvptoggle.pvpStateManager.isInList(name)) {
                    Box area = new Box(
                            pos.getX() - 5,-64 , pos.getZ() - 5,
                            pos.getX() + 5, 320, pos.getZ() + 5
                    );

                    List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, area,
                            other -> !other.getUuid().equals(player.getUuid()) && !other.isSpectator() && !Adminpvptoggle.lindpriiStateManager.isInList(other.getName().getString()));

                    if (!nearbyPlayers.isEmpty()) {
                        player.sendMessage(Text.literal("§cPlease follow the geneva convention while your PvP is disabled!"), true);
                        cir.setReturnValue(false);
                        cir.cancel();
                    }
                }
            }
        }
    }
}
