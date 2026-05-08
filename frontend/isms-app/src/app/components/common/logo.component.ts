import { Component, Input, input } from '@angular/core';

@Component({
  selector: 'app-logo',
  template: `
    <div class="flex flex-row items-center gap-1 justify-start">
      <mat-icon class="logo">inventory</mat-icon>
      <div>
        <div class="leading-none font-medium text-2xl">Inventory</div>
        <div class="font-light leading-none text-xs">Management</div>
      </div>
    </div>
  `,
  styles: `
    .logo {
      font-size: 28px;
      width:28px;
      height: 28px;
    }
  `,
  standalone: false,
})
export class LogoComponent {
  // @Input() justify = 'start';
  // @Input() height = '45px';
  // @Input() width = 'auto';
  // @Input() title = '9xl';
  // @Input() subTitle = 'sm';
}
