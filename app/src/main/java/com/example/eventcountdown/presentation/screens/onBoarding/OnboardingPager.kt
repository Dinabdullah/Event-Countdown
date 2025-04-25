package com.example.eventcountdown.presentation.screens.onBoarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.eventcountdown.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingPager(onFinish: () -> Unit) {  // Removed navController and context
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val prefsHelper = remember { PreferencesHelper(context) }
    val onboardingPages = listOf(
        OnboardingPageData(
            image = R.drawable.onboarding_image1,
            description = stringResource(id = R.string.onboarding_desc1)
        ),
        OnboardingPageData(
            image = R.drawable.onboarding_image2,
            description = stringResource(id = R.string.onboarding_desc2)
        ),
        OnboardingPageData(
            image = R.drawable.onboarding_image3,
            description = stringResource(id = R.string.onboarding_desc3)
        )
    )

    HorizontalPager(
        count = onboardingPages.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        OnboardingPage(
            imageId = onboardingPages[page].image,
            description = onboardingPages[page].description,
            pagerState = pagerState,
            onNextClick = {
                scope.launch {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        prefsHelper.onboardingCompleted = true  // Fixed property name
                        onFinish()
                    }
                }
            },
            onSkipClick = {
                prefsHelper.onboardingCompleted = true  // Fixed property name
                onFinish()
            }
        )
    }
}





