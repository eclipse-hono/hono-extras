output "gke_cluster_name" {
  value       = google_container_cluster.hono_cluster.name
  description = "Name of the GKE Cluster"
}

output "gke_cluster_name_endpoint" {
  value       = google_container_cluster.hono_cluster.endpoint
  description = "Endpoint of the GKE Cluster"
}

output "gke_cluster_ca_certificate" {
  value       = google_container_cluster.hono_cluster.master_auth
  description = "CA-Certificate for the Cluster"
}
