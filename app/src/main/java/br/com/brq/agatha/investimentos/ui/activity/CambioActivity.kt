package br.com.brq.agatha.investimentos.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.brq.agatha.investimentos.R
import br.com.brq.agatha.investimentos.constantes.CHAVE_MOEDA
import br.com.brq.agatha.investimentos.constantes.CHAVE_RESPOSTA_MENSAGEM
import br.com.brq.agatha.investimentos.constantes.CHAVE_RESPOSTA_TITULO
import br.com.brq.agatha.investimentos.extension.setMyActionBar
import br.com.brq.agatha.investimentos.extension.transacaoFragment
import br.com.brq.agatha.investimentos.model.Moeda
import br.com.brq.agatha.investimentos.ui.fragment.CambioFragment
import br.com.brq.agatha.investimentos.ui.fragment.RespostaFragment
import java.io.Serializable

@Suppress("DEPRECATION")
class CambioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambio)
        iniciaComFragmentCambio()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is CambioFragment -> {
                configuraFragmentsCambio(fragment)
            }
            is RespostaFragment -> {
                configuraFragmentResposta(fragment)
            }
        }
    }

    private fun iniciaComFragmentCambio() {
        val cambioFragment = CambioFragment()
        setArgumentsDadosMoedas(cambioFragment)
        transacaoFragment {
            replace(R.id.activity_cambio_container, cambioFragment, "CAMBIO")
        }
    }

    private fun setArgumentsDadosMoedas(cambioFragment: CambioFragment) {
        if (intent.hasExtra(CHAVE_MOEDA)) {
            val serializableExtra: Serializable? = intent.getSerializableExtra(CHAVE_MOEDA)
            if (serializableExtra != null) {
                val moedaRecebida = serializableExtra as Moeda
                val moedaBundle = Bundle()
                moedaBundle.putSerializable(CHAVE_MOEDA, moedaRecebida)
                cambioFragment.arguments = moedaBundle
            }
        }
    }

    private fun configuraFragmentResposta(fragment: RespostaFragment) {
        setMyActionBar(fragment.tituloAppBar, true, setOnClickButtonVoltar = {
            onBackPressed()
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun configuraFragmentsCambio(fragment: CambioFragment) {
        setMyActionBar("Câmbio", true, setOnClickButtonVoltar = {
            voltaParaTelaDeMoedas()
        })
        setAcaoQuandoMoedaInvalida(fragment)
        vaiParaFragmentRespostaQuandoCompraOuVenda(fragment)
    }

    private fun vaiParaFragmentRespostaQuandoCompraOuVenda(fragment: CambioFragment) {
        val respostaFragment = RespostaFragment()
        fragment.quandoCompraOuVendaSucesso = { mensagem, tituloAppBar ->
            val dados = Bundle()
            dados.putString(CHAVE_RESPOSTA_MENSAGEM, mensagem)
            dados.putString(CHAVE_RESPOSTA_TITULO, tituloAppBar)
            respostaFragment.arguments = dados

            transacaoFragment {
                replace(R.id.activity_cambio_container, respostaFragment, "RESPOSTA")
                addToBackStack("CAMBIO")
            }
        }
    }


    private fun setAcaoQuandoMoedaInvalida(fragment: CambioFragment) {
        fragment.quandoRecebidaMoedaInvalida = {
            voltaParaTelaDeMoedas()
            Toast.makeText(
                this,
                it,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun voltaParaTelaDeMoedas() {
        val intent = Intent(this, HomeMoedasActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
    }

}