variable "project_id" {
  type        = string
  description = "The project ID to deploy to"
}

variable "region" {
  type        = string
  description = "The region to deploy to"
}

variable "ip_cidr_range" {
  type        = string
  description = "The range of internal addresses that are owned by this subnetwork. Provide this property when you create the subnetwork.Ranges must be unique and non-overlapping within a network. Only IPv4 is supported."
}

variable "secondary_ip_range_service" {
  type        = string
  description = "Secondary IP Range for Services"
}

variable "secondary_ip_range_pods" {
  type        = string
  description = "Secondary IP Range for Pods"
}
