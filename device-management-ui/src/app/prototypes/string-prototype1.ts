String.prototype.toBold = function (): string {
  if (!this) {
    return '';
  }
  return '<strong>' + this + '</strong>';
};
