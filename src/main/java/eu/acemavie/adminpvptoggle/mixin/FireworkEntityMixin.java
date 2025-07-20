package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FireworkRocketEntity.class)
public class FireworkEntityMixin {
    @Shadow @Nullable private LivingEntity shooter;

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void cancelExplosion(CallbackInfo ci) {
        Entity shooter = ((ProjectileEntity)(Object)this).getOwner();
        if (shooter != null) {
            if (shooter instanceof PlayerEntity player && Adminpvptoggle.stateManager.isPvPDisabled(player.getName().getString())) {
                Random rd = new Random();
                if(rd.nextBoolean()) player.sendMessage(Text.literal("§cYou can't launch pyrotechnic artillery death at others while your PvP is disabled!"), true);
                else player.sendMessage(Text.literal("§cYou don’t get to cremate players with sky-explosives while wrapped in your no-PvP diaper."), true);
                ci.cancel();
            }
        }
    }
}