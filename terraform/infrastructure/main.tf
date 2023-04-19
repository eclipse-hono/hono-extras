resource "google_project_service" "project" {
  for_each = toset([
    "compute.googleapis.com",
    "container.googleapis.com",
    "pubsub.googleapis.com",
    "sqladmin.googleapis.com",
    "servicenetworking.googleapis.com",
    "iap.googleapis.com",
    "servicecontrol.googleapis.com"
  ])

  project = var.project_id
  service = each.key

  disable_on_destroy = false
}

module "networking" {
  source = "../modules/networking"

  project_id                 = var.project_id
  region                     = var.region
  ip_cidr_range              = var.ip_cidr_range
  secondary_ip_range_service = var.secondary_ip_range_services
  secondary_ip_range_pods    = var.secondary_ip_range_pods

  depends_on = [
    google_project_service.project
  ]
}

module "cloud_sql" {
  source = "../modules/cloud_sql"

  project_id                     = var.project_id
  region                         = var.region
  storage_size_gb                = var.storage_size_gb
  service_networking             = module.networking.service_networking
  network_id                     = module.networking.network_id
  sql_instance_name              = var.sql_instance_name
  sql_instance_version           = var.sql_instance_version
  sql_instance_machine_type      = var.sql_instance_machine_type
  sql_instance_disk_type         = var.sql_instance_disk_type
  sql_instance_deletion_policies  = var.sql_instance_deletion_policies
  sql_instance_activation_policy = var.sql_instance_activation_policy
  sql_public_ip_enable           = var.sql_instance_ipv4_enable
  sql_db_user_name               = var.sql_db_user_name
  sql_database_name              = var.sql_database_name
}

module "google_iam" {
  source                       = "../modules/google_iam"
  service_name_communication   = module.cloud_endpoint.service_name_communication
  project_id                   = var.project_id
  service_account_roles_gke_sa = var.service_account_roles_gke_sa
}

module "gke" {
  source = "../modules/gke"

  project_id                = var.project_id
  gke_cluster_name          = var.gke_cluster_name
  region                    = var.region
  network_name              = module.networking.network_name
  subnetwork_name           = module.networking.subnetwork_name
  gke_release_channel       = var.gke_release_channel
  ip_ranges_services        = module.networking.ip_ranges_services_name
  ip_ranges_pods            = module.networking.ip_ranges_pods_name
  gke_service_account_email = module.google_iam.gke_service_account_email
  gke_machine_type          = var.gke_machine_type
  gke_node_pool_name        = var.gke_node_pool_name
  node_locations            = var.node_locations
  node_pool_disk_type       = var.node_pool_disk_type
  node_pool_disk_size       = var.node_pool_disk_size

  depends_on = [
    google_project_service.project
  ]
}

module "pubsub" {
  source = "../modules/pubsub"

  project_id = var.project_id

  depends_on = [
    google_project_service.project
  ]
}

module "cloud_endpoint" {
  source     = "../modules/cloud_endpoint"
  project_id = var.project_id

  depends_on = [
    google_project_service.project
  ]
}
