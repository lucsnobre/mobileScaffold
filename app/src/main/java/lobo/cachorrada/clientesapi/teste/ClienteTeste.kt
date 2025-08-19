package lobo.cachorrada.clientesapi.teste

import br.dev.celso.clientesapp.model.Cliente
import br.dev.celso.clientesapp.service.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await

fun main() {

    val c1 = Cliente(
        nome = "Xpto",
        email = "pele@santosfc.com.br"
    )

    CoroutineScope(Dispatchers.IO).launch {
        val retrofit = RetrofitFactory().getClienteService()
        val cliente = retrofit.gravar(c1).await()
    }

}