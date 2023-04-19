locals {
  # creating the configuration to connect to the SQL Database
  db_connection_config = {
    # build the URL for the connection
    url         = "jdbc:postgresql://${var.sql_ip}:5432/${var.sql_database}"
    driverClass = "org.postgresql.Driver"
    username    = var.sql_user
    password    = var.sql_db_pw
  }

  # creation of database block to use in the resource call
  database_block = {
    jdbc = {
      adapter    = local.db_connection_config
      management = local.db_connection_config
    }
  }

  values = [jsonencode(
    {
      googleProjectId = var.project_id
      adapters = {
        mqtt = {
          svc = {
            loadBalancerIP = var.mqtt_static_ip # sets a static IP loadbalancerIP for mqtt
          }
          tlsKeysSecret = var.mqtt_secret_name
        }
      }
      deviceRegistryExample = {
        # sets database connection config
        jdbcBasedDeviceRegistry = {
          tenant   = local.database_block
          registry = local.database_block
        }
      }
      deviceCommunication = {
        app = {
          name = var.oauth_app_name
        }
        api = {
          database = { # database connection for device Communication
            name     = var.sql_database
            host     = var.sql_ip
            port     = 5432
            username = var.sql_user
            password = var.sql_db_pw
          }
        }
      }
      cloudEndpoints = {
        esp = {
          serviceName = var.service_name_communication
        }
      }
      externalIngress = {
        ingressTlsSecret  = var.ingress_secret_name
        staticIpName      = var.device_communication_static_ip_name
        host              = var.device_communication_dns_name
      }
      managementUi = {
        googleClientId = var.oauth_client_id
      }
    }
  )]
}

resource "kubernetes_namespace" "hono" {
  metadata {
    name = var.namespace
  }
}

resource "kubernetes_secret" "ingress_secret_tls" {
  metadata {
    name      = var.ingress_secret_name
    namespace = kubernetes_namespace.hono.metadata[0].name
  }
  data = {
    "tls.crt" = var.api_tls_crt
    "tls.key" = var.api_tls_key
  }
}

resource "kubernetes_secret" "esp-ssl" {
  metadata {
    name      = "esp-ssl"
    namespace = kubernetes_namespace.hono.metadata[0].name
  }
  data = {
    "server.crt" = var.api_tls_crt
    "server.key" = var.api_tls_key
  }
}

resource "kubernetes_secret" "mqtt_secret" {
  metadata {
    name      = var.mqtt_secret_name
    namespace = kubernetes_namespace.hono.metadata[0].name
  }
  data = {
    "tls.crt" = var.mqtt_tls_crt
    "tls.key" = var.mqtt_tls_key
  }
}

resource "kubernetes_secret" "cloud_endpoints_key_file" {
  metadata {
    name      = "service-account-creds"
    namespace = kubernetes_namespace.hono.metadata[0].name
  }
  binary_data = {
    "hono-cloud-endpoint-manager.json" = var.cloud_endpoints_key_file
  }
}

resource "kubernetes_secret" "iap_client_secret" {
  metadata {
    name = "iap-client-secret"
    namespace = kubernetes_namespace.hono.metadata[0].name
  }
  data = {
    "client_id"     = var.oauth_client_id
    "client_secret" = var.oauth_client_secret
  }
}


resource "helm_release" "hono" {
  name             = "eclipse-hono"
  repository       = var.helm_package_repository # Repository of the hono package
  chart            = var.hono_chart_name         # name of the Chart in the Repository         
  namespace        = kubernetes_namespace.hono.metadata[0].name
  create_namespace = false
  timeout          = 600

  # using json to set values in the helm chart
  values = local.values
}
