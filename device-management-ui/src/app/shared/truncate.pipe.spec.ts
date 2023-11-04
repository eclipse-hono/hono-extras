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
