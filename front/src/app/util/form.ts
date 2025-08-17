import { Renderer2 } from "@angular/core";
import { FormGroup } from "@angular/forms";


export class FormMethods {
    
    static addSubscribesForm(form: FormGroup, renderer: Renderer2) {
        Object.keys(form.controls).forEach(controlName => {
            const control = form.get(controlName);

            control?.statusChanges.subscribe(() => {
                this.updateControlClasses(form, controlName, renderer);
            });

            control?.valueChanges.subscribe(() => {
                this.updateControlClasses(form, controlName, renderer);
            });
        });
    }

    static validateForm(form: FormGroup, renderer: Renderer2) {
        Object.keys(form.controls).forEach(controlName => {
            const control = form.get(controlName);
            if (control) {
                this.updateControlClasses(form, controlName, renderer);
            }
        });
    }

    static updateControlClasses(form: FormGroup, controlName: string, renderer: Renderer2) {
        const control = form.get(controlName);
        const element = document.querySelector(`[formControlName="${controlName}"]`);

        if (element && control) {
            renderer.removeClass(element, 'form-error');

            if (control.invalid) {
                renderer.addClass(element, 'form-error');
            }
        }
    }
}