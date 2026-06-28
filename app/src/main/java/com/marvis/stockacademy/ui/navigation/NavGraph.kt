package com.marvis.stockacademy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.marvis.stockacademy.data.Category
import com.marvis.stockacademy.data.KnowledgeBase
import com.marvis.stockacademy.ui.screens.*

object Routes {
    const val HOME = "home"; const val CATEGORY = "category/{name}"
    const val DETAIL = "detail/{id}"; const val SEARCH = "search"
    fun category(name: String) = "category/$name"
    fun detail(id: String) = "detail/$id"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onCategoryClick = { navController.navigate(Routes.category(it.name)) }, onItemClick = { navController.navigate(Routes.detail(it.id)) }, onSearchClick = { navController.navigate(Routes.SEARCH) })
        }
        composable(Routes.CATEGORY, arguments = listOf(navArgument("name") { type = NavType.StringType })) { backStack ->
            val name = backStack.arguments?.getString("name") ?: return@composable
            CategoryScreen(Category.valueOf(name), onBack = { navController.popBackStack() }, onItemClick = { navController.navigate(Routes.detail(it.id)) })
        }
        composable(Routes.DETAIL, arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            KnowledgeBase.allItems.find { it.id == id }?.let { DetailScreen(it, KnowledgeBase.allItems, onBack = { navController.popBackStack() }, onItemClick = { navController.navigate(Routes.detail(it.id)) }) }
        }
        composable(Routes.SEARCH) {
            SearchScreen(onBack = { navController.popBackStack() }, onItemClick = { navController.navigate(Routes.detail(it.id)) })
        }
    }
}
