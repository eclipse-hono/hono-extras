import {Injectable, QueryList} from '@angular/core';
import {SortableTableDirective, SortEvent} from "./sortable-table.directive";

@Injectable({
  providedIn: 'root'
})
export class SortableTableService {

  public resetHeaders(sortableHeaders: QueryList<SortableTableDirective>, column: string): QueryList<SortableTableDirective> {
    sortableHeaders.forEach(header => {
      if (header.sortable !== column) {
        header.direction = '';
      }
    });
    return sortableHeaders;
  }

  public sortItems<T>(items: T[],{ column, direction }: SortEvent): T[] {
    if (!items || items.length === 0) {
      return items;
    }

    if (direction === '' || column === '') {
      return items;
    }
    const dotIndex = column.indexOf('.');
    if (dotIndex < 0) {
      return items.sort((i1: T, i2: T) => {
        // @ts-ignore
        const res = this.compare(i1[column], i2[column]);
        return direction === 'asc' ? res : -res;
      });
    } else {
      const firstProperty = column.substring(0, dotIndex);
      const secondProperty = column.substring(dotIndex + 1, column.length);
      return items.sort((i1: T, i2: T) => {
        // @ts-ignore
        const res = this.compare(i1[firstProperty][secondProperty], i2[firstProperty][secondProperty]);
        return direction === 'asc' ? res : -res;
      });
    }

  }

  private compare(v1: any, v2: any) {
    const normalizedV1 = this.normalize(v1);
    const normalizedV2 = this.normalize(v2);
    if (normalizedV1 < normalizedV2) {
      return -1;
    } else if (normalizedV1 > normalizedV2) {
      return 1;
    } else {
      return 0;
    }
  }

  private normalize(value: number | string): number | string {
    if (!value) {
      return '';
    }
    if (!isNaN(Number(value.toString()))) {
      return value;
    }
    return (value + '').toLowerCase();
  }
}
