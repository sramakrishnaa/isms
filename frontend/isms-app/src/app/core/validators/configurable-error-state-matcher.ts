import { AbstractControl, FormGroupDirective, NgForm } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

export type ErrorDisplayMode =
  | 'dirty'
  | 'touched'
  | 'submitted'
  | 'dirtyOrTouched'
  | 'dirtyOrSubmitted'
  | 'touchedOrSubmitted';

export class ConfigurableErrorStateMatcher implements ErrorStateMatcher {
  constructor(private readonly mode: ErrorDisplayMode = 'dirtyOrSubmitted') {}

  isErrorState(
    control: AbstractControl | null,
    form: FormGroupDirective | NgForm | null,
  ): boolean {
    if (!control) return false;

    const invalid = control.invalid;
    const dirty = control.dirty;
    const touched = control.touched;
    const submitted = !!form?.submitted;

    if (!invalid) return false;

    switch (this.mode) {
      case 'dirty':
        return dirty;
      case 'touched':
        return touched;
      case 'submitted':
        return submitted;
      case 'dirtyOrSubmitted':
        return dirty || submitted;
      case 'dirtyOrTouched':
        return dirty || touched;
      case 'touchedOrSubmitted':
        return touched || submitted;
      default:
        return false;
    }
  }
}