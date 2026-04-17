import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export class CustomValidators {

    static passwordPattern(): ValidatorFn {
        const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        return Validators.pattern(PASSWORD_PATTERN);
    }
   static passwordMatch(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');

    if (!password || !confirmPassword) return null;

    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    } else {
      // remove only passwordMismatch error
      if (confirmPassword.hasError('passwordMismatch')) {
        confirmPassword.setErrors(null);
      }
    }

    return null;
  };
}

//     static passwordMatch(): ValidatorFn {
//     return (group: AbstractControl): ValidationErrors | null => {
//       const password = group.get('password')?.value;
//       const confirmCtrl = group.get('confirmPassword');

//       if (!confirmCtrl) return null;

//       if (password !== confirmCtrl.value) {
//         confirmCtrl.setErrors({ ...confirmCtrl.errors, passwordMismatch: true });
//         return { passwordMismatch: true };
//       } else {
//         // Remove only the passwordMismatch error, preserve others (e.g. required)
//         if (confirmCtrl.hasError('passwordMismatch')) {
//           const { passwordMismatch, ...remainingErrors } = confirmCtrl.errors || {};
//           confirmCtrl.setErrors(Object.keys(remainingErrors).length ? remainingErrors : null);
//         }
//         return null;
//       }
//     };
//   }

    static phoneNumber(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) return null;
            const PHONE_PATTERN = /^(\+91|0)?[6-9]\d{9}$/;
            return PHONE_PATTERN.test(control.value.trim())
                ? null
                : { invalidIndianPhone: true };
        };
    }
}