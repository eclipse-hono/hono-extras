# Creating Cloud Endpoints for Device Communication
resource "google_endpoints_service" "device_communication" {
  service_name   = "hono-device-apis-gce.endpoints.${var.project_id}.cloud.goog"
  project        = var.project_id
  openapi_config = templatefile("${path.module}/device-communication.yml", { project_id = var.project_id }) # path to the openapi config 
}
