import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-user-profile',
  standalone: false,
  templateUrl: './edit-user-profile.component.html',
  styleUrl: './edit-user-profile.component.css',
})
export class EditUserProfileComponent {
  data = inject(MAT_DIALOG_DATA);

  user: any;
  profileUpdateForm!: FormGroup<{
    firstName: FormControl<string | null>;
    lastName: FormControl<string>;
  }>;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditUserProfileComponent>,
  ) {
    this.user = { ...this.data };
  }
  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.profileUpdateForm = this.fb.group({
      firstName: [
        this.user.firstName,
        [Validators.required, Validators.minLength(2)],
      ],
      lastName: [
        this.user.lastName,
        [Validators.required, Validators.minLength(2)],
      ],
    });
  }
  get f(): { [key: string]: AbstractControl } {
    return this.profileUpdateForm.controls;
  }

  saveProfile(): void {
    const data = this.profileUpdateForm.getRawValue();
    this.dialogRef.close(data);
  }
}
