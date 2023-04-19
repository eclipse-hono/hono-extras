output "sql_db_pw" {
  value       = google_sql_user.hono-sql-user.password
  description = "Output of the SQL user password"
  sensitive   = true
}

output "sql_user" {
  value       = google_sql_user.hono-sql-user.name
  description = "Output of the SQL user name"
}

output "sql_ip" {
  value       = google_sql_database_instance.hono_sql.ip_address[0].ip_address
  description = "URL of the Postgres Database"
}

output "sql_database" {
  value       = google_sql_database.hono_sql_db.name
  description = "Name of the Postgres Database"
}
