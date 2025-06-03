pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "AppFrame10"
include(":app")
include(":zt-scroll")
include(":zt-broadcastreceiver")
include(":zt-br-send")
include(":zt-loadfiledirimage")
include(":zt-deviceid")
include(":zt-groupfragment")
include(":zt-downtosecondview")
include(":zt-java-lib")
include(":zt-pushevent")
include(":zt-allinstallpackaget")
include(":zt_downtome_view")
