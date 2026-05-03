package com.example.mycomposeplayground
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

// --- 1. Data Models and Screen Navigation ---

// Represents the data structure for any habit
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

// The systematic steps that are the same for every habit
enum class HabitStep {
    CREATION,          // Step 1: Choose or Create Habit
    GOAL_SETTING,      // Step 2: Define Commitment (Frequency/Duration)
    FOUNDATION,        // Step 3: Establish Triggers (Habit Stacking: When/Where)
    START_SMALL,       // Step 4: The 2-Minute Rule (Minimum Viable Action)
    CONFIRMATION       // Step 5: Ready to Start Tracking
}

enum class AppScreen {
    SETUP_FLOW,
    TRACKING
}

// Mock data for initial selection
val predefinedHabits = listOf(
    Habit(name = "Daily Walk", description = "Improve cardiovascular health.", icon = Icons.Default.Star, color = Color(0xFF4CAF50)),
    Habit(name = "Drink Water", description = "Maintain hydration levels.", icon = Icons.Default.ThumbUp, color = Color(0xFF2196F3)),
    Habit(name = "Read 20 min", description = "Expand knowledge and focus.", icon = Icons.Default.Create, color = Color(0xFF9C27B0))
)

// --- 2. Main App Entry Point ---

/**
 * The main component managing screen navigation and global state.
 */
@Composable
fun HabitFlowApp() {
    // Global state for screen navigation
    var currentScreen by remember { mutableStateOf(AppScreen.SETUP_FLOW) }

    // State to hold the habit being configured/tracked
    var selectedHabit by remember { mutableStateOf<Habit?>(null) }

    // State to hold configured details
    var habitDetails by remember { mutableStateOf(mapOf<String, String>()) }

    // Simple progress state
    var completionCount by remember { mutableStateOf(0) }

    when (currentScreen) {
        AppScreen.SETUP_FLOW -> {
            HabitFlowScreen(
                initialHabit = selectedHabit,
                initialDetails = habitDetails,
                onFlowComplete = { habit, details ->
                    selectedHabit = habit
                    habitDetails = details
                    currentScreen = AppScreen.TRACKING
                }
            )
        }
        AppScreen.TRACKING -> {
            if (selectedHabit != null) {
                HabitProgressScreen(
                    habit = selectedHabit!!,
                    completionCount = completionCount,
                    onMarkComplete = { completionCount++ },
                    onResetFlow = {
                        selectedHabit = null
                        habitDetails = emptyMap()
                        completionCount = 0
                        currentScreen = AppScreen.SETUP_FLOW
                    }
                )
            } else {
                // Should not happen, but return to setup if state is inconsistent
                currentScreen = AppScreen.SETUP_FLOW
            }
        }
    }
}

// --- 3. Systematic Setup Flow Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFlowScreen(
    initialHabit: Habit?,
    initialDetails: Map<String, String>,
    onFlowComplete: (Habit, Map<String, String>) -> Unit
) {
    var currentStep by remember { mutableStateOf(HabitStep.CREATION) }
    var selectedHabit by remember { mutableStateOf(initialHabit) }
    var habitDetails by remember { mutableStateOf(initialDetails.toMutableMap()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Habit Builder Flow") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Step title display
            val stepTitle = when (currentStep) {
                HabitStep.CREATION -> "1. Choose or Create Habit"
                HabitStep.GOAL_SETTING -> "2. Define Commitment"
                HabitStep.FOUNDATION -> "3. Establish Trigger"
                HabitStep.START_SMALL -> "4. Start with 2 Minutes"
                HabitStep.CONFIRMATION -> "5. Ready to Go!"
            }
            Text(
                text = stepTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Dynamic Step Content based on state
            when (currentStep) {
                HabitStep.CREATION -> Step1_Creation(
                    onHabitSelected = { habit ->
                        selectedHabit = habit
                        currentStep = HabitStep.GOAL_SETTING
                    }
                )
                HabitStep.GOAL_SETTING -> selectedHabit?.let { habit ->
                    Step2_GoalSetting(
                        habit = habit,
                        onNext = { goal ->
                            habitDetails["goal"] = goal
                            currentStep = HabitStep.FOUNDATION
                        }
                    )
                }
                HabitStep.FOUNDATION -> selectedHabit?.let { habit ->
                    Step3_Foundation(
                        habit = habit,
                        onNext = { trigger ->
                            habitDetails["trigger"] = trigger
                            currentStep = HabitStep.START_SMALL
                        }
                    )
                }
                HabitStep.START_SMALL -> selectedHabit?.let { habit ->
                    Step4_StartSmall(
                        habit = habit,
                        onNext = { minAction ->
                            habitDetails["minAction"] = minAction
                            currentStep = HabitStep.CONFIRMATION
                        }
                    )
                }
                HabitStep.CONFIRMATION -> selectedHabit?.let { habit ->
                    Step5_Confirmation(
                        habit = habit,
                        details = habitDetails,
                        onStart = { onFlowComplete(habit, habitDetails) },
                        onBack = { currentStep = HabitStep.CREATION }
                    )
                }
            }
        }
    }
}

// --- 4. Individual Step Composables ---

/**
 * Step 1: User chooses a predefined habit or defines a new one.
 */
@Composable
fun Step1_Creation(onHabitSelected: (Habit) -> Unit) {
    var isCreatingNew by remember { mutableStateOf(false) }

    if (isCreatingNew) {
        NewHabitForm(
            onHabitCreated = { habit ->
                onHabitSelected(habit)
            },
            onCancel = { isCreatingNew = false }
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(predefinedHabits) { habit ->
                HabitCard(habit = habit) { onHabitSelected(habit) }
            }
            item {
                OutlinedButton(
                    onClick = { isCreatingNew = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create New")
                    Spacer(Modifier.width(8.dp))
                    Text("Create a New Habit")
                }
            }
        }
    }
}

@Composable
fun HabitCard(habit: Habit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = habit.color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = habit.icon,
                contentDescription = null,
                tint = habit.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = habit.name, style = MaterialTheme.typography.titleMedium)
                Text(text = habit.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun NewHabitForm(onHabitCreated: (Habit) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isValid = name.isNotBlank() && description.isNotBlank()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Define Your New Habit", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit Name (e.g., Learn Spanish)") },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Why do you want this habit?") },
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Note: Icon and Color are hardcoded for simplicity in this flow.
        val newHabit = Habit(
            name = name.trim(),
            description = description.trim(),
            icon = Icons.Default.ThumbUp, // Default icon for new habits
            color = Color(0xFFFDD835) // Default color
        )

        Button(
            onClick = { onHabitCreated(newHabit) },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Create Habit and Continue Setup")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onCancel) {
            Text("Cancel")
        }
    }
}

/**
 * Step 2: Define the goal and frequency. (Goal: 30 minutes, 5 days a week).
 */
@Composable
fun Step2_GoalSetting(habit: Habit, onNext: (String) -> Unit) {
    var goalText by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "How often and how much do you commit to your habit: ${habit.name}?",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = goalText,
            onValueChange = { goalText = it },
            label = { Text("Example: 30 minutes, 5 days per week") },
            leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { if (goalText.isNotBlank()) onNext(goalText) },
            enabled = goalText.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Set Goal and Continue")
        }
    }
}

/**
 * Step 3: Establish a trigger using Habit Stacking (When/Where).
 */
@Composable
fun Step3_Foundation(habit: Habit, onNext: (String) -> Unit) {
    var triggerText by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Use Habit Stacking: What current, reliable routine will **trigger** your ${habit.name} habit?",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            "Formula: 'After I [CURRENT HABIT], I will [NEW HABIT] at [LOCATION]'",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = triggerText,
            onValueChange = { triggerText = it },
            label = { Text("Example: After I finish my coffee, I will open my Spanish app.") },
            leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { if (triggerText.isNotBlank()) onNext(triggerText) },
            enabled = triggerText.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Establish Trigger and Continue")
        }
    }
}

/**
 * Step 4: The 2-Minute Rule - Define the minimum action to ensure success.
 */
@Composable
fun Step4_StartSmall(habit: Habit, onNext: (String) -> Unit) {
    var minActionText by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "The 2-Minute Rule: Define the absolute minimum action for ${habit.name}. This is the 'Show up' action.",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = minActionText,
            onValueChange = { minActionText = it },
            label = { Text("Example: Open the textbook to Chapter 1 (for Learn Spanish)") },
            leadingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { if (minActionText.isNotBlank()) onNext(minActionText) },
            enabled = minActionText.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Define Minimum and Finish Setup")
        }
    }
}

/**
 * Step 5: Review and Confirmation.
 */
@Composable
fun Step5_Confirmation(
    habit: Habit,
    details: Map<String, String>,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text("Configuration Complete!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(habit.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

       // HabitDetailRow(label = "Goal", value = details["goal"] ?: "N/A", icon = Icons.Default.ThumbUp)
       // HabitDetailRow(label = "Trigger", value = details["trigger"] ?: "N/A", icon = Icons.Default.Favorite)
       // HabitDetailRow(label = "Start Action", value = details["minAction"] ?: "N/A", icon = Icons.Default.Star)

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Start Tracking This Habit")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back and Edit Setup")
        }
    }
}

@Composable
fun HabitDetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

// --- 5. Progress Tracking Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitProgressScreen(
    habit: Habit,
    completionCount: Int,
    onMarkComplete: () -> Unit,
    onResetFlow: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(habit.name) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = habit.color.copy(alpha = 0.3f)
                ),
                navigationIcon = {
                    IconButton(onClick = onResetFlow) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Setup")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Text("Total Completions", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)

            // Progress Display
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(habit.color, RoundedCornerShape(75.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$completionCount",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
            }

            Spacer(Modifier.height(48.dp))

            // Progress Button
            Button(
                onClick = onMarkComplete,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = habit.color)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Mark Complete")
                Spacer(Modifier.width(8.dp))
                Text("I Did My 2-Minute Action Today!", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))

            // Display the 2-Minute Rule action
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Your Minimum Action:", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Put on my walking shoes (for Walking)", // This should dynamically pull from details map
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Remember: Never miss twice. Focus on consistency over intensity!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHabitFlowApp() {
    MaterialTheme {
        HabitFlowApp()
    }
}
