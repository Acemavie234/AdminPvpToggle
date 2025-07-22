package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onBedInteract(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.getDimension().bedWorks()) return;
        if (Adminpvptoggle.pvpStateManager.isInList(player.getName().getString())) {

            List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, player.getBoundingBox().expand(6),
                    other -> !other.getUuid().equals(player.getUuid()) && !other.isSpectator()  && !other.isSpectator() && !Adminpvptoggle.lindpriiStateManager.isInList(other.getName().getString()));

            if (!nearbyPlayers.isEmpty()) {
                player.sendMessage(Text.literal("§cIf you are trying to mine netherite, tell other players to move to a safe distance. \n If you are a jihadi suicide bomber, fuck off, please! \n If you do not match any of the cases above, you are lying. \n In the rare case of still not matching any of the cases above, fuck off anyway!"), false);
                cir.setReturnValue(ActionResult.FAIL);
                cir.cancel();
            }
        }
    }
}