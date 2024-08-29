package com.kato.routes;

import com.kato.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val users = mutableListOf(
    User(1, "Andy", 25, "speed21@outlook.com"),
    User(2, "Milene", 24, "mileneescobar2907@gmail.com")
)

fun Route.userRouting() {
    route("/user") {
        get {
            if (users.isNotEmpty()) {
                call.respond(users)
            } else {
                call.respondText("No hay usuarios", status = HttpStatusCode.NotFound)
            }
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText("Id no encontrado", status = HttpStatusCode.BadRequest)
            val user = users.find { it.id == id.toInt() } ?: return@get call.respondText("Usuario con $id no encontrado", status = HttpStatusCode.NotFound)
            call.respond(user)
        }
        post {
            val user = call.receive<User>()
            users.add(user)
            call.respondText("Usuario creado correctamente", status = HttpStatusCode.Created)
        }
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText("Id no encontrado", status = HttpStatusCode.BadRequest)
            val existingUserIndex = users.indexOfFirst { it.id == id.toInt() }

            if (existingUserIndex == -1) {
                return@put call.respondText("Usuario con $id no encontrado", status = HttpStatusCode.NotFound)
            }

            val updatedUser = call.receive<User>()

            if (updatedUser.id != id.toInt()) {
                return@put call.respondText("El ID del usuario no coincide con el ID en la ruta", status = HttpStatusCode.BadRequest)
            }

            users[existingUserIndex] = updatedUser
            call.respondText("Usuario actualizado correctamente", status = HttpStatusCode.OK)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText("Id no encontrado", status = HttpStatusCode.BadRequest)
            if (users.removeIf { it.id == id.toInt() }) {
                call.respondText("Usuario eliminado correctamente", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("No encontrado", status = HttpStatusCode.NotFound)
            }
        }
    }
}