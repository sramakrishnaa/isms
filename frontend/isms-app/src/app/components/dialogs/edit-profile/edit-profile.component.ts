import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { EditProfileModule } from '../../../modules/edit-profile/edit-profile.module';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  AbstractControl,
  Validators,
} from '@angular/forms';
import { SnackbarService } from '../../../services/snackbar/snackbar.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-profile',
  standalone: false,
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditProfileComponent implements OnInit {
  data = inject(MAT_DIALOG_DATA);

  user:any;
  profileUpdateForm!: FormGroup<{
    firstName: FormControl<string | null>;
    lastName: FormControl<string>;
  }>;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditProfileComponent>,
  ) {
   this.user = {...this.data};
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
