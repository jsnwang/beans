package com.moo.beans.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Persistent banner ad. Pinned above the nav bar in [com.moo.beans.BeansApp]. */
@Composable
expect fun AdBanner(modifier: Modifier = Modifier)
