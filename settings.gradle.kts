pluginManagement {
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/central") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { setUrl("https://jitpack.io") } // 添加这一行
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/central") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        google()
        mavenCentral()
    }
}


rootProject.name = "My Application"
include(":app")
 