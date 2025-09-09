package zzhangaao1z.cobblemonmovedex.mixin.common;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.moves.Learnset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(value = Learnset.class, remap = false)
public class LearnsetMixin {

    @Shadow @Final private List<MoveTemplate> tmMoves;

    @Shadow @Final private Set<MoveTemplate> evolutionMoves;

    @Shadow @Final private List<MoveTemplate> tutorMoves;

    @Shadow @Final private List<MoveTemplate> eggMoves;

    @Inject(method = "decode", at = @At("TAIL"))
    private void decode(RegistryFriendlyByteBuf buffer, CallbackInfo ci){
        int evo_size = buffer.readShort();
        this.evolutionMoves.clear();
        for(int i = 1; i <= evo_size; i++){
            this.evolutionMoves.add(Moves.INSTANCE.getByNumericalId(buffer.readInt()));
        }
        int tm_size = buffer.readShort();
        this.tmMoves.clear();
        for(int i = 1; i <= tm_size; i++){
            this.tmMoves.add(Moves.INSTANCE.getByNumericalId(buffer.readInt()));
        }
        int tutor_size = buffer.readShort();
        this.tutorMoves.clear();
        for(int i = 1; i <= tutor_size; i++){
            this.tutorMoves.add(Moves.INSTANCE.getByNumericalId(buffer.readInt()));
        }
        int egg_size = buffer.readShort();
        this.eggMoves.clear();
        for(int i = 1; i <= egg_size; i++){
            this.eggMoves.add(Moves.INSTANCE.getByNumericalId(buffer.readInt()));
        }
    }

    @Inject(method = "encode", at = @At("TAIL"))
    private void encode(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeShort(this.evolutionMoves.size());
        this.evolutionMoves.forEach(move -> buffer.writeInt(move.getNum()));
        buffer.writeShort(this.tmMoves.size());
        this.tmMoves.forEach(move -> buffer.writeInt(move.getNum()));
        buffer.writeShort(this.tutorMoves.size());
        this.tutorMoves.forEach(move -> buffer.writeInt(move.getNum()));
        buffer.writeShort(this.eggMoves.size());
        this.eggMoves.forEach(move -> buffer.writeInt(move.getNum()));
    }

}
