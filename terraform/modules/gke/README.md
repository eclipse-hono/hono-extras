## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_google"></a> [google](#provider\_google) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [google_container_cluster.hono_cluster](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/container_cluster) | resource |
| [google_container_node_pool.standard_node_pool](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/container_node_pool) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_gke_cluster_name"></a> [gke\_cluster\_name](#input\_gke\_cluster\_name) | Name of the GKE Cluster | `string` | n/a | yes |
| <a name="input_gke_machine_type"></a> [gke\_machine\_type](#input\_gke\_machine\_type) | Machine Type for node\_pools | `string` | n/a | yes |
| <a name="input_gke_node_pool_name"></a> [gke\_node\_pool\_name](#input\_gke\_node\_pool\_name) | The name of the Node Pool in the Hono Cluster | `string` | n/a | yes |
| <a name="input_gke_release_channel"></a> [gke\_release\_channel](#input\_gke\_release\_channel) | Which Release Channel to use for the Cluster | `string` | n/a | yes |
| <a name="input_gke_service_account_email"></a> [gke\_service\_account\_email](#input\_gke\_service\_account\_email) | Email of the GKE Service Account | `string` | n/a | yes |
| <a name="input_ip_ranges_pods"></a> [ip\_ranges\_pods](#input\_ip\_ranges\_pods) | Secondary IP Ranges in Subnetwork for Pods | `string` | n/a | yes |
| <a name="input_ip_ranges_services"></a> [ip\_ranges\_services](#input\_ip\_ranges\_services) | Secondary IP Ranges in Subnetwork for Services | `string` | n/a | yes |
| <a name="input_network_name"></a> [network\_name](#input\_network\_name) | name of the network | `string` | n/a | yes |
| <a name="input_node_locations"></a> [node\_locations](#input\_node\_locations) | List of Strings for the Node Locations | `list(string)` | n/a | yes |
| <a name="input_node_pool_disk_size"></a> [node\_pool\_disk\_size](#input\_node\_pool\_disk\_size) | Size of the Node Pool Disk | `number` | n/a | yes |
| <a name="input_node_pool_disk_type"></a> [node\_pool\_disk\_type](#input\_node\_pool\_disk\_type) | Disk type of the Node Pool | `string` | n/a | yes |
| <a name="input_project_id"></a> [project\_id](#input\_project\_id) | The project ID to deploy to | `string` | n/a | yes |
| <a name="input_region"></a> [region](#input\_region) | The region to deploy to | `string` | n/a | yes |
| <a name="input_subnetwork_name"></a> [subnetwork\_name](#input\_subnetwork\_name) | name of the subnetwork | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_gke_cluster_ca_certificate"></a> [gke\_cluster\_ca\_certificate](#output\_gke\_cluster\_ca\_certificate) | CA-Certificate for the Cluster |
| <a name="output_gke_cluster_name"></a> [gke\_cluster\_name](#output\_gke\_cluster\_name) | Name of the GKE Cluster |
| <a name="output_gke_cluster_name_endpoint"></a> [gke\_cluster\_name\_endpoint](#output\_gke\_cluster\_name\_endpoint) | Endpoint of the GKE Cluster |
