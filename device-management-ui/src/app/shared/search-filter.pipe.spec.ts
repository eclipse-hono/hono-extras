import {SearchFilterPipe} from './search-filter.pipe';

describe('SearchFilterPipe', () => {
  it('create an instance', () => {
    const pipe = new SearchFilterPipe();
    expect(pipe).toBeTruthy();
  });

  it('should return passed array when array is empty', () => {
    const searchText: string = 'test-tenant';
    const items: any[] = [];
    const pipe = new SearchFilterPipe();
    const result = pipe.transform(items, searchText);
    expect(result).toEqual(items);
    expect(result?.length).toEqual(0);
  });

  it('should return passed array when searchText is empty', () => {
    const searchText: string = '';
    const items: any[] = getItems();
    const pipe = new SearchFilterPipe();
    const result = pipe.transform(items, searchText);
    expect(result).toEqual(items);
    expect(result?.length).toEqual(3);
  });

  it('should return one item when searchText matches', () => {
    const searchText: string = 'second';
    const items: any[] = getItems();
    const pipe = new SearchFilterPipe();
    const result = pipe.transform(items, searchText);
    // @ts-ignore
    expect(result[0].name).toEqual('second test name');
    expect(result?.length).toEqual(1);
  });
});

function getItems(): any[] {
  return [
    {id: 'first id', name: 'first test name'},
    {id: 'second id', name: 'second test name'},
    {id: 'third id', name: 'third test name'},
  ];
}
