output "project_id" {
  value       = local.project_id
  description = "ID of the Project"
}

output "cluster_name" {
  value       = module.infrastructure.gke_cluster_name
  description = "Name of the GKE Cluster"
}

output "region" {
  value       = local.region
  description = "Region in which the Cluster will be deployed"
}

output "zone" {
  value       = local.zone
  description = "Zone in which the Cluster will be deployed"
}

output "mqtt_static_ip" {
  value       = module.infrastructure.mqtt_static_ip
  description = "Static IP for the MQTT Workload"
}

output "sql_db_pw" {
  value       = module.infrastructure.sql_db_pw
  sensitive   = true
  description = "SQL Database User Password"
}

output "sql_user" {
  value       = module.infrastructure.sql_user
  description = "SQL Database Username"
}

output "sql_ip" {
  value       = module.infrastructure.sql_ip
  description = "IP of the SQL Database"
}

output "sql_database" {
  value       = module.infrastructure.sql_database
  description = "Name of the SQL Database"
}

output "service_name_communication" {
  value       = module.infrastructure.service_name_communication
  description = "Name of the Could Endpoints Service Device Registry"
}

output "device_communication_static_ip_name" {
  value       = module.infrastructure.device_communication_static_ip_name
  description = "Name of the Static IP for External Ingress"
}

output "cloud_endpoints_key_file" {
  value       = module.infrastructure.cloud_endpoints_key_file
  description = "Service Account Key File for Cloud Endpoints Service Account"
  sensitive   = true
}
