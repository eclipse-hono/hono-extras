output "network_name" {
  value       = google_compute_network.vpc_network.name
  description = "name of the network"
}

output "subnetwork_name" {
  value       = google_compute_subnetwork.subnetwork.name
  description = "Name ouf the subnetwork"
}

output "service_networking" {
  value       = google_service_networking_connection.private_vpc_connection
  description = "Output of the service networking connection for sql instance private IP and vpc network"
}

output "network_id" {
  value       = google_compute_network.vpc_network.id
  description = "Output of the network id of the network that is created"
}

output "mqtt_static_ip" {
  value       = google_compute_address.mqtt_static_ip.address
  description = "Output of the mqtt static ip address"
}

output "device_communication_static_ip_name" {
  value       = google_compute_global_address.device_communication_static_ip.name
  description = "Name of the Static IP for External Ingress"
}

output "ip_ranges_services_name" {
  value = google_compute_subnetwork.subnetwork.secondary_ip_range.0.range_name
}

output "ip_ranges_pods_name" {
  value = google_compute_subnetwork.subnetwork.secondary_ip_range.1.range_name
}
