moved {
  from = kubernetes_namespace.hono
  to   = module.hono.kubernetes_namespace.hono
}

module "hono" {
  source                              = "../modules/hono"
  namespace                           = var.namespace
  cluster_name                        = var.cluster_name
  project_id                          = var.project_id
  mqtt_static_ip                      = var.mqtt_static_ip
  sql_user                            = var.sql_user
  sql_db_pw                           = var.sql_db_pw
  sql_database                        = var.sql_database
  sql_ip                              = var.sql_ip
  service_name_communication          = var.service_name_communication
  device_communication_static_ip_name = var.device_communication_static_ip_name
  helm_package_repository             = var.helm_package_repository
  hono_chart_name                     = var.hono_chart_name
  oauth_app_name                      = var.oauth_app_name
  device_communication_dns_name       = var.device_communication_dns_name
  api_tls_key                         = var.api_tls_key
  api_tls_crt                         = var.api_tls_crt
  mqtt_tls_key                        = var.mqtt_tls_key
  mqtt_tls_crt                        = var.mqtt_tls_crt
  cloud_endpoints_key_file            = var.cloud_endpoints_key_file
  mqtt_secret_name                    = var.mqtt_secret_name
  ingress_secret_name                 = var.ingress_secret_name
  oauth_client_id                     = var.oauth_client_id
  oauth_client_secret                 = var.oauth_client_secret
}


