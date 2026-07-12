import { Component, computed, input } from '@angular/core';
import { AlertType } from '../../../core/models/status-message';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrl: './message.component.css',
  standalone: false,
})
export class MessageComponent {
  message = input<string | undefined>('sample message');
  type = input<AlertType | undefined>('success');

  cssClass = computed(() => {
    switch (this.type()) {
      case 'success':
        return 'text-green-700';
      case 'warning':
        return 'text-orange-700';
      case 'error':
        return 'text-red-700';
      default:
        return '';
    }
  });
}
