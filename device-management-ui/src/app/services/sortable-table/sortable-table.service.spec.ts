import {TestBed} from '@angular/core/testing';

import {SortableTableService} from './sortable-table.service';
import {QueryList} from "@angular/core";
import {SortableTableDirective} from "./sortable-table.directive";

describe('SortableTableService', () => {
  let service: SortableTableService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SortableTableService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return queryList when resetHeaders is called', () => {
    const sortableHeaders: QueryList<SortableTableDirective> = getSortableHeaders();
    const result = service.resetHeaders(sortableHeaders, 'thirdHeader');
    expect(result.length).toEqual(3);
    expect(result.get(0)?.direction).toEqual('');
    expect(result.get(1)?.direction).toEqual('');
    expect(result.get(2)?.direction).toEqual('asc');
  });

  it('should return passed array when array is empty', () => {
    const result = service.sortItems([], {column:'col', direction:'asc'});
    expect(result.length).toEqual(0);
  });

  it('should return passed array when column is empty', () => {
    const items: any[] = getItems();
    const result = service.sortItems(items, {column:'', direction:'asc'});
    expect(result.length).toEqual(3);
    expect(result).toEqual(items);
  });

  it('should return sorted array in descending order when sortItems is called', () => {
    const items: any[] = getItems();
    const result = service.sortItems(items, {column:'thirdCol', direction:'desc'});
    expect(result[0].firstCol).toEqual('third row data');
    expect(result[1].firstCol).toEqual('second row data');
    expect(result[2].firstCol).toEqual('first row data');
  });

  it('should return sorted array in ascending order when sortItems is called', () => {
    const items: any[] = getItems();
    const result = service.sortItems(items, {column:'status.report', direction:'asc'});
    expect(result[0].status.report).toEqual('error');
    expect(result[1].status.report).toEqual('success');
    expect(result[2].status.report).toEqual('warning');
  });

});

function getSortableHeaders(): QueryList<SortableTableDirective> {
  const sortableHeaders: QueryList<SortableTableDirective> = new QueryList<SortableTableDirective>();

  const firstHeader: SortableTableDirective = new SortableTableDirective();
  firstHeader.sortable = 'firstHeader';
  firstHeader.direction = 'asc';

  const secondHeader: SortableTableDirective = new SortableTableDirective();
  secondHeader.sortable = 'secondHeader';
  secondHeader.direction = 'desc';

  const thirdHeader: SortableTableDirective = new SortableTableDirective();
  thirdHeader.sortable = 'thirdHeader';
  thirdHeader.direction = 'asc';

  const headers = [firstHeader, secondHeader, thirdHeader];
  sortableHeaders.reset(headers);
  return sortableHeaders;
}

function getItems(): any[] {
  return [
    {firstCol: 'first row data', secondCol: 'first row data', thirdCol: 'first row data', status: {report: 'success'}},
    {firstCol: 'second row data', secondCol: 'second row data', thirdCol: 'second row data', status: {report: 'warning'}},
    {firstCol: 'third row data', secondCol: 'third row data', thirdCol: 'third row data', status: {report: 'error'}},
  ];
}
