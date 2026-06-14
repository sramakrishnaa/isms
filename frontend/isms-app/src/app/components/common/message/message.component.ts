import { Component, computed, input } from '@angular/core';

type MessageType = 'success' | 'error' | 'warning';
@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrl: './message.component.css',
  standalone: false,
})
export class MessageComponent {
  message = input<string | null>('sample message');
  type = input<MessageType>('success');

  cssClass = computed(() => {
    switch (this.type()) {
      case 'success':
        return 'bg-green-200 text-green-700 border-green-300';
      case 'warning':
        return 'bg-orange-200 text-orange-700 border-orange-300';
      case 'error':
        return 'bg-red-200 text-red-700 border-red-300';
      default:
        return '';
    }
  });
}
