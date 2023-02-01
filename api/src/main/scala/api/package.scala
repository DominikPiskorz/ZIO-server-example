import io.github.gaelrenoux.tranzactio.doobie.Database

package object api {
  type RunEnvironment = Routes with ApiSettings with Database
}
