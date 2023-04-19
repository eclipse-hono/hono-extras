output "service_name_communication" {
  value       = google_endpoints_service.device_communication.service_name
  description = "Name of the Cloud Endpoint service for device communication"
}
