import { Component, Input, input } from '@angular/core';

@Component({
  selector: 'app-logo',
  template: `
    <div class="flex flex-row items-center gap-1 justify-start">
      <img class="logo" src="./../../../../assets/dolly-solid.png" alt="ISMS" />
      <div>
        <div class="leading-none font-medium text-2xl">Inventory</div>
        <div class="text-gray-600 leading-none text-sm">Management</div>
      </div>
    </div>
  `,
  styles: `
    .logo {
      height: 45px;
      width: auto;
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
