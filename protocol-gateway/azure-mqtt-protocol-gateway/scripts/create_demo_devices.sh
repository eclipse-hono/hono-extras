#!/bin/bash
#*******************************************************************************
# Copyright (c) 2020, 2022 Contributors to the Eclipse Foundation
#
# See the NOTICE file(s) distributed with this work for additional
# information regarding copyright ownership.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0
#
# SPDX-License-Identifier: EPL-2.0
#*******************************************************************************

################################################################################
# This simple shell script registers devices to be used to demonstrate a protocol gateway.
# It does the following:
# 1. create a new tenant (or use an existing one)
# 2. register a gateway device
# 3. create credentials for the gateway
# 4. register a demo device and configure it to use the gateway
################################################################################

################################################################################
#                                CONFIGURATION
#
DEVICE_TO_CREATE="4712" # If changed, change it in the mosquitto requests as well
GATEWAY_TO_CREATE="gw"
GATEWAY_PASSWORD="gw-secret"
# TENANT_TO_USE="" # Set this to use an existing tenant
################################################################################

REGISTRY_IP=${REGISTRY_IP:-hono.eclipseprojects.io}

set -e # exit script on error

if [ -z "$GATEWAY_PASSWORD" ] || [ -z "$REGISTRY_IP" ] ; then
  echo "ERROR: missing configuration. Exit."
  exit 1
fi

echo "# Using device registry: ${REGISTRY_IP}"

# register new tenant
if [ -z "$TENANT_TO_USE" ] ; then
  TENANT_TO_USE=$(curl --fail -X POST "http://${REGISTRY_IP}:28080/v1/tenants" 2>/dev/null | jq -r .id)
  echo "# Registered new tenant: ${TENANT_TO_USE}"
else
  echo "# Using configured tenant: ${TENANT_TO_USE}"
fi

# register new gateway
GATEWAY_TO_CREATE=$(curl --fail -X POST "http://${REGISTRY_IP}:28080/v1/devices/${TENANT_TO_USE}/${GATEWAY_TO_CREATE}" -d '{"enabled":true}' -H "Content-Type: application/json" 2>/dev/null | jq -r .id)

# set credentials for gateway
curl --fail -X PUT -H "content-type: application/json" --data-binary "[{
  \"type\": \"hashed-password\",
  \"auth-id\": \"${GATEWAY_TO_CREATE}\",
  \"secrets\": [{ \"pwd-plain\": \"${GATEWAY_PASSWORD}\" }]
}]" "http://${REGISTRY_IP}:28080/v1/credentials/${TENANT_TO_USE}/${GATEWAY_TO_CREATE}"
HONO_CLIENT_AMQP_PASSWORD=$GATEWAY_PASSWORD

# register demo device
HONO_DEMO_DEVICE_DEVICE_ID=$(curl --fail -X POST "http://${REGISTRY_IP}:28080/v1/devices/${TENANT_TO_USE}/${DEVICE_TO_CREATE}" -d "{\"enabled\":true,\"via\":[\"${GATEWAY_TO_CREATE}\"]}" -H "Content-Type: application/json" 2>/dev/null | jq -r .id)

echo "# --- DONE ---"
echo "# Please copy the following properties into the configuration of your protocol gateway:"
echo
echo "hono.demo.device.deviceId=${HONO_DEMO_DEVICE_DEVICE_ID}"
echo "hono.client.amqp.password=${HONO_CLIENT_AMQP_PASSWORD}"
echo "hono.client.amqp.username=${GATEWAY_TO_CREATE}@${TENANT_TO_USE}"
echo "hono.demo.device.tenantId=${TENANT_TO_USE}"
