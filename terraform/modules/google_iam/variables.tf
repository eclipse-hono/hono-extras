variable "project_id" {
  type = string
}

variable "service_name_communication" {
  type        = string
  description = "Name of the Cloud Endpoint service for device communication"
}

variable "service_account_roles_gke_sa" {
  description = "Additional roles to be added to the service account."
  type        = list(string)
}