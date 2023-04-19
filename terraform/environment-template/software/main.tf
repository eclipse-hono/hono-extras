module "software" {
  source = "../../software"

  project_id                          = data.terraform_remote_state.infrastructure.outputs.project_id
  cluster_name                        = data.terraform_remote_state.infrastructure.outputs.cluster_name
  mqtt_static_ip                      = data.terraform_remote_state.infrastructure.outputs.mqtt_static_ip
  sql_user                            = data.terraform_remote_state.infrastructure.outputs.sql_user
  sql_db_pw                           = data.terraform_remote_state.infrastructure.outputs.sql_db_pw
  sql_ip                              = data.terraform_remote_state.infrastructure.outputs.sql_ip
  sql_database                        = data.terraform_remote_state.infrastructure.outputs.sql_database
  service_name_communication          = data.terraform_remote_state.infrastructure.outputs.service_name_communication
  device_communication_static_ip_name = data.terraform_remote_state.infrastructure.outputs.device_communication_static_ip_name
  cloud_endpoints_key_file            = data.terraform_remote_state.infrastructure.outputs.cloud_endpoints_key_file
  oauth_app_name                      = local.oauth_app_name
  api_tls_key                         = local.api_tls_key
  api_tls_crt                         = local.api_tls_crt
  mqtt_tls_key                        = local.mqtt_tls_key
  mqtt_tls_crt                        = local.mqtt_tls_crt
  helm_package_repository             = local.helm_package_repository
  hono_chart_name                     = local.hono_chart_name
  device_communication_dns_name       = local.device_communication_dns_name
  oauth_client_id                     = local.oauth_client_id
  oauth_client_secret                 = local.oauth_client_secret
}
