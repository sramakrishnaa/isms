import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-user',
  standalone: false,
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css'
})
export class AddUserComponent {

  constructor(private dialogRef: MatDialogRef<AddUserComponent>) { }
 cancel(): void {
    this.dialogRef.close(false);
  }
}
