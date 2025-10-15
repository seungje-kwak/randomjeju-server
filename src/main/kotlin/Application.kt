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
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        module() // ✅ 아래 정의한 module() 함수 호출
    }.start(wait = true)
}

// ✅ Ktor가 application.conf에서 찾는 entry point 함수
fun Application.module() {
    install(ContentNegotiation) { json() }
    DB.init()

    routing {
        get("/random-location") {
            val dto = RandomLocationService.pickAndSave()
            call.respond(dto)
        }
        get("/") {
            call.respondText("✅ Random Jeju API server is running!")
        }
    }
}
