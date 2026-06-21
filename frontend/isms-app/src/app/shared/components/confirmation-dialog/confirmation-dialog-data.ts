export interface ConfirmationDialogData {
  title: string;
  message: string;

  confirmButtonText?: string;
  cancelButtonText?: string;

  confirmButtonColor?: 'primary' | 'accent' | 'warn';
}

export interface ViewUserDialogData {
  userId: string;
}