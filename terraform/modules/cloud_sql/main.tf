# Creating the Postgres SQL database instance with a private ip
resource "google_sql_database_instance" "hono_sql" {
  project          = var.project_id
  region           = var.region
  name             = var.sql_instance_name
  database_version = var.sql_instance_version

  deletion_protection = var.sql_instance_deletion_policies

  depends_on = [var.service_networking]

  settings {
    tier              = var.sql_instance_machine_type
    disk_size         = var.storage_size_gb
    disk_type         = var.sql_instance_disk_type
    activation_policy = var.sql_instance_activation_policy
    ip_configuration {
      # Disable Public IP
      ipv4_enabled = var.sql_public_ip_enable
      # Create private in the networking with the given
      private_network = var.network_id
    }
  }
}

resource "random_password" "password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

resource "google_sql_user" "hono-sql-user" {
  name     = var.sql_db_user_name
  instance = google_sql_database_instance.hono_sql.id
  password = random_password.password.result
}

resource "google_sql_database" "hono_sql_db" {
  name     = var.sql_database_name
  instance = google_sql_database_instance.hono_sql.id
}


