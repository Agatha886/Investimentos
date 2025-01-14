package br.com.brq.agatha.investimentos.ui

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import br.com.brq.agatha.investimentos.R
import br.com.brq.agatha.base.custom.CustomAssertions
import br.com.brq.agatha.base.custom.ListaMoedaMatcher
import br.com.brq.agatha.base.custom.ToastMachter
import br.com.brq.agatha.base.repository.MoedaApiDataSource
import br.com.brq.agatha.base.util.TestContextProvider
import br.com.brq.agatha.presentation.adapter.ListaMoedasAdpter.ListaMoedasViewHolder
import br.com.brq.agatha.investimentos.viewmodel.HomeViewModel
import br.com.brq.agatha.investimentos.viewModel.MoedaWrapper
import br.com.brq.agatha.presentation.ui.activity.CambioActivity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.math.BigDecimal

@RunWith(JUnit4::class)
class HomeMoedasActivityTest : KoinTest {

    private val moedaMatcher = ListaMoedaMatcher()

    private lateinit var mockedModule: Module

    @MockK
    private lateinit var dataSource: MoedaApiDataSource

    @MockK
    private lateinit var moedaWrapper: MoedaWrapper

    @MockK
    private lateinit var finance: br.com.brq.agatha.domain.model.Finance

    private val listaMoedasBanco = listOf<br.com.brq.agatha.domain.model.Moeda>(
        br.com.brq.agatha.domain.model.Moeda(
            name = "Dollar",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = "FAKE DO BANCO",
            totalDeMoeda = 0,
            variation = BigDecimal(-1)
        )
    )

    private val listaMoedasApi = listOf<br.com.brq.agatha.domain.model.Moeda>(
        br.com.brq.agatha.domain.model.Moeda(
            name = "Dollar",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(1)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Japanese Yen",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(-1)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Pound Sterling",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(0)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Euro",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(-10)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Renminbi",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(10)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Canadian Dollar",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(0)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Bitcoin",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(0)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Australian Dollar",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(0)
        ),
        br.com.brq.agatha.domain.model.Moeda(
            name = "Argentine Peso",
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            abreviacao = null,
            variation = BigDecimal(0)
        )
    )


    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockedModule = module {
            single<HomeViewModel>(override = true) {
                HomeViewModel(
                    dataSource,
                    TestContextProvider(),
                    moedaWrapper
                )
            }
        }

        coEvery { dataSource.buscaTodasMoedasNoBanco() } returns listaMoedasBanco
        every {moedaWrapper.agrupaTodasAsMoedasNaLista(finance)} returns listaMoedasApi

    }


    @After
    fun after() {
        unloadKoinModules(mockedModule)
    }

    private fun iniciaActivity() {
        loadKoinModules(mockedModule)
        launchActivity<HomeMoedasActivity>()
    }

    @Test
    fun deveApresentarOTextoDeTituloDaAppBar_quandoCarregaATela() {
        iniciaActivity()
        onView(ViewMatchers.withText("Home/Moeda"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.include_my_toolbar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun deveApresentarORecyclerView_quandoAbreATela() {
        iniciaActivity()
        onView(withId(R.id.home_recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun deveVerificarQuantidadeDeMoedasDaListaECadaMoeda_quandoCarregaListaDaApi() {
        coEvery { dataSource.getFinanceDaApi() } returns finance
        iniciaActivity()

        onView(withId(R.id.home_recyclerView))
            .check(CustomAssertions.hasItemCount(9))

        onView(withId(R.id.home_recyclerView))
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("USD", 0))
            )
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("JPY", 1))

            )
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("GBP", 2))
            )
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("EUR", 3))
            )
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("CNY", 4))
            )
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("CAD", 5))
            )
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("BTC", 6))
            )

            .perform(RecyclerViewActions.scrollToPosition<ListaMoedasViewHolder>(8))

            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("AUD", 7))
            )
            .check(
                ViewAssertions
                    .matches(moedaMatcher.apresentaMoeda("ARS", 8))
            )
    }

    @Test
    fun deveVerificarQuantidadeDeMoedasDaListaEMensagemDeErroAtualizarDados_quandoCarregarListaDoBanco() {
        coEvery { dataSource.getFinanceDaApi() } throws Exception("Sem internet")
        iniciaActivity()

        onView(withId(R.id.home_recyclerView))
            .check(CustomAssertions.hasItemCount(1))

        onView(ViewMatchers.withText(br.com.brq.agatha.domain.util.MENSAGEM_FALHA_API))
            .inRoot(ToastMachter()) // verifica se a hierarquia RAIZ É UM Toast
            .check(ViewAssertions.matches(isDisplayed())) // verifica SE APARECE A MENSAGEM NO TOAST

    }

    @Test
    fun deveVerificarFormatacaoDaVariacao_quandoCarregaListaDoBanco(){
        coEvery { dataSource.getFinanceDaApi() } throws Exception("Sem internet")
        iniciaActivity()

        onView(withId(R.id.home_recyclerView))
            .check(ViewAssertions.matches(moedaMatcher.verificaVariacao("-1,00%", 0)))

    }

    @Test
    fun deveAparecerMensagemDadosNaoAtualizados_quandoCarregaListaDoBancoEClicaNaMoedaDaLista(){
        coEvery { dataSource.getFinanceDaApi() } throws Exception("Sem internet")
        iniciaActivity()

        onView(withId(R.id.home_recyclerView))
            .check(CustomAssertions.hasItemCount(1))

        onView(withId(R.id.home_recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ListaMoedasViewHolder>(0, click()))

        onView(ViewMatchers.withText(br.com.brq.agatha.domain.util.MENSAGEM_DADOS_NAO_ATUALIZADOS))
            .inRoot(ToastMachter()) // verifica se a hierarquia RAIZ É UM Toast
            .check(ViewAssertions.matches(isDisplayed())) // verifica SE APARECE A MENSAGEM NO TOAST
    }

    @Test
    fun deveVerificarSeVaiParaCambio_quandoCarregaListaDaApiEClicaNaMoeda(){
        Intents.init()
        coEvery { dataSource.getFinanceDaApi() } returns finance
        iniciaActivity()

        onView(withId(R.id.home_recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ListaMoedasViewHolder>(0, click()))

        Intents.intended(IntentMatchers.hasComponent(CambioActivity::class.java.name))
    }


    @Test
    fun deveRetornarIndexOutOfBoundsException_quandoIndexDaMoedaIndicadoNoTesteNaoTemNaLista() {
        coEvery { dataSource.getFinanceDaApi() } returns finance
        iniciaActivity()

        try {
            onView(withId(R.id.home_recyclerView))
                .check(
                    ViewAssertions
                        .matches(moedaMatcher.apresentaMoeda("ITEM", 9))
                )
        } catch (e: IndexOutOfBoundsException) {
            assertEquals("Item na posição 9 não foi encontrado", e.message)
        }
    }

}