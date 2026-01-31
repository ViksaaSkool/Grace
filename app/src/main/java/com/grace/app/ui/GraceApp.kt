package com.grace.app.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.Glide
import com.grace.app.R
import com.grace.app.constants.Constants
import com.grace.app.model.MealPhoto
import com.grace.app.util.GracePhotoUtil
import com.grace.app.util.ShareUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

private object Routes {
    const val Splash = "splash"
    const val Home = "home"
    const val Loading = "loading"
    const val Photo = "photo"
    const val PhotoDetails = "photoDetails"
}

@Composable
fun GraceApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: MainViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                MainEvent.NavigateToPhoto -> navController.navigate(Routes.Photo) {
                    popUpTo(Routes.Loading) { inclusive = true }
                }

                MainEvent.NavigateToHome -> navController.navigate(Routes.Home) {
                    popUpTo(Routes.Home) { inclusive = true }
                }

                is MainEvent.ShowSnackbar -> scope.launch {
                    snackbarHostState.showSnackbar(
                        message = ""
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.Splash) {
                SplashScreen {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            }
            composable(Routes.Home) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateLoading = { navController.navigate(Routes.Loading) }
                )
            }
            composable(Routes.Loading) {
                LoadingScreen(viewModel = viewModel)
            }
            composable(Routes.Photo) {
                val mealPhoto = uiState.mealPhoto
                if (mealPhoto != null) {
                    PhotoScreen(
                        mealPhoto = mealPhoto,
                        onLeftAction = {
                            when (mealPhoto.leftButtonText) {
                                R.string.done_text, R.string.no_text -> navController.navigate(
                                    Routes.Home
                                ) {
                                    popUpTo(Routes.Home) { inclusive = true }
                                }

                                R.string.feel_wraith_text -> Unit
                            }
                        },
                        onRightAction = {
                            when (mealPhoto.rightButtonText) {
                                R.string.yes_text -> {
                                    viewModel.requestBlessing()
                                    navController.navigate(Routes.Loading)
                                }

                                R.string.share_text -> {
                                    mealPhoto.photoUri?.let {
                                        ShareUtil.shareBlessedPhoto(
                                            context,
                                            it
                                        )
                                    }
                                }

                                R.string.another_try_text -> navController.navigate(Routes.Home) {
                                    popUpTo(Routes.Home) { inclusive = true }
                                }
                            }
                        },
                        onOpenDetails = {
                            navController.navigate(Routes.PhotoDetails)
                        }
                    )
                }
            }
            composable(Routes.PhotoDetails) {
                PhotoDetailsScreen(
                    photoUri = uiState.mealPhoto?.photoUri,
                    onClose = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    var showLogo by remember { mutableStateOf(false) }
    val logoAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (showLogo) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(Constants.ANIMATION_LOGO_DURATION)
    )
    val logoOffset by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (showLogo) 0f else 300f,
        animationSpec = androidx.compose.animation.core.tween(Constants.ANIMATION_LOGO_DURATION)
    )

    LaunchedEffect(Unit) {
        val animationDuration =
            context.resources.getInteger(R.integer.frame_duration_could_sun_animation_0) +
                    context.resources.getInteger(R.integer.frame_duration_could_sun_animation) * 9
        delay(animationDuration.toLong())
        showLogo = true
        delay(Constants.LOADING_ANIMATION_DURATION.toLong())
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF03A9F4))
    ) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    setBackgroundResource(R.drawable.splash_clouds_sun_animation)
                    (background as? android.graphics.drawable.AnimationDrawable)?.apply {
                        isOneShot = true
                        start()
                    }
                    scaleType = ImageView.ScaleType.FIT_XY
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
        Image(
            painter = painterResource(id = R.drawable.grace_main),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(logoAlpha)
                .padding(bottom = 120.dp)
                .offset(y = logoOffset.dp)
        )
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateLoading: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val cameraPhotoPath = remember { mutableStateOf<String?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val path = cameraPhotoPath.value
            if (!path.isNullOrBlank()) {
                viewModel.setPhotoUri(path)
                viewModel.setLoadingMode(LoadingMode.CHECK_MEAL)
                onNavigateLoading()
            }
        }
    }

    val pickGalleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = GracePhotoUtil.getSelectedPhotoPath(context, uri)
            if (path.isNotBlank()) {
                viewModel.setPhotoUri(path)
                viewModel.setLoadingMode(LoadingMode.CHECK_MEAL)
                onNavigateLoading()
            }
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera(
                context,
                cameraPhotoUri,
                cameraPhotoPath
            ) { takePictureLauncher.launch(it) }
        }
    }

    val requestGalleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pickGalleryLauncher.launch("image/*")
    }

    LaunchedEffect(Unit) {
        viewModel.ensureTncState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF03A9F4))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            HomeCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.take_photo_of_meal,
                textRes = R.string.capture_meal_text
            ) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    launchCamera(
                        context,
                        cameraPhotoUri,
                        cameraPhotoPath
                    ) { takePictureLauncher.launch(it) }
                } else {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
            HomeCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.upload_meal,
                textRes = R.string.from_file_meal_text
            ) {
                val permission = if (android.os.Build.VERSION.SDK_INT >= 33) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                if (ContextCompat.checkSelfPermission(context, permission)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    pickGalleryLauncher.launch("image/*")
                } else {
                    requestGalleryPermissionLauncher.launch(permission)
                }
            }
        }

        if (uiState.showTncDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissTnc() },
                title = { Text(text = stringResource(id = R.string.disclaimer_title)) },
                text = { Text(text = stringResource(id = R.string.i_agree_text)) },
                confirmButton = {
                    Button(onClick = { viewModel.acceptTnc() }) {
                        Text(text = stringResource(id = R.string.enter_app_text))
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    textRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF03A9F4))
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentSize(Alignment.Center)
            )
            Text(
                text = stringResource(id = textRes),
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun LoadingScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messageRes = uiState.loadingMode?.messageRes ?: R.string.let_me_see_text

    LaunchedEffect(uiState.loadingMode) {
        viewModel.startProcessingIfNeeded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF03A9F4))
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.loading_god),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = painterResource(id = R.drawable.loading_god_no_circles),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = messageRes),
                color = Color.White,
                fontSize = 22.sp
            )
            LoadingDots()
        }
    }
}

@Composable
private fun LoadingDots() {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition()
    val scale by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(300),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        )
    )
    Row(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .scale(scale)
                    .background(Color(0xFFFF4081), shape = RoundedCornerShape(50))
            )
        }
    }
}

@Composable
fun PhotoScreen(
    mealPhoto: MealPhoto,
    onLeftAction: () -> Unit,
    onRightAction: () -> Unit,
    onOpenDetails: () -> Unit
) {
    var feelingWraith by remember { mutableStateOf(false) }
    val transition = androidx.compose.animation.core.rememberInfiniteTransition()
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(Constants.LOADING_ANIMATION_DURATION),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        if (mealPhoto.photoUri != null && mealPhoto.rightButtonText != R.string.another_try_text) {
                            onOpenDetails()
                        }
                    })
                }
        ) {
            if (mealPhoto.photoUri == null) {
                Image(
                    painter = painterResource(id = R.drawable.error_god),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (feelingWraith) pulse else 1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.error_god_top),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    },
                    update = { imageView ->
                        Glide.with(imageView)
                            .load(File(mealPhoto.photoUri))
                            .into(imageView)
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                if (mealPhoto.rightButtonText != R.string.another_try_text) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color(0xFFFF4081).copy(alpha = 0.3f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.tap_for_full_text),
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(Color.Black)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = mealPhoto.title),
                    color = Color(0xFFFF4081),
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = mealPhoto.subTitle),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (mealPhoto.leftButtonText == R.string.feel_wraith_text) {
                            feelingWraith = !feelingWraith
                        } else {
                            onLeftAction()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = stringResource(id = mealPhoto.leftButtonText),
                        color = Color(0xFFFF4081)
                    )
                }
                Button(
                    onClick = onRightAction,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                ) {
                    Text(
                        text = stringResource(id = mealPhoto.rightButtonText),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoDetailsScreen(photoUri: String?, onClose: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += offsetChange
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .transformable(state = transformState)
    ) {
        if (photoUri != null) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                },
                update = { imageView ->
                    Glide.with(imageView)
                        .load(File(photoUri))
                        .into(imageView)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        }
        Text(
            text = stringResource(id = R.string.close_text),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clickable(onClick = onClose)
        )
    }
}

private fun launchCamera(
    context: Context,
    cameraPhotoUri: androidx.compose.runtime.MutableState<Uri?>,
    cameraPhotoPath: androidx.compose.runtime.MutableState<String?>,
    launch: (Uri) -> Unit
) {
    val file = GracePhotoUtil.getOutputMediaFile(context, Constants.GRACE_PHOTO)
    if (file != null) {
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        cameraPhotoUri.value = photoUri
        cameraPhotoPath.value = file.absolutePath
        launch(photoUri)
    }
}
