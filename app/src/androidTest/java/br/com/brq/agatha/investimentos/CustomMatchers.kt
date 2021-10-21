package br.com.brq.agatha.investimentos

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import java.lang.IndexOutOfBoundsException

class CustomMatchers {

    fun apresentaMoeda(text: String, positionItem: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            //describeTo nos permite anexar nossa própria descrição ao matcher personalizado.
            override fun describeTo(description: Description?) {
                description?.appendText("Item com a descrição: $text não foi encontrado")
            }

            //matchSafely é onde implementamos a lógica de comparação para a contagem de itens.
            override fun matchesSafely(item: RecyclerView?): Boolean {
                val viewHolder = item?.findViewHolderForAdapterPosition(positionItem)?.itemView
                    ?: throw IndexOutOfBoundsException("Item na posição $positionItem não foi encontrado")

                return verificaAbreviacaoMoeda(viewHolder, text) && verificaVariacaoMoeda(viewHolder)
            }
        }
    }

    private fun verificaAbreviacaoMoeda(
        viewHolder: View,
        text: String
    ): Boolean {
        val view = viewHolder.findViewById<TextView>(R.id.cardView_home_nome_moeda)
        return view?.text.toString() == text
    }

    private fun verificaVariacaoMoeda(viewHolder: View): Boolean {
        val view = viewHolder.findViewById<TextView>(R.id.cardView_home_variation_moeda)
        if(view?.isVisible == true && verificaCorDaVariacaoMoeda(view, viewHolder)) return true
        else throw  NullPointerException("Campo variação do item não foi encontrado ou está com problemas")
    }

    private fun verificaCorDaVariacaoMoeda(view: TextView?, viewHolder: View): Boolean {
        val context = viewHolder.context
        val colorRed: Int = ContextCompat.getColor(context, R.color.red)
        val colorWhite: Int = ContextCompat.getColor(context, R.color.white)
        val colorVerde = ContextCompat.getColor(context, R.color.verde)

        return when(view?.currentTextColor){
            colorRed -> true
            colorVerde -> true
            colorWhite -> true
            else -> false
        }
    }

}