package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.EasyTaskViewModel
import com.example.ui.viewmodel.OtpDialogState

@Composable
fun AuthScreen(
    viewModel: EasyTaskViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val otpState by viewModel.otpState.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Register, 1: Login

    // Register form fields
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("ETADMIN") } // default referral code from inviter
    var otpDeliveryMethod by remember { mutableStateOf("WHATSAPP") } // "WHATSAPP" or "SMS"

    // Login form fields
    var loginPhone by remember { mutableStateOf("01811223344") }
    var loginPin by remember { mutableStateOf("1234") }

    val scrollState = rememberScrollState()

    // Flag whether user is in the mandatory Step 2 (OTP verification)
    val isOtpStepActive = otpState.isOpen && otpState.targetPhone.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // App Logo & Branding
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(NavyDark),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "EASY TASK Logo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "EASY TASK",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = NavyDark,
            letterSpacing = 1.sp
        )
        Text(
            text = "bKash & Nagad 4% Commission Earning Platform",
            fontSize = 12.sp,
            color = SlateTextSecondary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Tab Selector (Hidden during active OTP verification to maintain focus)
        if (!isOtpStepActive) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = RoyalBlue,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = RoyalBlue
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Register (New)",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_register")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Login",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_login")
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (selectedTab == 0) {
            // ==========================================
            // REGISTRATION FLOW (MANDATORY OTP STEP)
            // ==========================================
            if (isOtpStepActive) {
                // STEP 2: MANDATORY OTP VERIFICATION
                OtpVerificationStepCard(
                    otpState = otpState,
                    onOtpChange = { viewModel.setEnteredOtp(it) },
                    onAutoFill = { viewModel.autoFillOtp() },
                    onVerify = {
                        viewModel.verifyOtp { success, _ ->
                            if (success) {
                                Toast.makeText(context, "Account verified! Welcome to EASY TASK.", Toast.LENGTH_LONG).show()
                                onAuthSuccess()
                            }
                        }
                    },
                    onResend = { viewModel.resendOtp() },
                    onSwitchChannel = { newMethod ->
                        viewModel.switchOtpChannel(newMethod)
                    },
                    onBack = {
                        viewModel.cancelRegistration()
                    }
                )
            } else {
                // STEP 1: REGISTRATION FORM + OTP DELIVERY CHANNEL SELECTION
                RegistrationFormCard(
                    name = name,
                    onNameChange = { name = it },
                    phone = phone,
                    onPhoneChange = {
                        phone = it
                        if (whatsapp.isBlank() || whatsapp == phone.dropLast(1)) {
                            whatsapp = it
                        }
                    },
                    whatsapp = whatsapp,
                    onWhatsappChange = { whatsapp = it },
                    pin = pin,
                    onPinChange = { if (it.length <= 6) pin = it },
                    referralCode = referralCode,
                    onReferralCodeChange = { referralCode = it },
                    otpDeliveryMethod = otpDeliveryMethod,
                    onOtpDeliveryMethodChange = { otpDeliveryMethod = it },
                    onSubmit = {
                        viewModel.requestOtpForRegistration(
                            name = name.trim(),
                            phone = phone.trim(),
                            whatsapp = if (whatsapp.isNotBlank()) whatsapp.trim() else phone.trim(),
                            pin = pin.trim(),
                            referralCode = referralCode.trim().ifBlank { null },
                            method = otpDeliveryMethod
                        )
                    }
                )
            }
        } else {
            // ==========================================
            // LOGIN FLOW
            // ==========================================
            LoginFormCard(
                loginPhone = loginPhone,
                onLoginPhoneChange = { loginPhone = it },
                loginPin = loginPin,
                onLoginPinChange = { loginPin = it },
                onLogin = {
                    viewModel.login(loginPhone, loginPin) {
                        onAuthSuccess()
                    }
                },
                onQuickLogin = { p, pinCode, isAdmin ->
                    loginPhone = p
                    loginPin = pinCode
                    viewModel.login(p, pinCode) {
                        if (isAdmin) {
                            viewModel.toggleAdminMode(true)
                        }
                        onAuthSuccess()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        TelegramSupportCard()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// REGISTRATION FORM CARD (STEP 1)
// ==========================================
@Composable
fun RegistrationFormCard(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    whatsapp: String,
    onWhatsappChange: (String) -> Unit,
    pin: String,
    onPinChange: (String) -> Unit,
    referralCode: String,
    onReferralCodeChange: (String) -> Unit,
    otpDeliveryMethod: String,
    onOtpDeliveryMethodChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Step Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(RoyalBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Step 1: Account Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("+৳100 Bonus", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Complete 5,000৳ tasks in 2 days to win 100৳ cash bonus!",
                fontSize = 12.sp,
                color = EmeraldGreen,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Full Name Input
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RoyalBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Phone Number Input
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Mobile Phone Number (01XXXXXXXXX)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalBlue) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_phone_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // WhatsApp Number Input
            OutlinedTextField(
                value = whatsapp,
                onValueChange = onWhatsappChange,
                label = { Text("WhatsApp Number") },
                leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = Color(0xFF16A34A)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_whatsapp_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // PIN Input
            OutlinedTextField(
                value = pin,
                onValueChange = onPinChange,
                label = { Text("4-6 Digit Security PIN") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalBlue) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_pin_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Referral Code Input
            OutlinedTextField(
                value = referralCode,
                onValueChange = onReferralCodeChange,
                label = { Text("Referral Link / Code (e.g. ETADMIN)") },
                leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFD97706)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_referral_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // OTP Delivery Method Selection (Mandatory Choice)
            Text(
                text = "Choose OTP Delivery Method (Mandatory Step):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "We will send a 6-digit security OTP to verify your account.",
                fontSize = 11.sp,
                color = SlateTextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp Delivery Option
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOtpDeliveryMethodChange("WHATSAPP") }
                        .testTag("select_whatsapp_otp"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (otpDeliveryMethod == "WHATSAPP") Color(0xFFDCFCE7) else Color(0xFFF8FAFC)
                    ),
                    border = BorderStroke(
                        if (otpDeliveryMethod == "WHATSAPP") 2.dp else 1.dp,
                        if (otpDeliveryMethod == "WHATSAPP") Color(0xFF16A34A) else SlateBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (otpDeliveryMethod == "WHATSAPP") Color(0xFF16A34A) else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Message,
                                contentDescription = "WhatsApp",
                                tint = if (otpDeliveryMethod == "WHATSAPP") Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "WhatsApp",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (otpDeliveryMethod == "WHATSAPP") Color(0xFF16A34A) else SlateTextPrimary
                        )
                        Text(
                            text = "Instant & Free",
                            fontSize = 10.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                // SIM Card / SMS Delivery Option
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOtpDeliveryMethodChange("SMS") }
                        .testTag("select_sms_otp"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (otpDeliveryMethod == "SMS") Color(0xFFDBEAFE) else Color(0xFFF8FAFC)
                    ),
                    border = BorderStroke(
                        if (otpDeliveryMethod == "SMS") 2.dp else 1.dp,
                        if (otpDeliveryMethod == "SMS") RoyalBlue else SlateBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (otpDeliveryMethod == "SMS") RoyalBlue else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SimCard,
                                contentDescription = "SIM SMS",
                                tint = if (otpDeliveryMethod == "SMS") Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "SIM SMS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (otpDeliveryMethod == "SMS") RoyalBlue else SlateTextPrimary
                        )
                        Text(
                            text = "Standard Cellular",
                            fontSize = 10.sp,
                            color = SlateTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val isFormValid = name.isNotBlank() && phone.length >= 10 && pin.length >= 4

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("send_otp_register_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(14.dp),
                enabled = isFormValid
            ) {
                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Proceed to OTP Verification", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// MANDATORY OTP VERIFICATION STEP (STEP 2)
// Matches Mockup Design with 6-Digit Boxes
// ==========================================
@Composable
fun OtpVerificationStepCard(
    otpState: OtpDialogState,
    onOtpChange: (String) -> Unit,
    onAutoFill: () -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onSwitchChannel: (String) -> Unit,
    onBack: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SlateTextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 2: Mandatory OTP Verification",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue
                    )
                    Text(
                        text = "Account activation required",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Lock / Channel Graphic
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (otpState.method == "WHATSAPP") Color(0xFFDCFCE7) else Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (otpState.method == "WHATSAPP") Icons.Default.Message else Icons.Default.Smartphone,
                    contentDescription = "Security Channel",
                    tint = if (otpState.method == "WHATSAPP") Color(0xFF16A34A) else RoyalBlue,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Enter 6-Digit Security Code",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "We sent an activation code via ${if (otpState.method == "WHATSAPP") "WhatsApp" else "SIM SMS"} to",
                fontSize = 12.sp,
                color = SlateTextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = otpState.targetPhone,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (otpState.method == "WHATSAPP") Color(0xFF16A34A) else RoyalBlue,
                modifier = Modifier.padding(top = 2.dp)
            )

            // SIMULATED INCOMING NOTIFICATION BANNER (Matches mockup delivery preview)
            if (otpState.simulatedNotification != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (otpState.method == "WHATSAPP") Color(0xFF16A34A) else RoyalBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (otpState.method == "WHATSAPP") "WhatsApp • EASY TASK" else "SMS • EASY TASK Security",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = "Code: ${otpState.generatedOtp} (Valid 2 mins)",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }

                        OutlinedButton(
                            onClick = onAutoFill,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("autofill_otp_button")
                        ) {
                            Text("Auto-Fill", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalBlue)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6-DIGIT OTP BOX UI (Matches Mockups with 6 individual rounded boxes)
            OtpDigitsBoxes(
                code = otpState.enteredOtp,
                onCodeChange = onOtpChange,
                isError = otpState.errorMessage != null
            )

            // Error Feedback with subtle animation
            AnimatedVisibility(visible = otpState.errorMessage != null) {
                Text(
                    text = otpState.errorMessage ?: "",
                    color = Color(0xFFDC2626),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Countdown Timer and Resend Channel Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (otpState.secondsLeft > 0) {
                    Text(
                        text = "Resend code in ${otpState.secondsLeft}s",
                        fontSize = 12.sp,
                        color = SlateTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onResend() }
                            .testTag("resend_otp_text")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Resend OTP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    }
                }

                // Switch Delivery Channel Option (WhatsApp <-> SIM)
                val alternateMethod = if (otpState.method == "WHATSAPP") "SMS" else "WHATSAPP"
                val alternateLabel = if (otpState.method == "WHATSAPP") "Send via SIM SMS" else "Send via WhatsApp"

                Text(
                    text = alternateLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (alternateMethod == "WHATSAPP") Color(0xFF16A34A) else RoyalBlue,
                    modifier = Modifier
                        .clickable { onSwitchChannel(alternateMethod) }
                        .testTag("switch_otp_channel")
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Action Button with subtle loading & success state
            Button(
                onClick = onVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("verify_otp_confirm_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(14.dp),
                enabled = otpState.enteredOtp.length >= 4 && !otpState.isSubmitting
            ) {
                if (otpState.isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verifying & Registering...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify & Complete Registration", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 6-DIGIT OTP BOXES COMPONENT
// ==========================================
@Composable
fun OtpDigitsBoxes(
    code: String,
    onCodeChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Hidden input field that receives the keypresses
        BasicTextField(
            value = code,
            onValueChange = {
                val filtered = it.filter { ch -> ch.isDigit() }
                if (filtered.length <= 6) {
                    onCodeChange(filtered)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("otp_input_field")
        )

        // 6 Visual Digit Boxes
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                val char = if (i < code.length) code[i].toString() else ""
                val isFocused = i == code.length

                Box(
                    modifier = Modifier
                        .size(46.dp, 52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (char.isNotEmpty()) Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = when {
                                isError -> Color(0xFFDC2626)
                                isFocused -> RoyalBlue
                                char.isNotEmpty() -> RoyalBlue.copy(alpha = 0.6f)
                                else -> SlateBorder
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SlateTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// ==========================================
// LOGIN FORM CARD
// ==========================================
@Composable
fun LoginFormCard(
    loginPhone: String,
    onLoginPhoneChange: (String) -> Unit,
    loginPin: String,
    onLoginPinChange: (String) -> Unit,
    onLogin: () -> Unit,
    onQuickLogin: (phone: String, pin: String, isAdmin: Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Login to Your Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "Access your 4% commission wallet & tasks",
                fontSize = 12.sp,
                color = SlateTextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            OutlinedTextField(
                value = loginPhone,
                onValueChange = onLoginPhoneChange,
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalBlue) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_phone_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = loginPin,
                onValueChange = onLoginPinChange,
                label = { Text("Security PIN") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalBlue) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_pin_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(14.dp),
                enabled = loginPhone.isNotBlank() && loginPin.isNotBlank()
            ) {
                Text("Login to Dashboard", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Demo Profiles
            Text(
                text = "Quick Demo Profiles (One-Tap Access):",
                fontSize = 12.sp,
                color = SlateTextSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onQuickLogin("01811223344", "1234", false)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_user_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Demo User", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalBlue)
                }

                OutlinedButton(
                    onClick = {
                        onQuickLogin("01700000000", "1234", true)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_admin_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Admin Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                }
            }
        }
    }
}
