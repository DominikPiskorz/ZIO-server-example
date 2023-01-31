import io.github.gaelrenoux.tranzactio.doobie.Database
import org.http4s.HttpRoutes
import zio.RIO

package object api {
  type RunEnvironment = Routes with ApiSettings with Database
}
