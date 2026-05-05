import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-error-message',
  template: `<div
    class="mb-3 bg-red-200 text-sm border-solid text-red-800 border border-red-300 rounded-md p-2 flex items-center justify-between"
    role="alert"
    tabindex="-1"
    aria-labelledby="hs-soft-color-danger-label"
  >
    <span id="hs-soft-color-danger-label">{{ message }}</span>
  </div>`,
  styles: ``,
})
export class ErrorMessageComponent {

  @Input({required: true}) message:string|null = '';
}
