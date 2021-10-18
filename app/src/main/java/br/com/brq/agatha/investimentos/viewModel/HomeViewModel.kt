package br.com.brq.agatha.investimentos.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.brq.agatha.investimentos.model.Finance
import br.com.brq.agatha.investimentos.model.Moeda
import br.com.brq.agatha.investimentos.repository.MoedaApiDataSource
import br.com.brq.agatha.investimentos.viewModel.base.CoroutinesContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeViewModel(
    val dataSource: MoedaApiDataSource,
    coroutinesContextProvider: CoroutinesContextProvider,
    private val moedaWrapper: MoedaWrapper
) : ViewModel() {

    var quandoFinaliza: () -> Unit = {}

    private val eventRetornoDaApi = MutableLiveData<RetornoStadeApi>()

    val viewModelRetornoDaApi: LiveData<RetornoStadeApi> = eventRetornoDaApi

    private val io: CoroutineScope = CoroutineScope(coroutinesContextProvider.io)

    fun buscaDaApi() {
        io.launch {
            val moedasDoBanco = dataSource.buscaMoedasNoBanco()
            var exception: Exception? = null
            var financeDaApi: Finance? = null
            try {
                financeDaApi = dataSource.getFinanceDaApi()
            } catch (e: Exception) {
                exception = e
            } finally {
                verificaSeDeuExcecaoAoChamarDaApi(exception, financeDaApi, moedasDoBanco)
            }
        }
    }

    private fun verificaSeDeuExcecaoAoChamarDaApi(
        exception: Exception?,
        financeDaApi: Finance?,
        moedasDoBanco: List<Moeda>
    ) {
        if (exception != null || financeDaApi == null) {
            setEventRetornoEFinalizaBusca(RetornoStadeApi.SucessoRetornoBanco(moedasDoBanco))
        } else {
            dataSource.atualizaBancoDeDados(moedasDoBanco, financeDaApi)
            val listaMoedadaApi = moedaWrapper.agrupaTodasAsMoedasNaLista(financeDaApi)
            setEventRetornoEFinalizaBusca(RetornoStadeApi.SucessoRetornoApi(listaMoedadaApi))
        }
    }

    private fun setEventRetornoEFinalizaBusca(retornoStadeApi: RetornoStadeApi) {
        eventRetornoDaApi.postValue(retornoStadeApi)
        quandoFinaliza()
    }

}

