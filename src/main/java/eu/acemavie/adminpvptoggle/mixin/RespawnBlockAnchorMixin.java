package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.block.RespawnAnchorBlock.CHARGES;
import static net.minecraft.block.RespawnAnchorBlock.isNether;

@Mixin(RespawnAnchorBlock.class)
public class RespawnBlockAnchorMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void preventPvPAnchorUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!isNether(world)) {
            if (!world.isClient) {
                if (Adminpvptoggle.stateManager.isPvPDisabled(player.getName().getString())) {

                    List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, player.getBoundingBox().expand(6),
                            other -> !other.getUuid().equals(player.getUuid()) && !other.isSpectator());

                    if (!nearbyPlayers.isEmpty()) {
                        player.sendMessage(Text.literal("§cYou can't harass other players with respawn anchors while your PvP is disabled!"), true);
                        cir.setReturnValue(ActionResult.FAIL);
                        cir.cancel();
                    }
                }

            }
        }
    }

}
