package com.github.timepsilon.ironsspellbooks.spells.losemoney;

import com.github.timepsilon.Core;
import com.github.timepsilon.ironsspellbooks.ModSchoolRegistry;
import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.time.TimerHandler;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class LoseMoney extends AbstractSpell {

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(ModSchoolRegistry.TIME_RESSOURCE)
            .setMaxLevel(4)
            .setCooldownSeconds(600)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.stonkstimecore.lose_money", Utils.stringTruncation(getSpellPower(spellLevel, caster)*60*TimeUtils.TIME_TO_MONEY, 1))
                        .withStyle(ChatFormatting.WHITE)
        );
    }

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Core.MODID, "lose_money");

    public LoseMoney() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 20;
        this.spellPowerPerLevel = 5;
        this.castTime = 20*10;
        this.baseManaCost = 200;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 8, .1f,true, e -> e instanceof Player);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
            if ((targetData.getTarget((ServerLevel) level) instanceof ServerPlayer targetEntity) && (entity instanceof ServerPlayer senderEntity)) {
                BankAccount targetAccount = Numismatics.BANK.getOrCreateAccount(targetEntity.getUUID(), BankAccount.Type.PLAYER);
                BankAccount receiverAccount = Numismatics.BANK.getOrCreateAccount(senderEntity.getUUID(), BankAccount.Type.PLAYER);

                int amount = (int) Math.min(getSpellPower(spellLevel, entity)*60*TimeUtils.TIME_TO_MONEY, targetAccount.getBalance());

                targetAccount.deduct(amount);
                TimerHandler.sendInfoPacket(targetEntity, "-"+amount+"\u9000");

                receiverAccount.deposit(amount);
                TimerHandler.sendInfoPacket(senderEntity, "+"+amount+"\u9000");

                rayStealing(targetEntity.position().add(0,1,0), senderEntity.position().add(0,1,0), (ServerLevel) level);
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private void rayStealing(Vec3 start, Vec3 end, ServerLevel level) {
        for (float i = 0; i < 1; i += 0.01f) {
            Vec3 pos = start.lerp(end, i);
            level.sendParticles(ParticleTypes.SCULK_SOUL, pos.x, pos.y, pos.z, 1, 0.1, 0.1, 0.1, 0);
        }
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public Vector3f getTargetingColor() {
        return new Vector3f(.69f, 0.07f, 0.03f);
    }
}
