package eu.acemavie.adminpvptoggle.mixin;

import eu.acemavie.adminpvptoggle.Adminpvptoggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityPushMixin extends Entity {

    public LivingEntityPushMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if ((Object) this instanceof PlayerEntity self && entity instanceof PlayerEntity other) {
            if (Adminpvptoggle.pvpStateManager.isInList(self.getName().getString()) ||
                    Adminpvptoggle.pvpStateManager.isInList(other.getName().getString())) {

                return;
            }
        }

        super.pushAwayFrom(entity);
    }
}
