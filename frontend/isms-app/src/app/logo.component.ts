import { Component, Input, input } from '@angular/core';

@Component({
    selector: 'app-logo',

    template: `
  <div class="flex flex-row items-end gap-1" [ngClass]="'justify-' + justify">
                <img [style]="'height:' + height + ';width:' + width"  src="./../../../../assets/dolly-solid.png" alt="ISMS">
                <div>
                    <div class="text-primary font-medium" [ngClass]="'text-' + textSize1">
                        Inventory
                    </div>
                    <div class="text-gray-600" [ngClass]="'!text-' + textSize2">
                        Management
                    </div>
                </div>
            </div>
  `,
    styles: `.logo{
    height: 45px;
    width:auto;
}`
})
export class LogoComponent {

    @Input() justify = 'start';
    @Input() height = '45px';
    @Input() width = 'auto';
    @Input() textSize1 = 'xl';
    @Input() textSize2 = 'xs';
}
