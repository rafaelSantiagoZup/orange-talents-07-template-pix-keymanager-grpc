package com.edu.zup.pix.dto.bcb.enums

import com.edu.zup.TipoConta

enum class TiposDeContaBCB {
    CACC,SVGS;

    fun fromTipoContaItau(tipo:TipoConta): TiposDeContaBCB {
        when{
            tipo == TipoConta.CONTA_CORRENTE -> return CACC
            else -> return SVGS
        }
    }
    companion object {

        fun TiposDeContaBCB.toTipoContaItau(tipo: TiposDeContaBCB):TipoConta{
            when{
                tipo == CACC -> return TipoConta.CONTA_CORRENTE
                else -> return TipoConta.CONTA_POUPANCA
            }
        }

    }
}