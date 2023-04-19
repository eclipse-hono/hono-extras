variable "project_id" {
  type        = string
  description = "The project ID to deploy to"
}

variable "region" {
  type        = string
  description = "The region for the Cloud SQL Instance"
}

variable "storage_size_gb" {
  type        = number
  description = "The storage size in GB for the Cloud SQL Instance (100 or 200)"
}

variable "service_networking" {
  description = "Reference to service_networking connection from VPC_network to SQL Instance"
}

variable "network_id" {
  type        = string
  description = "Reference to VPC Network ID only"
}

variable "sql_instance_name" {
  type        = string
  description = "Name of the SQL Instance"
}
variable "sql_instance_version" {
  type        = string
  description = "Database Version"
}

variable "sql_instance_machine_type" {
  type        = string
  description = "Machine Type of the SQL Instance"
}

variable "sql_instance_disk_type" {
  type        = string
  description = "Disk Type of the SQL Instance"
}

variable "sql_instance_deletion_policies" {
  type        = bool
  description = "Used to block Terraform from deleting a SQL Instance. Defaults to false."
}

variable "sql_instance_activation_policy" {
  type        = string
  description = "This specifies when the instance should be active. Can be either ALWAYS, NEVER or ON_DEMAND."
}

variable "sql_public_ip_enable" {
  type        = bool
  description = "Whether this Cloud SQL instance should be assigned a public IPV4 address. At least ipv4_enabled must be enabled or a private_network must be configured."
}

variable "sql_db_user_name" {
  type        = string
  description = "The name of the user. Changing this forces a new resource to be created."
}

variable "sql_database_name" {
  type        = string
  description = "The name of the database in the Cloud SQL instance. This does not include the project ID or instance name."
}
