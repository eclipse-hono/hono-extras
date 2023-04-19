output "gke_cluster_name" {
  value       = module.gke.gke_cluster_name
  description = "Name of the GKE Cluster"
}

output "mqtt_static_ip" {
  value       = module.networking.mqtt_static_ip
  description = "Output of the mqtt static ip address"
}

output "sql_db_pw" {
  value       = module.cloud_sql.sql_db_pw
  sensitive   = true
  description = "Output of the SQL user password"
}

output "sql_user" {
  value       = module.cloud_sql.sql_user
  description = "Output of the SQL user name"
}

output "sql_ip" {
  value       = module.cloud_sql.sql_ip
  description = "URL of the Postgres Database"
}

output "sql_database" {
  value       = module.cloud_sql.sql_database
  description = "Name of the Postgres Database"
}

output "gke_cluster_name_endpoint" {
  value       = module.gke.gke_cluster_name_endpoint
  description = "Endpoint of the GKE Cluster"
}

output "gke_cluster_ca_certificate" {
  value       = module.gke.gke_cluster_ca_certificate
  description = "CA-Certificate for the Cluster"
}

output "service_name_communication" {
  value       = module.cloud_endpoint.service_name_communication
  description = "Name of the Cloud Endpoint service for device communication"
}

output "device_communication_static_ip_name" {
  value       = module.networking.device_communication_static_ip_name
  description = "Name of the Static IP for External Ingress"
}

output "cloud_endpoints_key_file" {
  value       = module.google_iam.cloud_endpoints_key_file
  description = "Service Account Key File for Cloud Endpoints Service Account"
  sensitive   = true
}
