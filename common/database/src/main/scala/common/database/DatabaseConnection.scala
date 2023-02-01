package common.database

import io.github.gaelrenoux.tranzactio.doobie.Database
import javax.sql.DataSource
import zio.{RLayer, ZLayer, ZIO}

object DatabaseConnection {
  def run() = println("DB running")

  val dataSource: RLayer[DbConfig, DataSource] =
    ZLayer {
      for {
        conf <- ZIO.service[DbConfig]
      } yield {
        val dataSource = new com.zaxxer.hikari.HikariDataSource()
        dataSource.setDriverClassName(conf.driver)
        dataSource.setJdbcUrl(conf.url)
        dataSource.setUsername(conf.user)
        dataSource.setPassword(conf.password)

        dataSource
      }
    }

  val layer: RLayer[DbConfig, Database] =
    dataSource >>> Database.fromDatasource
}
