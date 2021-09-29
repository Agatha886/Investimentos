package br.com.brq.agatha.investimentos.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.math.BigDecimal

@Entity(
    foreignKeys = [ForeignKey(
        entity = Moeda::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idMoeda")
    )]
)
class Usuario(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var saldoDisponivel: BigDecimal = BigDecimal.ZERO,
    var idMoeda: Int? = null
): Serializable{

    fun setIdMoeda(idMoeda: Int){
        this.idMoeda = idMoeda
    }


}
