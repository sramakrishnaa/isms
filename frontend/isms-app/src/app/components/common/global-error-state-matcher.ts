import { AbstractControl, FormGroupDirective, NgForm } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

export class GlobalErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(
    control: AbstractControl | null,
    form: FormGroupDirective | NgForm | null,
  ): boolean {
    const isSubmitted = !!form?.submitted;
    const isDirty = !!control?.dirty;
    const isInvalid = !!control?.invalid;

    return isInvalid && (isDirty || isSubmitted);
  }
}
