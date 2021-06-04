import io.github.remodstudios.datakenesis.asId
import io.github.remodstudios.datakenesis.scripts.ids
import io.github.remodstudios.datakenesis.templates.ItemModelTemplate

ids("lumidep") {
    entry("bruh", ItemModelTemplate) {
        parent = "hello".asId
    }
}