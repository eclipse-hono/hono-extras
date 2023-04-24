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
