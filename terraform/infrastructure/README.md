## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_google"></a> [google](#provider\_google) | n/a |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_cloud_endpoint"></a> [cloud\_endpoint](#module\_cloud\_endpoint) | ../modules/cloud_endpoint | n/a |
| <a name="module_cloud_sql"></a> [cloud\_sql](#module\_cloud\_sql) | ../modules/cloud_sql | n/a |
| <a name="module_gke"></a> [gke](#module\_gke) | ../modules/gke | n/a |
| <a name="module_google_iam"></a> [google\_iam](#module\_google\_iam) | ../modules/google_iam | n/a |
| <a name="module_networking"></a> [networking](#module\_networking) | ../modules/networking | n/a |
| <a name="module_pubsub"></a> [pubsub](#module\_pubsub) | ../modules/pubsub | n/a |

## Resources

| Name | Type |
|------|------|
| [google_project_service.project](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/project_service) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_gke_cluster_name"></a> [gke\_cluster\_name](#input\_gke\_cluster\_name) | Name of the GKE Cluster | `string` | `"hono-cluster"` | no |
| <a name="input_gke_machine_type"></a> [gke\_machine\_type](#input\_gke\_machine\_type) | Machine Type for node\_pools | `string` | `"c2-standard-8"` | no |
| <a name="input_gke_node_pool_name"></a> [gke\_node\_pool\_name](#input\_gke\_node\_pool\_name) | The name of the Node Pool in the Hono Cluster | `string` | `"standard-node-pool"` | no |
| <a name="input_gke_release_channel"></a> [gke\_release\_channel](#input\_gke\_release\_channel) | Which Release Channel to use for the Cluster | `string` | `"STABLE"` | no |
| <a name="input_ip_cidr_range"></a> [ip\_cidr\_range](#input\_ip\_cidr\_range) | The range of internal addresses that are owned by this subnetwork. Provide this property when you create the subnetwork.Ranges must be unique and non-overlapping within a network. Only IPv4 is supported. | `string` | `"10.10.1.0/24"` | no |
| <a name="input_node_locations"></a> [node\_locations](#input\_node\_locations) | List of Strings for the Node Locations | `list(string)` | n/a | yes |
| <a name="input_node_pool_disk_size"></a> [node\_pool\_disk\_size](#input\_node\_pool\_disk\_size) | Size of the Node Pool Disk | `number` | `50` | no |
| <a name="input_node_pool_disk_type"></a> [node\_pool\_disk\_type](#input\_node\_pool\_disk\_type) | n/a | `string` | `"pd-standard"` | no |
| <a name="input_project_id"></a> [project\_id](#input\_project\_id) | The project ID to deploy to | `string` | n/a | yes |
| <a name="input_region"></a> [region](#input\_region) | The region to deploy to | `string` | `"europe-west1"` | no |
| <a name="input_secondary_ip_range_pods"></a> [secondary\_ip\_range\_pods](#input\_secondary\_ip\_range\_pods) | Secondary IP Ranges in Subnetwork for Pods | `string` | `"10.1.0.0/20"` | no |
| <a name="input_secondary_ip_range_services"></a> [secondary\_ip\_range\_services](#input\_secondary\_ip\_range\_services) | Secondary IP Ranges in Subnetwork for Services | `string` | `"10.10.11.0/24"` | no |
| <a name="input_service_account_roles_gke_sa"></a> [service\_account\_roles\_gke\_sa](#input\_service\_account\_roles\_gke\_sa) | Additional roles to be added to the GKE service account. | `list(string)` | `[]` | no |
| <a name="input_sql_database_name"></a> [sql\_database\_name](#input\_sql\_database\_name) | The name of the database in the Cloud SQL instance. This does not include the project ID or instance name. | `string` | `"hono-db"` | no |
| <a name="input_sql_db_user_name"></a> [sql\_db\_user\_name](#input\_sql\_db\_user\_name) | The name of the user. Changing this forces a new resource to be created. | `string` | `"hono-user"` | no |
| <a name="input_sql_instance_activation_policy"></a> [sql\_instance\_activation\_policy](#input\_sql\_instance\_activation\_policy) | This specifies when the instance should be active. Can be either ALWAYS, NEVER or ON\_DEMAND. | `string` | `"ALWAYS"` | no |
| <a name="input_sql_instance_deletion_policies"></a> [sql\_instance\_deletion\_policies](#input\_sql\_instance\_deletion\_policies) | Used to block Terraform from deleting a SQL Instance. Defaults to false. | `bool` | `false` | no |
| <a name="input_sql_instance_disk_type"></a> [sql\_instance\_disk\_type](#input\_sql\_instance\_disk\_type) | Disk Type of the SQL Instance | `string` | `"pd-hdd"` | no |
| <a name="input_sql_instance_ipv4_enable"></a> [sql\_instance\_ipv4\_enable](#input\_sql\_instance\_ipv4\_enable) | Whether this Cloud SQL instance should be assigned a public IPV4 address. At least ipv4\_enabled must be enabled or a private\_network must be configured. | `bool` | `false` | no |
| <a name="input_sql_instance_machine_type"></a> [sql\_instance\_machine\_type](#input\_sql\_instance\_machine\_type) | Machine Type of the SQL Instance | `string` | `"db-custom-1-3840"` | no |
| <a name="input_sql_instance_name"></a> [sql\_instance\_name](#input\_sql\_instance\_name) | Name of the SQL Instance | `string` | `"hono-sql"` | no |
| <a name="input_sql_instance_version"></a> [sql\_instance\_version](#input\_sql\_instance\_version) | Database Version | `string` | `"POSTGRES_14"` | no |
| <a name="input_storage_size_gb"></a> [storage\_size\_gb](#input\_storage\_size\_gb) | The storage size in GB for the Cloud SQL Instance (100 or 200) | `number` | `100` | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_cloud_endpoints_key_file"></a> [cloud\_endpoints\_key\_file](#output\_cloud\_endpoints\_key\_file) | Service Account Key File for Cloud Endpoints Service Account |
| <a name="output_device_communication_static_ip_name"></a> [device\_communication\_static\_ip\_name](#output\_device\_communication\_static\_ip\_name) | Name of the Static IP for External Ingress |
| <a name="output_gke_cluster_ca_certificate"></a> [gke\_cluster\_ca\_certificate](#output\_gke\_cluster\_ca\_certificate) | CA-Certificate for the Cluster |
| <a name="output_gke_cluster_name"></a> [gke\_cluster\_name](#output\_gke\_cluster\_name) | Name of the GKE Cluster |
| <a name="output_gke_cluster_name_endpoint"></a> [gke\_cluster\_name\_endpoint](#output\_gke\_cluster\_name\_endpoint) | Endpoint of the GKE Cluster |
| <a name="output_mqtt_static_ip"></a> [mqtt\_static\_ip](#output\_mqtt\_static\_ip) | Output of the mqtt static ip address |
| <a name="output_service_name_communication"></a> [service\_name\_communication](#output\_service\_name\_communication) | Name of the Cloud Endpoint service for device communication |
| <a name="output_sql_database"></a> [sql\_database](#output\_sql\_database) | Name of the Postgres Database |
| <a name="output_sql_db_pw"></a> [sql\_db\_pw](#output\_sql\_db\_pw) | Output of the SQL user password |
| <a name="output_sql_ip"></a> [sql\_ip](#output\_sql\_ip) | URL of the Postgres Database |
| <a name="output_sql_user"></a> [sql\_user](#output\_sql\_user) | Output of the SQL user name |
