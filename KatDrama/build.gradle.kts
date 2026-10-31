dependencies {
}

version = 1

cloudstream {
    description = "KatDrama - Korean, Chinese, Japanese, Thai dramas with Hindi dubbed"
    authors = listOf("YourName")

    status = 1
    tvTypes = listOf("TvSeries", "Movie", "AsianDrama")
    iconUrl = "https://www.google.com/s2/favicons?domain=katdrama.org&sz=%size%"
}

android {
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}
