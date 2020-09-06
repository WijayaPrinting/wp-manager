package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.content.Resources
import com.hendraanggrian.openpss.content.STYLESHEET_OPENPSS
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.ui.login.LoginPane
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import ktfx.launchApplication
import ktfx.layouts.scene
import ktfx.windows.icon
import org.apache.log4j.BasicConfigurator
import java.util.Properties
import java.util.ResourceBundle
import kotlin.system.exitProcess

class App : Application(), Resources {

    companion object {
        const val DURATION_SHORT = 3000L
        const val DURATION_LONG = 6000L

        @JvmStatic fun main(args: Array<String>) = launchApplication<App>(*args)

        fun exit() {
            Platform.exit() // exit JavaFX
            exitProcess(0) // exit Java
        }
    }

    override lateinit var resourceBundle: ResourceBundle
    override lateinit var dimenResources: Properties
    override lateinit var colorResources: Properties

    override fun init() {
        resourceBundle = PreferencesFile.language.toResourcesBundle()
        dimenResources = getProperties(R.dimen._dimen)
        colorResources = getProperties(R.color._color)
        if (DEBUG) {
            BasicConfigurator.configure()
        }
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_small)
        stage.isResizable = false
        stage.title = getString(R.string.openpss_login)
        stage.scene = scene {
            stylesheets += STYLESHEET_OPENPSS
            addChild(
                LoginPane(this@App).apply {
                    onSuccess = { employee ->
                        val loader = FXMLLoader(getResource(R.layout.controller_main), resourceBundle)
                        this@scene.run {
                            addChild(loader.pane)
                        }
                        val controller = loader.controller
                        controller.login = employee

                        stage.isResizable = true
                        stage.height = 800.0
                        stage.width = 480.0
                    }
                }
            )
        }
        stage.show()
    }
}