package br.infnet.tp1guilda.mapper;

import br.infnet.tp1guilda.domain.aventura.Companheiro;
import br.infnet.tp1guilda.dto.companheiro.ResponseCompanheiro;
import org.springframework.stereotype.Component;

@Component
public class CompanheiroMapper {

    public ResponseCompanheiro toResponse(Long aventureiroId, Companheiro companheiro) {
        if (companheiro == null) {
            return null;
        }
        return new ResponseCompanheiro(
                aventureiroId,
                companheiro.getNome(),
                companheiro.getEspecie(),
                companheiro.getLealdade()
        );
    }
}