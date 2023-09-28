/*
 * *******************************************************************************
 *  * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  *
 *  * See the NOTICE file(s) distributed with this work for additional
 *  * information regarding copyright ownership.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 */

import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'searchFilter',
})
export class SearchFilterPipe implements PipeTransform {

  transform(items: any [] | null, searchText: string): any [] | null {
    if (!items) {
      return null;
    }
    if (!searchText) {
      return items;
    }
    return items.filter(item => {
      return (item.id.toLowerCase().includes(searchText.toLowerCase()));
    });
  }
}
