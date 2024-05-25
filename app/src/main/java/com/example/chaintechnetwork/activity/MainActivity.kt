package com.example.chaintechnetwork.activity

import EncryptionUtils
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.chaintechnetwork.db.AccountEntity
import com.example.chaintechnetwork.viewmodel.AccountViewModel
import com.example.chaintechnetwork.viewmodel.AccountViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChaintechNetworkApp()
        }
    }
}


@Composable
fun ChaintechNetworkApp() {
    val context = LocalContext.current
    val accountViewModel: AccountViewModel = ViewModelProvider(
        context as MainActivity,
        AccountViewModelFactory(Application(), context)
    )[AccountViewModel::class.java]
    Scaffold(
        containerColor = Color(0xFFE9ECF3),
        topBar = { AppTopBar() },
        floatingActionButton = { AddAccountFab(accountViewModel, context) },
        content = {
            Column(Modifier.padding(it)) {
                AccountList(accountViewModel)
            }
        },
    )
}

@Composable
fun AppTopBar() {
    TopAppBar(
        backgroundColor = Color(0xFFE9ECF3),
        elevation = 4.dp,
        title = {
            Text(
                text = "Password Manager",
                style = TextStyle(fontSize = 23.sp),
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )
        },
    )
}

@Composable
fun AddAccountFab(accountViewModel: AccountViewModel, context: MainActivity) {
    val showBottomSheet = remember { mutableStateOf(false) }

    FloatingActionButton(
        contentColor = Color.White,
        containerColor = Color(0xFF72AEDF),
        onClick = { showBottomSheet.value = true },
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add")
    }

    if (showBottomSheet.value) {
        AddAccountBottomSheet(
            onDismiss = { showBottomSheet.value = false },
            accountViewModel,
            context
        )
    }
}

@Composable
fun AccountList(accountViewModel: AccountViewModel) {
    val accounts by accountViewModel.allAccounts.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val viewModel: AccountViewModel = ViewModelProvider(
        LocalContext.current as MainActivity,
        AccountViewModelFactory(Application(), context)
    )[AccountViewModel::class.java]
//    val accounts = viewModel.getAllData() // You need to define allAccounts in AccountViewModel

    if (accounts.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "There is no data Available")
        }
    } else {
        LazyColumn {
            items(accounts.size) {
                AccountItem(accounts[it], viewModel)
            }
        }
    }

}

@Composable
fun AccountItem(account: AccountEntity, viewModel: AccountViewModel) {
    val showBottomSheet = remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { showBottomSheet.value = true },
        shape = RoundedCornerShape(25.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            var star = ""
            val context = LocalContext.current
            val decPass = EncryptionUtils.decrypt(context, account.password)

            Log.e("Dec Pass", "AccountItem: $decPass")
            for (i in 0..decPass.length) {
                star += "*"
            }
//            Row {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = star,
                    style = TextStyle(color = Color.LightGray),
                )

            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to details"
            )
        }
    }

    if (showBottomSheet.value) {
        UpdateAccountBottomSheet(
            onDismiss = { showBottomSheet.value = false },
            account,
            viewModel
        )
    }
    Log.e("DataBase", "AccountItem:$account ")
    // Display account details
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountBottomSheet(
    onDismiss: () -> Unit,
    accountViewModel: AccountViewModel,
    context: MainActivity
) {
    val textStateAccountName = remember { mutableStateOf(TextFieldValue()) }
    val textStateAccountId = remember { mutableStateOf(TextFieldValue()) }
    val textStateAccountPassword = remember { mutableStateOf(TextFieldValue()) }
    val strength = calculatePasswordStrength(textStateAccountPassword.value.text)
    val colors = listOf(Color.Red, Color.Red, Color.Yellow, Color.Green, Color.Blue)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE9ECF3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = textStateAccountName.value,
                onValueChange = { textStateAccountName.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = { Text(text = "Account Name", style = TextStyle(color = Color.LightGray)) }
            )

            OutlinedTextField(
                value = textStateAccountId.value,
                onValueChange = { textStateAccountId.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = {
                    Text(
                        text = "Username/ Email",
                        style = TextStyle(color = Color.LightGray)
                    )
                }
            )

            OutlinedTextField(
                value = textStateAccountPassword.value,
                onValueChange = { textStateAccountPassword.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = { Text(text = "Password", style = TextStyle(color = Color.LightGray)) },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 5) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .padding(horizontal = 1.dp)
                            .background(
                                color = if (i < strength) colors[i] else Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Password Strength: ${strengthDescription(strength)}")
            Button(
                onClick = {
                    val accountName = textStateAccountName.value.text
                    val accountId = textStateAccountId.value.text
                    val accountPassword = textStateAccountPassword.value.text

                    val encPass =
                        EncryptionUtils.encrypt(context, textStateAccountPassword.value.text)

                    Log.e("Enc Pass", "AddAccountBottomSheet: $encPass")

                    if (accountName.isNotBlank() && accountId.isNotBlank() && accountPassword.isNotBlank()) {
                        accountViewModel.insert(AccountEntity(0, accountName, accountId, encPass))
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)

            ) {
                Text(
                    text = "Add New Account",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAccountBottomSheet(
    onDismiss: () -> Unit,
    account: AccountEntity,
    viewModel: AccountViewModel
) {
    val context = LocalContext.current
    val textStateAccountPassword =
        remember { mutableStateOf(EncryptionUtils.decrypt(context, account.password)) }
    val passwordVisible = remember { mutableStateOf(false) }
    val strength = calculatePasswordStrength(textStateAccountPassword.value)
    val colors = listOf(Color.Red, Color.Red, Color.Yellow, Color.Green, Color.Blue)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE9ECF3),
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Account Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF708AE6)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Account Type",
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Text(
                text = account.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Username/ Email",
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = account.accountId,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Password",
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = textStateAccountPassword.value,
                onValueChange = { textStateAccountPassword.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible.value) "Hide password" else "Show password"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 5) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .padding(horizontal = 1.dp)
                            .background(
                                color = if (i < strength) colors[i] else Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Password Strength: ${strengthDescription(strength)}")
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        viewModel.update(
                            EncryptionUtils.encrypt(
                                context,
                                textStateAccountPassword.value
                            ), account.accountId
                        )
                        onDismiss()
                        Toast.makeText(
                            context,
                            "Your account ${account.name} is updated now",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        "Edit",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        viewModel.deleteAccount(account)
                        onDismiss()
                        Toast.makeText(
                            context,
                            "Your account ${account.name} is Deleted now",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

fun calculatePasswordStrength(password: String): Int {
    var strength = 0

    if (password.length >= 8) strength += 1
    if (password.any { it.isDigit() }) strength += 1
    if (password.any { it.isUpperCase() }) strength += 1
    if (password.any { it.isLowerCase() }) strength += 1
    if (password.any { !it.isLetterOrDigit() }) strength += 1

    return strength
}

fun strengthDescription(strength: Int): String {
    return when (strength) {
        0 -> "Very Weak"
        1 -> "Weak"
        2 -> "Medium"
        3 -> "Strong"
        4 -> "Very Strong"
        else -> "Unknown"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChaintechNetworkApp() {
    ChaintechNetworkApp()
}
