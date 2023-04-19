#Creating the vpc network
resource "google_compute_network" "vpc_network" {
  project                 = var.project_id
  name                    = "hono-network"
  auto_create_subnetworks = false
}

#Creating the subnetwork
resource "google_compute_subnetwork" "subnetwork" {
  project       = var.project_id
  region        = var.region
  network       = google_compute_network.vpc_network.id
  name          = "honosubnet-01"
  ip_cidr_range = var.ip_cidr_range
  secondary_ip_range = [
    {
      range_name    = "services"
      ip_cidr_range = var.secondary_ip_range_service
    },
    {
      range_name    = "pods"
      ip_cidr_range = var.secondary_ip_range_pods
    }
  ]

  private_ip_google_access = true
}

#Creating the Static IP address(external)
resource "google_compute_address" "mqtt_static_ip" {
  project      = var.project_id
  region       = var.region
  name         = "mqtt-static-ip"
  address_type = "EXTERNAL"
}

# Creating global static ip for Device Communication
resource "google_compute_global_address" "device_communication_static_ip" {
  project      = var.project_id
  name         = "device-communication-api"
  address_type = "EXTERNAL"
}

#Creating the Private IP address for Cloud SQL instance
resource "google_compute_global_address" "private_ip_address" {
  name          = "private-ip-address"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.vpc_network.id
}

#Creating the Private VPC connection for SQL Private IP and the VPC network
resource "google_service_networking_connection" "private_vpc_connection" {
  network                 = google_compute_network.vpc_network.id
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip_address.name]
}

