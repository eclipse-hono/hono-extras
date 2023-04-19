variable "project_id" {
  type        = string
  description = "The project ID to deploy to"
}

variable "gke_cluster_name" {
  type        = string
  description = "Name of the GKE Cluster"
}

variable "region" {
  type        = string
  description = "The region to deploy to"
}

variable "network_name" {
  type        = string
  description = "name of the network"
}

variable "subnetwork_name" {
  type        = string
  description = "name of the subnetwork "
}

variable "gke_release_channel" {
  type        = string
  description = "Which Release Channel to use for the Cluster"
}

variable "ip_ranges_services" {
  type        = string
  description = "Secondary IP Ranges in Subnetwork for Services"
}

variable "ip_ranges_pods" {
  type        = string
  description = "Secondary IP Ranges in Subnetwork for Pods"
}

variable "gke_service_account_email" {
  type        = string
  description = "Email of the GKE Service Account"
}

variable "gke_machine_type" {
  type        = string
  description = "Machine Type for node_pools"
}

variable "gke_node_pool_name" {
  type        = string
  description = "The name of the Node Pool in the Hono Cluster"
}

variable "node_locations" {
  type        = list(string)
  description = "List of Strings for the Node Locations"
}

variable "node_pool_disk_type" {
  type        = string
  description = "Disk type of the Node Pool"
}

variable "node_pool_disk_size" {
  type        = number
  description = "Size of the Node Pool Disk"
}
