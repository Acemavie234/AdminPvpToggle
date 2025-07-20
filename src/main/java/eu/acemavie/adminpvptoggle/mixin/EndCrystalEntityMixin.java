package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalEntity.class)
public class EndCrystalEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity attacker) {
            if (Adminpvptoggle.stateManager == null) return;

            String name = attacker.getName().getString();
            if (Adminpvptoggle.stateManager.isPvPDisabled(name)) {
                Vec3d crystalPos = ((EndCrystalEntity)(Object)this).getPos();
                Box radius = new Box(crystalPos, crystalPos).expand(6.0);
                boolean playerNearby = !world.getEntitiesByClass(PlayerEntity.class, radius,
                        other -> !other.getUuid().equals(attacker.getUuid()) && !other.isSpectator()).isEmpty();

                if (playerNearby) {
                    attacker.sendMessage(Text.literal("§cYou can't harass other players with end crystals while your PvP is disabled!"), true);
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
