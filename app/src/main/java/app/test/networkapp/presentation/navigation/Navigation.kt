package app.test.networkapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import app.test.networkapp.presentation.screens.NetworkDetailsScreen
import app.test.networkapp.presentation.screens.OrganizationDetailsScreen
import app.test.networkapp.presentation.screens.SearchScreen

@Composable
fun NavigationController() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Search
    ) {
        composable<Screens.Search> {
            SearchScreen(
                onOrganizationSelected = { orgId ->
                    navController.navigate(Screens.Organization(orgId))
                },
                onNetworkSelected = { inetnum ->
                    navController.navigate(Screens.Network(inetnum))
                }
            )
        }

        composable<Screens.Organization> { backStackEntry ->
            OrganizationDetailsScreen(
                organizationId = backStackEntry.toRoute<Screens.Organization>().orgId,
                onBackPressed = { navController.navigateUp() },
                onNetworkSelected = { inetnum ->
                    navController.navigate(Screens.Network(inetnum))
                }
            )
        }

        composable<Screens.Network> { backStackEntry ->
            NetworkDetailsScreen(
                inetnum = backStackEntry.toRoute<Screens.Network>().inetnum,
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}