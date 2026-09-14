package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onReturnToGarage: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Box(
            modifier = Modifier
                .width(380.dp)
                .background(Color(0xFF09090B), RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "GAME PAUSED",
                    color = Color(0xFFF59E0B),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "TRUCK SIMULATOR KASHMIR • HIMALAYAN HIGHWAY",
                    color = Color(0xFFA1A1AA),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )

                // Controls Quick Reference
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF18181B), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "DRIVING CONTROLS:", color = Color(0xFFFDE047), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Engine Ignition", color = Color(0xFFA1A1AA), fontSize = 10.sp)
                            Text("START / STOP Key", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Throttle / Drive", color = Color(0xFFA1A1AA), fontSize = 10.sp)
                            Text("GAS Pedal", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Braking & Stopping", color = Color(0xFFA1A1AA), fontSize = 10.sp)
                            Text("BRAKE Pedal", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Steering & Mountain Sway", color = Color(0xFFA1A1AA), fontSize = 10.sp)
                            Text("Steering Wheel / Arrows", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Musical Pressure Horn", color = Color(0xFFA1A1AA), fontSize = 10.sp)
                            Text("HORN Button (Basuri)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Action Buttons
                Button(
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("RESUME DRIVING", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Restart Mission from Start", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onReturnToGarage,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B))
                ) {
                    Text("Return to Truck Garage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
