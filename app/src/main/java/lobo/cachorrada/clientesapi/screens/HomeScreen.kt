package lobo.cachorrada.clientesapi.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lobo.cachorrada.clientesapi.R
import lobo.cachorrada.clientesapi.model.Cliente
import lobo.cachorrada.clientesapi.service.RetrofitFactory
import lobo.cachorrada.clientesapi.ui.theme.ClientesAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import retrofit2.await


@Composable
fun HomeScreens(modifier: Modifier = Modifier){


    var navController = rememberNavController()

    Scaffold(
        topBar = {
            BarraDeTiTulo()
        },
        bottomBar = {
            BarraDeNavegacao(navController)
        },
        floatingActionButton = {
            BotoaoFlutuante(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.background
                )
        ) {
            NavHost(
                navController = navController,
                startDestination = "Home"
            ){
                composable(route = "Home"){ TelaHome(paddingValues) }
                composable(route = "Form"){ FormCliente(navController) }
            }

        }
    }
}

@Composable
fun TelaHome(paddingValues: PaddingValues) {
    val retrofit = RetrofitFactory().getClienteService()

    //Variavel para amazenar a lista de personagens da API
    var clienteList by remember {
        mutableStateOf(listOf<Cliente>())
    }

    //Fazer uma chamadada na API
    LaunchedEffect(Dispatchers.IO) {
        clienteList = retrofit.exibirTodos().await()
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        Row {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Icone",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Lista de clientes"
            )
        }
        LazyColumn {
            items(clienteList){
                ClienteCard(
                    nome = it.nome,
                    email = it.email,
                )
            }
        }
    }
}

@Composable
fun ClienteCard (
    id: Long? = null,
    nome: String = "nome",
    email: String = "email"
) {
    var mostraTelaDeConfimarExclusao by remember {
        mutableStateOf(true)
    }
    val retrofit = RetrofitFactory().getClienteService()

    val cliente = Cliente(
        id,
        nome,
        email
    )


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(
                start = 8.dp,
                end = 8.dp,
                bottom = 4.dp
            ),
        colors = CardDefaults
            .cardColors(
                contentColor = MaterialTheme.colorScheme.primaryContainer
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = nome,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = email,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            IconButton(
                onClick = {
                    mostraTelaDeConfimarExclusao = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "texto",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

        }

        if (mostraTelaDeConfimarExclusao){
            AlertDialog(
                onDismissRequest = {
                    mostraTelaDeConfimarExclusao = false
                },
                title = {
                    Text(
                        text = "Excluir?"
                    )
                },
                text = {
                    Text(
                        text = "Quer excluir o cliente ${nome}?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            LaunchedEffect(Dispatchers.IO) {
                                val ClienteExcluido = retrofit.excluir(cliente).await()
                            }
                        }
                    ) {
                        Text(
                            text = "Sim",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mostraTelaDeConfimarExclusao = false
                        }
                    ) {
                        Text(
                            text = "Não",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Preview
@Composable
private fun ClienteCardPreview() {
    ClienteCard()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraDeTiTulo (modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        colors = TopAppBarDefaults
            .topAppBarColors(
                containerColor = MaterialTheme
                    .colorScheme.primary
            ),
        title = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Texto atoa",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "email atoa",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Card(
                    modifier = Modifier
                        .size(60.dp),
                    shape = CircleShape
                ) {
                    Image(
                        painter = painterResource(R.drawable.objetivo),
                        contentDescription = "foto perfil"
                    )
                }

            }
        }
    )
}

//@Preview
@Composable
private fun BarraDeTiTuloPreview() {
    BarraDeTiTulo()
}

@Composable
fun BarraDeNavegacao(navController: NavHostController?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        NavigationBarItem(
            selected = false,
            onClick = {
                navController?.navigate("Home")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        )
    }
}

//@Preview
//@Composable
//private fun BarraDeNavegacaoPreview() {
//    BarraDeNavegacao(navController)
//}

@Composable
fun BotoaoFlutuante(navController: NavHostController?) {
    FloatingActionButton(
        onClick = {
            navController?.navigate("Form")
        },
        containerColor = MaterialTheme.colorScheme.tertiary
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Botão adicionar",
            tint = MaterialTheme.colorScheme.onTertiary
        )
    }
}

//@Preview
//@Composable
//private fun BotoaoFlutuantePreview() {
//    BotoaoFlutuante(navController)
//}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreensPreview(){
    ClientesAppTheme {
        HomeScreens()
    }
}