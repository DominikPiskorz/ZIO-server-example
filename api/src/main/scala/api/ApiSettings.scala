package api

import common.database.DbConfig

final case class ApiSettings(
    host: String,
    port: Int,
    db: DbConfig
)
