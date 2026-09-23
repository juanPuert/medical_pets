package com.aistudio.petcare.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aistudio.petcare.ui.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

@Serializable object HomeRoute
@Serializable data class PetDetailRoute(val petId: Int)
@Serializable object AddPetRoute
@Serializable object ForumRoute
@Serializable object MapRoute
@Serializable object StatsRoute
@Serializable object ProfileRoute

@Composable
fun PetCareApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                viewModel = viewModel,
                onPetClick = { id -> navController.navigate(PetDetailRoute(id)) },
                onAddPetClick = { navController.navigate(AddPetRoute) },
                onForumClick = { navController.navigate(ForumRoute) },
                onMapClick = { navController.navigate(MapRoute) },
                onStatsClick = { navController.navigate(StatsRoute) },
                onProfileClick = { navController.navigate(ProfileRoute) }
            )
        }
        composable<PetDetailRoute> { backStackEntry ->
            val route: PetDetailRoute = backStackEntry.toRoute()
            PetDetailScreen(
                petId = route.petId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<AddPetRoute> {
            AddPetScreen(
                onAddClick = { name, species, breed, birthDate, weight, photoUri ->
                    viewModel.addPet(name, species, breed, birthDate, weight, photoUri)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<ForumRoute> {
            ForumScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() })
        }
        composable<MapRoute> {
            MapScreen(onBackClick = { navController.popBackStack() })
        }
        composable<StatsRoute> {
            StatsScreen(onBackClick = { navController.popBackStack() })
        }
        composable<ProfileRoute> {
            ProfileScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() })
        }
    }
}
