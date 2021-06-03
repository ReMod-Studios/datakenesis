package io.github.remodstudios.datakenesis.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.Convention
import org.gradle.api.tasks.TaskContainer

internal inline fun <reified T> Convention.getPlugin()
        = getPlugin(T::class.java)

internal inline fun <reified T: Task> TaskContainer.register(name: String, noinline conf: (T) -> Unit)
        = register(name, T::class.java, conf)


internal inline fun Project.tasks(conf: TaskContainer.() -> Unit)
        = with(tasks, conf)

internal inline fun Project.repositories(conf: RepositoryHandler.() -> Unit)
        = with(repositories, conf)