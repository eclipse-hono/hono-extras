#!/usr/bin/env python3

import json
import argparse
from uuid import uuid4
from enum import Enum
from typing import Dict
from datetime import datetime


class Collection(Enum):
    CREDENTIALS = 'credentials'
    DEVICES = 'devices'
    TENANTS = 'tenants'


IGNORED_TENANTS = ['DEFAULT_TENANT', 'HTTP_TENANT']


class HonoResourceTransformer:
    def __init__(self, collection: Collection, dump_path: str):
        self.collection = collection
        self.dump_path = dump_path
        self.updated_on = datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ')

    def transform(self):
        transform_func_name = '_transform_%s' % self.collection.value
        transform_func = getattr(self, transform_func_name)

        with open(self.dump_path) as dumpfile:
            dump = json.load(dumpfile)
            output = transform_func(dump)
            print(json.dumps(output, indent=4))

    def _transform_credentials_item(self, tenant: str, item: Dict):
        return {
            'tenant-id': tenant,
            'device-id': item['device-id'],
            'version': str(uuid4()),
            'updatedOn': self.updated_on,
            'credentials': [{
                'auth-id': item['auth-id'],
                'type': item['type'],
                'secrets': item['secrets'],
                'enabled': True
            }]
        }

    def _transform_credentials(self, dump: Dict):
        credentials = []
        for tenant_obj in dump:
            tenant_id = tenant_obj['tenant']
            if tenant_id in IGNORED_TENANTS:
                continue
            for item in tenant_obj['credentials']:
                transformed = self._transform_credentials_item(tenant_id, item)
                credentials.append(transformed)
        return credentials

    def _transform_devices_item(self, tenant: str, item: Dict):
        return {
            'tenant-id': tenant,
            'device-id': item['device-id'],
            'version': str(uuid4()),
            'updatedOn': self.updated_on,
            'device': item['data']
        }

    def _transform_devices(self, dump: Dict):
        devices = []
        for tenant_obj in dump:
            tenant_id = tenant_obj['tenant']
            if tenant_id in IGNORED_TENANTS:
                continue
            for item in tenant_obj['devices']:
                transformed = self._transform_devices_item(tenant_id, item)
                devices.append(transformed)
        return devices

    def _transform_tenants(self, dump: Dict):
        tenants = []
        for item in dump:
            tenant_id = item['tenant-id']
            if tenant_id in IGNORED_TENANTS:
                continue
            tenant = {
                'tenant-id': tenant_id,
                'version': str(uuid4()),
                'updatedOn': self.updated_on,
                'tenant': {}
            }
            if 'tenant' in item:
                tenant['tenant'] = item['tenant']

            tenants.append(tenant)
        return tenants


def main():
    parser = argparse.ArgumentParser(description='Transfrom hono collections from file-based registry to mongo.')

    parser.add_argument('collection', type=Collection, help='Type of dump')
    parser.add_argument('dump', type=str, help='Json dump to import')

    args = parser.parse_args()

    transformer = HonoResourceTransformer(args.collection, args.dump)
    transformer.transform()


if __name__ == '__main__':
    main()