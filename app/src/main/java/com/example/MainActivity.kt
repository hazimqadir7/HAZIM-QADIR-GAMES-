package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.AchievementsDialog
import com.example.ui.screens.DeliveryCompleteDialog
import com.example.ui.screens.DrivingHudScreen
import com.example.ui.screens.GarageScreen
import com.example.ui.screens.JobSelectScreen
import com.example.ui.screens.PauseDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TruckSimulatorKashmirApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TruckSimulatorKashmirApp(viewModel: MainGameViewModel) {
    val screenState by viewModel.screenState.collectAsState()
    val profile by viewModel.profileState.collectAsState()
    val telemetry by viewModel.telemetryState.collectAsState()
    val activeJob by viewModel.activeJob.collectAsState()
    val activeRoute by viewModel.activeRoute.collectAsState()
    val showPause by viewModel.showPause.collectAsState()
    val showDeliveryComplete by viewModel.showDeliveryComplete.collectAsState()
    val showAchievements by viewModel.showAchievements.collectAsState()

    if (profile == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF09090B)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFF59E0B))
        }
        return
    }

    val currentProfile = profile!!

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF09090B)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            when (screenState) {
                "GARAGE" -> {
                    GarageScreen(
                        profile = currentProfile,
                        soundEngine = viewModel.soundEngine,
                        onSelectTruck = { viewModel.selectTruck(it) },
                        onBuyTruck = { id, price -> viewModel.buyTruck(id, price) },
                        onSelectLivery = { viewModel.selectLivery(it) },
                        onBuyLivery = { id, price -> viewModel.buyLivery(id, price) },
                        onSelectHorn = { viewModel.selectHorn(it) },
                        onBuyHorn = { id, price -> viewModel.buyHorn(id, price) },
                        onSelectDashboardColor = { viewModel.selectDashboardColor(it) },
                        onApplyUpgrade = { type, cost -> viewModel.applyUpgrade(type, cost) },
                        onOpenJobSelect = { viewModel.openJobSelect() },
                        onStartFreeRoam = { viewModel.startFreeRoam() },
                        onOpenAchievements = { viewModel.openAchievements() }
                    )
                }

                "JOB_SELECT" -> {
                    BackHandler { viewModel.returnToGarage() }
                    JobSelectScreen(
                        onStartJob = { job, route, weather -> viewModel.startJob(job, route, weather) },
                        onBack = { viewModel.returnToGarage() }
                    )
                }

                "DRIVING" -> {
                    BackHandler { viewModel.pauseGame() }
                    DrivingHudScreen(
                        telemetry = telemetry,
                        physicsEngine = viewModel.physicsEngine,
                        truckModel = viewModel.getActiveTruck(),
                        livery = viewModel.getActiveLivery(),
                        route = activeRoute,
                        activeJob = activeJob,
                        dashboardColor = viewModel.getActiveDashboardColor(),
                        onPlayHorn = { viewModel.playEquippedHorn() },
                        onPauseClicked = { viewModel.pauseGame() }
                    )
                }
            }

            // Pause Dialog
            if (showPause) {
                PauseDialog(
                    onResume = { viewModel.resumeGame() },
                    onRestart = { viewModel.restartMission() },
                    onReturnToGarage = { viewModel.returnToGarage() }
                )
            }

            // Delivery Complete Dialog
            if (showDeliveryComplete && activeJob != null) {
                DeliveryCompleteDialog(
                    job = activeJob!!,
                    telemetry = telemetry,
                    currentLevel = currentProfile.driverLevel,
                    currentXp = currentProfile.driverXp,
                    onClaimAndContinue = { money, xp -> viewModel.claimDeliveryReward(money, xp) }
                )
            }

            // Achievements Dialog
            if (showAchievements) {
                AchievementsDialog(
                    profile = currentProfile,
                    onClaim = { viewModel.claimAchievement(it) },
                    onDismiss = { viewModel.closeAchievements() }
                )
            }
        }
    }
}
