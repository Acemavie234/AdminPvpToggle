package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity {

    @Shadow
    private Entity hookedEntity;

    public FishingBobberEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preventHookingPlayers(CallbackInfo ci) {
        Entity owner = ((FishingBobberEntity)(Object)this).getOwner();
        if(owner instanceof ServerPlayerEntity player) {
            if(Adminpvptoggle.pvpStateManager.isInList(player.getName().getString())) {
                if (hookedEntity instanceof PlayerEntity victim) {
                    if (!Adminpvptoggle.lindpriiStateManager.isInList(victim.getName().getString())) {
                        hookedEntity = null;
                        player.sendMessage(Text.literal("§cYour PvP's off — you can't go angling for spinal cords just yet."), true);
                    }
                }
            }
        }
    }
}
