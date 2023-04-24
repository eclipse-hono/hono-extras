import {TruncatePipe} from './truncate.pipe';

describe('TruncatePipe', () => {

  it('create an instance', () => {
    const pipe = new TruncatePipe();
    expect(pipe).toBeTruthy();
  });

  it('transform - should not truncate and return text', () => {
    const text = 'test';
    const pipe = new TruncatePipe();
    const result = pipe.transform(text);
    expect(result).toEqual(text);
  });

  it('transform - should truncate for length of 4 and return truncated text', () => {
    const text = 'textShouldBeTruncated';
    const pipe = new TruncatePipe();
    const result = pipe.transform(text, 4);
    expect(result).toEqual('text...');
  });

  it('transform - should truncate for length of 4 and suffix and return truncated text', () => {
    const text = 'textShouldBeTruncated';
    const pipe = new TruncatePipe();
    const result = pipe.transform(text, 4, '*');
    expect(result).toEqual('text*');
  });
});
