package com.edu.zup.pix.dto.bcb.enums

import com.edu.zup.TipoChave

enum class TiposDeChaveBCB {
    CPF,CNPJ,PHONE,EMAIL,RANDOM;

    companion object {

        fun TiposDeChaveBCB.toPixItauRequest(chave: TiposDeChaveBCB):TipoChave{
            when{
                chave == CPF -> return TipoChave.CPF
                chave == EMAIL -> return TipoChave.email
                chave == PHONE -> return TipoChave.telefone_celular
                else -> return TipoChave.chave_aleatoria

            }
        }
    }
}