import {Directive, EventEmitter, Input, Output} from '@angular/core';

type SortDirection = 'asc' | 'desc' | '';
const rotate: { [key: string]: SortDirection } = {'asc': 'desc', 'desc': '', '': 'asc'};

export interface SortEvent {
  column: string;
  direction: SortDirection;
}

@Directive({
  selector: 'th[sortable]',
  host: {
    '[class.sort-asc]': 'direction === "asc"',
    '[class.sort-desc]': 'direction === "desc"',
    '(click)': 'rotate()'
  }
})
export class SortableTableDirective {

  @Input()
  public sortable: string = '';

  @Input()
  public direction: SortDirection = '';

  @Output()
  public sort = new EventEmitter<SortEvent>();

  constructor() {
  }

  rotate() {
    this.direction = rotate[this.direction];
    this.sort.emit({column: this.sortable, direction: this.direction});
  }
}
