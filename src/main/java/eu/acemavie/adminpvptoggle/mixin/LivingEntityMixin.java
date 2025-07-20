package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(
            method = "damage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        Entity projectile = source.getSource();

        PlayerEntity shooter = null;
        if (attacker instanceof PlayerEntity) {
            shooter = (PlayerEntity) attacker;
        } else if (projectile instanceof PersistentProjectileEntity) {
            Entity owner = ((PersistentProjectileEntity) projectile).getOwner();
            if (owner instanceof PlayerEntity) shooter = (PlayerEntity) owner;
        }

        if (shooter != null && ((Object) this) instanceof PlayerEntity) {
            String name = shooter.getName().getString();
            if (Adminpvptoggle.stateManager != null && Adminpvptoggle.stateManager.isPvPDisabled(name)) {
                shooter.sendMessage(Text.literal("§cYour PvP is currently disabled!"), true);
                cir.setReturnValue(false); // Cancel damage
            }
        }
    }
}
