package ch.skyfy.entitycrammingdropxp.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntityMixin{
    @Shadow protected int experiencePoints;
    @Override
    public void apply(DamageSource damageSource, CallbackInfo info) {
        if (damageSource.equals(DamageSource.CRAMMING)) {
            var mobEntity = (MobEntity) (Object) this;
            ExperienceOrbEntity.spawn((ServerWorld) mobEntity.world, mobEntity.getPos(), experiencePoints);
        }
    }
}
