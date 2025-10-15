import com.sjcompany.db.DB
import com.sjcompany.service.RandomLocationService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        DB.init()

        routing {
            get("/random-location") {
                val dto = RandomLocationService.pickAndSave()
                call.respond(dto)
            }
        }
    }.start(wait = true)
}