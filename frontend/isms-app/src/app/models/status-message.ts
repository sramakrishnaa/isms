export type AlertType = 'success' | 'error' | 'warning';

export interface StatusMessage {
  message: string;
  type: AlertType;
}
