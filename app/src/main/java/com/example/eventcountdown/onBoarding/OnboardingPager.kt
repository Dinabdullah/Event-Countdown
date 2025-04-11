package com.example.eventcountdown.onBoarding

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.eventcountdown.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingPager(navController: NavController, context: Context, onFinish: () -> Unit) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val preferencesHelper = remember { PreferencesHelper(context) }
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
                        preferencesHelper.isOnboardingComplete = true
                        onFinish()
                    }
                }
            },
            onSkipClick = {
                preferencesHelper.isOnboardingComplete = true
                onFinish()
            }
        )
    }
}





